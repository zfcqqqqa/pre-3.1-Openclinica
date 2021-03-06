<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<h1><span class="title_extract">Create Dataset: Select Subject Attributes</span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>

<P><jsp:include page="../showMessage.jsp"/></P>

<p>Please select one CRF from the left side info panel, then select one or more items in a CRF that you would like to include to this dataset.
You may select all items in the study by going to the "View Selected Items" (hyperlink) page and clicking "Select All".
</p>
<p>You may also click Event Attributes/Subject Attributes to specify which event/subject attribute will be shown in the dataset.</p>


<form action="CreateDataset" method="post" name="cl">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="hidden" name="crfId" value="0">
<input type="hidden" name="subAttr" value="1">

 
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectDob}">
       <input type="checkbox" checked name="dob" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="dob" value="yes">
     </c:otherwise>
   </c:choose>
   
   <c:choose>
    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
     Date Of Birth
    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '3'}">
     Date Of Birth
    </c:when>
    <c:otherwise>
     Year of Birth
    </c:otherwise> 
   </c:choose>
   </p>
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectGender}">
       <input type="checkbox" checked name="gender" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="gender" value="yes">
     </c:otherwise>
   </c:choose>   
   Gender
   </p>
  
<table border="0" cellpadding="0" cellspacing="0" >
  <tr>
   <td><input type="submit" name="save" value="Save and Add More Items" class="button_xlong"/></td>
   <td><input type="submit" name="saveContinue" value="Save and Define Scope" class="button_xlong"/></td>
  </tr>
</table>
</form>

<jsp:include page="../include/footer.jsp"/>