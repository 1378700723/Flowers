package com.flower.admin;

import java.io.File;
import java.io.PrintWriter;
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
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.flower.tables.Teamwork;
import com.flower.util.UUIDUtil;

public class TeamWorkHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(IndexHelper.class);
	
	@HttpListening(urlPattern="/admin/teamworkHelper/createMessage.do")
	public Response createMessage(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 Map map= flowersUploadImager(request);
		 Teamwork teamwork = new Teamwork();
		 teamwork.flowerStoreName = map.get("flowerStoreName")+"";
		 teamwork.userName = map.get("userName")+"";
		 teamwork.phone = map.get("phone")+"";
		 teamwork.qqWinx = map.get("qqWinx")+"";
		 teamwork.picture = map.get("picture")+"";
		 teamwork.address = map.get("shen")+""+map.get("shi")+map.get("xian")+map.get("details_addr")+"";
		 Session session = HibernateSessionFactory.getSession();
	     Transaction t = session.beginTransaction();
		 session.save(teamwork);
		 t.commit();
		return Response.forward("/index/success.jsp");
	}
	public static Map flowersUploadImager(HttpServletRequest request){
  		String saveFileName=null;
  		String funName="";
  		 Map map =new HashMap();
		//判断本次表单是否是一个multipart表单
  		boolean isMultipart=ServletFileUpload.isMultipartContent(request);
  		if(isMultipart){			
   			//获取工厂对象
  			DiskFileItemFactory factory=new DiskFileItemFactory();
  			//设置缓冲区大小,单位字节
  			factory.setSizeThreshold(1024*4);
  			//产生servlet上传对象
  			ServletFileUpload uploader=new ServletFileUpload(factory);
  			//设置上传文件的最大大小，位置字节
  			uploader.setSizeMax(4*1024*1024);
  			//获取表单项
  			try {
  				List<FileItem> fileItems=uploader.parseRequest(request);
  				int i = 0;
  				for (FileItem item : fileItems) {
  					//判断表单项是普通字段还是上传项
  					if(item.isFormField()){
  						funName=item.getString();
  						funName =new String(funName.getBytes("ISO-8859-1"),"UTF-8");
  					    switch(i){
  					     case 0 :map.put("flowerStoreName", funName);
  					            break;
  					     case 1 :map.put("userName", funName);
			                    break;
  					     case 2 :map.put("phone", funName);
			                    break;
  					     case 3 :map.put("qqWinx", funName);
	                             break;
  					     case 5 :map.put("shen", funName);
                                  break;
  					     case 6 :map.put("shi", funName);
			                       break;
			  			 case 7 :map.put("xian", funName);
			                     break;
			  			 case 8 :map.put("details_addr", funName);
	                             break;
  					    }
  					}else{
  						//上传项目
  						String fileName=item.getName();
  						if(!fileName.isEmpty()){
   	  						String fix=fileName.substring(fileName.lastIndexOf(".")+1);
  							fileName = UUIDUtil.图片名称.id();
  							fileName+="."+fix;
  							saveFileName=ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+fileName;
  							//构建文件对象的路径
  							File file=new File(saveFileName);
  							if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
  							//写入文件对象
  							item.write(file);
  							map.put("picture", ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+fileName);	
  							FlowersHelper.backupImage(file,fileName);
  						}
  					}
  					i++;
  				}
  			} catch (Exception e) {
  			    log.info("读取表单出现异常   map:" + map);
  				e.printStackTrace();
  			}
  		}
	    return map;
	}
}
