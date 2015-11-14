<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<% String contextPath = request.getContextPath(); %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
     <meta charset="utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
	
<title>设置移动端首页展示商品</title>
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
	             <div class="row" style="display:none" id ="showAlter">
                        <div class="col-md-12">
							<div class="alert alert-info">
								<button type="button" class="close" data-dismiss="alert">
									<span aria-hidden="true">&times;</span>
									<span class="sr-only">Close</span>
								</button>
								<div id="alText">	
								</div>
 							</div>
						</div>
	             </div>
		       <form  role="form" id="addFlower">
					  <div class="row">
							<div class="col-md-6">			
								<div class="panel panel-default">
									<div class="panel-heading">特价显示商品</div>
									<div class="panel-body">
			                           <c:forEach begin="0" end="2" step="1">
											<div class="form-group">
												<select class="form-control" name="bargainId">
													<option value="">请选择</option>
													<c:forEach items="${goodTemplateList}" var="goodTemplate">
													    <option value="${goodTemplate.id}">${goodTemplate.name}</option>
													 </c:forEach>
												</select>
											</div>
									   </c:forEach>
									</div>
								</div>
							</div>
							
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading">母亲节专区</div>
									<div class="panel-body">
			                           <c:forEach begin="0" end="2" step="1">
											<div class="form-group">
												<select class="form-control"  name="mqId">
													<option value="">请选择</option>
													<c:forEach items="${goodTemplateList}" var="goodTemplate">
													    <option value="${goodTemplate.id}">${goodTemplate.name}</option>
													 </c:forEach>
												</select>
											</div>
									     </c:forEach>
									</div>
								</div>
							</div>
					  </div>
					  <div class="row">
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading">情人节专区</div>
									<div class="panel-body">
			                          <c:forEach begin="0" end="2" step="1">
											<div class="form-group">
												<select class="form-control"  name="qrId">
													<option value="">请选择</option>
													<c:forEach items="${goodTemplateList}" var="goodTemplate">
													    <option value="${goodTemplate.id}">${goodTemplate.name}</option>
													 </c:forEach>
												</select>
											</div>
									   </c:forEach>
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading">教师节专区</div>
									<div class="panel-body">
			                           	<c:forEach begin="0" end="2" step="1">
											<div class="form-group">
												<select class="form-control"  name="jsId">
													<option value="">请选择</option>
													<c:forEach items="${goodTemplateList}" var="goodTemplate">
													    <option value="${goodTemplate.id}">${goodTemplate.name}</option>
													 </c:forEach>
												</select>
											</div>
										 </c:forEach>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading">探病专区</div>
									<div class="panel-body">
			                           	<c:forEach begin="0" end="2" step="1">
											<div class="form-group">
												<select class="form-control"  name="tbId">
													<option value="">请选择</option>
													<c:forEach items="${goodTemplateList}" var="goodTemplate">
													    <option value="${goodTemplate.id}">${goodTemplate.name}</option>
													 </c:forEach>
												</select>
											</div>
										 </c:forEach>
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading">缅怀专区</div>
									<div class="panel-body">
			                             <c:forEach begin="0" end="2" step="1">
											<div class="form-group">
												<select class="form-control"  name="mhId">
													<option value="">请选择</option>
													<c:forEach items="${goodTemplateList}" var="goodTemplate">
													    <option value="${goodTemplate.id}">${goodTemplate.name}</option>
													 </c:forEach>
												</select>
											</div>
											</c:forEach>
									</div>
								</div>
							</div>
						</div>
						 <!-- 活动专区所选的商品Id集合 -->
						 <input type="hidden" value="" id="goodsTemplate_ids">
	           </form>
	           <!-- 选择活动专区商品 -->
	           	<div class="row">
					<div class="col-sm-12">
						<div class="panel panel-default">
							<div class="panel-heading">
							     <h3 class="panel-title">活动专区</h3> 
								 <div class="panel-options">
									<button id="permission"  class="btn btn-danger btn-sm btn-icon">添加商品</button>
								</div>
							</div>
							<div class="panel-body">
	                            <table class="table  table-hover">
										<thead>
											<tr>
											    <th><input type="checkbox" class="cbr"></th>
												<th>商品名称</th>
												<th>商品价格</th>
												<th>邮费</th>
												<th>是否特价</th>
												<th>删除</th>
											</tr>
										</thead>
										<tbody id="tbody_append">
											<c:forEach items="${homePageActivityList}" var="homePageActivityList">	   
												   <c:forEach items="${goodTemplateList}" var="goodTemplate">
												     <c:if test="${goodTemplate.id==homePageActivityList.index}">
												       <tr id="tr_${goodTemplate.id}">
														    <td><input type="checkbox" class="cbr"></td>
														    <td>${goodTemplate.name}</td>
													        <td>${goodTemplate.curPrice}</td>
													        <td><c:if test="${empty goodTemplate.delivery}">免运费</c:if>
													        ${goodTemplate.delivery}
														    </td>
														    <td>
														    <c:if test="${goodTemplate.bargain==true}">是</c:if>
													        <c:if test="${goodTemplate.bargain==false}">否</c:if>
														    </td>
															<td> 
															 <button   class='btn btn-danger btn-sm btn-icon' onclick='removeTr(${goodTemplate.id})'>移除</button>
															</td>
														</tr>
												     </c:if>
												  </c:forEach>
										     </c:forEach>			 
										</tbody>
									</table>
						    </div>
						</div>
					</div>
				 </div>
		 <div class="form-group" align="center">
		    <button type="button" class="btn btn-success" id="submitIndexSetting">提交</button>
		 	<button type="reset" class="btn btn-white">重置</button>
		 </div>
			<%@ include file="/admin/common/import-footer.jsp" %>
		</div>
	</div>
	<!-- 弹出模板选择商品 -->
   <div class="modal fade custom-width" id="showModel">
		<div class="modal-dialog" style="width: 60%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">活动管理</h4>
				</div>
				
				<div class="modal-body">
			     <table class="table table-bordered table-striped" id="example-2">
						<thead>
							<tr>
								<th class="no-sorting">
									<input type="checkbox" class="cbr">
								</th>
								<th>商品名称</th>
								<th>商品价格</th>
								<th>邮费</th>
								<th>是否特价</th>
							</tr>
						</thead>
						<tbody class="middle-align">
                             <c:forEach items="${goodTemplateList}" var="goodTemplate">
							    <tr class="${goodTemplate.id}">
							        <td><input type="checkbox" class="cbr" id="check_${goodTemplate.id}" value="${goodTemplate.id}" name="goodsTemplate_id"></td>
							        <td>${goodTemplate.name}</td>
							        <td>${goodTemplate.curPrice}</td>
							        <td><c:if test="${empty goodTemplate.delivery}">免运费</c:if>
							        ${goodTemplate.delivery}
								    </td>
								    <td>
								    <c:if test="${goodTemplate.bargain==true}">是</c:if>
							        <c:if test="${goodTemplate.bargain==false}">否</c:if>
								   </td>
							    </tr>
							 </c:forEach>
						</tbody>
					</table>	 
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal" id="closeGoods_tem">关闭</button>
					<button type="button" class="btn btn-info" id="saveGoods_tem">保存</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
