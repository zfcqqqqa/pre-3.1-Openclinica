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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
   			
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='presetValues' class='java.util.HashMap' />

<c:set var="firstName" value="" />
<c:set var="lastName" value="" />
<c:set var="email" value="" />
<c:set var="institutionalAffiliation" value="" />
<c:set var="userTypeId" value="${0}" />
<c:set var="resetPassword" value="${0}" />
<c:set var="displayPwd" value="no" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "stepNum"}'>
		<c:set var="stepNum" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "userId"}'>
		<c:set var="userId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "firstName"}'>
		<c:set var="firstName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastName"}'>
		<c:set var="lastName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "email"}'>
		<c:set var="email" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "institutionalAffiliation"}'>
		<c:set var="institutionalAffiliation" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "userType"}'>
		<c:set var="userType" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "resetPassword"}'>
		<c:set var="resetPassword" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "displayPwd"}'>
		<c:set var="displayPwd" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "piiPrivilegeId"}'>
		<c:set var="piiPrivilegeId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "piiPrivilegeName"}'>
		<c:set var="piiPrivilegeName" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_Admin">Edit a User Account - Confirmation Screen</span></h1>

<form action="EditUserAccount" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<input type="hidden" name="userId" value='<c:out value="${userId}"/>'/>
<input type="hidden" name="stepNum" value='<c:out value="${stepNum}"/>'/>
<input type="hidden" name="firstName" value='<c:out value="${firstName}"/>'/>
<input type="hidden" name="lastName" value='<c:out value="${lastName}"/>'/>
<input type="hidden" name="email" value='<c:out value="${email}"/>'/>
<input type="hidden" name="institutionalAffiliation" value='<c:out value="${institutionalAffiliation}"/>'/>
<input type="hidden" name="userType" value='<c:out value="${userType}"/>'/>
<input type="hidden" name="resetPassword" value='<c:out value="${resetPassword}"/>'/>
<input type="hidden" name="displayPwd" value='<c:out value="${displayPwd}"/>'/>
<input type="hidden" name="piiPrivilegeId" value='<c:out value="${piiPrivilegeId}"/>'/>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
	  	<td class="table_header_column_top">First Name:</td>
	  	<td class="table_cell"><c:out value="${firstName}" /></td>
	</tr>

  <tr valign="bottom">
  	<td class="table_header_column">Last Name:</td>
  	<td class="table_cell"><c:out value="${lastName}" /></td>
  </tr>

  <tr valign="bottom">
  	<td class="table_header_column">Email:</td>
  	<td class="table_cell"><c:out value="${email}" /></td>
  </tr>

  <tr valign="bottom">
  	<td class="table_header_column">Institutional Affiliation:</td>
  	<td class="table_cell"><c:out value="${institutionalAffiliation}" /></td>
  </tr>

  <tr valign="bottom">
	<td class="table_header_column">User Type:</td>
	<td class="table_cell">
		<c:choose>
			<c:when test="${userType == 1}">
				Business Administrator
			</c:when>
			<c:when test="${userType == 3}">
				Technical Administrator
			</c:when>
			<c:otherwise>
				User
			</c:otherwise>
		</c:choose>
	</td>
  </tr>
  <tr valign="bottom">
	<td class="table_header_column">Pii Privilege:</td>
	<td class="table_cell">
	<c:out value="${piiPrivilegeName}"/>
	</td>
  </tr>

	<tr>
		<td class="table_header_column">Reset password?</td>
		<td class="table_cell">
			<c:choose>
				<c:when test="${resetPassword == 1}">
					yes, 
					<c:choose>
					  <c:when test="${displayPwd == 'no'}">
					   and send password to user via email
					  </c:when>
					  <c:otherwise>
					   and show password to system admin
					  </c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					no
				</c:otherwise>
			</c:choose>
	</tr>

	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>
<br>

<input type="submit" name="submit" value="Back" class="button">
<input type="submit" name="submit" value="Confirm" class="button">

</form>

<c:import url="../include/workflow.jsp">
 <c:param name="module" value="admin"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
