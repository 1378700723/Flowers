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
		
		<script type="text/javascript">
			jQuery(document).load("http://127.0.0.1:8080/flowers/test.txt",function(responseTxt,statusTxt,xhr){
		      if(statusTxt=="success")
		        //alert("外部内容加载成功！ " + responseTxt);
		      if(statusTxt=="error")
		        alert("Error: "+xhr.status+": "+xhr.statusText);
		    });
		
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
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">权限管理</h3>
				<div class="panel-options">
					<button id="add-permission" class="btn btn-danger btn-sm btn-icon">添加</button>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered table-striped" id="example-2">
					<thead>
						<tr>
							<th width="20%">权限名称</th>
							<th>权限模块</th>
							<th width="15%">操作</th>
						</tr>
					</thead>
					<tbody class="middle-align">
						<tr>
							<td>Randy S. Smith</td>
							<td>
								aaaa <br>
								bbbbbb<br>
							</td>
							<td>
								<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left">
									修改
								</a>
								
								<a href="#" class="btn btn-danger btn-sm btn-icon icon-left">
									删除
								</a>
							</td>
						</tr>
						<tr>
							<td>Ellen C. Jones</td>
							<td>7.2</td>
							<td>
								<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left">
									修改
								</a>
								
								<a href="#" class="btn btn-danger btn-sm btn-icon icon-left">
									删除
								</a>
							</td>
						</tr>
						
						<tr>
							<td>Carl D. Kaya</td>
							<td>9.5</td>
							<td>
								<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left">
									修改
								</a>
								
								<a href="#" class="btn btn-danger btn-sm btn-icon icon-left">
									删除
								</a>
							</td>
						</tr>
						
						<tr>
							<td>Jennifer J. Jefferson</td>
							<td>10</td>
							<td>
								<a href="#" class="btn btn-secondary btn-sm btn-icon icon-left">
									修改
								</a>
								
								<a href="#" class="btn btn-danger btn-sm btn-icon icon-left">
									删除
								</a>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="modal-6">
		<div class="modal-dialog">
			<div class="modal-content">
				
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Modal Content is Responsive</h4>
				</div>
				
				<div class="modal-body">
				
					<div class="row">
						<div class="col-md-6">
							
							<div class="form-group">
								<label for="field-1" class="control-label">Name</label>
								
								<input type="text" class="form-control" id="field-1" placeholder="John">
							</div>	
							
						</div>
						
						<div class="col-md-6">
							
							<div class="form-group">
								<label for="field-2" class="control-label">Surname</label>
								
								<input type="text" class="form-control" id="field-2" placeholder="Doe">
							</div>	
						
						</div>
					</div>
				
					<div class="row">
						<div class="col-md-12">
							
							<div class="form-group">
								<label for="field-3" class="control-label">Address</label>
								
								<input type="text" class="form-control" id="field-3" placeholder="Address">
							</div>	
							
						</div>
					</div>
				
					<div class="row">
						<div class="col-md-4">
							
							<div class="form-group">
								<label for="field-4" class="control-label">City</label>
								
								<input type="text" class="form-control" id="field-4" placeholder="Boston">
							</div>	
							
						</div>
						
						<div class="col-md-4">
							
							<div class="form-group">
								<label for="field-5" class="control-label">Country</label>
								
								<input type="text" class="form-control" id="field-5" placeholder="United States">
							</div>	
						
						</div>
						
						<div class="col-md-4">
							
							<div class="form-group">
								<label for="field-6" class="control-label">Zip</label>
								
								<input type="text" class="form-control" id="field-6" placeholder="123456">
							</div>	
						
						</div>
					</div>
				
					<div class="row">
						<div class="col-md-12">
							
							<div class="form-group no-margin">
								<label for="field-7" class="control-label">Personal Info</label>
								
								<textarea class="form-control autogrow" id="field-7" placeholder="Write something about yourself"></textarea>
							</div>	
							
						</div>
					</div>
					
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