<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="reswords"/>
 
 <tr id="sidebar_IconKey_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="icon_key" bundle="${reswords}"/></b><br clear="all"><br>

		<table border="0" cellpadding="4" cellspacing="0" width="100%">
			<tr>
				<td><img src="images/icon_DEcomplete.gif"></td>
				<td><fmt:message key="completed" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_InitialDE.gif"></td>
				<td><fmt:message key="started" bundle="${reswords}"/></td>
			</tr>
			<!--  see studyInfoPanel.java -->
			<c:if test="${showDDEIcon}">
			<tr>
				<td><img src="images/icon_DDE.gif"></td>
				<td><fmt:message key="double_data_entry_start" bundle="${reswords}"/></td>
			</tr>
			</c:if>
			<tr>
				<td><img src="images/icon_InitialDEcomplete.gif"></td>
				<td><fmt:message key="initial_data_entry_complete" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_NotStarted.gif"></td>
				<td><fmt:message key="not_started" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_Scheduled.gif"></td>
				<td><fmt:message key="scheduled" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_Stopped.gif"></td>
				<td><fmt:message key="stopped" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_Skipped.gif"></td>
				<td><fmt:message key="skipped" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_Locked.gif"></td>
				<td><fmt:message key="locked" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/icon_Invalid.gif"></td>
				<td><fmt:message key="invalid" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/bt_View.gif"></td>
				<td><fmt:message key="view" bundle="${reswords}"/></td>
			</tr>
			<tr>
				<td><img src="images/bt_Edit.gif"></td>
				<td><fmt:message key="edit" bundle="${reswords}"/></td>
			</tr>			
			<c:if test="${userRole.manageStudy}">
			 <tr>
				<td><img src="images/bt_Restore.gif"></td>
				<td><fmt:message key="restore" bundle="${reswords}"/></td>
			</tr>
			
			<tr>
				<td><img src="images/bt_Remove.gif"></td>
				<td><fmt:message key="remove" bundle="${reswords}"/></td>
			</tr>
			 <tr>
				<td><img src="images/bt_Reassign.gif"></td>
				<td><fmt:message key="reassign" bundle="${reswords}"/></td>
			</tr>
			
			</c:if>
		</table>

		<div class="sidebar_tab_content">

			<a href="#" onClick="openDefWindow('includes/allIcons.jsp'); return false;"><fmt:message key="view_all_icons" bundle="${reswords}"/></a>

		</div>

		</td>
	</tr>
	
	<tr id="sidebar_IconKey_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="icon_key" bundle="${reswords}"/></b>

		</td>
	</tr>
