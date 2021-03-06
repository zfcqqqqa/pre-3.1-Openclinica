<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />

<jsp:useBean scope='request' id='sites' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='seds' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='users' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='subs' class='java.util.ArrayList' />
<jsp:useBean id="audits" scope="request" class="java.util.ArrayList" />

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<div id="sidebar_Instructions_open" class="sidebar_tab" style="display: all">
		

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">

		   <fmt:message key="director_coordinator_privileges_manage" bundle="${restext}"/><br><br>

           <fmt:message key="side_tables_shows_last_modified" bundle="${restext}"/>
			
		</div>

		</div>
	<div id="sidebar_Instructions_closed" class="sidebar_tab" style="display: none">
		

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</div>
<jsp:include page="../include/sideInfo.jsp"/>


<h1 style="margin-left: 185px;"><span class="title_manage"><fmt:message key="manage_study" bundle="${resworkflow}"/> <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/study-setup')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>
<div style="padding-left: 185px; float: left;">
<%-- 3057 removed this: <span style="font-size:12px"><fmt:message key="select_to_manage" bundle="${restext}"/></span>--%>
<h2><fmt:message key="recent_activity" bundle="${restext}"/> ${studyIdentifier}</h2>


	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage"><fmt:message key="subjects" bundle="${resworkflow}"/>
	<c:choose>
	  <c:when test="${subsCount>0}">
	   (<c:out value="${subsCount}"/> <fmt:message key="of" bundle="${restext}"/> <c:out value="${allSubsCount}"/> <fmt:message key="shown" bundle="${restext}"/>)
	  </c:when>
	  <c:otherwise>
	   <fmt:message key="currently_no_subjects" bundle="${restext}"/>
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
	<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
					<tr valign="top">
						<th><fmt:message key="study_subject_ID" bundle="${resword}"/></th>
						<th><fmt:message key="date_updated" bundle="${resword}"/></th> 
						<th><fmt:message key="status" bundle="${resword}"/></th> 
					</tr>
					<c:forEach var="sub" items="${subs}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${sub.label}"/></td> 
						<td class="table_cell">
						<c:choose>
						<c:when test="${sub.updatedDate != null}">
						 <fmt:formatDate value="${sub.updatedDate}" pattern="${dteFormat}"/>
						 </c:when>
						 <c:otherwise>
						  <fmt:formatDate value="${sub.createdDate}" pattern="${dteFormat}"/>
						 </c:otherwise>
						</c:choose>
						</td>
						<td class="table_cell"><c:out value="${sub.status.name}"/></td>
					</tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3">
					   <c:if test="${subsCount>0}">
					    <a href="ListStudySubjects"><fmt:message key="show_all" bundle="${resword}"/></a>
					   </c:if>
					     <c:if test="${study.status.available}">
                             | <a href="AddNewSubject"><fmt:message key="add_new" bundle="${resword}"/></a>
                         </c:if>   

                     </td>
					</tr>					
				</table>

			

			</td>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage"><fmt:message key="users" bundle="${resword}"/>
	<c:choose>
	  <c:when test="${usersCount>0}">
	   (<c:out value="${usersCount}"/> <fmt:message key="of" bundle="${restext}"/> <c:out value="${allUsersCount}"/> <fmt:message key="shown" bundle="${restext}"/>)
	  </c:when>
	  <c:otherwise>
	   <fmt:message key="currently_no_users" bundle="${restext}"/>
	  </c:otherwise>
	</c:choose></div>

	<!-- These DIVs define shaded box borders -->
		<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
		
						<tr valign="top">
						<th><fmt:message key="user_name" bundle="${resword}"/></th>
						<th><fmt:message key="role" bundle="${resword}"/></th> 
						<th><fmt:message key="status" bundle="${resword}"/></th> 
					</tr>
					<c:forEach var="user" items="${users}">
					 <tr valign="top">   
						<td class="table_cell_left"><c:out value="${user.userName}"/></td> 
						<td class="table_cell"><c:out value="${user.role.description}"/></td>
						<td class="table_cell"><c:out value="${user.status.name}"/></td>
					 </tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3"> <c:if test="${usersCount>0}"><a href="ListStudyUser"><fmt:message key="show_all" bundle="${resword}"/></a> | </c:if><a href="AssignUserToStudy"><fmt:message key="add_new" bundle="${resword}"/></a> </td>
					</tr>					
				</table>

		

	</td>
		</tr>
		</table><br>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage"><fmt:message key="sites" bundle="${resword}"/>
	<c:choose>
	  <c:when test="${sitesCount>0}">
	   (<c:out value="${sitesCount}"/> <fmt:message key="of" bundle="${restext}"/> <c:out value="${allSitesCount}"/> <fmt:message key="shown" bundle="${restext}"/>)
	  </c:when>
	  <c:otherwise>
	   <fmt:message key="currently_no_sites" bundle="${restext}"/>
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
		<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
					<tr valign="top">
						<th><fmt:message key="name" bundle="${resword}"/></th>
						<th><fmt:message key="date_updated" bundle="${resword}"/></th> 
						<th><fmt:message key="status" bundle="${resword}"/></th> 
					</tr>
					<c:choose>
					 <c:when test="${study.parentStudyId>0}">
					  <tr valign="top"><td class="table_cell" colspan="3"><fmt:message key="site_itself_cannot_have_sites" bundle="${restext}"/></td></tr>
					  
					 </c:when>
					<c:otherwise>
					
					<c:forEach var="site" items="${sites}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${site.name}"/></td> 
						<td class="table_cell">
						<c:choose>
						 <c:when test="${site.updatedDate != null}">
						  <fmt:formatDate value="${site.updatedDate}" pattern="${dteFormat}"/>
						 </c:when>
						 <c:otherwise>
						   <fmt:formatDate value="${site.createdDate}" pattern="${dteFormat}"/>
						 </c:otherwise>
						</c:choose> 
						</td>
						<td class="table_cell"><c:out value="${site.status.name}"/></td>
					</tr>	
					
					</c:forEach>
					</c:otherwise>
					</c:choose>
					 <c:if test="${study.parentStudyId==0}">
					  <tr valign="top">
					   <td class="table_cell" align="right" colspan="3">
					    <c:if test="${sitesCount>0}">
					     <a href="ListSite"><fmt:message key="show_all" bundle="${resword}"/></a>
					    </c:if>
                        <c:if test="${!study.status.locked}">
                         | <a href="CreateSubStudy"><fmt:message key="add_new" bundle="${resword}"/></a> </td>
                        </c:if>    
                      </tr>
					</c:if>				
				</table>

		
			</td>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage"><fmt:message key="study_event_definitions" bundle="${resworkflow}"/>
	<c:choose>
	  <c:when test="${sedsCount>0}">
	   (<c:out value="${sedsCount}"/> <fmt:message key="of" bundle="${restext}"/> <c:out value="${allSedsCount}"/> <fmt:message key="shown" bundle="${restext}"/>)
	  </c:when>
	  <c:otherwise>	
	   <fmt:message key="currently_no_definitions" bundle="${restext}"/>	  
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
	<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
					<tr valign="top">
						<th><fmt:message key="name" bundle="${resword}"/></th>
						<th><fmt:message key="date_updated" bundle="${resword}"/></th> 
						<th><fmt:message key="status" bundle="${resword}"/></th> 
					</tr>
					<c:choose>
					  <c:when test="${study.parentStudyId>0}">
					    <tr valign="top"><td class="table_cell" colspan="3"><fmt:message key="site_itself_cannot_have_definitions" bundle="${restext}"/></td></tr>
					  </c:when>
					  <c:otherwise>
					   <c:forEach var="def" items="${seds}">
					     <tr valign="top">   
						  <td class="table_cell_left"><c:out value="${def.name}"/></td> 
						  <td class="table_cell">
						   <c:choose>
						   <c:when test="${def.updatedDate != null}">
						    <fmt:formatDate value="${def.updatedDate}" pattern="${dteFormat}"/>
						   </c:when>
						   <c:otherwise>
						     <fmt:formatDate value="${def.createdDate}" pattern="${dteFormat}"/>
						   </c:otherwise>
						  </c:choose> 
						  </td>
						  <td class="table_cell"><c:out value="${def.status.name}"/></td>
					    </tr>
					   </c:forEach>
					  </c:otherwise>
					</c:choose>
					
					 <c:if test="${study.parentStudyId==0}">
					   <tr valign="top">
					    <td class="table_cell" align="right" colspan="3">
					     <c:if test="${sedsCount>0}">					  
					       <a href="ListEventDefinition"><fmt:message key="show_all" bundle="${resword}"/></a>
					      </c:if>
                          <c:if test="${!study.status.locked}">
                            | <a href="DefineStudyEvent"><fmt:message key="add_new" bundle="${resword}"/></a>
                          </c:if>
                        </td>
					  </tr>
					 </c:if>
					
				</table>

		

	</td>
		</tr>
		
	</table>





<!-- End Main Content Area -->

</div>
<br><br>
<jsp:include page="../include/footer.jsp"/>
