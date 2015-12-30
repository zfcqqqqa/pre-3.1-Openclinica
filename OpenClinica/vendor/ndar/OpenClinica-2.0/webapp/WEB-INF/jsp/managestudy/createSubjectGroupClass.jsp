<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
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

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>

<h1><span class="title_manage">Create a Subject Group Class <a href="javascript:openDocWindow('help/4_7_subjectGroups_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="Help" title="Help"></a></span></h1>

<form action="CreateSubjectGroupClass" method="post">
* indicates required field.<br>
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
   
  <tr valign="top"><td class="formlabel">Name:</td><td><div class="formfieldXL_BG">
  <input type="text" name="name" value="<c:out value="${group.name}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td><td>*</td></tr>
    
  <tr valign="top"><td class="formlabel">Type:</td><td><div class="formfieldL_BG">
   <c:set var="groupClassTypeId1" value="${group.groupClassTypeId}"/>   
   <select name="groupClassTypeId" class="formfieldL">
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
   </select></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="groupClassTypeId"/></jsp:include></td><td>*</td></tr>      
   
   
   <tr valign="top"><td class="formlabel">Subject Assignment:</td><td>
  
   <c:choose>
   <c:when test="${group.subjectAssignment =='Required'}">
    <input type="radio" checked name="subjectAssignment" value="Required">Required
    <input type="radio" name="subjectAssignment" value="Optional">Optional
   </c:when>
   <c:otherwise>
    <input type="radio" name="subjectAssignment" value="Required">Required
    <input type="radio" checked name="subjectAssignment" value="Optional">Optional
   </c:otherwise>
  </c:choose>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectAssignment"/></jsp:include></td><td>*</td></tr>      
        
  
  <tr valign="top"><td class="formlabel">Study Groups:</td>
  
  <td>
  <c:set var="count" value="0"/>
  <table border="0" cellpadding="0" cellspacing="0">  
   <tr>      
       <td>&nbsp;</td>
       <td>&nbsp;Name</td>
       <td>&nbsp;Description</td>
    </tr>   
   <c:forEach var="studyGroup" items="${studyGroups}">
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
</div>
</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="Confirm Subject Group Class" class="button_long">
</form>
<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>
