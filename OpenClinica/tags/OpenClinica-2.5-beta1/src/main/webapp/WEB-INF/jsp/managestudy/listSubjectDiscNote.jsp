<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<%-- <jsp:include page="../include/submit-header.jsp"/> --%>
<c:choose>
    <c:when test="${module eq 'manage'}">
        <jsp:include page="../include/managestudy-header.jsp"/>
    </c:when>
    <c:otherwise><jsp:include page="../include/submit-header.jsp"/>
    </c:otherwise>
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">

            <fmt:message key="select_subject_view_more_details" bundle="${restext}"/>

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<!-- the object inside the array is StudySubjectBean-->
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>



<h1>
    <c:choose>
        <c:when test="${module eq 'manage'}"><span class="title_manage"></c:when>
        <c:otherwise><span class="title_Submit"></c:otherwise>
        </c:choose>
<fmt:message key="manage_all_discrepancy_notes_in" bundle="${restext}"/> <c:out value="${study.name}"/> <a href="javascript:openDocWindow('help/4_2_subjects_Help.html')"><c:choose><c:when test="${module eq 'manage'}"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></c:when><c:otherwise><img src="images/bt_Help_Submit.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></c:otherwise></c:choose></a>
</span></h1>
<!--Message about [] repeating events symbol; the key to filtering the flag icons and Disc Note types -->
<div class="dnKey"><strong><fmt:message key="Filter_by_status" bundle="${resword}"/>
    :</strong>

    <a href="ListDiscNotesSubjectServlet?module=${module}&type=${param.type}" <c:if test="${param.type == 50}">style="color:green"</c:if>>All Notes</a>&nbsp;

    <a href="ListDiscNotesSubjectServlet?module=${module}&resolutionStatus=1&type=${param.type}"><img
      name="icon_Note" src="images/icon_Note.gif" border="0"
      alt="<fmt:message key="Open" bundle="${resterm}"/>" title="<fmt:message key="Open" bundle="${resterm}"/>"/></a> (<fmt:message key="Open" bundle="${resterm}"/>)&nbsp;

    <a href="ListDiscNotesSubjectServlet?module=${module}&resolutionStatus=2&type=${param.type}"><img
      name="icon_flagYellow" src="images/icon_flagYellow.gif" border="0"
      alt="<fmt:message key="Updated" bundle="${resterm}"/>" title="<fmt:message key="Updated" bundle="${resterm}"/>"/></a> (<fmt:message key="Updated" bundle="${resterm}"/>)&nbsp;

    <a href="ListDiscNotesSubjectServlet?module=${module}&resolutionStatus=3&type=${param.type}"><img
      name="icon_flagGreen" src="images/icon_flagGreen.gif" border="0"
      alt="<fmt:message key="Resolved" bundle="${resterm}"/>" title="<fmt:message key="Resolved" bundle="${resterm}"/>"/></a> (<fmt:message key="Resolved" bundle="${resterm}"/>)&nbsp;

    <a href="ListDiscNotesSubjectServlet?module=${module}&resolutionStatus=4&type=${param.type}"><img
      name="icon_flagBlack" src="images/icon_flagBlack.gif" border="0"
      alt="<fmt:message key="Closed" bundle="${resterm}"/>" title="<fmt:message key="Closed" bundle="${resterm}"/>"/></a> (<fmt:message key="Closed" bundle="${resterm}"/>)&nbsp;

    &nbsp;<strong>[#] = <fmt:message key="Repeated_events" bundle="${resword}"/></strong>
</div>

<div class="dnKey"><strong><fmt:message key="Filter_by_note_type" bundle="${resword}"/>
    :</strong>
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=50" <c:if test="${param.type == 50}">style="color:green"</c:if>><fmt:message key="all_notes" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=4&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 4}">style="color:green"</c:if>><fmt:message key="Annotation" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=1&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 1}">style="color:green"</c:if>><fmt:message key="Failed_Validation_Check" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=2&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 2}">style="color:green"</c:if>><fmt:message key="Incomplete" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=6&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 6}">style="color:green"</c:if>><fmt:message key="query" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=7&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 7}">style="color:green"</c:if>><fmt:message key="reason_for_change" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=3&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 3}">style="color:green"</c:if>><fmt:message key="Unclear/Unreadable" bundle="${resterm}"/></a>&nbsp;|&nbsp;
    <a href="ListDiscNotesSubjectServlet?module=${module}&defId=${eventDefinitionId}&type=5&resolutionStatus=${param.resolutionStatus}" <c:if test="${param.type == 5}">style="color:green"</c:if>><fmt:message key="Other" bundle="${resterm}"/></a>

