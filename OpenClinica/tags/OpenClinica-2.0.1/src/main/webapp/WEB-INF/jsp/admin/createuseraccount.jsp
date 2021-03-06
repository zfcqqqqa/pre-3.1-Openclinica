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



<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='studies' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='roles' class='java.util.ArrayList'/>
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="userName" value="" />
<c:set var="firstName" value="" />
<c:set var="lastName" value="" />
<c:set var="email" value="" />
<c:set var="institutionalAffiliation" value="" />
<c:set var="activeStudyId" value="${0}" />
<c:set var="roleId" value="${0}" />
<c:set var="userTypeId" value="${2}" />
<c:set var="displayPwd" value="no" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "userName"}'>
		<c:set var="userName" value="${presetValue.value}" />
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
	<c:if test='${presetValue.key == "activeStudy"}'>
		<c:set var="activeStudyId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "role"}'>
		<c:set var="roleId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "type"}'>
		<c:set var="userTypeId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "displayPwd"}'>
		<c:set var="displayPwd" value="${presetValue.value}" />
	</c:if>
</c:forEach>



<h1><span class="title_Admin">Create a User Account</span></h1>

* indicates required field.
<form action="CreateUserAccount" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<%
java.lang.String fieldName;
java.lang.String fieldValue;
int selectedValue;
%>
<div style="width: 450px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
	<tr valign="top">
		<td class="formlabel">Username:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="userName" value="<c:out value="${userName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="userName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">First Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="firstName" value="<c:out value="${firstName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr valign="top">
		<td class="formlabel">Last Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
					<input type="text" name="lastName" value="<c:out value="${lastName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lastName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>


	<tr valign="top">
		<td class="formlabel">Email:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="email" value="<c:out value="${email}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>(username@institution) *</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	
	<tr valign="top">
		<td class="formlabel">Institutional Affiliation:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="institutionalAffiliation" value="<c:out value="${institutionalAffiliation}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="institutionalAffiliation" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
	  	<td class="formlabel">Active Study:</td>
<!-- EDIT !! -->
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<select name="activeStudy" class="formfieldXL">
							<option value="0">-Select-</option>
							<c:forEach var="study" items="${studies}">
								<c:choose>
									<c:when test="${activeStudyId == study.id}">
										<option value='<c:out value="${study.id}" />' selected><c:out value="${study.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${study.id}" />'><c:out value="${study.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="activeStudy" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
  
	<tr valign="top">
	  	<td class="formlabel">Role:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<select name="role" class="formfieldM">
							<option value="0">-Select-</option>
							<c:forEach var="currRole" items="${roles}">
								<c:choose>
									<c:when test="${roleId == currRole.id}">
										<option value='<c:out value="${currRole.id}" />' selected><c:out value="${currRole.description}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${currRole.id}" />'><c:out value="${currRole.description}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="role" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
	  	<td class="formlabel">User Type:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<select name="type" class="formfieldM">
						<c:forEach var="currType" items="${types}">
								<c:choose>
									<c:when test="${userTypeId == currType.id}">
										<option value='<c:out value="${currType.id}" />' selected><c:out value="${currType.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${currType.id}" />'><c:out value="${currType.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>			
					</div></td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
    <tr valign="top">
	  <td class="formlabel">User Password<br>(generated by system):</td>
	  	<td>
	  	<c:choose>
         <c:when test="${displayPwd == 'no'}">
            <input type="radio" checked name="displayPwd" value="no">Send User Password via Email
            <br><input type="radio" name="displayPwd" value="yes">Show User Password to Admin
         </c:when>
         <c:otherwise>
            <input type="radio" name="displayPwd" value="no">Send User Password via Email
            <br><input type="radio" checked name="displayPwd" value="yes">Show User Password to Admin
         </c:otherwise>
       </c:choose>
      </td>
	</tr>
</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>

<input type="submit" name="Submit" value="Submit" class="button_medium" /><br/>
</form>
<c:import url="../include/workflow.jsp">
 <c:param name="module" value="admin"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>
