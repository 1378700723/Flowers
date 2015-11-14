function editPwd(){
	 var original_pwd =$("#original_pwd").val();
	     new_pwd =$("#new_pwd").val();
	     confirm_pwd =$("#confirm_pwd").val();
    if(original_pwd==""){
    	$("#showAlter").show();
    	$("#alText").html("<strong><span style='color:red'>请输入原密码 ！<span></strong>")
    	 
    	return ;
    }
    if(new_pwd==""){
    	$("#showAlter").show();
    	$("#alText").html("<strong><span style='color:red'>请输新密码 ！<span></strong>")
    	 
    	return ;
    }
    if(confirm_pwd==""){
    	$("#showAlter").show();
    	$("#alText").html("<strong><span style='color:red'>请输入确认密码 ！<span></strong>")
     
    	return ;
    }
    if(new_pwd !=confirm_pwd){
    	$("#showAlter").show();
    	$("#alText").html("<strong><span style='color:red'>两次密码输入不一致 ！<span></strong>")
    	 
    	return ;
    }
	 $.ajax({
			url: '../editPwd.do',
			method: 'POST',
			dataType: 'json',
			data: {
				original_pwd:original_pwd,
				new_pwd:new_pwd,
			},
			success: function(resp)
			{
				 var _state=resp.state;
				 if(_state ==0){
					    $("#showAlter").show();
				    	$("#alText").html("<strong><span style='color:red'>原密码输入错误 ！<span></strong>")
				 }
				 if(_state ==1){
					    $("#showAlter").show();
				    	$("#alText").html("<strong>修改成功 ！</strong>")
				    	$("#showAlter").fadeOut(2000);
				    	setTimeout(function(){
				    		window.location.href="../admin-login.jsp"
                         },1000);
				 }
			}
		});
}