</div>

<!--<p>List of all subjects with their enrollment dates and status of study events. Select any subject for details on his/her subject record and study events or assign or reassign him/her to a different study/site.</p>

<p>The list of subjects in the current study/site is shown below.</p>

<div class="homebox_bullets"><a href="AddNewSubject">Enroll a New Subject</a></div>
<p></p>
-->
<!---study event definition tabs -->
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td style="padding-left: 12px" valign="bottom">
            <div id="Tab0NotSelected" style="display:none">
                <div class="tab_BG"><div class="tab_L"><div class="tab_R">

                    <a class="tabtext" href="ListDiscNotesSubjectServlet?module=${module}" onclick="javascript:HighlightTab(0);"><fmt:message key="all_events" bundle="${restext}"/></a>

                </div></div></div>
            </div>
            <div id="Tab0Selected">
                <div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">

                    <span class="tabtext"><fmt:message key="all_events" bundle="${restext}"/></span>

                </div></div></div>
            </div>
        </td>
        <td align="right" style="padding-left: 12px; display: none" id="TabsBack"><a href="javascript:TabsBack()"><img src="images/arrow_back.gif" border="0"></a></td>
        <td align="right" style="padding-left: 12px; display: all" id="TabsBackDis"><img src="images/arrow_back_dis.gif" border="0"></td>


        <script language="JavaScript">
            <!--

            // Total number of tabs (one for each CRF)
            var TabsNumber = <c:out value="${allDefsNumber}"/>;

            // Number of tabs to display at a time
            var TabsShown = 3;

            // Labels to display on each tab (name of CRF)
            var TabLabel = new Array(TabsNumber)
            var TabFullName = new Array(TabsNumber)
            var TabDefID = new Array(TabsNumber)
            <c:set var="count" value="0"/>
            <c:forEach var="def" items="${allDefsArray}">
            TabFullName[<c:out value="${count}"/>]= "<c:out value="${def.name}"/>";

            TabLabel[<c:out value="${count}"/>]= "<c:out value="${def.name}"/>";
            if (TabLabel[<c:out value="${count}"/>].length>12)
            {
                var shortName = TabLabel[<c:out value="${count}"/>].substring(0,11);
                TabLabel[<c:out value="${count}"/>]= shortName + '...';
            }
            TabDefID[<c:out value="${count}"/>]= "<c:out value="${def.id}"/>";
            <c:set var="count" value="${count+1}"/>
            </c:forEach>

            DisplayEventTabs()

            function DisplayEventTabs()
            {
                TabID=1;

                while (TabID<=TabsNumber)

                {
                    defID = TabDefID[TabID-1];
                    url = "ListDiscNotesForCRFServlet?module=${module}&defId=" + defID + "&tab=" + TabID;
                    if (TabID<=TabsShown)
                    {
                        document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: all">');
                    }
                    else
                    {
                        document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: none">');
                    }
                    document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
                    document.write('<a class="tabtext" title="' + TabFullName[(TabID-1)] + '" href=' + url + ' onclick="javascript:HighlightTab(' + TabID + ');">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
                    document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
                    document.write('</td>');

                    TabID++

                }
            }


            //-->
        </script>

        <td align="right"id="TabsNextDis" style="display: none"><img src="images/arrow_next_dis.gif" border="0"></td>
        <td align="right"id="TabsNext"><a href="javascript:TabsForward()"><img src="images/arrow_next.gif" border="0"></a></td>

    </tr>
</table>

<script language="JavaScript">
    <!--

    function showSubjectRow(strLeftNavRowElementName, groupNum, subjectRowID1,subjectRowID2 ){

        var objLeftNavRowElement;

        objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
        if (objLeftNavRowElement != null) {
            if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
            if (objLeftNavRowElement.display == "none" ){
                HideGroups(0,groupNum,10);
            }
        }
        leftnavExpand(subjectRowID1);
        leftnavExpand(subjectRowID2);
    }

    //-->
</script>

<%-- added 11-2007 per rjenkins' request, tbh --%>

<!-- Invisible div to block other icons when menus are expanded -->

<script language="JavaScript">

    <!--

    document.write('<div id="Lock_all" style="position: absolute; visibility: hidden; z-index: 2; width: ' + (document.body.clientWidth - 180) + 'px; height: ' + (document.body.clientHeight - 271) + 'px; top: 243px; left: 180px;">');

    document.write('<img src="images/spacer.gif" style="width:' + (document.body.clientWidth - 180) + 'px; height:' + (document.body.clientHeight - 271) + 'px;" border="0">');

    document.write('</div>');

    //-->

</script>

<c:import url="../include/showTableWithTabForDNotes.jsp">
    <c:param name="rowURL" value="showSubjectDiscNote.jsp" />
    <c:param name="groupNum" value="${groupSize}"/>
    <c:param name="resolutionStatus" value="${resolutionStatus}"/>
    <c:param name="discNoteType" value="${discrepancyNoteType}"/>
    <c:param name="module" value="${module}"/>
    <c:param name="suppressAddSubject" value="true"/>
    <c:param name="studyHasDiscNotes" value="${studyHasDiscNotes}"/>
</c:import>

<c:import url="../submit/addNewSubjectExpress.jsp">
</c:import>

<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
    <tr>
        <td id="sidebar_Workflow_closed" style="display: none">
            <a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
        </td>
        <td id="sidebar_Workflow_open" style="display: all">
            <table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
                <tr>
                    <td class="workflowBox_T" valign="top">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <td class="workflow_tab">
                                    <a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

                                    <b><fmt:message key="workflow" bundle="${restext}"/></b>

                                </td>
                            </tr>
                        </table>
                    </td>
                    <td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
                </tr>
                <tr>
                    <td colspan="2" class="workflowbox_B">
                        <div class="box_R"><div class="box_B"><div class="box_BR">
                            <div class="workflowBox_center">


                                <!-- Workflow items -->

                                <table border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td>

                                            <!-- These DIVs define shaded box borders -->
                                            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

                                                <div class="textbox_center" align="center">

                                                    <c:choose>
                                                    <c:when test="${userRole.manageStudy}">
                               <span class="title_manage">
                               <a href="ManageStudy"><fmt:message key="manage_study" bundle="${resworkflow}"/></a>
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                               <a href="ListStudySubjectsSubmit"><fmt:message key="submit_data" bundle="${resworkflow}"/></a>
                             </c:otherwise>
                             </c:choose>




							</span>

                                                </div>
                                            </div></div></div></div></div></div></div></div>

                                        </td>
                                        <td><img src="images/arrow.gif"></td>
                                        <td>

                                            <!-- These DIVs define shaded box borders -->
                                            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

                                                <div class="textbox_center" align="center">

                                                    <c:choose>
                                                    <c:when test="${userRole.manageStudy}">
                               <span class="title_manage">
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                             </c:otherwise>
                             </c:choose>

							<b><fmt:message key="list_discrepancy_notes" bundle="${restext}"/></b>

							</span>

                                                </div>
                                            </div></div></div></div></div></div></div></div>

                                        </td>
                                    </tr>
                                </table>


                                <!-- end Workflow items -->

                            </div>
                        </div></div></div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<!-- END WORKFLOW BOX -->

<%--<c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/>
</c:import>--%>

<jsp:include page="../include/footer.jsp"/>

