/**
 * create by 朱施健
 */
package com.flower.customer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.util.ConfigUtil;
import org.guyou.util.DateUtil;
import org.guyou.util.FileUtil;
import org.guyou.util.MD5;
import org.guyou.util.MathUtil;
import org.guyou.util.SerializeUtil;
import org.guyou.util.ServletUtil;
import org.guyou.util.StringUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.easemob.server.jersey.EasemobManager;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flower.customer.beans.CustomerUser;
import com.flower.enums.RegisteChannel;
import com.flower.enums.ResultState;
import com.flower.enums.SexType;
import com.flower.tables.Customer;
import com.flower.tables.Images;
import com.flower.util.UUIDUtil;

/**
 * @author 朱施健
 * 登陆相关Helper
 */
public class CustomerLoginHelper extends AbstractHttpHelper {
	
	private static final Logger log = Logger.getLogger(CustomerLoginHelper.class);
	
	private CCPRestSDK _SMS_SDK;
	private boolean  _IS_SMS_CHECK = "true".equalsIgnoreCase(ConfigUtil.getConfigParam("YUNTONGXUN.CHECK.ISOPEN"));
	
	public CustomerLoginHelper(){
		_SMS_SDK = new CCPRestSDK();
		_SMS_SDK.init(ConfigUtil.getConfigParam("YUNTONGXUN.REST_IP"), ConfigUtil.getConfigParam("YUNTONGXUN.REST_PORT"));// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		_SMS_SDK.setAccount(ConfigUtil.getConfigParam("YUNTONGXUN.ACCOUNT_SID"),ConfigUtil.getConfigParam("YUNTONGXUN.AUTH_TOKEN"));// 初始化主帐号名称和主帐号令牌
		_SMS_SDK.setAppId(ConfigUtil.getConfigParam("YUNTONGXUN.APP_ID"));// 初始化应用ID
	}
	
	private class SmsAuthcode{
		String phone;
		String code;
		long time;
		SmsAuthcode(String phone,String code,long time){
			this.phone = phone;
			this.code = code;
			this.time = time;
		}
	}
	
	@HttpListening(urlPattern="/customer/sms_authcode.do")
	public Response smsAuthcodeHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
		int number = 0;
		for (int i = 0; i < 6; i++) {
			number += MathUtil.randomInt(1, 9) * Math.pow(10, i);
		}
		String timeout = ConfigUtil.getConfigParam("YUNTONGXUN.SMS_CODE_TIMEOUT");
		
		if(!_IS_SMS_CHECK){
			result.put("state",ResultState.Z_正常.state);
			result.put("leftTime",120);
			return Response.stationary(result);
		}
		
		HashMap<String, Object> sms_result = _SMS_SDK.sendTemplateSMS(phone,ConfigUtil.getConfigParam("YUNTONGXUN.SMS_TEMPLATE_ID") ,new String[]{""+number,timeout});
		
