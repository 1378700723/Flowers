<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<% String contextPath = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
     <meta charset="utf-8">
	<%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
<title>花品录入</title>
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
							<h3 class="panel-title">花品录入</h3>
							<div class="panel-options">
								<a href="#" data-toggle="panel">
									<span class="collapse-icon">&ndash;</span>
									<span class="expand-icon">+</span>
								</a>
								<a href="#" data-toggle="remove">
									&times;
								</a>
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
							
							<form action="<%=contextPath %>/admin/flowersHelper/addFlowers.do" class="validate" method="post" role="form" id="addFlower"  enctype="multipart/form-data">
								 <input type="hidden" name="id" id="flowerId" value="${flower.id}"/>
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>花名称：</B></label>
									<input type="text"   class="form-control" id="name" name="name" data-validate="required,maxlength[20]" data-message-required="必填字段" data-message-maxlength="不能超过二十个字符" placeholder="输入花名" value="${flower.name}">
								</div>
					
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>花语：</B></label>
									<input type="text" class="form-control" id="flowerLanguage" name="flowerLanguage" data-validate="required" data-message-required="必填字段"  placeholder="输入话语" value="${flower.flowerLanguage}">
									 
								</div>
								
								<div class="form-group-separator"></div>
							
								<div class="form-group-separator"></div>			
								<div class="form-group">
									<label class="col-sm-2 control-label"><B>类型：</B></label>
										<p>
											<label class="radio-inline">
											   <input id="radio1" type="radio" name="ptype" value="1" checked>
												鲜花
											</label>
											<label class="radio-inline">
												<input id="radio2" type="radio" name="ptype" value="2">
												种植
											</label>
											<label class="radio-inline">
												<input id="radio3" type="radio" name="ptype" value="3">
												 服务
											</label>
										</p>
									 
								</div>
							    <div class="form-group">
									<label class="col-sm-2 control-label"><B>花类型：</B></label>
										<p>
											<label class="radio-inline">
											   <input id="meigui" type="radio" name="ftype" value="meigui" checked>
												玫瑰
											</label>
											<label class="radio-inline">
												<input id="baihe" type="radio" name="ftype" value="baihe">
												百合
											</label>
											<label class="radio-inline">
												<input id="kangnaixin" type="radio" name="ftype" value="kangnaixin">
												 康乃馨
											</label>
											<label class="radio-inline">
												<input id="juhua" type="radio" name="ftype" value="juhua">
												菊花
											</label>
											<label class="radio-inline">
												<input id="lanhua" type="radio" name="ftype" value="lanhua">
												 兰花
											</label>
										</p>
									 
								</div>
								<div class="form-group-separator"></div>
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>主材：</B></label>
									<input type="text"   class="form-control" id="mainMaterial" name="mainMaterial" data-validate="required,maxlength[20]" data-message-required="必填字段" data-message-maxlength="不能超过二十个字符" placeholder="输入花的主材" value="${flower.mainMaterial}">
								</div>
								<div class="form-group-separator"></div>
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>辅材：</B></label>
									<input type="text"   class="form-control" id="auxiliaryMaterial" name="auxiliaryMaterial" data-validate="required,maxlength[20]" data-message-required="必填字段" data-message-maxlength="不能超过二十个字符" placeholder="输入花的辅材" value="${flower.auxiliaryMaterial}">
								</div>
								<div class="form-group-separator"></div>
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>工艺：</B></label>
									<input type="text"   class="form-control" id="craft" name="craft" data-validate="required,maxlength[20]" data-message-required="必填字段" data-message-maxlength="不能超过二十个字符" placeholder="输入花的工艺" value="${flower.craft}">
								</div>
								<div class="form-group-separator"></div>
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-3"><B>适用场景：</B></label>
  								    <select class="form-control" id="scenario" name="scenario" data-validate="required" data-message-required="必选字段">
										<option value="">请选择</option>								 
									    <option value="smm">送妈妈</option>
										<option value="qrj">情人节</option>
										<option value="jsj">教师节</option>
										<option value="tb">探病</option>
										<option value="mh">缅怀</option>
										<option value="qt">其他</option>
									 </select>
								</div>
								<div class="form-group-separator"></div>
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>适用对象：</B></label>
								     <select class="form-control" id="suitable" name="suitable" data-validate="required" data-message-required="必选字段">
										<option value="">请选择</option>								 
									    <option value="smm">送妈妈</option>
										<option value="qrj">情人节</option>
										<option value="jsj">教师节</option>
										<option value="tb">探病</option>
										<option value="mh">缅怀</option>
										<option value="qt">其他</option>
									 </select>
								</div>
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>尺寸规格：</B></label>
									<input type="text"   class="form-control" id="dimension" name="dimension" data-validate="required,maxlength[20]" data-message-required="必填字段" data-message-maxlength="不能超过二十个字符" placeholder="尺寸规格100*100" value="${flower.dimension}">
								</div>
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-5"><B>描述：</B></label>
								    <textarea class="form-control autogrow" cols="5" id="desc" name="desc" data-validate="required" data-message-required="必填字段"  placeholder="输入描述">${flower.des}</textarea>
								</div>
								
								<div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-4"><B>图标：</B></label>				
			                    	<input type="file" class="form-control" id="icon" name="icon"   >
								</div>
								
						        <div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-4"><B>图片：</B></label>		
									<input type="hidden" value="" name="images" id="images">		 
								</div>
								<div class="form-group-separator"></div>
								
								<div id="dropz" class="dropzone"></div>
								<div class="form-group" align="center">
									<button type="submit" class="btn btn-success">提交</button>
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
	<!-- 判断是否增加成功 -->
	<input type="hidden" value ="${showModel}" id="showModel"/>
</body>
</html>
<script type="text/javascript">
  var ptype="${flower.ptype}";
      scenario ="${flower.scenario}";
      suitable ="${flower.suitable}";
      ftype ="${flower.ftype}";
      
  switch(ptype){
  case '1':$("input[name='ptype']").get(0).checked=true;
	  break;
  case '2':$("input[name='ptype']").get(1).checked=true; 
	  break;
  case '3':$("input[name='ptype']").get(2).checked=true; 
	  break;
  }
  switch(ftype){
  case 'meigui':$("input[name='ftype']").get(0).checked=true;
	  break;
  case 'baihe':$("input[name='ftype']").get(1).checked=true; 
	  break;
  case 'kangnaixin':$("input[name='ftype']").get(2).checked=true; 
	  break;
  case 'juhua':$("input[name='ftype']").get(3).checked=true; 
      break;
  case 'lanhua':$("input[name='ftype']").get(4).checked=true; 
      break;
  }
  if(scenario!=""){
	  $("#scenario").val(scenario);
  }
  if(suitable!=""){
	  $("#suitable").val(suitable);
  }
  var images=[];
  $("#dropz").dropzone({
      url: "<%=request.getContextPath()%>/admin/flowersHelper/textUploadFile.do",
      paramName:"files",
      maxFiles: 10,
      maxFilesize:5,
      addRemoveLinks:false, //是否加上删除按钮
      uploadMultiple:true, //允许一次提交多个文件
      acceptedFiles: ".jpg,.png",
      success:function(file,data){
    	      images.push(data);
    	      $("#images").val(images);
      }
  });
</script>
 <script src="<%=contextPath %>/admin/flowers_sets/js/addFlower.js"></script>