<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath(); %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
	
	<!-- Imported styles and scripts on this page -->
	<link rel="stylesheet" href="<%=contextPath %>/assets/js/datatables/dataTables.bootstrap.css">
	<script src="<%=contextPath %>/assets/js/datatables/js/jquery.dataTables.min.js"></script>
	<script src="<%=contextPath %>/assets/js/datatables/dataTables.bootstrap.js"></script>
	<script src="<%=contextPath %>/assets/js/datatables/yadcf/jquery.dataTables.yadcf.js"></script>
	<script src="<%=contextPath %>/assets/js/datatables/tabletools/dataTables.tableTools.min.js"></script>
</head>
<body class="page-body">
	
	<div class="page-container">
		<div class="sidebar-menu toggle-others fixed">
			<div class="sidebar-menu-inner">	
	            <%@ include file="/admin/common/import-header.jsp" %>
				<%@ include file="/admin/common/import-menu.jsp" %>
			</div>
		</div>

		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">管理员管理</h3>
				<div class="panel-options">
					<button id="add-permission" class="btn btn-danger btn-sm btn-icon">添加</button>
				</div>
			</div>
			<div class="panel-body">
			  内容
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-6">
		<div class="modal-dialog">
			<div class="modal-content">
				
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">管理员管理</h4>
				</div>
				
				<div class="modal-body">
								 
				</div>
				
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-info">Save changes</button>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>
<script type="text/javascript">
jQuery(document).ready(function($)
{
	$("#example-2").dataTable({
		dom: "t" + "<'row'<'col-xs-6'i><'col-xs-6'p>>",
		aoColumns: [
			null,
			{bSortable: false},
			{bSortable: false}
		],
	});
	$("#add-permission").click(function(){
		jQuery('#modal-6').modal('show', {backdrop: 'static'});
	});
});
</script>