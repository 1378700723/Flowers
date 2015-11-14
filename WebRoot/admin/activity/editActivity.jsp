<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<% String contextPath = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
     <meta charset="utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
	<script src="<%=contextPath %>/admin/activity/js/ajaxfileupload.js"></script>
 <title>活动设置</title>
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
							<h3 class="panel-title">录入活动</h3>
							<div class="panel-options">
								<button id="avtivityList" class="btn btn-danger btn-sm btn-icon">活动列表</button>
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
							<form  role="form" >
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>标题：</B></label>
									<input type="hidden" id="id"  name="id" value="${activity.id}">
									<input type="text" class="form-control" id="title_name"  name="title_name" value="${activity.title}"  placeholder="例如：情人节活动"  >
								</div>
					
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>活动地址：</B></label>
									<input type="text" class="form-control" id="activity_url"   name="activity_url" value="${activity.url}"  placeholder="例如：127.10.10:8080/flowers/html.html">
								</div>
								
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-3"><B>图片：</B></label>
 								    <input type="file" class="form-control"  id="picture"  name="picture" onchange="uploadImage()">
 								    <input type="hidden" class="form-control"  id="picture_url"  name="picture_url" value="${activity.picture}">
 								</div>
								<div class="form-group" align="center">
									<button type="button" class="btn btn-success" id="update_activity">提交</button>
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
<script src="<%=contextPath %>/admin/activity/js/editactivity.js"></script>