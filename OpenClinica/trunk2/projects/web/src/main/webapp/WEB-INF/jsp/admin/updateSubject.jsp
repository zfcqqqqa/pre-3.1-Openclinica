<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

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

<jsp:useBean scope="request" id="fathers" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="mothers" class="java.util.ArrayList" />
<jsp:useBean scope="session" id="subjectToUpdate" class="org.akaza.openclinica.bean.submit.SubjectBean" />
<jsp:useBean scope="session" id="localBirthDate" class="java.lang.String" />

<body class="aka_bodywidth" onload="<c:if test='${popUpURL != ""}'>openDNoteWindow('<c:out value="${popUpURL}" />');</c:if>">

<h1><span class="title_manage"><fmt:message key="update_subject_details" bundle="${resword}"/></span></h1>
<P><div class="indicates_required_field">* <fmt:message key="indicates_required_field" bundle="${resword}"/></div></P>
<form action="UpdateSubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${subjectToUpdate.id}"/>">
<input type="hidden" name="studySubId" value="<c:out value="${studySubId}"/>">
<!-- These DIVs define shaded box borders -->

<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 ">
	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
		<td>
		  <input type="text" name="uniqueIdentifier" value="<c:out value="${subjectToUpdate.uniqueIdentifier}"/>" class="formfieldXL">
		  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel"><fmt:message key="gender" bundle="${resword}"/>:</td>
		<td>
		 <c:choose>
		 <c:when test="${subjectToUpdate.gender == 109}">
          <input type="radio" name="gender" checked value="m"><fmt:message key="male" bundle="${resword}"/>
          <input type="radio" name="gender" value="f"><fmt:message key="female" bundle="${resword}"/>
          <input type="radio" name="gender" value=""><fmt:message key="not_specified" bundle="${resword}"/>
         </c:when>
         <c:when test="${subjectToUpdate.gender == 102}">
          <input type="radio" name="gender" value="m"><fmt:message key="male" bundle="${resword}"/>
          <input type="radio" checked name="gender" value="f"><fmt:message key="female" bundle="${resword}"/>
          <input type="radio" name="gender" value=""><fmt:message key="not_specified" bundle="${resword}"/>
         </c:when>
         <c:otherwise>
         <input type="radio" name="gender" value="m"><fmt:message key="male" bundle="${resword}"/>
         <input type="radio" name="gender" value="f"><fmt:message key="female" bundle="${resword}"/>
         <input type="radio" checked name="gender" value=""><fmt:message key="not_specified" bundle="${resword}"/>
        </c:otherwise>
        </c:choose>
        <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="gender"/></jsp:include>
        <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
		<a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?subjectId=${studySubId}&name=subject&id=<c:out value="${subjectToUpdate.id}"/>&field=gender&column=gender','spanAlert-gender'); return false;">
		<img name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
		</c:if>
		</td>
	</tr>
	<c:choose>
	<c:when test="${subjectToUpdate.dobCollected==true && subjectToUpdate.dateOfBirth != null }">
	<tr valign="top">
		<td class="formlabel"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td>
	  	<td>
		 <%--
		  <input type="text" name="dateOfBirth" size="15" value="<fmt:formatDate value="${subjectToUpdate.dateOfBirth}"  pattern="${dteFormat}"/>" class="formfieldXL">
		  --%>
		  <input type="text" name="dateOfBirth" size="15" value="<c:out value="${localBirthDate}"/>" class="formfieldXL required">
		  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dateOfBirth"/></jsp:include>
	  	<span class="formlabel ">(<fmt:message key="date_format" bundle="${resformat}"/>) *
	  	<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
		 <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?subjectId=${studySubId}&name=subject&id=<c:out value="${subjectToUpdate.id}"/>&field=dateOfBirth&column=date_of_birth','spanAlert-dateOfBirth'); return false;">
		 <img name="flag_dateOfBirth" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
		</c:if></span>
	  	</td>
	</tr>
	</c:when>
	<c:when test="${subjectToUpdate.dobCollected==false && subjectToUpdate.dateOfBirth == null}">
	<tr valign="top">
		<td class="formlabel"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td>
	  	<td>
		 <input type="text" name="dateOfBirth" size="15" value="<c:out value="${dateOfBirth}"/>" class="formfieldXL required">
		  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="yearOfBirth"/></jsp:include>
	  	<span class="formlabel ">(<fmt:message key="date_format" bundle="${resformat}"/>) *
	  	<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
		 <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?subjectId=${studySubId}&name=subject&id=<c:out value="${subjectToUpdate.id}"/>&field=dateOfBirth&column=date_of_birth','spanAlert-dateOfBirth'); return false;">
		 <img name="flag_dateOfBirth" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
		</c:if></span>
	  	</td>
	</tr>
	</c:when>
	<c:otherwise>
	<tr valign="top">
		<td class="formlabel"><fmt:message key="year_of_birth" bundle="${resword}"/>:</td>
	  	<td>
		  <input type="text" name="yearOfBirth" size="15" value="<c:out value="${yearOfBirth}"/>" class="formfieldM required">
		  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="yearOfBirth"/></jsp:include>
	  	<span class="formlabel  ">(<fmt:message key="date_format_year" bundle="${resformat}"/>)*
	  	<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
		 <a href="#" onClick="openDefWindow('CreateDiscrepancyNote?subjectId=${studySubId}&name=subject&id=<c:out value="${subjectToUpdate.id}"/>&field=yearOfBirth&column=date_of_birth','spanAlert-yearOfBirth'); return false;">
		 <img name="flag_yearOfBirth" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
		</c:if>
		</span>
	  	</td>
	</tr>
	</c:otherwise>
    </c:choose>

</table>


 <input type="submit" name="Submit" value="<fmt:message key="confirm" bundle="${resword}"/>" class="button_medium">
 <input type="button" onclick="confirmCancel('ListSubject');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>
<br><br>
<jsp:include page="../include/footer.jsp"/>
</body>


