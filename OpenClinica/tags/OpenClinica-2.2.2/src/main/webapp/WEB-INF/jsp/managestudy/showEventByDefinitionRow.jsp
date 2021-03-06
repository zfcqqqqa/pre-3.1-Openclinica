<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

	
<c:set var="count" value="${param.eblRowCount}" />
<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.StudyEventRow" />   

   <input type="hidden" name="id<c:out value="${count}"/>" value="<c:out value="${currRow.bean.id}"/>">  
  
   <c:choose>
    <c:when test="${currRow.bean.scheduledDatePast}">
      <tr valign="top" bgcolor="#FFFF80">  
    </c:when>
    <c:otherwise>
    <tr valign="top">  
   </c:otherwise>   
   </c:choose>    
      <td class="table_cell_left"><c:out value="${currRow.bean.studySubjectLabel}"/></td>

      <td class="table_cell">
      <c:choose>
      <c:when test="${currRow.bean.startTimeFlag}"><fmt:formatDate value="${currRow.bean.dateStarted}" dateStyle="short"/>
      </c:when>
      <c:otherwise><fmt:formatDate value="${currRow.bean.dateStarted}" dateStyle="short"/></c:otherwise>
      </c:choose>
      </td>

      <td class="table_cell"><c:out value="${currRow.bean.subjectEventStatus.name}"/></td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td>
        <a href="EnterDataForStudyEvent?eventId=<c:out value="${currRow.bean.id}"/>"
		onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
		onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
		
		</td>
		<c:if test="${userBean.sysAdmin || userRole.manageStudy || (currRow.bean.owner.id == userBean.id)}">		
		<td>
        <a href="UpdateStudyEvent?module=<c:out value="${module}"/>&event_id=<c:out value="${currRow.bean.id}"/>&ss_id=<c:out value="${currRow.bean.studySubjectId}"/>"
		onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
		onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
		name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="<fmt:message key="edit" bundle="${resword}"/>" title="<fmt:message key="edit" bundle="${resword}"/>" align="left" hspace="6"></a>
		</c:if>
		</td>
		</tr>
		</table>
      </td>          
    <c:set var="count" value="${count+1}"/>
   </tr>
