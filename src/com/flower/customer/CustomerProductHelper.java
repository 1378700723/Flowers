package com.flower.customer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.guyou.util.StringUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.alibaba.fastjson.JSONObject;
import com.flower.customer.beans.CustomerUser;
import com.flower.enums.FlowerType;
import com.flower.enums.ProductTagType;
import com.flower.enums.ResultState;
import com.flower.tables.Activity;
import com.flower.tables.AppSetting;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsTemplate;

/**
 * @author 王雪冬
 * 首页展示商品相关Helper
 */
public class CustomerProductHelper   extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(CustomerProductHelper.class);
	
	@HttpListening(urlPattern="/customer/flower_type_list.do")
	public Response flowerTypeListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> types = new ArrayList<Map<String,Object>>();
		for (FlowerType type : FlowerType.values()) {
			Map<String,Object> t = new HashMap<String,Object>();
			t.put("id",type.type);
			t.put("name",type.name());
			types.add(t);
		}
		result.put("state", ResultState.Z_正常.state);
		result.put("types", types);
		return Response.stationary(result);
	}
		
	@HttpListening(urlPattern="/customer/getBargainProduct.do")
	public Response getBargainProduct(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("开始查询特价商品   ");
		Map<String,Object> result = new HashMap<String,Object>();
		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.id goodTemplateId,g.curPrice,g.oldPrice,f.id flowerTemplateId,f.icon,g.name ");
		sql.append("from "+GoodsTemplate.class.getSimpleName()+" g  left join "+FlowerTemplate.class.getSimpleName()+" f ");
		sql.append("on g.flowerid=f.id where g.bargain =1");
		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List  goodTemplateList =query.list();
        result.put("data",goodTemplateList);
        result.put("state", ResultState.Z_正常.state);
		log.info("查询特价商品 结束 ");
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/getProductDetails.do")
	public Response getProductDetails(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String goodsTemplateId = StringUtil.getNotNull(request.getParameter("goodTemplateId")).trim();
		
		CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		
		log.info("开始查询商品详情   "+goodsTemplateId);
		Map<String,Object> result = new HashMap<String,Object>();
		String isFavorite_sql = user==null?"0":("(select count(*) from Relations_Customer_GoodsTemplate where uid='"+user.customer.uid+"' and goods_template_id="+goodsTemplateId+")");
		String find_sql = "select g.id goodTemplateId,g.city,g.curPrice,g.grade,g.monthSales,g.delivery,g.name,f.images,f.mainMaterial,f.auxiliaryMaterial,f.scenario,f.suitable,f.craft,f.dimension,f.des,g.detailClassify,g.labels,"+isFavorite_sql+" isFavorite "
							+"from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id  "
							+ "where  g.id =?";
		Session session = HibernateSessionFactory.getSession();
		Map<String,Object> productDetails = (Map<String, Object>) session.createSQLQuery(find_sql.toString()).setString(0, goodsTemplateId).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
        
		productDetails.put("images", StringUtil.getNotNull((String)productDetails.remove("images")).split(","));
		productDetails.put("labels", ProductTagType.getLabelNames(StringUtil.getNotNull((String)productDetails.remove("labels")).split(",")));
		productDetails.put("detailClassify", StringUtil.getNotNull((String)productDetails.remove("detailClassify")).split(","));
		
		List<Map<String,Object>> attributes = new ArrayList<Map<String,Object>>();
		
		String mainMaterial = (String)productDetails.remove("mainMaterial");
		if(!StringUtil.isNullValue(mainMaterial)){
			Map<String,Object> attribute = new HashMap<String,Object>();
			attribute.put("attribute", "主材");
			attribute.put("value", mainMaterial);
			attributes.add(attribute);
		}
		
		String auxiliaryMaterial = (String)productDetails.remove("auxiliaryMaterial");
		if(!StringUtil.isNullValue(auxiliaryMaterial)){
			Map<String,Object> attribute = new HashMap<String,Object>();
			attribute.put("attribute", "辅材");
			attribute.put("value", auxiliaryMaterial);
			attributes.add(attribute);
		}
		
		String scenario = (String)productDetails.remove("scenario");
		if(!StringUtil.isNullValue(scenario)){
			Map<String,Object> attribute = new HashMap<String,Object>();
			attribute.put("attribute", "适用场景");
			attribute.put("value", scenario);
			attributes.add(attribute);
		}
		
		String suitable = (String)productDetails.remove("suitable");
		if(!StringUtil.isNullValue(suitable)){
			Map<String,Object> attribute = new HashMap<String,Object>();
			attribute.put("attribute", "适用对象");
			attribute.put("value", suitable);
			attributes.add(attribute);
		}
		
		String craft = (String)productDetails.remove("craft");
		if(!StringUtil.isNullValue(craft)){
			Map<String,Object> attribute = new HashMap<String,Object>();
			attribute.put("attribute", "工艺");
			attribute.put("value", craft);
			attributes.add(attribute);
		}
		
		String dimension = (String)productDetails.remove("dimension");
		if(!StringUtil.isNullValue(dimension)){
			Map<String,Object> attribute = new HashMap<String,Object>();
			attribute.put("attribute", "尺寸规格");
			attribute.put("value", dimension);
			attributes.add(attribute);
		}
		
		productDetails.put("attributes", attributes);
		
        result.put("data",productDetails);
        result.put("state", ResultState.Z_正常.state);
        log.info("开始查询商品详情 结果   "+productDetails);
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/search_flowergoods.do")
	public Response searchFlowerGoodsListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String city = StringUtil.getNotNull(request.getParameter("city")).trim();
		String keyword = StringUtil.getNotNull(request.getParameter("keyword")).trim();
		int startIndex = Integer.valueOf(StringUtil.getNotNull(request.getParameter("startIndex")).trim());
		int rows = Integer.valueOf(StringUtil.getNotNull(request.getParameter("rows")).trim());
		
		Map<String,Object> result = new HashMap<String,Object>();
		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.id goodTemplateId,g.city,g.curPrice,g.monthSales,g.delivery,f.icon,g.name ");
		sql.append("from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id where 1=1 ");
		
		if(!StringUtil.isNullValue(city)){
		    sql.append("and city = '"+city+"' ");	
		}
		if(!StringUtil.isNullValue(keyword)){
		    sql.append("and concat(f.name,',',g.name) like '%"+keyword+"%' ");	
		}
		sql.append("order by curPrice asc limit "+startIndex+","+rows);
		List<Map<String,Object>> productList = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        result.put("datas",productList);
        result.put("state", ResultState.Z_正常.state);
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/getFlowerGoodsList.do")
	public Response getFlowerGoodsListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String city = StringUtil.getNotNull(request.getParameter("city")).trim();
		String flowerType = StringUtil.getNotNull(request.getParameter("flowerType")).trim();
		int startIndex = Integer.valueOf(StringUtil.getNotNull(request.getParameter("startIndex")).trim());
		int rows = Integer.valueOf(StringUtil.getNotNull(request.getParameter("rows")).trim());
		
		Map<String,Object> result = new HashMap<String,Object>();
		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.id goodTemplateId,g.city,g.curPrice,g.monthSales,g.delivery,f.icon,g.name ");
		sql.append("from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id where 1=1 ");
		
		if(!StringUtil.isNullValue(city)){
		    sql.append("and city = '"+city+"' ");	
		}
		if(!StringUtil.isNullValue(flowerType)){
			 sql.append("and ftype = '"+flowerType+"' ");	;	
		}
		sql.append("order by curPrice asc limit "+startIndex+","+rows);
		List<Map<String,Object>> productList = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        result.put("datas",productList);
        result.put("state", ResultState.Z_正常.state);
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/getPlateFlowerGoodsList.do")
	public Response getPlateFlowerGoodsListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String city = StringUtil.getNotNull(request.getParameter("city")).trim();
		String plateId = StringUtil.getNotNull(request.getParameter("plateId")).trim();
		int startIndex = Integer.valueOf(StringUtil.getNotNull(request.getParameter("startIndex")).trim());
		int rows = Integer.valueOf(StringUtil.getNotNull(request.getParameter("rows")).trim());
		
		Map<String,Object> result = new HashMap<String,Object>();
		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.id goodTemplateId,g.city,g.curPrice,g.monthSales,g.delivery,f.icon,g.name ");
		sql.append("from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f ");
		sql.append("on g.flowerid=f.id  where 1=1 ");
		if(!StringUtil.isNullValue(city)){
		    sql.append("and city = '"+city+"' ");	
		}
		if(!StringUtil.isNullValue(plateId)){
		    sql.append("and find_in_set('"+plateId+"',g.labels) ");	
		}
		sql.append("order by curPrice asc limit "+startIndex+","+rows);
		List<Map<String,Object>>  goodTemplateList =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        result.put("datas",goodTemplateList);
        result.put("state", ResultState.Z_正常.state);
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/colectProduct.do",isCheckSession=true)
	public Response colectProduct(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		int goodTemplateId = Integer.valueOf(request.getParameter("goodTemplateId"));
		Map<String,Object> result = new HashMap<String,Object>();
		Session session = HibernateSessionFactory.getSession();
	  	Transaction t = session.beginTransaction();
	  	String sql = "insert into Relations_Customer_GoodsTemplate (uid,goods_template_id) select ?,? from dual where not exists (select 1 from Relations_Customer_GoodsTemplate where uid=? and goods_template_id=?)";
		session.createSQLQuery(sql)
			.setString(0, user.customer.uid)
			.setInteger(1, goodTemplateId)
			.setString(2, user.customer.uid)
			.setInteger(3, goodTemplateId)
			.executeUpdate();
        t.commit();
        result.put("state", ResultState.Z_正常.state);
        result.put("goodTemplateId", goodTemplateId);
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/getIndexProduct.do")
	public Response getIndexProduct(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("开始查询商品详情 ");
		Map<String,Object> result = new HashMap<String,Object>();
		Session session = HibernateSessionFactory.getSession();
		AppSetting appSetting = (AppSetting)session.get(AppSetting.class,AppSetting.FIXED_ID);
		Map map = AppSettingToDetails(appSetting);
        result.put("datas",map);
        result.put("state", ResultState.Z_正常.state);
        log.info("开始查询商品详情 结果   "+map);
		return Response.stationary(result);
	}
	
	private Map AppSettingToDetails(AppSetting appSetting) {
 		String homePageBargainGoods = appSetting.getHomePageBargainGoods();
		String homePageProductTags = appSetting.getHomePageProductTags();
		Map<String,Object> map =new HashMap<String, Object>();
		//TODO
		List<Map<String,Object>> alist = null;
		if(appSetting.homePageActivitys!=null && appSetting.homePageActivitys.length>0){
			alist =  HibernateSessionFactory.getSession()
										.createSQLQuery("select title,picture,url from "+Activity.class.getSimpleName()+" where isDelete=0 and id in ("+StringUtils.join(appSetting.homePageActivitys, ',')+")")
										.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
										.list();
		}else{
			alist = new ArrayList<Map<String,Object>>();
		}
		map.put("activitys", alist);
		if (homePageBargainGoods!=null){
			String numbers = getNumbers(homePageBargainGoods);
			if(numbers!=""&&numbers!=null){
				List<Map<String,Object>> productList =getProductList(numbers);
				map.put("homePageBargainGoods", productList);
				map.put("guessYouLike", productList);
			}
		}else{
			map.put("homePageBargainGoods", "没有相关产品");
		}
		if(homePageProductTags!=null){
			List<Map<String,Object>> plates = new ArrayList<Map<String,Object>>();
            JSONObject parseObject = JSONObject.parseObject(homePageProductTags);
            productTagToMap(parseObject,plates,ProductTagType.送妈妈);
            productTagToMap(parseObject,plates,ProductTagType.教师节);
            productTagToMap(parseObject,plates,ProductTagType.探病);
            productTagToMap(parseObject,plates,ProductTagType.缅怀);
            productTagToMap(parseObject,plates,ProductTagType.情人节);
            map.put("plates", plates);
		} 
		return map;
	}
	
	private void productTagToMap(JSONObject parseObject,List<Map<String,Object>> plates,ProductTagType tagType) {
 		String tag2 =parseObject.getString(tagType.tag);
		if(tag2!=null){
			tag2 = getNumbers(tag2);
	        if(tag2!=""&&tag2!=null){
				Map<String,Object> plate = new HashMap<String, Object>();
				plate.put("plateId", tagType.tag);
				plate.put("plateName", tagType.name());
	        	List<Map<String,Object>> productList =getProductList(tag2);
	        	plate.put("goods", productList);
	        	plates.add(plate);
			}
		}
	}
	
	private List<Map<String,Object>> getProductList(String numbers) {
 		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.id goodTemplateId,g.oldPrice,g.curPrice,g.delivery,f.icon,g.name,g.monthSales ");
		sql.append("from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id where 1=1 ");
		sql.append(" and g.id in ("+numbers+")");
		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); 
		return query.list();
		
	}
	
	private String getNumbers(String content) {  
		   Pattern pattern = Pattern.compile("\\d+");  
	       Matcher matcher = pattern.matcher(content);  
	       String str ="";
	       while(matcher.find()){
	    	     str +=matcher.group()+",";
	       }
	       str = str.substring(0,str.length()-1);
	       return str;  
	} 
}