<script type="text/javascript">
jQuery(document).ready(function($)
		{
			$("#example-2").dataTable({
				dom: "t" + "<'row'<'col-xs-6'i><'col-xs-6'p>>",
				aoColumns: [
					{bSortable: false},
					null,
					null,
					null,
					null
				],
			});
			// Replace checkboxes when they appear
			var $state = $("#example-2 thead input[type='checkbox']");
			
			$("#example-2").on('draw.dt', function()
			{
				cbr_replace();
				
				$state.trigger('change');
			});
			
			// Script to select all checkboxes
			$state.on('change', function(ev)
			{
				var $chcks = $("#example-2 tbody input[type='checkbox']");
				
				if($state.is(':checked'))
				{
					$chcks.prop('checked', true).trigger('change');
				}
				else
				{
					$chcks.prop('checked', false).trigger('change');
				}
			});

			$("#permission").click(function(){
				$('#showModel').modal('show',{backdrop: 'static'});
			});
			
			$("#saveGoods_tem").click(function(){
				var goodsTemplate_ids=[];
				var goodsTemplateIds = $("#goodsTemplate_ids").val();
		     	    goodsTemplate_ids_array = goodsTemplateIds.split(",");
 				$("input[name='goodsTemplate_id']:checked").each(function(){
					var goodsId = $(this).val();
				        if(!in_array(goodsId,goodsTemplate_ids_array)){
				        	$("#tbody_append").append("<tr id='tr_"+goodsId+"'>"+$("."+goodsId+"").html()+"<td> <button   class='btn btn-danger btn-sm btn-icon' onclick='removeTr("+goodsId+")'>移除</button></td></tr>") 
				        }
					goodsTemplate_ids.push(goodsId);
				})
				if(goodsTemplate_ids.length==0){
					$("#tbody_append").html("");
				}else{
					for(var i=0;i<goodsTemplate_ids_array.length;i++){
						if(!in_array(goodsTemplate_ids_array[i],goodsTemplate_ids)){
							$("#tr_"+goodsTemplate_ids_array[i]+"").remove();
						}
					}
				}
				$("#goodsTemplate_ids").val(goodsTemplate_ids);
				$("#closeGoods_tem").click();
			})
			function in_array(search,array){
			    for(var i in array){
			        if(array[i]==search){
			            return true;
			        }
			    }
			    return false;
			}
});
var  homePageBargainGoods="${homePageBargainGoods}";
     smm="${smm}";
     qrj="${qrj}";
     jsj="${jsj}";
     tb="${tb}";
     mh="${mh}";
     homePageActivitys="${homePageActivitys}";
     homePageBargainGoods !=""?pand(homePageBargainGoods,'bargainId'):"";
     smm !=""? pand(smm,'mqId'):"";
     qrj !=""? pand(qrj,'qrId'):"";
     jsj !=""? pand(jsj,'jsId'):"";
     tb  !=""? pand(tb,'tbId'):"";
     mh  !=""? pand(mh,'mhId'):"";
     if(homePageActivitys!=""){
     	 homePageActivitys = homePageActivitys.substring(1,homePageActivitys.length-1); 
 		 array =  homePageActivitys.split(",");
		 $("#goodsTemplate_ids").val(array);
		 for(var i=0;i<array.length;i++){
 			 var $chcks = $("#check_"+array[i]+"");
			 $chcks.prop('checked', true).trigger('change');
		 }
     }
	function pand(str,name){
		 strToArray(str);
		 
		 function strToArray(str){
			  str = str.substring(1,str.length-1); 
			  array =  str.split(",");
		 evaluation(array,name);
		 }
		 function evaluation(array,name){
	  		 for(var i=0;i<array.length;i++){
	  	         $("select[name='"+name+"']:eq("+i+")").val(array[i]);
	  	  	 }
	  	 }
	}
	function removeTr(id){
		$("#tr_"+id+"").remove();
		var goodsTemplateIds = $("#goodsTemplate_ids").val();
	 	goodsTemplate_ids_array = goodsTemplateIds.split(",");
	  	for(var i=0;i<goodsTemplate_ids_array.length;i++){
	  		if(goodsTemplate_ids_array[i]==id){
	 	    	goodsTemplate_ids_array.splice(i,1);//从下标为i的元素开始，连续删除1个元素
	 	        i--;//因为删除下标为i的元素后，该位置又被新的元素所占据，所以要重新检测该位置
	 	    }
	 	}
	   	$("#goodsTemplate_ids").val(goodsTemplate_ids_array);
	    var $chcks = $("#check_"+id+"");
	    $chcks.prop('checked', false).trigger('change');
	}
</script>
<script src="<%=contextPath %>/admin/appIndex_sets/js/appIndexSetting.js"></script>