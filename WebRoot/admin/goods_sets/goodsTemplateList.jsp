<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <div class="page-container"><!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
 
		<div class="panel panel-default">
			 <div class="panel-heading">
				<h3 class="panel-title">商品模板查询</h3>
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
 				<table cellspacing="0" class="table table-small-font table-bordered table-striped" id="example-2">
					<thead>
						<tr>
						    <th>商品名称</th>
							<th>所在城市</th>
							<th>运费</th>
							<th>是否特价</th>
							<th>当前价</th>
							<th>原价</th>
							<th>所属花品</th>
							<th width="15%">操作</th>
						</tr>
					</thead>
					<tbody class="middle-align">
						<c:forEach items="${goodsTemolateList}" var="goodsTemplate">
							<tr>
							    <td>${goodsTemplate.name}</td>	
								<td> 
								 <c:if test="${goodsTemplate.city==010}">北京</c:if>
								 <c:if test="${goodsTemplate.city==021}">上海</c:if>
								 <c:if test="${goodsTemplate.city==022}">天津</c:if>
								 <c:if test="${goodsTemplate.city==020}">广州</c:if>
								</td>
								<td>
								<c:if test="${empty goodsTemplate.delivery}">免运费</c:if>
							        ${goodsTemplate.delivery}
								</td>
								<td>
								    <c:if test="${goodsTemplate.bargain==true}">是</c:if>
							        <c:if test="${goodsTemplate.bargain==false}">否</c:if>
								</td>
								<td>${goodsTemplate.curPrice}</td>
								<td>${goodsTemplate.oldPrice}</td>
								<td>${goodsTemplate.flowerName}</td>				 
								<td>
									<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left" onclick="upGoodsTemplate(${goodsTemplate.id})">
										修改
									</a>
									<a href="#" class="btn btn-danger btn-sm btn-icon icon-left" onclick="delGoodsTemplate(${goodsTemplate.id})">
										删除
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
</div>
 <script src="<%=contextPath %>/admin/goods_sets/js/goodsTemplateList.js"></script>
