<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath();%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
	 <link rel="stylesheet" href="http://fonts.useso.com/css?family=Arimo:400,700,400italic">
	<title>管理员登陆</title>
	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
		<script src="<%=contextPath %>/assets/js/html5shiv.min.js"></script>
		<script src="<%=contextPath %>/assets/js/respond.min.js"></script>
	<![endif]-->
</head>
<body class="page-body login-page">
	<div class="login-container">
		<div class="row">
			<div class="col-sm-6">
				<script type="text/javascript">
					jQuery(document).ready(function($)
					{
						// Reveal Login form
						setTimeout(function(){ $(".fade-in-effect").addClass('in'); }, 1);
						
						// Validation and Ajax action
						$("form#login").validate({
							rules: {
								username: {
									required: true
								},
								
								passwd: {
									required: true
								}
							},
							
							messages: {
								username: {
									required: '请输入用户名'
								},
								
								passwd: {
									required: '请输入密码'
								}
							},
							
							// Form Processing via AJAX
							submitHandler: function(form)
							{
								show_loading_bar(70); // Fill progress bar to 70% (just a given value)
								var opts = {
									"closeButton": true,
									"debug": false,
									"positionClass": "toast-top-full-width",
									"onclick": null,
									"showDuration": "300",
									"hideDuration": "1000",
									"timeOut": "5000",
									"extendedTimeOut": "1000",
									"showEasing": "swing",
									"hideEasing": "linear",
									"showMethod": "fadeIn",
									"hideMethod": "fadeOut"
								};
									
								$.ajax({
									url: '<%=contextPath %>/admin/login.do',
									method: 'POST',
									dataType: 'json',
									data: {
										do_login: true,
										username: $(form).find('#username').val(),
										passwd: $(form).find('#passwd').val(),
									},
									success: function(resp)
									{
										show_loading_bar({
											delay: .5,
											pct: 100,
											finish: function(){
												var _state = resp.state;
												// Redirect after successful login page (when progress bar reaches 100%)
												if(_state==1)
												{
													window.location.href = '<%=contextPath %>/admin/index/index.jsp';
												}
												else if(_state==-1){
													toastr.error("用户名不存在，请重新输入用户名 ！", "登陆错误：", opts);
													$passwd.select();
												}
												else if(_state==-5){
													toastr.error("密码错误 ！", "登陆错误：", opts);
													$passwd.select();
												}
											}
										});
									}
								});
							}
						});
						// Set Form focus
						$("form#login .form-group:has(.form-control):first .form-control").focus();
					});
				</script>
				
				<!-- Errors container -->
				<div class="errors-container">
				
									
				</div>
				
				<!-- Add class "fade-in-effect" for login form effect -->
				<form method="post" role="form" id="login" class="login-form fade-in-effect">
					
					<div class="login-header">
						<a href="#" class="logo">
							<img src="<%=contextPath %>/admin/images/logo.png" alt="" width="80" />
							<span>登陆</span>
						</a>
						<!-- 
						<p>Dear user, log in to access the admin area!</p>
						 -->
					</div>
	 				
					<div class="form-group">
						<label class="control-label" for="username">用户名</label>
						<input type="text" class="form-control input-dark" name="username" id="username" autocomplete="off" />
					</div>
					
					<div class="form-group">
						<label class="control-label" for="passwd">密码</label>
						<input type="password" class="form-control input-dark" name="passwd" id="passwd" autocomplete="off" />
					</div>
					
					<div class="form-group">
						<button type="submit" class="btn btn-dark  btn-block text-left">
							<i class="fa-lock"></i>
							登陆
						</button>
					</div>
					 
				</form>
			</div>
		</div>
	</div>
</body>
</html>
