/**
 * create by 朱施健
 */
package com.flower.customer;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.guyou.util.ConfigUtil;
import org.guyou.util.FileUtil;
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

import com.flower.customer.beans.CustomerUser;
import com.flower.enums.ResultState;
import com.flower.enums.SexType;
import com.flower.tables.Images;
import com.flower.util.UUIDUtil;

/**
 * @author 朱施健 登陆相关Helper
 */
public class CustomerSettingsHelper extends AbstractHttpHelper {

	private static final Logger log = Logger.getLogger(CustomerSettingsHelper.class);
	
	@HttpListening(urlPattern = "/customer/modify_headicon.do",isCheckSession=true)
	public Response modifyHeadiconHandler(HttpServletRequest request,HttpServletResponse response, PrintWriter out) throws Exception {
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String headIcon = request.getParameter("headIcon");
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(!StringUtil.isNullValue(headIcon)){
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			
			Images img = new Images();
			img.name = "headicon_"+UUIDUtil.图片名称.id()+".jpg";
			img.datas = SerializeUtil.base64StringToBytes(headIcon);
			if(!StringUtil.isNullValue(user.customer.headIcon) && user.customer.headIcon.startsWith("http://")){
				String old_headIcon_name = user.customer.headIcon.substring(user.customer.headIcon.lastIndexOf("/")+1);
				session.createSQLQuery("delete from "+Images.class.getSimpleName()+" where name=?")
					.setString(0, old_headIcon_name)
					.executeUpdate();
				FileUtil.deleteFile(new File(ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+old_headIcon_name), false);
			}
			user.customer.headIcon = ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+img.name;
			session.update(user.customer);
			session.save(img);
			t.commit();
			FileUtil.writeFile(ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+img.name, img.datas);
			result.put("state", ResultState.Z_正常.state);
			result.put("customer", user.customer.toJsonObject());
			
		}else{
			result.put("state", ResultState.T_图片个数异常.state);
		}
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern = "/customer/add_picture_to_photowall.do",isCheckSession=true)
	public Response addPictureToPhotowallHandler(HttpServletRequest request,HttpServletResponse response, PrintWriter out) throws Exception {
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String picture = request.getParameter("picture");
		
		Map<String,Object> result = new HashMap<String,Object>();
		if(!StringUtil.isNullValue(picture)){
			Images img = new Images();
			img.name = UUIDUtil.图片名称.id()+".jpg";
			img.datas = SerializeUtil.base64StringToBytes(picture);
			user.customer.images = ArrayUtils.add(user.customer.images, ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+img.name);
			
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			session.update(user.customer);
			session.save(img);
			t.commit();
			FileUtil.writeFile(ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+img.name, img.datas);
			result.put("state", ResultState.Z_正常.state);
			result.put("images", user.customer.images);
		}else{
			result.put("state", ResultState.T_图片个数异常.state);
		}
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern = "delete_picture_from_photowall.do",isCheckSession=true)
	public Response deletePictureFromPhotowallHandler(HttpServletRequest request,HttpServletResponse response, PrintWriter out) throws Exception {
		String picture_url = StringUtil.getNotNull(request.getParameter("picture_url")).trim();
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(user.customer.images==null || user.customer.images.length==0){
			result.put("state", ResultState.T_图片个数异常.state);
		}else{
			int index = ArrayUtils.indexOf(user.customer.images, picture_url);
			if(index==-1){
				result.put("state", ResultState.T_图片个数异常.state);
			}else{
				Session session = HibernateSessionFactory.getSession();
				Transaction t = session.beginTransaction();
				
				user.customer.images = ArrayUtils.remove(user.customer.images, index);
				String old = picture_url.substring(picture_url.lastIndexOf("/")+1);
				session.createSQLQuery("delete from "+Images.class.getSimpleName()+" where name=?")
						.setString(0, old)
						.executeUpdate();
				FileUtil.deleteFile(new File(ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+old), false);
				session.update(user.customer);
				t.commit();
				result.put("state", ResultState.Z_正常.state);
				
				result.put("state", ResultState.Z_正常.state);
				result.put("images", user.customer.images);
			}
		}
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern = "/customer/modify_details.do",isCheckSession=true)
	public Response modifyDetailsHandler(HttpServletRequest request,HttpServletResponse response, PrintWriter out) throws Exception {
		String email = StringUtil.getNotNull(request.getParameter("email")).trim();
		String isCanSearchByPhone = StringUtil.getNotNull(request.getParameter("isCanSearchByPhone")).trim();
		String isOpenMsgAlert = StringUtil.getNotNull(request.getParameter("isOpenMsgAlert")).trim();
		String isReceiveStrangerMsg = StringUtil.getNotNull(request.getParameter("isReceiveStrangerMsg")).trim();
		String likeFlower = StringUtil.getNotNull(request.getParameter("likeFlower")).trim();
		String nickname = StringUtil.getNotNull(request.getParameter("nickname")).trim();
		String sex = StringUtil.getNotNull(request.getParameter("sex")).trim();
		String signature = StringUtil.getNotNull(request.getParameter("signature")).trim();
		
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(!StringUtil.isNullValue(email)) user.customer.email = email;
		if(!StringUtil.isNullValue(isCanSearchByPhone)) user.customer.isCanSearchByPhone = "1".equals(isCanSearchByPhone) || "true".equalsIgnoreCase(isCanSearchByPhone);
		if(!StringUtil.isNullValue(isOpenMsgAlert)) user.customer.isOpenMsgAlert = "1".equals(isOpenMsgAlert) || "true".equalsIgnoreCase(isOpenMsgAlert);
		if(!StringUtil.isNullValue(isReceiveStrangerMsg)) user.customer.isReceiveStrangerMsg = "1".equals(isReceiveStrangerMsg) || "true".equalsIgnoreCase(isReceiveStrangerMsg);
		if(!StringUtil.isNullValue(likeFlower)) user.customer.likeFlower = likeFlower;
		if(!StringUtil.isNullValue(nickname)) user.customer.nickname = nickname;
		if(!StringUtil.isNullValue(sex)) user.customer.sex = SexType.getSex(Integer.valueOf(sex));
		if(!StringUtil.isNullValue(signature)) user.customer.signature = signature;
	
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.update(user.customer);
		t.commit();
		
		result.put("state", ResultState.Z_正常.state);
		result.put("customer", user.customer.toJsonObject());
		return Response.stationary(result);
	}
}
