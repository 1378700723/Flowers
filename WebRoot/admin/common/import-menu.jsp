<%@page import="com.flower.admin.beans.AdminMenu.AdminModule"%>
<%@page import="com.flower.Application"%>
<%@page import="com.flower.admin.beans.AdminMenu"%>
<%@page import="com.flower.tables.Permission"%>
<%@page import="com.flower.tables.Administrator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
	<ul id="main-menu" class="main-menu">
<% 
	String contextPath_import_menu = request.getContextPath();
	String requestURI = request.getRequestURI();
	String modulePath = requestURI.replace(contextPath_import_menu, "");
	String opend_menuid = Application.getMenuidByPath(modulePath);
	Administrator admin = (Administrator)request.getSession().getAttribute("ADMIN_USER_DATA");
	for(AdminMenu menu : admin.menus()){
		boolean isOpen = menu.menuID.equals(opend_menuid);
%>
		<li<%=isOpen?" class=\"active opened active\"":"" %>>>
			<a href="">
				<i class="linecons-desktop"></i>
				<span class="title"><%=menu.menuName %></span>
			</a>
			<ul>
<%
			for(AdminModule am : menu.modules.values()){
				boolean isActive = isOpen && am.modulePath.equals(modulePath);
%>
				<li<%=isActive?" class=\"active\"":"" %>>
					<a href="<%=contextPath_import_menu+am.modulePath%>">
						<span class="title"><%=am.moduleName%></span>
					</a>
				</li>
<%
			}
%>
			</ul>
		</li>
<%		
	}
%>
	</ul>