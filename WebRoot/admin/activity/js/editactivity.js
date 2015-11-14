$(function(){
	$("#update_activity").click(function(){
		 var title_name = $("#title_name").val();
		     activity_url = $("#activity_url").val();
		     picture_url = $("#picture_url").val();
		     id = $("#id").val();
		     $.ajax({
					url: '../activity/editActivity.do',
					method: 'POST',
					dataType: 'json',
					data: {
						id:id,
						title_name:title_name,
						activity_url:activity_url,
						picture_url:picture_url,
					},
					success: function(resp)
					{
						 var _state=resp.state;
						 if(_state ==0){
							    $("#showAlter").show();
						    	$("#alText").html("<strong><span style='color:red'>更新失败 ！<span></strong>")
						 }
						 if(_state ==1){
							    $("#showAlter").show();
						    	$("#alText").html("<strong>更新成功 ！</strong>")
						    	$("#showAlter").fadeOut(2000);
						 }
						 if(_state ==2){
							    $("#showAlter").show();
						    	$("#alText").html("<strong>创建成功 ！</strong>")
						    	$("#showAlter").fadeOut(2000);
						 }
					}
				});
	});
	$("#avtivityList").click(function(){
		window.location.href="../activity/activityList.do"
	})
})
function uploadImage(){
 	$.ajaxFileUpload({
		url:"../activity/uploadImage.do",
		secureuri:false, 
	    fileElementId:"picture",
	    dataType:"json",
	    success:function(data,status){
	    	if(status=='success'){
	    		$("#picture_url").val(data.icon);
	    	}else{
	    		alert("上传失败");
	    	}
	    	
	    }
	})
}