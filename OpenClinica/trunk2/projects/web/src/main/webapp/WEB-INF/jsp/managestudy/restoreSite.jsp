<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

 <c:import url="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='siteToRestore' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='userRolesToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectsToRestore' class='java.util.ArrayList'/>

  <h1><span class="title_manage"><fmt:message key="confirm_restore_of_site"  bundle="${resword}"/></span></h1>

<p><div class="table_name"><fmt:message key="you_choose_to_restore_the_following_site" bundle="${resword}"/></div>
<table border="0" cellpadding="0"    class="shaded_table  table_first_column_w30 fcolumn">

  <tr valign="top"><td ><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToRestore.name}"/>
  </td></tr>

  <tr valign="top"><td ><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToRestore.summary}"/>
  </td></tr>
 </table>


<br>
<div class="table_name"><fmt:message key="users_and_roles" bundle="${resword}"/></div>
<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 cell_borders  hrow">

  <tr valign="top">
   <th><fmt:message key="name" bundle="${resword}"/></th>
   <th><fmt:message key="role" bundle="${resword}"/></th>
  </tr>
  <c:forEach var="userRole" items="${userRolesToRestore}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${userRole.userName}"/>
   </td>
   <td class="table_cell">
    <c:out value="${userRole.role.name}"/>
   </td>
  </tr>
  </c:forEach>
  </table>

<br>
<div class="table_name"><fmt:message key="subjects" bundle="${resword}"/></div>
<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 cell_borders  hrow">
 <tr valign="top">
   <th><fmt:message key="subject_unique_identifier" bundle="${resword}"/></th>
   <th><fmt:message key="openclinica_subject_id" bundle="${resword}"/></th>
  </tr>
  <c:forEach var="subject" items="${subjectsToRestore}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${subject.label}"/>
   </td>
   <td class="table_cell">
    <c:out value="${subject.id}"/>
   </td>
  </tr>
  </c:forEach>
  </table>

<br>


 <form action='RestoreSite?action=submit&id=<c:out value="${siteToRestore.id}"/>' method="POST">
  <input type="submit" name="submit" value="<fmt:message key="restore_site" bundle="${resword}"/>" onClick='return confirm("<fmt:message key="are_you_sure_you_want_to_restore_this_site" bundle="${resword}"/>");' class="button_long">
 </form>

<br><br>

<!-- EXPANDING WORKFLOW BOX -->


<!-- END WORKFLOW BOX -->


<jsp:include page="../include/footer.jsp"/>
