$(document).ready(function() {
	var showModel=$("#showModel").val()
	if(showModel==1){
		$("#showAlter").show();
		$("#alText").html("<strong>添加成功 ！</strong>")
		setTimeout(function(){
			  $("#showAlter").fadeOut(2000);
		},2000);
	}
})