<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.extract.FilterRow" />

<tr>
	<td class="table_cell">
		<c:choose>
			<c:when test='${currRow.bean.status.name == "removed"}'>
				<font color='gray'><c:out value="${currRow.bean.name}" /></font>
			</c:when>
			<c:otherwise>
				<c:out value="${currRow.bean.name}" />
			</c:otherwise>
		</c:choose>
	</td>
	<td class="table_cell"><c:out value="${currRow.bean.description}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.owner.name}" /></td>
	<td class="table_cell"><fmt:formatDate value="${currRow.bean.createdDate}" pattern="MM/dd/yyyy"/></td>
	<td class="table_cell"><c:out value="${currRow.bean.status.name}" /></td>
	
	<%-- ACTIONS --%>
	<td class="table_cell">
	<table border="0" cellpadding="0" cellspacing="0">
      <tr>
		<c:choose>
			<c:when test='${currRow.bean.status.name == "removed"}'>
			<td></td>
				<%-- parts to be added later, look at showUserAccountRow.jsp, tbh --%>
			</c:when>
			<c:otherwise>
			<td>	
				<a href="ApplyFilter?action=details&filterId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		    </td><td>
		    	<a href="ApplyFilter?action=validate&submit=Apply Filter&filterId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="setImage('bt_Export1','images/bt_Export_d.gif');"
			onMouseUp="setImage('bt_Export1','images/bt_Export.gif');"><img 
		    name="bt_Export1" src="images/bt_Export.gif" border="0" alt="Apply Filter" title="Apply Filter" align="left" hspace="6"></a>
			</td>
				
			
			</c:otherwise>
		</c:choose>
		</tr>
		</table>	
	</td>
</tr>