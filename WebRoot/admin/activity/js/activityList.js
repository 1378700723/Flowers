 $(function(){
	  $("#toEditActivity").click(function(){
		  window.location.href="../activity/forwardActivity.do"
	  })
	   
 })
 function upActivity(id){
	 window.location.href="../activity/forwardActivity.do?id="+id;
 }
 var activityId =0;
function delActivity(id){
	jQuery('#showModel').modal('show', {backdrop: 'static'});
	activityId = id;
}
function confirm(){
	 $.ajax({
			url: '../activity/delActivity.do',
			method: 'POST',
			dataType: 'json',
			data: {
				"id":activityId,
			},
			success: function(resp)
			{   
			   var state =resp.state;
				if(state ==1){
					$("#cloModel").click();
					$("#alText").html("<strong>删除成功 ！</strong>")
					$("#showAlter").show();
					setTimeout(function(){
						  $("#showAlter").fadeOut(2000);
						  setTimeout(function(){parent.location.reload();},1000);
					},1000);
					   
				}
			}
		});
}