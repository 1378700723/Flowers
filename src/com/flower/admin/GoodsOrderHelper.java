package com.flower.admin;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;

import com.flower.admin.dao.GoodsOrderDao;
/**
 * @author 王雪冬
 *
 */
public class GoodsOrderHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(GoodsOrderHelper.class);
	
	@HttpListening(urlPattern="/admin/goodsOrder/getGoodsOrderList.do",isCheckSession=true)
	public Response getGoodsOrderList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		List goodsOrderist = GoodsOrderDao.getGoodsOrderList();
		request.setAttribute("goodsOrderist", goodsOrderist);
		return Response.forward("/admin/goodsOrder_sets/goodsOrderIndex.jsp");
	}
}
