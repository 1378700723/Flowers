package com.flower.admin;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.guyou.util.ConfigUtil;
import org.guyou.util.ServletUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;

import com.flower.admin.dao.ActivityDao;
import com.flower.admin.dao.FlowersDao;
import com.flower.admin.dao.GoodsTemplateDao;
import com.flower.tables.Activity;
import com.flower.util.UUIDUtil;

public class ActivityHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(ActivityHelper.class);
	
	@HttpListening(urlPattern="/admin/activity/delActivity.do",isCheckSession=true)
	public Response delActivity(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String id = request.getParameter("id");
	    Map<String,Object> map =ActivityDao.delActivity(id);
		return Response.stationary(map);
	}
	
	@HttpListening(urlPattern="/admin/activity/activityList.do",isCheckSession=true)
	public Response activityList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		List activityList = ActivityDao.getActivityList();
		request.setAttribute("activityList", activityList);
		return Response.forward("/admin/activity/activityIndex.jsp");
	}
	
	@HttpListening(urlPattern="/admin/activity/forwardActivity.do",isCheckSession=true)
	public Response forwordActivity(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 String id =request.getParameter("id"); 
		 if(id!=null&&id!=""){
			 Object activity =ActivityDao.getActivity(Integer.parseInt(id));
			 request.setAttribute("activity", activity);
		 } 
		return Response.forward("/admin/activity/editActivity.jsp");  
	}
	@HttpListening(urlPattern="/admin/activity/editActivity.do",isCheckSession=true)
	public Response editActivity(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("UTF-8");
		log.info("更新活动");
		String id = request.getParameter("id");
		String title_name = request.getParameter("title_name");
		String activity_url = request.getParameter("activity_url");
		String picture_url = request.getParameter("picture_url");
		Activity activity =new  Activity();
		activity.isDelete = false;
		activity.picture = picture_url;
		activity.title = title_name;
		activity.url = activity_url;
		SimpleDateFormat date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		activity.publishTime = date.format(new Date()).toString();
		if(id!=null&&id!=""){//修改
			activity.id = Integer.parseInt(id);
			ActivityDao.update(activity);
			result.put("state", 1);
		}else{//新增
			result.put("state", 2);
			ActivityDao.save(activity);
		}
		return Response.stationary(result);  
	}
	@HttpListening(urlPattern="/admin/activity/uploadImage.do",isCheckSession=true)
	public Response uploadImage(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		    log.info("开始上传图片");
		    Map<String,Object> result = new HashMap<String,Object>();
			DiskFileItemFactory factory=new DiskFileItemFactory();
			ServletFileUpload uploader=new ServletFileUpload(factory);
			String fileName ="";
			try {
				List<FileItem> fileItems=uploader.parseRequest(request);
				for (FileItem item : fileItems) {
				    fileName=item.getName();
					String fix=fileName.substring(fileName.lastIndexOf(".")+1);
					fileName = UUIDUtil.图片名称.id();
					fileName+="."+fix;
					File file=new File(ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+fileName);
					item.write(file);
					FlowersHelper.backupImage(file,fileName);//备份图片 
 					log.info("上传图片成功 图片名称："+fileName);
 					result.put("icon", ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+fileName);	
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("上传图片异常");
			}
 	  	    return Response.stationary(result);  
	}
}
