<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <div class="page-container"><!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
 
		<div class="panel panel-default">
			 <div class="panel-heading">
				<h3 class="panel-title">活动查询</h3>
				<div class="panel-options">
				       <button class="btn btn-danger btn-sm btn-icon" id="toEditActivity">活动录入</button>
				</div>
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
						    <th>活动名称</th>
							<th>活动时间</th>
							<th>活动地址</th>
							<th width="15%">操作</th>
						</tr>
					</thead>
					<tbody class="middle-align">
						<c:forEach items="${activityList}" var="activity">
							<tr>
							    <td>${activity.title}</td>	
								<td> ${activity.publishTime} </td>
								<td>${activity.url}</td>						 
								<td>
									<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left" onclick="upActivity(${activity.id})">
										修改
									</a>
									<a href="#" class="btn btn-danger btn-sm btn-icon icon-left" onclick="delActivity(${activity.id})">
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
<script src="<%=contextPath %>/admin/activity/js/activityList.js"></script>
