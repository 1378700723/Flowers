<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String contextPath = request.getContextPath();%>
<!DOCTYPE html>
 <html> 
<head>
    <title>花花卉首页</title>
    <%@ include file="/admin/common/import-css.jsp" %>
	<%@ include file="/admin/common/import-js.jsp" %>
    <link href="<%=contextPath %>/admin/index/css/font-awesome.min.css" rel="stylesheet" media="screen">
    <link rel="stylesheet" href="<%=contextPath %>/admin/index/css/templatemo_misc.css">
    <link rel="stylesheet" href="<%=contextPath %>/admin/index/css/animate.css">
    <link href="<%=contextPath %>/admin/index/css/templatemo_style.css" rel="stylesheet" media="screen">
    <script src="<%=contextPath %>/admin/index/js/jquery.mixitup.min.js"></script>
    <script src="<%=contextPath %>/admin/index/js/jquery.nicescroll.min.js"></script>
    <script src="<%=contextPath %>/admin/index/js/jquery.lightbox.js"></script>
    <script src="<%=contextPath %>/admin/index/js/templatemo_custom.js"></script>
    <script src="<%=contextPath %>/admin/index/js/modernizr.js"></script>
</head>
<body>
<div class="page-container"><!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<div class="sidebar-menu toggle-others fixed">
			<div class="sidebar-menu-inner">	
                <%@ include file="/admin/common/import-header.jsp" %>
				<%@ include file="/admin/common/import-menu.jsp" %>
			</div>
		</div>
    <div class="main-content bg-image">
          <div class="container">
            <div class="row">
                <!-- Begin Content -->
                <div class="col-md-10">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="templatemo_logo">
                                <a href="#">
                                    <img src="images/templatemo_logo.png" alt="Genius">
                                </a>
                            </div> 
                        </div> 
                    </div>  
                    <div id="menu-container">
                        
                        <div id="menu-1" class="homepage">
                            <div class="row">
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p1.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p1.jpg" alt="Image 1">
                                    </div>  
                                </div>  
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p2.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p2.jpg" alt="Image 2">
                                    </div>  
                                </div>  
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p3.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p3.jpg" alt="Image 3">
                                    </div>  
                                </div>  
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p4.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p4.jpg" alt="Image 4">
                                    </div>  
                                </div> 
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p5.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p5.jpg" alt="Image 5">
                                    </div>  
                                </div> 
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p6.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p6.jpg" alt="Image 6">
                                    </div> 
                                </div>  
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p7.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p7.jpg" alt="Image 7">
                                    </div>  
                                </div> 
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p8.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p8.jpg" alt="Image 8">
                                    </div>  
                                </div>  
                                <div class="col-md-4 col-sm-6 col-xs-6">
                                    <div class="portfolio-item">
                                        <div class="overlay">
                                            <a href="images/gallery/p9.jpg" data-rel="lightbox">
                                                <i class="fa fa-expand"></i>
                                            </a>
                                        </div>
                                        <img src="images/gallery/p9.jpg" alt="Image 9">
                                    </div>  
                                </div>  
                            </div>  
                        </div>  
                    </div> 
                </div>  
            </div> 
        </div>  
    </div> 
</div>
</body>
</html>