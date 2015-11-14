<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
 <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
		<div  id="getFlowerListInfo">
			   <jsp:include page="/admin/flowers_sets/flowersList.jsp" ></jsp:include>
		</div>
	  
	</div>
	<!-- -删除记录弹出提示框 -->
	<div class="modal fade" id="showModel">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">提示</h4>
				</div>

				<div class="modal-body">
			                       你确认删除此条记录么？
				</div>
				
				<div class="modal-footer">
					<button  id="cloModel" type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-info" onclick="confirm()">确定</button>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>
 <script>
 function LoadFlowerList(method,page)
 { 
     search(method,page)
 }
 function search(method,page){
 	  $.post("<%=request.getContextPath()%>/admin/flowersHelper/getFlowersList.do",{
 			    "method":method,
 			    "page":page,
 				"flag":1
 				},function (data){ 
  					 $("#getFlowerListInfo").html(data);
 				});
 }
 </script>
 <script src="<%=contextPath %>/admin/flowers_sets/js/flowerList.js"></script>