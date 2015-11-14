<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <div class="page-container"><!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
 
		<div class="panel panel-default">
			 <div class="panel-heading">
				<h3 class="panel-title">订单查询</h3>
			</div>
		   <div class="row" style="display:none" id ="showAlter">
				<div class="col-md-12">
					<div class="alert alert-default">
						<button type="button" class="close" data-dismiss="alert">
							<span aria-hidden="true">&times;</span>
							<span class="sr-only">Close</span>
						</button>
						<div id="alText">	
						
						</div>
					</div>
				</div>
		    </div>
			<div class="panel-body">
 				<table class="table table-striped table-bordered" id="example-3">
					<thead>
						<tr class="replace-inputs">
							<th>订单</th>
							<th>商品</th>
							<th>状态</th>
							<th>用户</th>
							<th>购买数量</th>
							<th>支付金额</th>
							<th>购买时间</th>
						</tr>
					</thead>
					<tbody class="middle-align">
						<c:forEach items="${goodsOrderist}" var="goodsOrder">
							<tr>
								<td>${goodsOrder.orderid}</td>
								<td>${goodsOrder.goodName}</td>
								<td>
								    <c:if test="${goodsOrder.state==1}">未支付</c:if>
								    <c:if test="${goodsOrder.state==2}">已支付未送货</c:if>
								    <c:if test="${goodsOrder.state==3}">已支付需要送货</c:if>
								    <c:if test="${goodsOrder.state==4}">已支付正在送货</c:if>
								    <c:if test="${goodsOrder.state==5}">已支付完成收货</c:if>
								</td>
								<td>${goodsOrder.nickName}</td>
								<td>${goodsOrder.goodsCount}</td>
								<td>${goodsOrder.payMoney}</td>
								<td>${goodsOrder.payTime}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
</div>
	<script type="text/javascript">
	jQuery(document).ready(function($)
	{
		$("#example-3").dataTable({
			aLengthMenu: [
						   [10, 25, 50, 100, -1], [10, 25, 50, 100, "全部"]
						]
					}).yadcf([
			{column_number : 0,filter_type: 'text'},
			{column_number : 1, filter_type: 'text'},
			{column_number : 2},
			{column_number : 3, filter_type: 'text'},
			{column_number : 4,filter_type: 'range_number'},
			{column_number : 5,filter_type: 'range_number'},
			{column_number : 6,filter_type: 'text'},
		]);
	});
    </script>