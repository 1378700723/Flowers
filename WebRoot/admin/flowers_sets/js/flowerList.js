var flowerId =0;

function delFlower(id){
	 jQuery('#showModel').modal('show', {backdrop: 'static'});
	 flowerId = id;
}
function confirm(){
	 $.ajax({
			url: '../flowersHelper/delFlower.do',
			method: 'POST',
			dataType: 'json',
			data: {
				"id":flowerId,
			},
			success: function(resp)
			{
			   var state =resp.state;
				if(state ==1){
					$("#cloModel").click();
					$("#showAlter").show();
					$("#alText").html("<strong>删除成功 ！</strong>")
					setTimeout(function(){
						  $("#showAlter").fadeOut(2000);
						  setTimeout(function(){parent.location.reload();},1000);
					},1000);
					   
				}else{
					$("#cloModel").click();
					$("#showAlter").show();
					$("#alText").html("<strong><span style='color:red'>此花品与商品模板关联 不能删除<span></strong>")
					setTimeout(function(){$("#showAlter").fadeOut(3000);},1000);
				}
			}
		});
}
function upFlower(id){
	window.location.href="../flowersHelper/updateFlowers.do?id="+id;
}