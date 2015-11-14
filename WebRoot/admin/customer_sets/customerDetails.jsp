<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
     <meta charset="utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
 <title>用户管理</title>
</head>
<body>
<div class="page-container">
   	<div class="sidebar-menu toggle-others fixed">
	<div class="sidebar-menu-inner">	
        <%@ include file="/admin/common/import-header.jsp" %>
		<%@ include file="/admin/common/import-menu.jsp" %>
	</div>
	 </div>
 	<div class="panel panel-default">
		<div class="panel-heading"> <h4>用户详细信息</h4></div>
		 
	 </div>
     <section class="profile-env">
				<div class="row">
					<div class="col-sm-3">
						<!-- User Info Sidebar -->
						<div class="user-info-sidebar">
							
							<a href="#" class="user-img">
								<img src="<%=contextPath%>/admin/xenon/assets/images/user-4.png" alt="user-img" class="img-cirlce img-responsive img-thumbnail" />
							</a>
							
							<a href="#" class="user-name">
								${customer.nickname}
								<span class="user-status is-online"></span>
							</a>
							<span class="user-title">
								${customer.signature}
							</span>
							<hr />
							
							<ul class="list-unstyled user-info-list">
							<li>
								<i class="fa-home"></i>
								 住址住址
							</li>
							<li>
								<i class="fa-briefcase"></i>
								 办公
							</li>
							<li>
								<i class="fa-graduation-cap"></i>
								大学
							</li>
						</ul>	
						<hr />
						<ul class="list-unstyled user-friends-count">
								<li>
									<span><c:if test="${empty sumMony}">0</c:if>${sumMony}</span>
									总消费
								</li>
								<li>
									<span><c:if test="${empty customer.flowerSeed}">0</c:if>${customer.flowerSeed}</span>
								           花籽数
								</li>
						</ul>
						</div>
					</div>
					
					<div class="col-sm-9">
						
						<!-- User Post form and Timeline -->
						<form method="post" action="" class="profile-post-form">
							<textarea class="form-control input-unstyled input-lg autogrow" placeholder="给用户发送通知?"></textarea>
							<i class="el-edit block-icon"></i>
							
							<ul class="list-unstyled list-inline form-action-buttons">
								<li>
									<button type="button" class="btn btn-unstyled">
										<i class="el-camera"></i>
									</button>
								</li>
								<li>
									<button type="button" class="btn btn-unstyled">
										<i class="el-attach"></i>
									</button>
								</li>
								<li>
									<button type="button" class="btn btn-unstyled">
										<i class="el-mic"></i>
									</button>
								</li>
								<li>
									<button type="button" class="btn btn-unstyled">
										<i class="el-music"></i>
									</button>
								</li>
							</ul>
							
							<button type="button" class="btn btn-single btn-xs btn-success post-story-button">发送</button>
						</form>

						<!-- User timeline stories -->
						<section class="user-timeline-stories">
						    		<!-- Timeline Story Type: Status -->
							<article class="timeline-story">
								<i class="fa-paper-plane-empty block-icon"></i>
								<!-- User info -->
								<header>
										<a href="#">个人花仓</a>.					 
								</header>
								<div class="story-content">
						              <table class="table table-condensed">
										<thead>
											<tr>
												<th>名称</th>
												<th>金额</th>
												<th>状态</th>
												<th>时间</th>
											</tr>
										</thead>
										
										<tbody>		
											<c:forEach items="${goodsEntityList}" var="goodEntity">
										     <tr>
										         <td>${goodEntity.name}</td>
										         <td>${goodEntity.actualpay}</td>
										         <td>
										            <c:if test="${goodEntity.state==1}">普通状态</c:if>
										            <c:if test="${goodEntity.state==2}">配送状态</c:if>
										            <c:if test="${goodEntity.state==3}">提货完成</c:if>
										         </td>
										         <td>${goodEntity.gainTime}</td>
										     </tr>
										    </c:forEach>
										</tbody>
									</table>
								</div>
							</article>
							<!-- Timeline Story Type: Status -->
							<article class="timeline-story">
								<i class="fa-paper-plane-empty block-icon"></i>
								<!-- User info -->
								<header>
										<a href="#">交易记录</a>.					 
								</header>
								<div class="story-content">
						              <table class="table table-condensed">
										<thead>
											<tr>
												<th>交易商品</th>
												<th>交易金额</th>
												<th>交易状态</th>
												<th>交易时间</th>
											</tr>
										</thead>
										
										<tbody>
											<c:forEach items="${goodsOrderList}" var="goodsOrder">
										     <tr>
										         <td>
										             <c:if test="${empty goodsOrder.name}">商品已删除</c:if>
 										             ${goodsOrder.name}
										         </td>
										         <td>${goodsOrder.payMoney}</td>
										         <td>
										            <c:if test="${goodsOrder.state==1}">未支付</c:if>
										            <c:if test="${goodsOrder.state==2}">已支付未送货</c:if>
										            <c:if test="${goodsOrder.state==3}">已支付需要送货</c:if>
										            <c:if test="${goodsOrder.state==4}">已支付正在送货</c:if>
										            <c:if test="${goodsOrder.state==5}">已支付完成收货</c:if>
										         </td>
										         <td>${goodsOrder.payTime}</td>
										     </tr>
										    </c:forEach>
										</tbody>
									</table>
								</div>
							</article>
						</section>
					</div>
				</div>
			</section>
	 <%@ include file="/admin/common/import-footer.jsp" %>
</div>
</body>
</html>