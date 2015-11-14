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
<title>商品录入</title>
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
							<h3 class="panel-title">商品模板录入</h3>
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
							<form action="<%=contextPath %>/admin/goodsHelper/addGoodsTemplate.do" class="validate" method="post" role="form" id="addFlower">
								 <input type="hidden" name="id" id="goodId" value="${goodsTemplate.id}"/>
							    <div class="form-group-separator"></div>	
							    
							    <div class="form-group">
									<label class="col-sm-2 control-label" for="field-1"><B>商品名称：</B></label>
									<input type="text"   class="form-control" id="name" name="name" data-validate="required,maxlength[50]" data-message-required="必填字段" data-message-maxlength="不能超过50个字符" placeholder="输入花名" value="${goodsTemplate.name}">
								</div>
								
								<div class="form-group-separator"></div>			 
								
								<div class="form-group">
									<label class="col-sm-2 control-label"><B>所在城市：</B></label> 
											<label class="radio-inline">
											   <input id="radio1" type="radio" name="city" value="010" checked>
												北京
											</label>
											<label class="radio-inline">
												<input id="radio2" type="radio" name="city" value="021">
												上海
											</label>
											<label class="radio-inline">
												<input id="radio3" type="radio" name="city" value="022">
												 天津
											</label>
											<label class="radio-inline">
												<input id="radio3" type="radio" name="city" value="020">
												 广州
										 </label>
								</div>
							   <div class="form-group-separator"></div>		
								<div class="form-group">
									<label class="col-sm-2 control-label"><B>适用场景对象：</B></label> 
											<label class="checkbox-inline">
											   <input id="smm" type="checkbox" name=labels value="smm">
												送妈妈
											</label>
											<label class="checkbox-inline">
												<input id="qrj" type="checkbox" name="labels" value="qrj">
												情人节
											</label>
											<label class="checkbox-inline">
												<input id="jsj" type="checkbox" name="labels" value="jsj">
												 教师节
											</label>
											<label class="checkbox-inline">
												<input id="tb" type="checkbox" name="labels" value="tb">
												探病
										    </label>
										    <label class="checkbox-inline">
												<input id="mh" type="checkbox" name="labels" value="mh">
												缅怀
										    </label>
								</div>
							    <div class="form-group-separator"></div>			 
								
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>当前价格：</B></label>
									<input type="text" class="form-control" id="curPrice" name="curPrice" data-validate="required,number" data-message-required="必填字段" data-message-number="输入数字"  placeholder="输入当前价格" value="${goodsTemplate.curPrice}">
									 
								</div>
						        
						        <div class="form-group-separator"></div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>原价格：</B></label>
									<input type="text" class="form-control" id="oldPrice" name="oldPrice" data-validate="required,number" data-message-required="必填字段" data-message-number="输入数字"  placeholder="输入原价格" value="${goodsTemplate.oldPrice}"> 
								</div>
						        <div class="form-group-separator"></div>
						        
						        <div class="form-group">
									<label class="col-sm-2 control-label"><B>是否特价：</B></label> 
											<label class="radio-inline">
											   <input id="radio1" type="radio" name="isBargain" value="1" checked>
												是
											</label>
											<label class="radio-inline">
												<input id="radio2" type="radio" name="isBargain" value="0">
												否
											</label>	 
								</div>	
                                <div class="form-group-separator"></div>
                                
                                <div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>运费：</B></label>
									<input type="text" class="form-control" id="delivery" name="delivery" data-validate="required,number" data-message-required="必填字段" data-message-number="输入数字"  placeholder="如果免运费请输入0" value="${goodsTemplate.delivery}"> 
								</div>
						        <div class="form-group-separator"></div>
                                <div class="form-group">
									<label class="col-sm-2 control-label" for="field-2"><B>商品包装：</B></label>
									<input type="text" class="form-control" id="detailClassify" name="detailClassify"      placeholder="例如:红玫瑰粉色包装,红玫瑰蓝色包装(注:多个分类请用英文逗号分开)" value="${goodsTemplate.detailClassify}"> 
								</div>
								<div class="form-group-separator"></div>
								<div class="form-group">
									<label class="col-sm-2 control-label"><B>花品</B></label>
										<select class="form-control" id="flowerId" name="flowerId"  data-validate="required" data-message-required="必选字段">
											<option value="">请选择</option>
											<c:forEach items="${flowerList}" var="flower">
											    <option value="${flower.id}">${flower.flowerName}</option>
											 </c:forEach>
										</select>
								</div>
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
  var city="${goodsTemplate.city}";
      flowerid="${goodsTemplate.flowerid}";
      isBargain="${goodsTemplate.bargain}";
      labels="${goodsTemplate.labels}";
  var label_array=labels.split(',');
  
  switch(city){
	  case '010':$("input[name='city']").get(0).checked=true;
		  break;
	  case '021':$("input[name='city']").get(1).checked=true; 
		  break;
	  case '022':$("input[name='city']").get(2).checked=true; 
		  break;
	  case '020':$("input[name='city']").get(3).checked=true; 
	      break;
  }
  if(isBargain!=''&&isBargain!=null){
	  isBargain=='true'?$("input[name='isBargain']").get(0).checked=true:$("input[name='isBargain']").get(1).checked=true;
  }
  if(flowerid>0){
	  $("#flowerId").val(flowerid);
  }
  for(var i=0;i<label_array.length;i++){
	  switch(label_array[i]){
	  case 'smm':$("input[name='labels']").get(0).checked=true;
		  break;
	  case 'qrj':$("input[name='labels']").get(1).checked=true; 
		  break;
	  case 'jsj':$("input[name='labels']").get(2).checked=true; 
		  break;
	  case 'tb':$("input[name='labels']").get(3).checked=true; 
	      break;
	  case 'mh':$("input[name='labels']").get(4).checked=true; 
          break;
  }
  }
</script>
 <script src="<%=contextPath %>/admin/goods_sets/js/addGoodsTemplate.js"></script>
 