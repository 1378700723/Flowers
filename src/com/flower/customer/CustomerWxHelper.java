package com.flower.customer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.guyou.util.ConfigUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.flower.customer.beans.ReceiveXmlEntity;
import com.flower.enums.OrderState;
import com.flower.tables.GoodsEntity;
import com.flower.tables.GoodsOrder;
import com.flower.tables.Transactionwx;
import com.flower.util.ReceiveXmlProcess;
/**
 * @author 王雪冬
 * 微信支付Helper
 */
public class CustomerWxHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(CustomerWxHelper.class);
 	
	private static String appid=null;
	private static String mch_id=null;
	private static String key=null;
	private static String unifiedorder=null;
	private static String orderquery=null;
	private static String notify_url="http://101.200.231.150:8080/flowers/customer/async_inform.do";
	static{
	     appid = ConfigUtil.getConfigParam("WX.APPID");
         mch_id = ConfigUtil.getConfigParam("WX.MCH_ID");
         key = ConfigUtil.getConfigParam("WX.KEY");
         unifiedorder = ConfigUtil.getConfigParam("WX.UNIFIEDORDER");
         orderquery = ConfigUtil.getConfigParam("WX.ORDERQUERY");
	}
	public static String sendPost(TreeMap<String, String> paramsMap,String key,String _url) throws URIException, UnsupportedEncodingException{
		    String paramsStr = toQueryString(paramsMap);
 		    paramsMap.put("sign",MD5(paramsStr+"&key="+key+"").toUpperCase());//校验码 
	        StringBuilder sb = new StringBuilder("<xml>").append("\n");
	        for (Entry<String,String> e : paramsMap.entrySet()) {
				sb.append("\t").append("<"+e.getKey()+">").append(e.getValue()).append("</"+e.getKey()+">").append("\n");
			}
	        sb.append("</xml>");
	    String a=null;
		try {
	        byte[] bb=sb.toString().getBytes("UTF-8");
	        //请求地址
			URL url = new URL(""+_url+"");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5 * 1000);//设置超时的时间
			conn.setDoInput(true);
			conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
			conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(bb.length));
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		    out.write(bb);//.writeBytes(sb.toString()); //写入请求的字符串
		    out.flush();
		    out.close();
			//请求返回的状态 
			if(conn.getResponseCode() ==200) {
				//请求返回的数据
				InputStream in=conn.getInputStream();
				 try {
					byte[] data1 = new byte[in.available()];
					in.read(data1);
					//转成字符串
					a = new String(data1);
					log.info(" 微信返回信息 ："+a);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				System.out.println("no++");
			}
			} catch (IOException e) {
				e.printStackTrace();
			}
		return a;
	}

	/**
	 * 对Map内所有value作utf8编码，拼接返回结果
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URIException
	 */
    public static String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException, URIException {
        StringBuffer queryString = new StringBuffer();
        for (Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append((String) pair.getValue() + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }
   
    /**
     * MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
     * @param md5
     * @return
     */
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
    /**
	 * 获取ip
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		if (request == null)
			return "";
		String ip = request.getHeader("X-Requested-For");
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
   
    public static  TreeMap<String, String>  unifiedorder(String body,String ip,String total_fee,String out_trade_no) throws URIException, UnsupportedEncodingException{
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
  	    TreeMap<String, String> resultMap = new TreeMap<String, String>();
  	    String nonce_str = System.currentTimeMillis()+"";
  	    //统一下单
    	  paramsMap.put("appid",appid);
  	      paramsMap.put("body",body);//商品描述
          paramsMap.put("mch_id",mch_id);
          paramsMap.put("nonce_str",nonce_str);//随机字符串
          paramsMap.put("notify_url",notify_url);//通知地址
          paramsMap.put("out_trade_no",out_trade_no);//商户订单号
          paramsMap.put("spbill_create_ip",ip);//终端IP
	      paramsMap.put("total_fee",total_fee);//总金额
	      paramsMap.put("trade_type","APP");//交易类型
	      String xml = sendPost(paramsMap,key,unifiedorder);
	      ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);  
	      if("FAIL".equals(xmlEntity.getReturn_code())) return resultMap;
	      //调起支付 返回app端参数
		    resultMap.put("appid",appid);
		    resultMap.put("partnerid",mch_id);//商户号
		    resultMap.put("prepayid",xmlEntity.getPrepay_id());//统一下单返回回话Id
		    resultMap.put("package","Sign=WXPay");//暂时固定值
		    resultMap.put("noncestr",nonce_str);//随机字符串，不长于32位
		    resultMap.put("timestamp",nonce_str);//时间戳
		    String paramsStr = toQueryString(resultMap);
		    resultMap.put("sign",MD5(paramsStr).toUpperCase());//签名
		   return resultMap;
    }
    /**
     * 客户端查询支付结果通知
     * @param request
     * @param response
     * @param out
     * @return
     * @throws Exception
     */
    @HttpListening(urlPattern="/customer/orderquery.do",isCheckSession=true)
  	public Response orderquery(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
    	String order_id = request.getParameter("order_id");
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
    	 String nonce_str = System.currentTimeMillis()+"";
    	 paramsMap.put("appid",appid);
         paramsMap.put("mch_id",mch_id);
         paramsMap.put("out_trade_no",order_id);//商户订单号
         paramsMap.put("nonce_str",nonce_str); 
 	     String xml = sendPost(paramsMap,key,orderquery);
	     return Response.stationary(xml);
    }
    /**
     * 微信支付 异步通知商户支付结果
     * @param request
     * @param response
     * @param out
     * @return
     * @throws Exception
     */
    @HttpListening(urlPattern="/customer/async_inform.do",isCheckSession=true)
	public Response async_inform(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
    	//请求返回的数据
    	InputStream in=request.getInputStream();
		byte[] data1 = new byte[in.available()];
		in.read(data1);
		//转成字符串
		String xml = new String(data1);
		log.info(" 微信返回信息 ："+xml);
		ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);  
	    if("FAIL".equals(xmlEntity.getReturn_code())) return Response.stationary("异步通知失败");
	    StringBuilder sb = new StringBuilder("<xml>").append("\n");
	    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
	    sb.append(" <return_msg><![CDATA[OK]]></return_msg>");
	    sb.append("</xml>");
	    //查询此数据是否被接受处理过
	    Session session = HibernateSessionFactory.getSession();
		Query query = session.createSQLQuery("select * from "+Transactionwx.class.getSimpleName()+" where openid=? and out_trade_no=?").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).setString(0, xmlEntity.getOpenid()).setString(1, xmlEntity.getOut_trade_no());
		List list = query.list();
 	    if(list!=null||list.size()>0){//被处理过的数据
 	    	 return Response.stationary(sb);
 	    }
 	    //没有处理过存储数据       支付成功修改订单号
 	   if("SUCCESS".equals(xmlEntity.getReturn_code())){
	 	    Transactionwx transaction = new Transactionwx();
	 	    transaction.openid = xmlEntity.getOpenid();
	 	    transaction.bank_type = xmlEntity.getBank_type();
	 	    transaction.cash_fee = xmlEntity.getCash_fee();
	 	    transaction.coupon_count = xmlEntity.getCoupon_count();
	 	    transaction.coupon_fee = xmlEntity.getCoupon_fee();
	 	    transaction.coupon_fee_$n = xmlEntity.getCoupon_fee_$n();
	 	    transaction.out_trade_no = xmlEntity.getOut_trade_no();
	 	    transaction.total_fee = xmlEntity.getTotal_fee();
	 	    transaction.transaction_id =xmlEntity.getTransaction_id();
	 	    Transaction t = session.beginTransaction();
	  	    session.save(transaction);
	  	    t.commit();
	 	    //修改订单状态
	  	    String id = xmlEntity.getOut_trade_no();
	  	    String _id = id.substring(0,1);
	  	    if("j".equals(_id)){//竞价单  修改我的花仓
	  	    	//得到竞价用户  和  商品实体id
	  	    	Map<String, Object> entityIdAndUid = CustomerGoodsAuctionBidHelper.getEntityIdAndUid(id);
	  	    	GoodsEntity goodEntity = new GoodsEntity();
	  	    	goodEntity.id = entityIdAndUid.get("goodsAcutionId").toString();
	  	    	goodEntity.uid = entityIdAndUid.get("uid").toString();
	  	    	t = session.beginTransaction();
	 	  	    session.update(goodEntity);
	 	  	    t.commit();
	  	    }
	  	    if("o".equals(_id)){//修改订单单
	  	    	GoodsOrder order = new GoodsOrder();
	 	    	order.orderid = id;
	 	    	order.state = OrderState.getEnum(2);
	 	    	t = session.beginTransaction();
	 	  	    session.update(order);
	 	  	    t.commit();
	  	    }
 	    	
 	    }
	    return Response.stationary(sb);
    }
    

	public static void main(String[] args) throws Exception {
	    TreeMap<String, String> paramsMap = new TreeMap<String, String>();
	    TreeMap<String, String> resultMap = new TreeMap<String, String>();
	    String nonce_str = System.currentTimeMillis()+"";
	    //统一下单
	    // String app = ConfigUtil.getConfigParam("WX.APPID");
 	    paramsMap.put("appid","wx210fd7c732469f74");
	    paramsMap.put("body","app测试第二次");//商品描述
        paramsMap.put("mch_id","1259144401");
        paramsMap.put("nonce_str",nonce_str);//随机字符串
        paramsMap.put("notify_url","http://101.200.231.150:8080/flowers/customer/async_inform.do");//通知地址
        paramsMap.put("out_trade_no",nonce_str);//商户订单号
        paramsMap.put("spbill_create_ip","10.10.11.240");//终端IP
        paramsMap.put("total_fee","1");//总金额
        paramsMap.put("trade_type","APP");//交易类型
        String xml = sendPost(paramsMap,"92529b042a053742dd38a899802544c6","https://api.mch.weixin.qq.com/pay/unifiedorder");
	        ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);  
        if("FAIL".equals(xmlEntity.getReturn_code())) return ;
        System.out.println("返回结果 "+xmlEntity.getReturn_code()+" prepay_id  "+xmlEntity.getPrepay_id());
        //调起支付 返回app端参数
        resultMap.put("appid","wx210fd7c732469f74");
        resultMap.put("partnerid","1259144401");//商户号
        resultMap.put("prepayid",xmlEntity.getPrepay_id());//统一下单返回回话Id
        resultMap.put("package","Sign=WXPay");//暂时固定值
        resultMap.put("noncestr",nonce_str);//随机字符串，不长于32位
        resultMap.put("timestamp",nonce_str);//时间戳
        String paramsStr = toQueryString(resultMap);
        System.out.println(paramsStr); 
	    paramsMap.put("sign",MD5(paramsStr).toUpperCase());//签名
     }
}
