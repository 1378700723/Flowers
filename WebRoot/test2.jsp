<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
    <% String contextPath = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style>

#scrollPics{
    height: 150px;
    width: 100%;
    margin-bottom: 10px;
    overflow: hidden;
    position:relative;
}
.num{
    position:absolute;
    right:5px; 
    bottom:5px;
}
#scrollPics .num li{
    float: left;
    color: #FF7300;
    text-align: center;
    line-height: 16px;
    width: 16px;
    height: 16px;
    cursor: pointer;
    overflow: hidden;
    margin: 3px 1px;
    border: 1px solid #FF7300;
    background-color: #fff;
}
#scrollPics .num li.on{
    color: #fff;
    line-height: 21px;
    width: 21px;
    height: 21px;
    font-size: 16px;
    margin: 0 1px;
    border: 0;
    background-color: #FF7300;
    font-weight: bold;
}

</style>
<head>
    <%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
 
<div id="scrollPics">
    <ul class="slider" >
        <li><img src="images/ads/1.gif"/></li>
        <li><img src="images/ads/2.gif"/></li>
        <li><img src="images/ads/3.gif"/></li>
        <li><img src="images/ads/4.gif"/></li>
        <li><img src="images/ads/5.gif"/></li>
    </ul>
    <ul class="num" >
        <li class="on">1</li>
        <li>2</li>
        <li>3</li>
        <li>4</li>
        <li>5</li>
    </ul>
</div>
 
</body>
</html>
<script>
//滚动广告
var len = $(".num > li").length;
var index = 0;  //图片序号
var adTimer;
$(".num li").mouseover(function() {
    index = $(".num li").index(this);  //获取鼠标悬浮 li 的index
    showImg(index);
}).eq(0).mouseover();
//滑入停止动画，滑出开始动画.
$('#scrollPics').hover(function() {
    clearInterval(adTimer);
}, function() {
    adTimer = setInterval(function() {
        showImg(index)
        index++;
        if (index == len) {       //最后一张图片之后，转到第一张
            index = 0;
        }
    }, 3000);
}).trigger("mouseleave");
function showImg(index) {
    var adHeight = $("#scrollPics>ul>li:first").height();
    $(".slider").stop(true, false).animate({
        "marginTop": -adHeight * index + "px"    //改变 marginTop 属性的值达到轮播的效果
    }, 1000);
    $(".num li").removeClass("on")
        .eq(index).addClass("on");
}
</script>