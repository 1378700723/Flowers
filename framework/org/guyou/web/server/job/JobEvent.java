/**
 * @author 朱施健
 */
package org.guyou.web.server.job;

import org.guyou.event.Event;


/**
 * @author 朱施健
 *
 */
public class JobEvent implements Event{
	
	// 事件名称
	private String _type;
	// 源数据
	private Object _source;
			
	
	public JobEvent(String jobName) {
		this._type = jobName;
	}
	public JobEvent(String jobName, Object source) {
		this._type = jobName;
		this._source = source;
	}
	
	@Override
	public int hashCode(){
		return this._type.hashCode();
	}
	
	@Override
	public String getType() {
		return _type;
	}
	
	@Override
	public Object getSource() {
		return _source;
	}
	
	@Override
	public void run() {
	}
}