		log.info("SDKTestGetSubAccounts result=" + sms_result);
		if("000000".equals(sms_result.get("statusCode"))){
			int timeout_s = Integer.valueOf(timeout)*60;
			long 过期时间 = System.currentTimeMillis()+timeout_s*1000L;
			request.getSession().setAttribute("SMS_AUTHCODE", new SmsAuthcode(phone, ""+number,过期时间));
			result.put("state",ResultState.Z_正常.state);
			result.put("leftTime",timeout_s);
		}else{
			//异常返回输出错误码和错误信息
			//System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			result.put("state",ResultState.F_发送验证码失败.state);
			result.put("statusMsg",sms_result.get("statusMsg"));
			log.error("手机号="+phone+", 错误码=" + result.get("statusCode") +", 错误信息= "+result.get("statusMsg"));
		}
		return Response.stationary(result);
	}
	
	/**
	@HttpListening(urlPattern="/customer/check_authcode.do")
	public Response checkAuthcodeHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
		String authcode = StringUtil.getNotNull(request.getParameter("authcode")).trim();
		
		long now = System.currentTimeMillis();
		Map<String,Object> result = new HashMap<String,Object>();

		if(true) {
			result.put("state",ResultState.Z_正常.state);
			return Response.stationary(result);
		}
		
		SmsAuthcode smsAuthcode = (SmsAuthcode) request.getSession().getAttribute("SMS_AUTHCODE");
		request.getSession().removeAttribute("SMS_AUTHCODE");
		
		if(smsAuthcode==null){
			result.put("state",ResultState.Z_数据逻辑错误.state);
		}else if(now>smsAuthcode.time){
			result.put("state",ResultState.H_会话超时.state);
		}else if(!smsAuthcode.code.equalsIgnoreCase(authcode)){
			result.put("state",ResultState.Y_验证码错误.state);
		}else if(!smsAuthcode.phone.equalsIgnoreCase(phone)){
			result.put("state",ResultState.S_手机号与验证码不匹配.state);
		}else {
			result.put("state",ResultState.Z_正常.state);
		}
		return Response.stationary(result);
	}
	*/
	
	@HttpListening(urlPattern="/customer/registe.do")
	public Response registeHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
		String authcode = StringUtil.getNotNull(request.getParameter("authcode")).trim();
		
		Map<String,Object> result = new HashMap<String,Object>();
		if(_IS_SMS_CHECK){
			SmsAuthcode smsAuthcode = (SmsAuthcode) request.getSession().getAttribute("SMS_AUTHCODE");
			request.getSession().removeAttribute("SMS_AUTHCODE");
			
			if(smsAuthcode==null){
				result.put("state",ResultState.Z_数据逻辑错误.state);
				return Response.stationary(result);
			}else if(System.currentTimeMillis()>smsAuthcode.time){
				result.put("state",ResultState.H_会话超时.state);
				return Response.stationary(result);
			}else if(!smsAuthcode.code.equalsIgnoreCase(authcode)){
				result.put("state",ResultState.Y_验证码错误.state);
				return Response.stationary(result);
			}else if(!smsAuthcode.phone.equalsIgnoreCase(phone)){
				result.put("state",ResultState.S_手机号与验证码不匹配.state);
				return Response.stationary(result);
			}
		}
		
		Session session = HibernateSessionFactory.getSession();
		//检查用户名和手机是否重复
		String check_sql = "select count(*) from "+Customer.class.getSimpleName()+" where phone=?";
		
		//8016 [main] INFO com.easemob.server.jersey.apidemo.EasemobIMUsers - 注册IM用户[单个]: {"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","path":"/users","uri":"https://a1.easemob.com/huahuahui/huahuahui/users","entities":[{"uuid":"336ba80a-1346-11e5-98ec-b9fcace24670","type":"user","created":1434362793600,"modified":1434362793600,"username":"kenshinnuser103","activated":true}],"timestamp":1434362793597,"duration":36,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
		Number counts = (Number) session.createSQLQuery(check_sql).setString(0,phone).uniqueResult();
		if(counts.intValue()>0){
			result.put("state",ResultState.S_手机号重复.state);
		}else{
			String uid = UUIDUtil.用户ID.id();
			ObjectNode resp = EasemobManager.registeCustomer(uid, phone);
			if(resp == null) {
				log.error("IM注册时返回NULL");
				result.put("state", ResultState.I_IM异常.state);
			}else{
				//{"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","path":"/users","uri":"http://a1.easemob.com/huahuahui/huahuahui/users","entities":[{"uuid":"90664c4a-2aa2-11e5-b7e4-51f5debd80c7","type":"user","created":1436931340036,"modified":1436931340036,"username":"RRRRRRRRRR","activated":true}],"timestamp":1436931340034,"duration":34,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
				//{"error":"duplicate_unique_property_exists","timestamp":1436931340446,"duration":0,"exception":"org.apache.usergrid.persistence.exceptions.DuplicateUniquePropertyExistsException","error_description":"Application f90baaf0-1324-11e5-b913-a5090b1fe5b5Entity user requires that property named username be unique, value of RRRRRRRRRR exists","statusCode":400}
				int statusCode = resp.get("statusCode").asInt();
				
				if(statusCode==200 || (statusCode==400 && "duplicate_unique_property_exists".equalsIgnoreCase(resp.get("error").asText()))){
					//"entities":[{"uuid":"336ba80a-1346-11e5-98ec-b9fcace24670","type":"user","created":1434362793600,"modified":1434362793600,"username":"kenshinnuser103","activated":true}]
					Customer c = new Customer();
					c.uid = uid;
					c.phone = phone;
					c.passwd = MD5.getStringHash(uid+System.currentTimeMillis()+phone);
					c.regtime = DateUtil.time();
					c.channel = RegisteChannel.手机注册;
					
					Transaction t = session.beginTransaction();
					session.save(c);
					t.commit();
					
					request.getSession().setAttribute(SessionKeyEnum.CUSTOMER_USER_DATA, new CustomerUser(c));
					result.put("state", ResultState.Z_正常.state);
					log.error("IM注册时返回正确信息:"+resp.toString());
				}else{
					log.error("IM注册时返回错误信息:"+resp.toString());
					result.put("state", ResultState.I_IM异常.state);
				}
			}
		}
		return Response.stationary(result);
	}
	
	
	/**
	 * 完成注册
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/finish_registe.do",isCheckSession=true)
	public Response finishRegisteHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
		String sex = StringUtil.getNotNull(request.getParameter("sex")).trim();
		String nickname = StringUtil.getNotNull(request.getParameter("nickname")).trim();
		String headIcon = request.getParameter("headIcon");
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		Images img = null;
		if(!StringUtil.isNullValue(headIcon)){
			img = new Images();
			img.name = "headicon_"+UUIDUtil.图片名称.id()+".jpg";
			img.datas = SerializeUtil.base64StringToBytes(headIcon);
		}
		
		if(user.customer.phone.equals(phone)){
			user.customer.sex = SexType.getSex(Integer.valueOf(sex));
			user.customer.nickname = nickname;
			user.customer.headIcon = img==null ? "" : ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+img.name;
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			session.update(user.customer);
			if(img!=null) session.save(img);
			t.commit();
			if(img!=null) FileUtil.writeFile(ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+img.name, img.datas);
			
			result.put("state", ResultState.Z_正常.state);
			result.put("customer", user.customer.toJsonObject());
		}else{
			result.put("state", ResultState.E_异常.state);
		}
		return Response.stationary(result);
	}
	
	
	@HttpListening(urlPattern="/customer/login.do")
	public Response loginHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
		//1[密码验证] | 2[短信码验证]
		String verifyType = StringUtil.getNotNull(request.getParameter("verifyType")).trim();
		String passwd = StringUtil.getNotNull(request.getParameter("passwd")).trim();
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		//短信码验证
		if("2".equals(verifyType)){
			if(_IS_SMS_CHECK){
				SmsAuthcode smsAuthcode = (SmsAuthcode) request.getSession().getAttribute("SMS_AUTHCODE");
				request.getSession().removeAttribute("SMS_AUTHCODE");
				
				if(smsAuthcode==null){
					result.put("state",ResultState.Z_数据逻辑错误.state);
					return Response.stationary(result);
				}else if(System.currentTimeMillis()>smsAuthcode.time){
					result.put("state",ResultState.H_会话超时.state);
					return Response.stationary(result);
				}else if(!smsAuthcode.code.equalsIgnoreCase(passwd)){
					result.put("state",ResultState.Y_验证码错误.state);
					return Response.stationary(result);
				}else if(!smsAuthcode.phone.equalsIgnoreCase(phone)){
					result.put("state",ResultState.S_手机号与验证码不匹配.state);
					return Response.stationary(result);
				}
			}
		}
		
		Session session = HibernateSessionFactory.getSession();
		Customer user = (Customer) session.createQuery("from "+Customer.class.getName()+" where phone=?").setString(0,phone).setCacheable(true).uniqueResult();
		if(user==null){
			result.put("state",ResultState.Y_用户不存在.state);
			return Response.stationary(result);
		}
		
		if("1".equals(verifyType) && !passwd.equals(user.passwd)){
			result.put("state",ResultState.M_密码错误.state);
			return Response.stationary(result);
		}
		request.getSession().setAttribute(SessionKeyEnum.CUSTOMER_USER_DATA, new CustomerUser(user));
		result.put("state", ResultState.Z_正常.state);
		result.put("customer", user.toJsonObject());
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/thirdparty_login.do")
	public Response thirdpartyLoginHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String uid = StringUtil.getNotNull(request.getParameter("thirdparty_uid")).trim();
		String nickname = StringUtil.getNotNull(request.getParameter("nickname")).trim();
		SexType sex = SexType.getSex(Integer.valueOf(StringUtil.getNotNull(request.getParameter("sex")).trim()));
		RegisteChannel channel = RegisteChannel.getEnum(Integer.valueOf(StringUtil.getNotNull(request.getParameter("channel")).trim()));
		//用户编号
		String realUid = "t"+channel.channel+uid;
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		Session session = HibernateSessionFactory.getSession();
		Customer user = (Customer) session.get(Customer.class, realUid);
		if(user==null){
			ObjectNode resp = EasemobManager.registeCustomer(realUid, uid);
			log.error("第三方登陆时  nickname="+nickname+" sex="+sex+" channel="+channel.toString()+" realUid="+realUid+" uid="+uid+" 注册IM信息  "+(resp==null?"NULL":resp.toString()));
			if(resp == null) {
				result.put("state", ResultState.I_IM异常.state);
			}else{
				//{"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","path":"/users","uri":"http://a1.easemob.com/huahuahui/huahuahui/users","entities":[{"uuid":"90664c4a-2aa2-11e5-b7e4-51f5debd80c7","type":"user","created":1436931340036,"modified":1436931340036,"username":"RRRRRRRRRR","activated":true}],"timestamp":1436931340034,"duration":34,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
				//{"error":"duplicate_unique_property_exists","timestamp":1436931340446,"duration":0,"exception":"org.apache.usergrid.persistence.exceptions.DuplicateUniquePropertyExistsException","error_description":"Application f90baaf0-1324-11e5-b913-a5090b1fe5b5Entity user requires that property named username be unique, value of RRRRRRRRRR exists","statusCode":400}
				int statusCode = resp.get("statusCode").asInt();
				if(statusCode==200 || (statusCode==400 && "duplicate_unique_property_exists".equalsIgnoreCase(resp.get("error").asText()))){
					//"entities":[{"uuid":"336ba80a-1346-11e5-98ec-b9fcace24670","type":"user","created":1434362793600,"modified":1434362793600,"username":"kenshinnuser103","activated":true}]
					user = new Customer();
					user.uid = realUid;
					user.phone = uid;
					user.nickname = nickname;
					user.sex = sex; 
					user.passwd = MD5.getStringHash(realUid+System.currentTimeMillis());
					user.regtime = DateUtil.time();
					user.channel = channel;
					
					Transaction t = session.beginTransaction();
					session.save(user);
					t.commit();
					
					request.getSession().setAttribute(SessionKeyEnum.CUSTOMER_USER_DATA, new CustomerUser(user));
					result.put("state", ResultState.Z_正常.state);
					result.put("customer", user.toJsonObject());
				}else{
					result.put("state", ResultState.I_IM异常.state);
				}
			}
		}else{
			request.getSession().setAttribute(SessionKeyEnum.CUSTOMER_USER_DATA, new CustomerUser(user));
			result.put("state", ResultState.Z_正常.state);
			result.put("customer", user.toJsonObject());
		}
		return Response.stationary(result);
	}
}
