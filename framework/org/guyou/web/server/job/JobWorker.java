/**
 * 
 */
package org.guyou.web.server.job;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.guyou.event.Event;
import org.guyou.event.IEventHandler;
import org.guyou.util.MyThread;
import org.guyou.util.MyThreadPoolExecutorContainer;
import org.guyou.web.server.HttpServletThreadPoolExcutorContainer.ThreadPoolType;

/**
 * @author 朱施健
 *
 */
public abstract class JobWorker implements Externalizable,IEventHandler<Event>,Runnable{
	
	private static final Logger log = Logger.getLogger(JobWorker.class);
	
	private static JobContainer _jobContainer = null;
	private static JobWorker _checkConditionJob = null;
	private static final ReentrantLock _lock = new ReentrantLock();
	
	//任务名称
	public String jobName = "-1";
	//线程池名称
	private String threadPoolName = "";
	//是否被销毁
	private boolean isDestory;
	//执行参数对象
	private Object excuteParam = null;
	long lastHashCode = 0;
	//添加任务时间
	private long addTime = 0;
	//移除任务时间
	private long removeTime = 0;
	//上次执行的时间
	private long lastExcuteTime = 0;
	//执行的次数
	private long excuteCount = 0;
	//离将来最近一次执行点的时间距离
	private long remainTime = 0;
	//是否重复执行
	private boolean isRepeat;
	
	@Override
	public int hashCode(){
		return 0;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(jobName);
		out.writeObject(excuteParam);
		out.writeLong(addTime);
		out.writeLong(removeTime);
		out.writeLong(lastExcuteTime);
		out.writeLong(excuteCount);
		out.writeLong(remainTime);
		out.writeBoolean(isRepeat);
		out.writeBoolean(isDestory);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		jobName = in.readUTF();
		excuteParam = in.readObject();
		addTime = in.readLong();
		removeTime = in.readLong();
		lastExcuteTime = in.readLong();
		excuteCount = in.readLong();
		remainTime = in.readLong();
		isRepeat = in.readBoolean();
		isDestory = in.readBoolean();
	}
	
	/**
	 * 构造函数
	 */
	public JobWorker(){
	}

	
	/**
	 * 在指定的时间点执行任务
	 * @param jobName 任务名称
	 * @param initialDelay 延迟（时间单位）
	 * @param period 周期（时间单位）
	 * @param timeUnit 时间单位
	 * @param isAddJobNow 十分立即加入job
	 */
	public JobWorker(String jobName,long initialDelay, long period,TimeUnit timeUnit,boolean isAddJobNow){
		this.jobName = jobName;
		this.excuteParam = new Object[]{initialDelay,period,timeUnit};
		this.isRepeat = true;
		if(isAddJobNow){
			addJob();
		}
	}
	
	/**
	 * 在指定的时间点执行任务
	 * @param jobName 任务名称
	 * @param excuteParam 任务启动时间，是将来的某个时间
	 * @param isAddJobNow 十分立即加入job
	 */
	public JobWorker(String jobName,long excuteParam,boolean isAddJobNow){
		this.jobName = jobName;
		this.excuteParam = excuteParam;
		this.isRepeat = false;
		if(isAddJobNow){
			addJob();
		}
	}
	
	/**
	 * 在指定的时间格式执行任务
	 * @param jobName 任务名称
	 * @param excuteParam 任务启动时间，格式yyyy-MM-dd HH:mm:ss中的局部，比如 yyyy-MM-dd、HH:mm:ss、mm:ss、ss等等
	 * @param isAddJobNow 十分立即加入job
	 */
	public JobWorker(String jobName,String excuteParam,boolean isAddJobNow){
		this.jobName = jobName;
		this.excuteParam = excuteParam;
		this.isRepeat= !(excuteParam.length()==19);
		if(isAddJobNow){
			addJob();
		}
	}
	
	/**
	 * 在指定条件执行任务
	 * @param jobName 任务名称
	 * @param tc 任务条件
	 * @param isAddJobNow 十分立即加入job
	 */
	public JobWorker(String jobName,JobCondition tc,boolean isAddJobNow){
		this.jobName = jobName;
		this.excuteParam = tc;
		this.isRepeat = true;
		if(isAddJobNow){
			addJob();
		}
	}
	
