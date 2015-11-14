package com.flower.admin;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;

import com.flower.admin.dao.GoodsTemplateDao;
import com.flower.enums.CityEnum;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsTemplate;
/**
 * @author 王雪冬
 *
 */
public class GoodsHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(GoodsHelper.class);
	
	@HttpListening(urlPattern="/admin/goodsHelper/toAddGoods.do",isCheckSession=true)
	public Response toAddGoods(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 List flowerList = GoodsTemplateDao.getFlowerList();
         request.setAttribute("flowerList", flowerList);
		 return Response.forward("/admin/goods_sets/addGoods.jsp");
	}
	
	@HttpListening(urlPattern="/admin/goodsHelper/addGoodsTemplate.do",isCheckSession=true)
	public Response addGoodsTemplate(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("UTF-8");
	    GoodsTemplate good =new GoodsTemplate();
	    String id = request.getParameter("id");
	    String city = request.getParameter("city");
	    String[] labels = request.getParameterValues("labels");
        good.city = CityEnum.getEnum(city);
        String name=request.getParameter("name");
        String detailClassify = request.getParameter("detailClassify");
       // name =new String(name.getBytes("ISO-8859-1"),"UTF-8");
        if(detailClassify!=null&&detailClassify!=""){
        	String[] split_detailClassify = detailClassify.split(",");
        	good.detailClassify = split_detailClassify;
        }
        good.name = name;
        good.curPrice = Integer.parseInt(request.getParameter("curPrice"));
        good.oldPrice = Integer.parseInt(request.getParameter("oldPrice"));
        good.delivery = Integer.parseInt(request.getParameter("delivery"));
        good.isBargain = Integer.parseInt(request.getParameter("isBargain"))==1?true:false;
        good.labels = labels;
        FlowerTemplate flower = GoodsTemplateDao.getFlower(Integer.parseInt(request.getParameter("flowerId")));
        good.flower = flower;
        if(!id.isEmpty()){
        	  good.setId(Integer.parseInt(id));
        	  GoodsTemplateDao.updateGoodsTemplate(good);
          	  return Response.redirect("../goodsHelper/getGoodsTemplateList.do");
        }else{
        	GoodsTemplateDao.addGoods(good);
        	request.setAttribute("showModel",1);
        	List flowerList = GoodsTemplateDao.getFlowerList();
            request.setAttribute("flowerList", flowerList);
   		    return Response.forward("/admin/goods_sets/addGoods.jsp");
        }
	}
	@HttpListening(urlPattern="/admin/goodsHelper/getGoodsTemplateList.do",isCheckSession=true)
	public Response getGoodsTemplateList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		List goodsTemolateList = GoodsTemplateDao.getGoodsTemplateList();
		request.setAttribute("goodsTemolateList", goodsTemolateList);
		return Response.forward("/admin/goods_sets/goodsTemplateIndex.jsp");
	}
	
	@HttpListening(urlPattern="/admin/goodsHelper/delGoodsTemplate.do",isCheckSession=true)
	public Response delGoodsTemplate(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 String id = request.getParameter("id");   
		 Map<String,Object> map =GoodsTemplateDao.delGoodsTemplate(id);
		 return Response.stationary(map);
	}
	@HttpListening(urlPattern="/admin/goodsHelper/updateGoodsTemplate.do",isCheckSession=true)
	public Response updateGoodsTemplate(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		 String id =request.getParameter("id"); 
		 Object goodsTemplate =GoodsTemplateDao.getGoodsTemplateById(Integer.parseInt(id));
		 request.setAttribute("goodsTemplate",goodsTemplate);
		 List flowerList = GoodsTemplateDao.getFlowerList();
         request.setAttribute("flowerList", flowerList);
		 return Response.forward("/admin/goods_sets/addGoods.jsp");
	}
}
