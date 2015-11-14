package com.flower.admin;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.util.StringUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.alibaba.fastjson.JSON;
import com.flower.admin.dao.GoodsTemplateDao;
import com.flower.enums.ProductTagType;
import com.flower.tables.AppSetting;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsTemplate;

public class IndexHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(IndexHelper.class);
	
	@HttpListening(urlPattern="/admin/index/toAppSetting.do",isCheckSession=true)
	public Response toAppSetting(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 //得到所有商品
		 List goodTemplateList = GoodsTemplateDao.getGoodsTemplateList();
         request.setAttribute("goodTemplateList", goodTemplateList);
		 //得到appSetting页面商品
         Session db = HibernateSessionFactory.getSession();
         int id  = AppSetting.FIXED_ID;
         AppSetting appSetting = (AppSetting)db.get(AppSetting.class, id);
         if(appSetting !=null){
	         if(appSetting.homePageBargainGoods != null ){
                   int[] homePageBargainGoods = appSetting.homePageBargainGoods;
                   request.setAttribute("homePageBargainGoods", JSON.toJSON(homePageBargainGoods));
	          }
	         Map<String,int[]> homePageProductTags = appSetting.homePageProductTags;
	         if(homePageProductTags !=null){
	        	   int[] smm =homePageProductTags.get(ProductTagType.送妈妈.tag);
                   request.setAttribute("smm",JSON.toJSON(smm));
                   int[] tb =homePageProductTags.get(ProductTagType.探病.tag);
  	               request.setAttribute("tb",JSON.toJSON(tb));
  	               int[] qrj =homePageProductTags.get(ProductTagType.情人节.tag);
	               request.setAttribute("qrj",JSON.toJSON(qrj));
	               int[] jsj =homePageProductTags.get(ProductTagType.教师节.tag);
		           request.setAttribute("jsj",JSON.toJSON(jsj));
		           int[] mh =homePageProductTags.get(ProductTagType.缅怀.tag);
		           request.setAttribute("mh",JSON.toJSON(mh));
	         }
	        int[] homePageActivitys =  appSetting.homePageActivitys;
	        request.setAttribute("homePageActivitys",JSON.toJSON(homePageActivitys));
	        //集合id在前端展示 进行匹配值
	        List homePageActivityList = new ArrayList();
	        for(int i=0;i<homePageActivitys.length;i++){
		        Map map =new HashMap();
	        	map.put("index", homePageActivitys[i]);
	        	homePageActivityList.add(map);
	        }
	        request.setAttribute("homePageActivityList",homePageActivityList);
       }
         return Response.forward("/admin/appIndex_sets/appIndexSetting.jsp");
	}
	@HttpListening(urlPattern="/admin/index/addIndexGoods.do",isCheckSession=true)
	public Response addIndexGoods(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		String[] bargainId = request.getParameterValues("bargainId[]");
		 String[] mqId = request.getParameterValues("mqId[]");
		 String[] qrId = request.getParameterValues("qrId[]");
		 String[] jsId = request.getParameterValues("jsId[]");
		 String[] tbId = request.getParameterValues("tbId[]");
		 String[] mhId = request.getParameterValues("mhId[]");
		 String homePageActivitys = request.getParameter("homePageActivitys");
		 AppSetting	_appSetting = new AppSetting();
		 if(homePageActivitys!=null&&homePageActivitys!=""){//设置活动首页
			 String[] split_homePageActivitys = homePageActivitys.split(",");
			   _appSetting.homePageActivitys = strArrayToIntArray(split_homePageActivitys);
		 }
		 try {
		 Map<String,int[]> tag =new HashMap();
		 if(bargainId !=null ){//专属特价
			 int[] bant= strArrayToIntArray(bargainId);
             _appSetting.homePageBargainGoods = bant;   }
		 if(mqId  !=null){//专属母亲节
			 int[] mqIdInt= strArrayToIntArray(mqId);
			 tag.put(ProductTagType.送妈妈.tag, mqIdInt);    }
		 if(qrId !=null ){//专属亲人节
			 int[] qrIdInt= strArrayToIntArray(qrId);
 			 tag.put(ProductTagType.情人节.tag, qrIdInt);	 }
		 if(jsId !=null ){//专属教师节
			 int[] jsIdInt= strArrayToIntArray(jsId);
 			 tag.put(ProductTagType.教师节.tag, jsIdInt);	 }
		 if(tbId !=null ){//专属探病
			 int[] tbIdInt= strArrayToIntArray(tbId);
 			 tag.put(ProductTagType.探病.tag, tbIdInt);		 }
		 if(mhId !=null){//专属缅怀
			 int[] mhIdInt= strArrayToIntArray(mhId);
 			 tag.put(ProductTagType.缅怀.tag,mhIdInt);		 }
	     
		 _appSetting.homePageProductTags = tag;
	     Session session = HibernateSessionFactory.getSession();
 	     Transaction t = session.beginTransaction();
	     session.saveOrUpdate(_appSetting);
	     t.commit();
	     result.put("state",1);
		}catch(Exception e){
			e.printStackTrace();
		    result.put("state",0);
		}finally {
			   HibernateSessionFactory.closeSession();
	     }
		 return Response.stationary(result);
	}
	private int[] strArrayToIntArray(String[] strId) {
		// TODO Auto-generated method stub
		 int[] mqIdInt =new int[strId.length];
		 for(int i=0;i<strId.length;i++){
			 if(strId[i] !="" && strId[i] !=null){
				 mqIdInt[i]=Integer.parseInt(strId[i]);
			 }
		 }
		 return mqIdInt;
	}
}
