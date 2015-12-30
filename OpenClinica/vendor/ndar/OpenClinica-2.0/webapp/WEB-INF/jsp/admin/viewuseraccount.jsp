<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='user' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><span class="title_Admin">
View User Account
</span></h1>


<%--<p><a href="EditUserAccount?userId=<c:out value="${user.id}" />">Edit this user account</a>
&nbsp;<br>
<a href="AuditLogUser?userLogId=<c:out value="${user.id}" />">View Audit Logs for this user</a>--%>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">First Name:</td>
		<td class="table_cell_top"><c:out value="${user.firstName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Last Name:</td>
		<td class="table_cell"><c:out value="${user.lastName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Email:</td>
		<td class="table_cell"><c:out value="${user.email}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Phone:</td>
		<td class="table_cell"><c:out value="${user.phone}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Institutional Affiliation:</td>
		<td class="table_cell"><c:out value="${user.institutionalAffiliation}" />&nbsp;</td>
	</tr>
	
	<tr>
		<td class="table_header_column">Business Administrator:</td>
		<c:choose>
			<c:when test="${user.sysAdmin}">
				<td class="table_cell">Yes</td>
			</c:when>
			<c:otherwise>
				<td class="table_cell">No</td>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<td class="table_header_column">Technical Administrator:</td>
		<c:choose>
			<c:when test="${user.techAdmin}">
				<td class="table_cell">Yes</td>
			</c:when>
			<c:otherwise>
				<td class="table_cell">No</td>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<td class="table_header_column">Status:</td>
		<td class="table_cell"><c:out value="${user.status.name}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Date Created:</td>
		<td class="table_cell"><c:out value="${user.createdDate}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Owner:</td>
		<td class="table_cell"><c:out value="${user.owner.name}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Date Updated:</td>
		<td class="table_cell"><c:out value="${user.updatedDate}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Updated by:</td>
		<td class="table_cell"><c:out value="${user.updater.name}" />&nbsp;</td>
	</tr>
<!-- TODO:
for each study user is in, show:
�	Role
�	Studies created/owned
�	CRFs created/owned (including versions)
�	Study Events created/owned
�	Subjects created/owned
�	Queries created/owned
�	Datasets downloaded
�	Link to reload page including full audit record for User.

-->

	<tr>
		<td class="table_header_column">Roles:</td>
		<td class="table_cell">
			<c:forEach var="studyUserRole" items="${user.roles}">
				<c:out value="${studyUserRole.studyName}" /> - <c:out value="${studyUserRole.role.description}" /><br/>
			</c:forEach>
		</td>
	</tr>
	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
   <td>
   <form action='EditUserAccount?userId=<c:out value="${user.id}" />' method="POST">
    <input type="submit" name="submit" value="Edit this user account" class="button_xlong">
   </form>
   </td>
   <td>
    <form action='AuditLogUser?userLogId=<c:out value="${user.id}" />' method="POST">
     <input type="submit" name="submit" value="View Audit Logs for this user" class="button_xlong">
    </form>
   </td>
  </tr>
</table>
 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>