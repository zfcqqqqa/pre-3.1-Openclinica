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

<jsp:useBean scope='request' id='studyToRestore' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='sitesToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='userRolesToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectsToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='definitionsToRestore' class='java.util.ArrayList'/>

<h1><span class="title_Admin">
Confirm Restore of Study
</span></h1>

<p>You choose to restore the following study:</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">
  <c:out value="${studyToRestore.name}"/>
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Brief Summary:</td><td class="table_cell">
  <c:out value="${studyToRestore.summary}"/>
  </td></tr>  
 </table>  
 
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <span class="table_title_Admin">Sites:</span> 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
  <td class="table_header_column_top">Name</td>
  <td class="table_header_column_top">Status</td>
  </tr>
  <c:forEach var="site" items="${sitesToRestore}">
  <tr valign="top">
   <td class="table_cell"><c:out value="${site.name}"/></td>
   <td class="table_cell"><c:out value="${site.status.name}"/></td>
  </tr>  
  </c:forEach> 
  </table>   
 </div>
</div></div></div></div></div></div></div></div>

</div>
  <br>
<span class="table_title_Admin">Users and Roles:</span> 
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_column_top">Name</td>   
   <td class="table_header_column_top">Role</td>
  </tr>  
  <c:forEach var="userRole" items="${userRolesToRestore}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${userRole.userName}"/>
   </td>
   <td>
    <c:out value="${userRole.role.name}"/>
   </td>
  </tr>  
  </c:forEach>   
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
  <br>
 <span class="table_title_Admin">Subjects:</span> 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column_top">Study Subject ID</td>
   
   <td class="table_header_column_top">Record ID</td>
  </tr>  
  <c:forEach var="subject" items="${subjectsToRestore}">
   <tr>
   <td class="table_cell">
    <c:out value="${subject.label}"/>
   </td>
   <td>
    <c:out value="${subject.id}"/>
   </td>
  </tr>  
  </c:forEach> 
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <span class="table_title_Admin">Event Definitions:</span> 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top">
   <td class="table_header_column_top">Name</td>   
   <td class="table_header_column_top">Description</td>
  </tr>  
  <c:forEach var="definition" items="${definitionsToRestore}">
   <tr>
   <td class="table_cell">
    <c:out value="${definition.name}"/>
   </td>
  <td class="table_cell">
    <c:out value="${definition.description}"/>
   </td>
  </tr>  
  </c:forEach>  
  </table>
  
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<form action='RestoreStudy?action=submit&id=<c:out value="${studyToRestore.id}"/>' method="POST">
 <input type="submit" name="submit" value="Restore Study" class="button_long" onClick='return confirm("Are you sure you want to restore this study?");'>
</form>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
