<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
 <nav>
		  <ul class="pagination" style="margin-top:-10px">
		    <li><a onclick="${paperFunc}('sub',${dataSource.currentPage}${paperFuncParam})" href="#">&laquo;</a></li>
			    <c:if test="${dataSource.currentPage>6 }">
			    	<li> <a onclick="${paperFunc}('',1${paperFuncParam})" href="#">1...</a></li>
			    </c:if>
			    
			    <c:forEach var="item" varStatus="status" begin="0" end="${(dataSource.pageDisplayCount-1)<0?0:(dataSource.pageDisplayCount-1)}">
			    	<c:if test="${(dataSource.firstPageNumber+status.index)<=dataSource.pageCount }">
			    	<c:choose>  
					    <c:when test="${dataSource.currentPage == (dataSource.firstPageNumber+status.index)}">
					    	<li class="active">
					    </c:when>
					     <c:otherwise>
					     	<li>
					     </c:otherwise>
					</c:choose> 
					
			    		<a onclick="${paperFunc}('',${dataSource.firstPageNumber+status.index}${paperFuncParam})" href="#"><c:out value="${dataSource.firstPageNumber+status.index}" /></a>
			    	</li>
			    	</c:if>
			    </c:forEach>
			    <c:if test="${(dataSource.pageCount-dataSource.currentPage)>=5 && dataSource.pageCount>10 }">
			    	<li> <a onclick="${paperFunc}('',${dataSource.pageCount}${paperFuncParam})" href="#">...${dataSource.pageCount}</a></li>
			    </c:if>
		    <li><a onclick="${paperFunc}('add',${dataSource.currentPage}${paperFuncParam})" href="#">&raquo;</a></li>
		  </ul>
 </nav>

