package com.flower.admin;

import java.io.File;
import java.io.IOException;
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
import org.guyou.util.FileUtil;
import org.guyou.util.ServletUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.flower.admin.beans.PagerInfo;
import com.flower.admin.dao.FlowersDao;
import com.flower.enums.FlowerType;
import com.flower.enums.ProductType;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.Images;
import com.flower.util.UUIDUtil;
/**
 * @author 王雪冬
 *
 */
public class FlowersHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(FlowersHelper.class);
	
	
	@HttpListening(urlPattern="/admin/flowersHelper/delFlower.do",isCheckSession=true)
	public Response delFlower(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 String id = request.getParameter("id");   
		 Map<String,Object> map =FlowersDao.delFlower(id);
		return Response.stationary(map);
	}
	@HttpListening(urlPattern="/admin/flowersHelper/getFlowersList.do",isCheckSession=true)
	public Response getFlowersList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		int intpage = 1;
		String page=request.getParameter("page");
		String method =request.getParameter("method");
		String flag =request.getParameter("flag");
		if(page != null && !page.equals(""))
		{
			intpage = Integer.parseInt(page);
		}
		if(method != null && !method.equals(""))
		{
			if(method.equals("sub"))
			{
				intpage--;
				intpage = intpage<=0?1:intpage;
			}
		 
			if(method.equals("add"))
			{
				intpage++;
			}
		}
		
		PagerInfo pi =FlowersDao.LoadAllFlowersBySearch(intpage);
 
		 
		request.setAttribute("page", pi);
		if(flag==null){
			return Response.forward("/admin/flowers_sets/flowerIndex.jsp");
		}else{
			return Response.forward("/admin/flowers_sets/flowersList.jsp");
		}
	}
	@HttpListening(urlPattern="/admin/flowersHelper/updateFlowers.do",isCheckSession=true)
	public Response updateFlowers(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 String id =request.getParameter("id"); 
		 Object flower =FlowersDao.getFlower(Integer.parseInt(id));
		 request.setAttribute("flower", flower);
		 return Response.forward("/admin/flowers_sets/addFlowers.jsp");
	}
	
	@HttpListening(urlPattern="/admin/flowersHelper/addFlowers.do",isCheckSession=true)
	public Response addFlowers(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("UTF-8");
	   
        Map map= flowersUploadImager(request);
        log.info("处理以后值 ：" +map);
        FlowerTemplate flower =new FlowerTemplate();
        
        String id=map.get("id")+"";
        flower.setName(map.get("name").toString());
        flower.setDes(map.get("desc").toString());
        flower.setFlowerLanguage(map.get("flowerLanguage").toString());
        flower.setScenario(map.get("scenario").toString());
        flower.setMainMaterial(map.get("mainMaterial").toString());
        flower.setAuxiliaryMaterial(map.get("auxiliaryMaterial").toString());
        flower.setCraft(map.get("craft").toString());
        flower.setSuitable(map.get("suitable").toString());
        flower.setDimension(map.get("dimension").toString());
        flower.setIcon(map.get("icon")+"");
        flower.ftype = FlowerType.getEnum(map.get("ftype").toString());
        flower.ptype = ProductType.getEnum(Integer.valueOf(map.get("ptype").toString()));
        String image =map.get("images").toString();
        String[] imgs = image.split(",");
        flower.images=imgs;
        if(!id.isEmpty()){
        	 flower.setId(Integer.parseInt(id));
        	 FlowersDao.updateFlowers(flower);
        	 request.setAttribute("showModel",1);
        	 return Response.redirect("../flowersHelper/getFlowersList.do");
        }else{
        	FlowersDao.addFlowers(flower);
        	request.setAttribute("showModel",1);
      		return Response.forward("/admin/flowers_sets/addFlowers.jsp");
        }
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
  					     case 0 :map.put("id", funName);
  					            break;
  					     case 1 :map.put("name", funName);
			                    break;
  					     case 2 :map.put("flowerLanguage", funName);
			                    break;
  					     case 3 :map.put("ptype", funName);
			                    break;
  					     case 4 :map.put("ftype", funName);
	                            break;
  					     case 5 :map.put("mainMaterial", funName);
			                    break;
  					     case 6 :map.put("auxiliaryMaterial", funName);
	                            break;
  					     case 7 :map.put("craft", funName);
                                break;
  					     case 8 :map.put("scenario", funName);
	                            break;
	  					 case 9 :map.put("suitable", funName);
	                            break;
	  					 case 10 :map.put("dimension", funName);
	                            break;
	  					 case 11 :map.put("desc", funName);
                                break;
	  					 case 13 :map.put("images", funName);
                                break;
  					    }
  					}else{
  						//上传项目
  						String fileName=item.getName();
  						if(!fileName.isEmpty()){
  						    //String fix=fileName.substring(fileName.lastIndexOf("\\")+1);可以获取文件名
  	  						String fix=fileName.substring(fileName.lastIndexOf(".")+1);
  							fileName = UUIDUtil.图片名称.id();
  							fileName+="."+fix;
  							saveFileName=ConfigUtil.getConfigParam("IMAGES.SAVE.PATH")+"/"+fileName;
  							//构建文件对象的路径
  							File file=new File(saveFileName);
  							if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
  							//写入文件对象
  							item.write(file);
  							map.put("icon", ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+fileName);	
  							backupImage(file,fileName);
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
	@HttpListening(urlPattern="/admin/flowersHelper/textUploadFile.do",isCheckSession=true)
	public Response textUploadFile(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
	    log.info("开始上传图片");
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
				backupImage(file,fileName);
				//String saveFileName="/upload/"+fileName;
				log.info("上传图片成功 图片名称："+fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("上传图片异常");
		}
		
  	    return Response.stationary(ConfigUtil.getConfigParam("IMAGES.WEBROOT.PATH").replace("${WebRootPath}", ServletUtil.getWebRootPath(request))+"/"+fileName);
	}
	public static void backupImage(File file,String fileName) throws IOException {
		 log.info("备份图片到数据库中");
 		 Images image =new Images();
		try {
             byte[] data =FileUtil.readFile(file);
             image.name=fileName;
             image.datas =data;
             Session session = HibernateSessionFactory.getSession();
 	    	 Transaction t = session.beginTransaction();
 			 session.save(image);
 			 t.commit();
		  } catch (Exception e) {
			  log.info("备份图片出错");
			 e.printStackTrace();
		  } 
	}
}
