<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<% String contextPath = request.getContextPath(); %>
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
	 <nav class="navbar navbar-default" role="navigation">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					 
					<a class="navbar-brand" href="#">用户详情</a>
				</div>
				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li class="active">
							<a href="#">Link</a>
						</li>
						<li class="disabled">
							<a href="#">Link</a>
						</li>
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown">查询方式 <b class="caret"></b></a>
							<ul class="dropdown-menu">
								<li>
									<a href="#">手机号</a>
								</li>
								<li>
									<a href="#">昵称</a>
								</li>
								<li>
									<a href="#">性别</a>
								</li>
								<li class="divider"></li>
								<li>
									<a href="#">全部</a>
								</li>
							</ul>
						</li>
					</ul>
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input type="text" class="form-control" placeholder="手机号查询" id="Keyword" onkeyup="search()">
						</div>
						<button type="button" class="btn btn-white" onclick="search()">搜索</button>
					</form>
				</div>
				<!-- /.navbar-collapse -->
		 </nav>
 	<div class="panel panel-default">
		<div class="panel-heading"></div>
		<div class="panel-body" id="customerList">
		      <jsp:include page="/admin/customer_sets/customerList.jsp" ></jsp:include>
		</div>
	 </div>
	 <%@ include file="/admin/common/import-footer.jsp" %>
</div>
</body>
</html>
 <script>
 function search(){
	  var Keyword =$("#Keyword").val();
 	  $.post("<%=request.getContextPath()%>/admin/customerHelper/getCustomerList.do",{
 			    "keyword":Keyword,
 				},function (data){ 
 					 $("#customerList").html(data);
 				});
 }
 </script>