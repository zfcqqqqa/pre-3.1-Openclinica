<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:include page="../include/managestudy-header.jsp"/>


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

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListSubjectGroupClass";
       return true;
      } else {
        return false;
       }
     }
     return true;    
   
  }
   //-->
</script>
<h1><span class="title_manage"><fmt:message key="update_a_subject_group_class" bundle="${resword}"/></span></h1>

<form action="UpdateSubjectGroupClass" method="post">
<span class="indicates_required_field">*</span> <fmt:message key="indicates_required_field" bundle="${resword}"/><br>
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${group.id}"/>">
<jsp:include page="../include/showSubmitted.jsp" />
<!-- These DIVs define shaded box borders -->
<table cellpadding="0" border="0" class="shaded_table ">
   
  <tr valign="top"><td class="formlabel"><fmt:message key="name" bundle="${resword}"/>:</td><td><div class="formfieldXL_BG">
 <div> <input type="text" name="name" value="<c:out value="${group.name}"/>" class="formfieldXL">
 <span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr>
    
  <tr valign="top"><td class="formlabel"><fmt:message key="type" bundle="${resword}"/>:</td><td><div class="formfieldL_BG">
   <c:set var="groupClassTypeId1" value="${group.groupClassTypeId}"/>   
   <div><select name="groupClassTypeId" class="formfieldL">
      <option value="">--</option>
      <c:forEach var="type" items="${groupTypes}">    
       <c:choose>
        <c:when test="${groupClassTypeId1 == type.id}">   
         <option value="<c:out value="${type.id}"/>" selected><c:out value="${type.name}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${type.id}"/>"><c:out value="${type.name}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>
   </select><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="groupClassTypeId"/></jsp:include></td></tr>      
   
   
   <tr valign="top"><td class="formlabel"><fmt:message key="subject_assignment" bundle="${resword}"/>:</td><td>
  <div>
   <c:choose>
   <c:when test="${group.subjectAssignment =='Required'}">
    <input type="radio" checked name="subjectAssignment" value="Required"><fmt:message key="required" bundle="${resword}"/>
    <input type="radio" name="subjectAssignment" value="Optional"><fmt:message key="optional" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" name="subjectAssignment" value="Required"><fmt:message key="required" bundle="${resword}"/>
    <input type="radio" checked name="subjectAssignment" value="Optional"><fmt:message key="optional" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
<span class="indicates_required_field">*</span>  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectAssignment"/></jsp:include></td></tr>      
        
  
  <tr valign="top"><td class="formlabel"><fmt:message key="study_groups" bundle="${resword}"/>:</td>
  
  <td>
  <c:set var="count" value="0"/>
  <table border="0" cellpadding="0" cellspacing="0">
   <tr>      
       <td>&nbsp;</td>
       <td>&nbsp;<fmt:message key="name" bundle="${resword}"/></td>
       <td>&nbsp;<fmt:message key="description" bundle="${resword}"/></td>
    </tr>
   <c:forEach var="studyGroup" items="${studyGroups}">
   <input type="hidden" name="studyGroupId<c:out value="${count}"/>" value="<c:out value="${studyGroup.id}"/>">
   <tr> 
   <td valign="top"><c:out value="${count+1}"/>&nbsp;</td>  
    <td>
     <div class="formfieldL_BG"><input type="text" name="studyGroup<c:out value="${count}"/>" value="<c:out value="${studyGroup.name}"/>" class="formfieldL"></div>
    </td>  
     
    <td> 
     <div class="formfieldXL_BG"><input type="text" name="studyGroupDescription<c:out value="${count}"/>" value="<c:out value="${studyGroup.description}"/>" class="formfieldL"></div>
    </td>
   </tr> 
   <c:set var="count" value="${count+1}"/>
   </c:forEach>
   <c:if test="${count < 9}">
    <c:forEach begin="${count}" end="9">
     <input type="hidden" name="studyGroupId<c:out value="${count}"/>" value="0">
      <tr>     
       <td valign="top"><c:out value="${count+1}"/>&nbsp;</td>  
       <td>
        <div class="formfieldL_BG"><input type="text" name="studyGroup<c:out value="${count}"/>" value="" class="formfieldL"></div>
       </td>       
       <td> 
       <div class="formfieldXL_BG"><input type="text" name="studyGroupDescription<c:out value="${count}"/>" value="<c:out value="${studyGroup.description}"/>" class="formfieldL"></div>
       </td>
      </tr>
       <c:set var="count" value="${count+1}"/>
    </c:forEach>
   </c:if> 
   <br>    
   </table> 
   <span class="alert"><c:out value="${studyGroupError}"/></span>
  </td></tr>  
   
  
 
</table>

<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<input type="submit" name="Submit" value="<fmt:message key="confirm_subject_group_class" bundle="${resword}"/>" class="button_long">
</td>
<td>
<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td>
</tr>
</table>
</form>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->


<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
