<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<%@page import="com.flower.util.UUIDUtil"%>
<%@page import="java.util.Collection"%>
<%@page import="org.guyou.event.Event"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="org.guyou.web.server.job.JobWorker"%>
<%
response.setHeader("zhusj", "haimen");
out.println(request.getSession(false));
out.println(request.getSession(false).getId());

//获取文件部件part
		Collection<Part> parts= request.getParts();
		for (Part part : parts) {
			//获取请求信息
			String name=part.getHeader("content-disposition");
			//得到上传文件保存的路径
			String root=request.getServletContext().getRealPath("/upload");
			//得到上传文件的后缀名
			String str=name.substring(name.lastIndexOf("."),name.length()-1);
			//生成一个随机的文件名
			String fileName=root+"\\"+UUIDUtil.图片名称.id()+str;
			//保存文件
			part.write(fileName);
		}
%>