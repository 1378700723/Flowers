<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" href="<%=contextPath %>/assets/css/xenon-core.css">	
<table cellspacing="0" class="table table-small-font table-bordered table-striped" id="example-2">
	<thead>
		<tr>
		    <th>用户编号</th>
			<th>手机号</th>
			<th>邮箱</th>
			<th>昵称</th>
			<th>性别</th>
			<th>花籽数</th>			
			<th>操作</th>
		</tr>
	</thead>
	<tbody class="middle-align">
	     <c:forEach items="${customerList}" var="customer">
	        <tr>
				<td>${customer.uid}</td>
				<td>${customer.phone}</td>
				<td>${customer.email}</td>
				<td>${customer.nickname}</td>
				<td>
					 <c:if test="${customer.sex==0}">保密</c:if>
					 <c:if test="${customer.sex==1}">男</c:if>
					 <c:if test="${customer.sex==2}">女</c:if>
				</td>
				<td>${customer.flowerSeed}</td>
				<td>
					<a href="<%=contextPath%>/admin/customerHelper/getCustomerDetails.do?uid=${customer.uid}" class="btn btn-secondary btn-sm btn-icon icon-left"  >查看详情</a>
				</td>
			</tr>
	     </c:forEach>
	</tbody>
</table>