	/**
	 * 添加任务
	 * @return boolean
	 */
	public boolean addJob(){
		Thread curThread = Thread.currentThread();
		_lock.lock();
		try {
			if(_jobContainer == null){
				_jobContainer = new JobContainer(null);
			}
			
			if(_jobContainer.conditionJobList.containsKey(jobName) || _jobContainer.taskList.containsKey(jobName)){
				log.error("\""+jobName+"\"任务已经存在");
				return false;
			}
			if(curThread instanceof MyThread){
				this.threadPoolName = ((MyThread)curThread).getThreadPoolName();
			}
			ScheduledFuture<?> future  = null;
			boolean isConditionJob = false;
			long now = System.currentTimeMillis();
			
			//单位时间重复执行
			if(this.excuteParam instanceof Object[]){
				Object[] objs = (Object[])this.excuteParam;
				long initialDelay = (Long)objs[0];
				long period = (Long)objs[1];
				TimeUnit timeUnit = (TimeUnit)objs[2];
				//有剩余时间
				if(remainTime>0){
					future  = _jobContainer.scheduExec.scheduleAtFixedRate(this, JobUtil.timeUnitsFromMillis(remainTime, timeUnit), period, timeUnit);
				}
				//无剩余时间，说明是初次构造
				else{
					future  = _jobContainer.scheduExec.scheduleAtFixedRate(this, initialDelay, period, timeUnit);
				}
			}
			//指定将来某个时间执行
			else if(this.excuteParam instanceof Long){
				long exTime = (Long)this.excuteParam;
				long delay = exTime-now;
				if(delay<50){
					log.error("任务时间过期，不能执行，添加["+jobName+"]任务无效!!");
					this.isDestory = true;
					return false;
				}
				future  = _jobContainer.scheduExec.schedule(this, delay, TimeUnit.MILLISECONDS);
			}
			//指定某个时间格式执行
			else if(this.excuteParam instanceof String){
				String exTime = (String) this.excuteParam;
				//固定时间的执行
				if(!this.isRepeat){
					try {
						long jlt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(exTime).getTime();
						long delay = jlt-now;
						if(delay<50){
							log.error("任务时间过期，不能执行，添加["+jobName+"]任务无效!!");
							this.isDestory = true;
							return false;
						}
						future  = _jobContainer.scheduExec.schedule(this, delay, TimeUnit.MILLISECONDS);
					} catch (ParseException e) {
						this.isDestory = true;
						return false;
					}
				}
				//重复执行
				else{
					long nexttime = JobUtil.getNextExcuteTimeForString(now,exTime);
					long delay = nexttime-now; 
					future  = _jobContainer.scheduExec.schedule(this, delay, TimeUnit.MILLISECONDS);
				}
			}
			//指定条件执行
			else if(this.excuteParam instanceof JobCondition){
				JobCondition cd = (JobCondition)this.excuteParam;
				boolean isright = cd.checkParam();
				if(!isright) return false;
				isConditionJob = true;
				if(_checkConditionJob == null){
					_checkConditionJob = new JobWorker("SYSTEM_CHECK_CONDITION_JOB_WORKER","00:00:01",true) {
						@Override
						public void doJob(Event event) {
							for (JobWorker job : _jobContainer.conditionJobList.values()) {
								checkConditionTask(job);
							}
						}
					};
				}
			}
			this.addTime = this.addTime>0 ? this.addTime : now;
			if(isConditionJob){
				_jobContainer.conditionJobList.put(this.jobName,this);
			}else{
				_jobContainer.taskList.put(this.jobName, future);
			}
			if(_jobContainer.threadPoolContainer!=null){
				_jobContainer.threadPoolContainer.getEventDispatcher().addEventListener(this.jobName, this);
			}
			this.isDestory = false;
			this.lastHashCode = excuteParamHashCode(); 
			
			if(isConditionJob){
				checkConditionTask(this);
			}
			return true;
		} finally {
			_lock.unlock();
		}
	}
	
	private static void checkConditionTask(final JobWorker job){
		long now = System.currentTimeMillis();
		if(job.excuteParam instanceof JobCondition){
			JobCondition condition = (JobCondition) job.excuteParam;
			if(condition.isMeetCondition(now)){
				long[] eTimes = condition.getExcuteTimePointList(now);
				Object[] marks = condition.getExcuteTimePointList();
				if(now<eTimes[0]){
					for (int i = 0; i < eTimes.length; i++) {
						final Object mark = marks[i];
						String jobName_a = job.jobName+"$"+i;
						if(!_jobContainer.taskList.containsKey(jobName_a)){
							new JobWorker(jobName_a,eTimes[i],true) {
								@Override
								public void doJob(Event event) {
									_jobContainer.execute(job,new JobEvent(job.jobName,mark));
								}
							};
						}
					}
				}
			}
		}
	}
	
