<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
   
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise> 
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='crf' class='org.akaza.openclinica.bean.admin.CRFBean'/>

<h1><c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
<span class="title_Admin">
</c:when>
<c:otherwise>
<span class="title_Manage">
</c:otherwise>
</c:choose><fmt:message key="view_CRF_details" bundle="${resword}"/></span></h1>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${crf.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${crf.description}"/>
  </td></tr> 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
<span class="table_title_Admin">
</c:when>
<c:otherwise>
<span class="table_title_Manage">
</c:otherwise>
</c:choose>
<fmt:message key="versions" bundle="${resword}"/></span>
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_row_left"><fmt:message key="version_name" bundle="${resword}"/></td>              
    <td class="table_header_row"><fmt:message key="description" bundle="${resword}"/></td>    
    <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>    
    <td class="table_header_row"><fmt:message key="revision_notes" bundle="${resword}"/></td>   
    <td class="table_header_row"><fmt:message key="action" bundle="${resword}"/></td>      
    </tr>     
  <c:forEach var ="version" items="${crf.versions}">   
    <tr valign="top">
    <td class="table_cell_left"><c:out value="${version.name}"/></td>              
    <td class="table_cell"><c:out value="${version.description}"/></td>    
    <td class="table_cell"><c:out value="${version.status.name}"/></td>    
    <td class="table_cell"><c:out value="${version.revisionNotes}"/></td>
    <td class="table_cell">
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <td>
     <!--
      <a href="ViewTableOfContent?crfVersionId=<c:out value="${version.id}"/>"
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
      -->
      <a href="ViewSectionDataEntry?module=<c:out value="${module}"/>&crfId=<c:out value="${crf.id}"/>&crfVersionId=<c:out value="${version.id}"/>&tabId=1"
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
      </td>
      <td>
      <a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${version.id}"/>')"
	  onMouseDown="javascript:setImage('bt_Print1','images/bt_Print_d.gif');"
	  onMouseUp="javascript:setImage('bt_Print1','images/bt_Print.gif');"><img 
	  name="bt_Print1" src="images/bt_Print.gif" border="0" alt="Print" title="Print" align="left" hspace="6"></a>
      
    </td>  
    <td>
      <a href="ViewCRFVersion?id=<c:out value="${version.id}"/>"><img 
	  name="bt_Metadata" src="images/bt_Metadata.gif" border="0" alt="Metadata" title="Metadata" align="left" hspace="6"></a>
      
    </td>         
    </tr>  
    </table>
 </c:forEach>
    
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>    
<p><a href="ListCRF?module=<c:out value="${module}"/>"><fmt:message key="go_back_to_CRF_list" bundle="${resword}"/></a></p>

<c:choose>
  <c:when test="${userBean.sysAdmin && module=='admin'}">
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/> 
  </c:import>
 </c:when>
  <c:otherwise>
   <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
  </c:import>
  </c:otherwise> 
 </c:choose> 
<jsp:include page="../include/footer.jsp"/>
