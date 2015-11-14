<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<% String contextPath = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
     <meta charset="utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
 <title>修改密码</title>
</head>
<body>
  <div class="page-container">
     		<div class="sidebar-menu toggle-others fixed">
			<div class="sidebar-menu-inner">	
	            <%@ include file="/admin/common/import-header.jsp" %>
				<%@ include file="/admin/common/import-menu.jsp" %>
			</div>
		</div>
		<div class="main-content">
			 <div class="row">
				<div class="col-sm-12">
					
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">修改密码</h3>
							<div class="panel-options">
								<a href="#" data-toggle="panel">
									<span class="collapse-icon">&ndash;</span>
									<span class="expand-icon">+</span>
								</a>
								<a href="#" data-toggle="remove">
									&times;
								</a>
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
							
							<form  class="validate" method="post" role="form"     >
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>原密码：</B></label>
									<input type="password" class="form-control" id="original_pwd"  name="original_pwd" data-validate="required" data-message-required="必填字段"   placeholder="输入原密码"  >
								</div>
					
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>新密码：</B></label>
									<input type="password" class="form-control" id="new_pwd"   name="new_pwd" data-validate="required" data-message-required="必填字段"  placeholder="新密码"  >
									 
								</div>
								
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-3"><B>确认密码：</B></label>
									<input type="password" class="form-control"  id="confirm_pwd"  name="confirm_pwd" data-validate="required" data-message-required="必填字段"   placeholder="确认密码"  >
 								 
								</div>
								<div class="form-group" align="center">
									<button type="button" class="btn btn-success" onclick="editPwd()">提交</button>
									<button type="reset" class="btn btn-white">重置</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
			<%@ include file="/admin/common/import-footer.jsp" %>
		</div>
	</div>
</body>
</html>
 <script src="<%=contextPath %>/admin/administrator_sets/js/editPwd.js"></script>