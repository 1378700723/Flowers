<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath_1 = request.getContextPath(); %>
<header class="logo-env">
	<!-- logo -->
	<div class="logo">
		<a href="<%=contextPath_1 %>/admin/index/index.jsp" class="logo-expanded">
			<img src="<%=contextPath_1 %>/admin/images/logo.png" width="80" alt="" />
		</a>
		
		<a href="#" class="logo-collapsed">
			<img src="<%=contextPath_1 %>/assets/images/logo-collapsed@2x.png" width="40" alt="" />
		</a>
	</div>
	<!-- This will open the popup with user profile settings, you can use for any purpose, just be creative -->
	<div class="settings-icon">
		<a href="#" data-toggle="settings-pane" data-animate="true">
			<i class="linecons-cog"></i>
		</a>
	</div>
</header>