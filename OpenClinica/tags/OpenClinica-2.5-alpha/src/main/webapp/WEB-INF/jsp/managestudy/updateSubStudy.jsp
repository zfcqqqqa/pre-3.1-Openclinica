<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="facRecruitStatusMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<jsp:useBean scope="session" id="parentName" class="java.lang.String"/>
<c:set var="startDate" value="" />
<c:set var="endDate" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>	
</c:forEach>
<h1><span class="title_manage">
<fmt:message key="update_site_details" bundle="${resword}"/>
</span></h1>

<jsp:include page="../include/alertbox.jsp" />
<form action="UpdateSubStudy" method="post">
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
  <tr valign="top"><td class="formlabel"><fmt:message key="parent_study" bundle="${resword}"/>:</td><td>
  <c:out value="${parentName}"/>
 </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="site_name" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${newStudy.name}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td><td class="formlabel">*</td></tr>
   
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;"><b><fmt:message key="unique_protocol_ID" bundle="${resword}"/></b>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="uniqueProId" value="<c:out value="${newStudy.identifier}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td><td class="formlabel">*</td></tr>
  
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds'); return false;"><b><fmt:message key="secondary_IDs" bundle="${resword}"/></b>:</a><br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>)</td>
  <td><div class="formtextareaXL4_BG">
   <textarea class="formtextareaXL4" name="secondProId" rows="4" cols="50"><c:out value="${newStudy.secondaryIdentifier}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="secondProId" value="facName"/></jsp:include>
  </td></tr>  
    
   
  <tr valign="top"><td class="formlabel"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="prinInvestigator" value="<c:out value="${newStudy.principalInvestigator}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include></td><td class="formlabel">*</td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td>
  <div class="formtextareaXL4_BG"><textarea class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${newStudy.summary}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td><td class="formlabel">*</td></tr>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
    <%=org.akaza.openclinica.i18n.util.HtmlUtils.getCalendarPopupCode("cal1","testdiv1")%>
  </SCRIPT>
  <tr valign="top"><td class="formlabel"><fmt:message key="start_date" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="startDate" value="<c:out value="${startDate}" />"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include></td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].startDate,'anchor1','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" TITLE="cal1.select(document.forms[0].startDate,'anchor1','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" /></a>
  <%--(<fmt:message key="date_format" bundle="${resformat}"/>)--%>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="endDate" value="<c:out value="${endDate}" />"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include></td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].endDate,'anchor2','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" TITLE="cal1.select(document.forms[0].endDate,'anchor2','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" NAME="anchor2" ID="anchor2"><img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" /></a>
  <%--(<fmt:message key="date_format" bundle="${resformat}"/>)--%>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="expected_total_enrollment" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="expectedTotalEnrollment" value="<c:out value="${newStudy.expectedTotalEnrollment}"/>" class="formfieldXL"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="expectedTotalEnrollment"/></jsp:include>
  </td><td class="formlabel">*</td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_name" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facName" value="<c:out value="${newStudy.facilityName}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facName"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_city" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facCity" value="<c:out value="${newStudy.facilityCity}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCity"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_state_province" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facState" value="<c:out value="${newStudy.facilityState}"/>"  class="formfieldXL">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facState"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_ZIP" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facZip" value="<c:out value="${newStudy.facilityZip}"/>"  class="formfieldXL">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facZip"/></jsp:include>
  </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_country" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facCountry" value="<c:out value="${newStudy.facilityCountry}"/>"  class="formfieldXL">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCountry"/></jsp:include>
  </td></tr> 
  
 <!-- <tr valign="top"><td class="formlabel"><fmt:message key="facility_recruitment_status" bundle="${resword}"/>:</td><td>
  <c:set var="facStatus" value="${newStudy.facilityRecruitmentStatus}"/> 
  <select name="facRecStatus">
    <option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
   <c:forEach var="recStatus" items="${facRecruitStatusMap}">    
       <c:choose>
        <c:when test="${facStatus == recStatus.key}">   
         <option value="<c:out value="${recStatus.key}"/>" selected><c:out value="${recStatus.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${recStatus.key}"/>"><c:out value="${recStatus.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select>
  
  </div></td></tr>  
  -->
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_name" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facConName" value="<c:out value="${newStudy.facilityContactName}"/>"  class="formfieldXL">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConName"/></jsp:include>
  </td></tr>   
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_degree" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facConDegree" value="<c:out value="${newStudy.facilityContactDegree}"/>"  class="formfieldXL">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConDegree"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_phone" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facConPhone" value="<c:out value="${newStudy.facilityContactPhone}"/>"  class="formfieldXL">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConPhone"/></jsp:include>
  </td></tr>       
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_email" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="facConEmail" value="<c:out value="${newStudy.facilityContactEmail}"/>"  class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConEmail"/></jsp:include></td></tr>  
  
  <tr valign="top"><td class="formlabel"><fmt:message key="study_system_status" bundle="${resword}"/>:</td><td>
   <%--
   <c:set var="status1" value="${newStudy.status.id}"/>   
   <div class="formfieldXL_BG"><select name="statusId" class="formfieldXL">
      <c:forEach var="status" items="${statuses}">    
       <c:choose>
        <c:when test="${status1 == status.id}">   
         <option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>
   </select></div>
   --%>
   <div class="formfieldXL_BG">
   <input type="text" name="statusName" value="Available" class="formfieldL" disabled> 
   <input type="hidden" name="statusId" value="1">
   </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="statusId"/></jsp:include></td><td class="formlabel">*</td></tr>   
 
 
  <c:forEach var="config" items="${newStudy.studyParameters}">   
   <c:choose>
   <c:when test="${config.parameter.handle=='collectDOB'}">
     <tr valign="top"><td class="formlabel"><fmt:message key="collect_subject_date_of_birth" bundle="${resword}"/>:</td><td>
       <c:choose>
         <c:when test="${config.value.value == '1'}">
           <input type="radio" checked name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
           <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
           <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:when>
         <c:when test="${config.value.value == '2'}">
            <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
            <input type="radio" checked name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
            <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:when>
         <c:otherwise>
            <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
            <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
            <input type="radio" checked name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:otherwise>
      </c:choose>  
      </td></tr>
   
   </c:when>
    
   <c:when test="${config.parameter.handle=='discrepancyManagement'}">
		  <tr valign="top"><td class="formlabel"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'false'}">
		    <input type="radio" name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	
	<c:when test="${config.parameter.handle=='genderRequired'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="gender_required" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'false'}">
		    <input type="radio" name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>	  
    <c:when test="${config.parameter.handle=='subjectPersonIdRequired'}">		
		  <tr valign="top"><td class="formlabel"><fmt:message key="subject_person_ID_required" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'required'}">
		    <input type="radio" checked name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
		   </c:when>
		    <c:when test="${config.value.value == 'optional'}">
		    <input type="radio" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		    <input type="radio" checked name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		    <input type="radio" checked name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdGeneration'}">	  
		   <tr valign="top"><td class="formlabel"><fmt:message key="how_to_generate_the_subject" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'manual'}">
		    <input type="radio" checked name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:when>
		    <c:when test="${config.value.value == 'auto editable'}">
		    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdPrefixSuffix'}">	  
		   <tr valign="top"><td class="formlabel"><fmt:message key="generate_study_subject_ID_automatically" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'true'}">
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="subjectIdPrefixSuffix" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="subjectIdPrefixSuffix" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameRequired'}">
		   <tr valign="top"><td class="formlabel"><fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewerNameRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewerNameRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewerNameRequired" value="false"><fmt:message key="no" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameDefault'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    <input type="radio" checked name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
		    <input type="radio" name="interviewerNameDefault" value="pre-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
		    <input type="radio" checked name="interviewerNameDefault" value="re-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameEditable'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_name_editable" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewDateRequired'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_required" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewDateRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewDateRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewDateRequired" value="false"><fmt:message key="no" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
    </c:when>		  
	<c:when test="${config.parameter.handle=='interviewDateDefault'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_default_as_blank" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    <input type="radio" checked name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
		    <input type="radio" name="interviewDateDefault" value="pre-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
		    <input type="radio" checked name="interviewDateDefault" value="re-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	 </c:when>
	 <c:otherwise>	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_editable" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
     </c:otherwise>
   </c:choose>
  </c:forEach>

</table>
</div>
  </div></div></div></div></div></div></div></div>

  </div>   
         
  <input type="submit" name="Submit" value="<fmt:message key="confirm_site" bundle="${resword}"/>" class="button_long">

</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<jsp:include page="../include/footer.jsp"/>
