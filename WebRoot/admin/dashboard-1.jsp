<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8"> 
 	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
</head>
<body class="page-body">
	<div class="page-container"><!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<div class="sidebar-menu toggle-others fixed">
			<div class="sidebar-menu-inner">	
                <%@ include file="/admin/common/import-header.jsp" %>
				<%@ include file="/admin/common/import-menu.jsp" %>
			</div>
		</div>
		<div class="main-content">
			 <h3>欢迎来到花花会后台管理页面</h3>
			<%@ include file="/admin/common/import-footer.jsp" %> 
		</div>
	</div>
</body>
</html>