package com.flower.customer;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.flower.customer.beans.CustomerUser;
import com.flower.enums.GoodsState;
import com.flower.enums.ResultState;
import com.flower.tables.Customer;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsEntity;
import com.flower.tables.GoodsTemplate;
/**
 * @author 王雪冬
 * 商品实体Helper
 */
public class CustomerGoodsEntityHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(CustomerGoodsEntityHelper.class);

	@HttpListening(urlPattern="/customer/getGoodEntityList.do",isCheckSession=true)
	public Response getGoodEntityList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("开始查询花仓商品   ");
		CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
	    Customer customer=user.customer;
		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select gs.*,gt.name,f.icon  from "+GoodsEntity.class.getSimpleName()+" gs  ");
		sql.append("left join "+GoodsTemplate.class.getSimpleName()+" gt on gs.goods_template_id=gt.id  ");
		sql.append("left join "+FlowerTemplate.class.getSimpleName()+" f on gt.flowerid=f.id  ");
		sql.append("where gs.state=1 and  gs.uid =？");
		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setString(0, customer.uid);
		List  goodEntityList =query.list();
        result.put("data",goodEntityList);
        result.put("state", ResultState.Z_正常.state);
		return Response.stationary(result);
	}
	//生成商品实体
	public static void savaGoodEntity(String goodId,String uid,float payMoney){
		log.info("开始生成商品实体");
		GoodsEntity good =new GoodsEntity();
		GoodsTemplate goodTemplat =new GoodsTemplate();
		good.actualPay=(int)payMoney;
		goodTemplat.id = Integer.parseInt(goodId) ;
		good.goodsTemplate = goodTemplat;
		good.uid = uid;
	    SimpleDateFormat date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = date.format(new Date()).toString();
		good.gainTime =  nowDate; 
		good.state =GoodsState.getEnum(1);
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.save(good);
		t.commit();
	}
}
