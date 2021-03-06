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
				<%-- parts to be added later, look at showUserAccountRow.jsp, tbh --%>
				<td></td>
			</c:when>
			<c:otherwise>
				<%--<a href="ApplyFilter?action=details&filterId=<c:out value="${currRow.bean.id}"/>">view</a> /--%>
				<%--<a href="EditFilter?filterId=<c:out value="${currRow.bean.id}"/>">edit</a> --%>
			<td>	
				<a href="ApplyFilter?action=details&filterId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
			</td><td>
				<a href="EditFilter?filterId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
			</td><td>
				<%--<c:set var="confirmQuestion" value="Are you sure you want to remove ${currRow.bean.name}?" />
				<c:set var="onClick" value="return confirm('${confirmQuestion}');"/>
				<a href="RemoveFilter?filterId=<c:out value="${currRow.bean.id}"/>" onClick="<c:out value="${onClick}" />">delete</a>--%>
			</td><td>	
				<a href="RemoveFilter?filterId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
			</td>
			</c:otherwise>
		</c:choose>	
		</tr>
		</table>
	</td>
</tr>