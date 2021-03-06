<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
         Confirm restoration of this subject to Study <c:out value="${study.name}"/>. The Subject and all data associated with it in this Study/Site will be restored.
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="events" class="java.util.ArrayList"/>
<h1><span class="title_manage">
Restore Subject to Study
</span></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column">Subject ID:</td><td class="table_cell"><c:out value="${subject.id}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Unique Identifier:</td><td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Gender:</td><td class="table_cell"><c:out value="${subject.gender}"/></td></tr>
    
  <tr valign="top"><td class="table_header_column">Study Subject ID:</td><td class="table_cell"><c:out value="${studySub.id}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Label:</td><td class="table_cell"><c:out value="${studySub.label}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Secondary Label:</td><td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column">Enrollment Date:</td>
  <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="MM/dd/yyyy"/></td></tr> 
  <tr valign="top"><td class="table_header_column">Created By:</td><td class="table_cell"><c:out value="${studySub.owner.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column">Last Updated By:</td><td class="table_cell"><c:out value="${studySub.updater.name}"/>&nbsp;
  </td></tr>
  <tr valign="top"><td class="table_header_column">Date Updated:</td><td class="table_cell"><fmt:formatDate value="${studySub.updatedDate}" pattern="MM/dd/yyyy"/>&nbsp;
  </td></tr>
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <c:if test="${!empty events}">
 <span class="table_title_manage">Subject Events </span>
 <div style="width: 600px">
   <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

   <div class="textbox_center">
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td class="table_header_column_top">Last Updated</td>
        <td class="table_header_column_top">Event</td>
        <td class="table_header_column_top">Start Date</td>
        <td class="table_header_column_top">End Date</td>
        <td class="table_header_column_top">Location</td>
        <td class="table_header_column_top">Updated By</td>
        <td class="table_header_column_top">Status</td>
      </tr>
    <c:forEach var="event" items="${events}">
    <tr>      
        <td class="table_cell"><fmt:formatDate value="${event.updatedDate}" pattern="MM/dd/yyyy"/></td>
        <td class="table_cell"><c:out value="${event.studyEventDefinition.name}"/></td>
        <td class="table_cell"><fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/></td>
        <td class="table_cell"><fmt:formatDate value="${event.dateEnded}" pattern="MM/dd/yyyy"/></td>
        <td class="table_cell"><c:out value="${event.location}"/></td>
        <td class="table_cell"><c:out value="${event.updater.name}"/></td>
        <td class="table_cell"><c:out value="${event.status.name}"/></td>
     </tr>
     </c:forEach>  
   </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
   </c:if>  
   
   <c:choose>
    <c:when test="${!empty events}">
     <form action='RestoreStudySubject?action=submit&id=<c:out value="${studySub.id}"/>&subjectId=<c:out value="${studySub.subjectId}"/>&studyId=<c:out value="${studySub.studyId}"/>' method="POST">
       <input type="submit" name="submit" value="Restore Subject to Study" class="button_xlong" onClick='return confirm("This subject has data from events shown on page. Are you sure you want to restore this subject and the associated data?");'>
     </form>   
    </c:when>
    <c:otherwise>
     <form action='RestoreStudySubject?action=submit&id=<c:out value="${studySub.id}"/>&subjectId=<c:out value="${studySub.subjectId}"/>&studyId=<c:out value="${studySub.studyId}"/>' method="POST">
       <input type="submit" name="submit" value="Restore Subject to Study" class="button_xlong" onClick='return confirm("Are you sure you want to restore it?");'>
     </form> 
     
    </c:otherwise>
   </c:choose>  
 

<jsp:include page="../include/footer.jsp"/>
