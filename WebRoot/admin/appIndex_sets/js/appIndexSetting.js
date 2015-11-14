$(function(){
	 $("#submitIndexSetting").click(function(){
		 var bargainId=[];
		      mqId =[];
		      qrId =[];
		      jsId =[];
		      tbId =[];
		      mhId =[];
		 $("select[name='bargainId']").each(function(index){
			 if($(this).val() !=""){
				bargainId[index]=$(this).val(); 
			 }
			     
		  });
		 $("select[name='mqId']").each(function(index){
			 if($(this).val() !=""){
			    mqId[index]=$(this).val();     
			 }
		  });
		 $("select[name='qrId']").each(function(index){
			 if($(this).val() !=""){
			    qrId[index]=$(this).val();    
			 }
		  });
		 $("select[name='jsId']").each(function(index){
			 if($(this).val() !=""){
			   jsId[index]=$(this).val();     
			 }
		  });
		 $("select[name='tbId']").each(function(index){
			 if($(this).val() !=""){
		    	 tbId[index]=$(this).val();     
			 }
		  });
		 $("select[name='mhId']").each(function(index){
			 if($(this).val() !=""){
			     mhId[index]=$(this).val();  
			 }
		  });
	 var goodsTemplateIds = $("#goodsTemplate_ids").val();
 		 $.ajax({
				url: '../index/addIndexGoods.do',
				method: 'POST',
				dataType: 'json',
				data: {
					bargainId:bargainId,
					mqId:mqId,
					qrId:qrId,
					jsId:jsId,
					tbId:tbId,
					mhId:mhId,
					homePageActivitys:goodsTemplateIds
				},
				success: function(resp)
				{
					 var _state=resp.state;
					 if(_state ==0){
						    $("#showAlter").show();
					    	$("#alText").html("<strong><span style='color:red'>设置失败！<span></strong>")
					 }
					 if(_state ==1){
						    $("#showAlter").show();
					    	$("#alText").html("<strong>设置成功 ！</strong>") 
					 }
				}
			});
	 });
})