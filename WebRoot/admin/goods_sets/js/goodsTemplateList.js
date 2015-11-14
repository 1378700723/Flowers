var goodsTemplateId =0;

function delGoodsTemplate(id){
	 jQuery('#showModel').modal('show', {backdrop: 'static'});
	 goodsTemplateId = id;
}
function confirm(){
	 $.ajax({
			url: '../goodsHelper/delGoodsTemplate.do',
			method: 'POST',
			dataType: 'json',
			data: {
				"id":goodsTemplateId,
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
					$("#alText").html("<strong><span style='color:red'>此商品有用户收藏 不能删除！<span></strong>")
					setTimeout(function(){$("#showAlter").fadeOut(3000);},1000);
				}
			}
		});
}
function upGoodsTemplate(id){
	window.location.href="../goodsHelper/updateGoodsTemplate.do?id="+id;
}