	/**
	 * 销毁任务
	 * @param taskName 任务名称
	 * @return BaseTaskHandler 被销毁的任务
	 */
	public JobWorker removeJob(){
		_lock.lock();
		try {
			boolean isConditionTask = (this.excuteParam instanceof JobCondition);
			if(isConditionTask){
				_jobContainer.conditionJobList.remove(jobName);
				JobCondition condition = (JobCondition) this.excuteParam;
				int count = condition.getExcuteTimePointList().length;
				for (int i = 0; i < count; i++) {
					String jn = jobName+"$"+i;
					_jobContainer.removeNormalTask(jn);
					if(_jobContainer.threadPoolContainer!=null){
						_jobContainer.threadPoolContainer.getEventDispatcher().removeEventListener(jn);
					}
				}
			}else{
				_jobContainer.removeNormalTask(jobName);
			}
			removeTime = System.currentTimeMillis();
			remainTime = getRemainTime();
			if(_jobContainer.threadPoolContainer!=null){
				_jobContainer.threadPoolContainer.getEventDispatcher().removeEventListener(jobName);
			}
			this.isDestory = true;
			this.threadPoolName = "";
			return this;
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * 暂停
	 */
	public void suspendJob() {
		removeJob();
	}

	/**
	 * 恢复
	 */
	public void resumeJob() {
		addJob();
	}
	
	public void changePeriod(long period) {
		if(this.excuteParam instanceof Object[]){
			try {
			_lock.lock();
				((Object[])this.excuteParam)[1] = period;
			} finally {
				_lock.unlock();
			}
		}
	}
	
	
	/**
	 * 是否销毁
	 * @return
	 */
	public boolean isDestroy(){
		return isDestory;
	}
	
	/**
	 * 获得当前时间距离下次触发时间点的距离
	 * @return
	 */
	public long getRemainTime(){
		long now = System.currentTimeMillis();
		//距离下次触发时间点的距离
		long rt = 0;
		
		//单位时间重复执行
		if(this.excuteParam instanceof Object[]){
			Object[] objs = (Object[])this.excuteParam;
			long initialDelay = (Long)objs[0];
			long period = (Long)objs[1];
			TimeUnit timeUnit = (TimeUnit)objs[2];
			
			long nextTime =0;
			//还没有执行过
			if(this.excuteCount==0 && this.lastExcuteTime==0 ){
				nextTime = addTime + timeUnit.toMillis(initialDelay);
			}
			//执行过了
			else{
				nextTime = this.lastExcuteTime+ timeUnit.toMillis(period);
			}
			rt = nextTime-now;
			return rt>0?rt:0;
		}
		//指定将来某个时间执行
		else if(this.excuteParam instanceof Long){
			long nextTime = (Long)this.excuteParam;
			rt = nextTime-now;
			return (this.excuteCount==0 && rt>0 ) ? rt : 0;
		}
		//指定某个时间格式执行
		else if(this.excuteParam instanceof String){
			String exTime = (String) this.excuteParam;
			//固定时间的执行
			if(!this.isRepeat){
				long nextTime = 0;
				try {
					nextTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(exTime).getTime();
				} catch (ParseException e) {
				}
				rt = nextTime-now;
				return (this.excuteCount==0 && rt>0 ) ? rt : 0;
			}
			//重复执行
			else{
				long nextTime = JobUtil.getNextExcuteTimeForString(now,exTime);
				rt = nextTime-now;
				return rt>0 ? rt : 0;
			}
		}
		//指定条件执行
		else if(this.excuteParam instanceof JobCondition){
//			long nextTime = JobUtil.getNextExcuteTimeForCondition(now,(JobCondition)this.excuteParam);
//			rt = nextTime-now;
//			return rt>0 ? rt : 0;
		}
		return 0;
	}
	
	private long excuteParamHashCode(){
		long r = 0;
		//单位时间重复执行
		if(this.excuteParam instanceof Object[]){
			Object[] objs = (Object[])this.excuteParam;
			long initialDelay = (Long)objs[0];
			long period = (Long)objs[1];
			TimeUnit timeUnit = (TimeUnit)objs[2];
			r = initialDelay*100000000 + period*10000 + timeUnit.hashCode();
		}
		else {
			r = this.excuteParam.hashCode();
		}
		return r;
	}
	
	@Override
	public void doExecute(Event event){
		if(!this.isRepeat){
			this.remainTime=0;
		}
		MyThread thread = MyThread.currentThread();
		String threadPoolName = thread.getThreadPoolName();
		doJob(event);
		if(thread.action!=null) thread.action.excute();
		if(!this.isRepeat){
			removeJob();
		}
		this.isDestory = !_jobContainer.taskList.containsKey(this.jobName) && !_jobContainer.conditionJobList.containsKey(this.jobName);
	}
	
	public JobCondition getCondition(){
		if(this.excuteParam instanceof JobCondition){
			return (JobCondition)this.excuteParam;
		}
		log.error("当前Job非条件类型的Job!!");
		return null;
	}
	
	public Object getExcuteParam(){
		return this.excuteParam;
	}
	
	/**
	 * 执行任务
	 * @param event
	 */
	public abstract void doJob(Event event);
	

	@Override
	public void run() {
		try {
			long now = System.currentTimeMillis();
			if(!this.isRepeat){
				_jobContainer.removeNormalTask(jobName);
			}
			//指定格式
			if(this.excuteParam instanceof String){
				String exTime = (String) this.excuteParam;
				//固定时间的执行
				if(this.isRepeat){
					long nexttime = JobUtil.getNextExcuteTimeForString(now,exTime);
					long delay = nexttime-now; 
					//添加后边执行的任务
					_jobContainer.taskList.put(this.jobName, _jobContainer.scheduExec.schedule(this, delay, TimeUnit.MILLISECONDS));
				}
			}
			//
			else if(this.excuteParam instanceof Object[]){
				long nowHashCode = excuteParamHashCode();
				if(this.lastExcuteTime!=nowHashCode){
					Object[] objs = (Object[])this.excuteParam;
					long period = (Long)objs[1];
					TimeUnit timeUnit = (TimeUnit)objs[2];
					_jobContainer.removeNormalTask(jobName);
					_jobContainer.taskList.put(this.jobName, _jobContainer.scheduExec.scheduleAtFixedRate(this,period, period, timeUnit));
				}
			}
			//指定条件执行
			else if(this.excuteParam instanceof JobCondition){
//				long nexttime = JobUtil.getNextExcuteTimeForCondition(now,(JobCondition)this.excuteParam);
//				long delay = nexttime-now; 
//				//添加后边执行的任务
//				container.taskList.put(this.jobName, new TaskData(this, container.scheduExec.schedule(this, delay, TimeUnit.MILLISECONDS)));
			}
			this.lastExcuteTime = now;
			this.excuteCount += 1;
			//添加事件到主线程
			_jobContainer.execute(this,null);
		} catch (Exception e) {
			log.error(jobName+"任务执行时异常!!", e);
		}
	}
	
	public static void removeAllJob(){
		for ( Iterator<ScheduledFuture<?>> iterator = _jobContainer.taskList.values().iterator() ; iterator.hasNext() ; ) {
			ScheduledFuture<?> s = iterator.next();
			s.cancel(false);
		}
		
		for ( Iterator<JobWorker> iterator = _jobContainer.conditionJobList.values().iterator() ; iterator.hasNext() ; ) {
			JobWorker job = iterator.next();
			job.removeJob();
		}
	}
	
	
	private static class JobContainer{
		//任务列表
		private Map<String,ScheduledFuture<?>> taskList = new HashMap<String, ScheduledFuture<?>>(); 
		private Map<String,JobWorker> conditionJobList = new HashMap<String,JobWorker>();
		private MyThreadPoolExecutorContainer<Event> threadPoolContainer = null;
		private ScheduledExecutorService scheduExec = null;
		
		private JobContainer(MyThreadPoolExecutorContainer<Event> threadPoolContainer){
			this.threadPoolContainer = threadPoolContainer;
			if(threadPoolContainer!=null){
				this.scheduExec = (ScheduledExecutorService) threadPoolContainer.getThreadPoolExecutor(ThreadPoolType.TIMER_THREADPOOL.name());
			}else{
				this.scheduExec = Executors.newSingleThreadScheduledExecutor();
			}
		} 
		
		void removeNormalTask(String jn){
			if(taskList.containsKey(jn)){
				taskList.remove(jn).cancel(false);
			}
		}
		
		void execute(JobWorker job, JobEvent event){
			if(event == null){
				event = new JobEvent(job.jobName,job.excuteParam);
			}
			if(this.threadPoolContainer!=null && this.threadPoolContainer.containsThreadPoolExecutor(job.threadPoolName)){
				this.threadPoolContainer.execute(job.threadPoolName,event);
			}else{
				job.doJob(event);
			}
		}
	}
}
