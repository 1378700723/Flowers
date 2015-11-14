<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	$(document).ready(function(){	
		$("#example-2 tr").find('td:eq(1)').easyTooltip();
		$("#example-2 tr").find('td:eq(5)').easyTooltip();
	});
</script>
<style type="text/css">
table {
	table-layout: fixed;
	}
td {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
	}
.autocut:hover
     {
     overflow:visible;
     white-space:normal;
    word-wrap: break-word;
 }
 #easyTooltip{
	padding:5px 10px;
	border:1px solid #6699CC;
	background:#6699CC url(bg.gif) repeat-x;
	color:#fff;
	}
</style>
<link rel="stylesheet" href="<%=contextPath %>/assets/css/xenon-core.css">	
 <div class="page-container"><!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
 
		<div class="panel panel-default">
			 <div class="panel-heading">
				<h3 class="panel-title">花品查询</h3>
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
							<th>花名</th>
							<th>花语</th>
							<th>种类</th>
							<th>适用场景</th>
							<th>适用对象</th>
							<th>描述</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody class="middle-align">
						<c:forEach items="${page.dataList}" var="flower">
							<tr>
								<td>${flower.name}</td>
								<td title="${flower.flowerLanguage}">${flower.flowerLanguage}</td>
								<td>
								    <c:if test="${flower.ptype==1}">鲜花</c:if>
								    <c:if test="${flower.ptype==2}">种植</c:if>
								    <c:if test="${flower.ptype==3}">服务</c:if>
								</td>
								<td>
									<c:if test="${flower.scenario=='smm'}">送妈妈</c:if>
									<c:if test="${flower.scenario=='qrj'}">情人节</c:if>
									<c:if test="${flower.scenario=='jsj'}">教师节</c:if>
									<c:if test="${flower.scenario=='tb'}">探病</c:if>
									<c:if test="${flower.scenario=='mh'}">缅怀</c:if>
									<c:if test="${flower.scenario=='qt'}">其他</c:if>
								</td>
								<td> 
									<c:if test="${flower.suitable=='smm'}">送妈妈</c:if>
									<c:if test="${flower.suitable=='qrj'}">情人节</c:if>
									<c:if test="${flower.suitable=='jsj'}">教师节</c:if>
									<c:if test="${flower.suitable=='tb'}">探病</c:if>
									<c:if test="${flower.suitable=='mh'}">缅怀</c:if>
									<c:if test="${flower.suitable=='qt'}">其他</c:if>
								</td>
								<td title="${flower.des}">${flower.des}</td>
								<td>
									<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left" onclick="upFlower(${flower.id})">
										修改
									</a>
									<a href="#" class="btn btn-danger btn-sm btn-icon icon-left" onclick="delFlower(${flower.id})">
										删除
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			    <c:set value="${page}" var="dataSource" scope="session"/>
		        <c:set value="LoadFlowerList" var="paperFunc" scope="session"/>
				<c:import url="../common/pagerCtrl.jsp"/>
			</div>
		</div>
</div>
<script src="<%=contextPath %>/admin/flowers_sets/js/flowerList.js"></script>
<script src="<%=contextPath %>/admin/flowers_sets/js/easyTooltip.js"></script>
