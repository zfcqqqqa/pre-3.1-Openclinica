<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />
<jsp:useBean scope="request" id="section" class=
  "org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="annotations" class="java.lang.String" />
<jsp:useBean scope='request' id='pageMessages' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>OpenClinica <fmt:message key="initial_data_entry" bundle="${resword}"/></title>
    <link rel="stylesheet" href="includes/styles.css" type="text/css" media="screen">
    <link rel="stylesheet" href="includes/styles2.css" type="text/css" media="screen">
    <link rel="stylesheet" href="includes/print.css" type="text/css" media="print">
    <script type="text/javascript" language="JavaScript">
        //this has been declared here so that it is accessible from other functions in the global_functions_javascript.js
        var checkboxObject;
    </script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js"></script>
    <script type="text/javascript"  language="JavaScript" src=
      "includes/repetition-model/repetition-model.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/prototype.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/effects.js"></script>

    <!-- Added for the new Calender -->

        <link rel="stylesheet" type="text/css" media="all" href="includes/new_cal/skins/aqua/theme.css" title="Aqua" />
        <script type="text/javascript" src="includes/new_cal/calendar.js"></script>
        <script type="text/javascript" src="includes/new_cal/lang/calendar-en.js"></script>
        <script type="text/javascript" src="includes/new_cal/calendar-setup.js"></script>
    <!-- End -->

</head>
<body class="aka_bodywidth" onload="alert('onload')">
<%-- onload="if(! detectFirefoxWindows(navigator.userAgent)){document.getElementById('centralContainer').style.display='none';new Effect.Appear('centralContainer', {duration:1});}"
giveFirstElementFocus(); BWP: TabsForwardByNum(<c:out value="${tabId}"/>);--%>
<div id="centralContainer" style=
  "padding-left:3em; margin-top:1em;background-color: white; color:black;">


<%-- set button text depending on whether or not the user is confirming values --%>
<c:choose>
    <c:when test="${section.checkInputs}">
        <c:set var="buttonAction"><fmt:message key="save" bundle="${resword}"/></c:set>
        <c:set var="checkInputsValue" value="1" />
    </c:when>
    <c:otherwise>
        <c:set var="buttonAction" value="Confirm values" />
        <c:set var="checkInputsValue" value="0" />
    </c:otherwise>
</c:choose>

<h1>
    <span class="title_submit"><fmt:message key="initial_data_entry_for" bundle="${resword}"/> <c:out value="${section.crf.name}" /> <c:out value="${section.crfVersion.name}" /> <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2c')"><img src="images/bt_Help_Submit.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<%--the tabId default value is set in DataEntryServlet.getInputBeans()--%>



<!--InitialDataEntry  inputs-->
<form id="mainForm" name="crfForm" method="post" action="InitialDataEntry">
<input type="hidden" name="eventCRFId" value="<c:out value="${section.eventCRF.id}"/>" />
<input type="hidden" name="sectionId" value="<c:out value="${section.section.id}"/>" />
<input type="hidden" name="checkInputs" value="<c:out value="${checkInputsValue}"/>" />
<input type="hidden" name="tab" value="<c:out value="${tabId}"/>" />
<%-- We have to feed this value to the method giveFirstElementFocus()--%>
<input id="formFirstField" type="hidden" name="formFirstField" value="${requestScope['formFirstField']}" />

<%--FF: ${requestScope['formFirstField']}<br />--%>



<script type="text/javascript" language="JavaScript">
    // <![CDATA[
    function getSib(theSibling){
        var sib;
        do {
            sib  = theSibling.previousSibling;
            if(sib.nodeType != 1){
                theSibling = sib;
            }
        } while(! (sib.nodeType == 1))

        return sib;
    }

    function getFocused(f){
        var v = document.getElementById(f);
        v.focus();
    }

    // ]]>
</script>

<c:import url="interviewer.jsp">
  <c:param name="hasNameNote" value="${hasNameNote}"/>
  <c:param name="hasDateNote" value="${hasDateNote}"/> 
</c:import>
<!--<br><br>-->
<br />
<%--I don't think we need this segment to accompany the existing error messages:--%>
<%-- need to put this back, otherwise, error msg from 'mark complete' cannot show--%>
<c:if test="${!empty pageMessages}">
    <div class="alert">
        <c:forEach var="message" items="${pageMessages}">
            <c:out value="${message}" escapeXml="false"/>
            <br><br>
        </c:forEach>
    </div>
</c:if>

<c:set var="sectionNum" value="0"/>
<c:forEach var="section" items="${toc.sections}">
    <c:set var="sectionNum" value="${sectionNum+1}"/>
</c:forEach>


<c:if test="${! empty formMessages}">
    <!-- initial position for data entry error messages; we'll
    improve the style as well -->
    <div id="errorMessagesContainer" class="aka_err_message">
        <ul>
            <c:forEach var="formMsg" items="${formMessages}">
                <li style="color:  #ff0000"><span style="text-decoration: underline"><strong>
                    <label onclick="getFocused('<c:out value="${formMsg.key}" />');"><c:out value="${formMsg.value}" /></label>
                </strong></span></li>
                <%--<li style="color:  #ff0000"><span style="text-decoration: underline"><strong><label for="<c:out value="${formMsg.key}"/>"><c:out value="${formMsg.value}" /></label></strong></span></li> --%>
            </c:forEach>
        </ul>
        <!--  Use the formMessages request attribute to grab each validation
      error message?
      error messages look like:

       Woops, you forgot to provide a value for
       <strong><label for="formElementName">formElementName</label></strong>.<br/>-->
    </div>
</c:if><%-- error messages are not null --%>
<div id="box" class="dialog">
<span id="mbm">
<c:choose>
  <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
  Marking this CRF complete will finalize data entry. You will be allowed to edit the data later but this data entry stage is completed. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?
  </c:when>
  <c:otherwise>
  Marking this CRF complete will finalize data entry. You will no longer be able to add or modify data unless the CRF is reset by an administrator. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?
  </c:otherwise>
</c:choose>
</span><br>
    <div style="text-align:center; width:100%;">
        <input align="center" type="password" name="password" id="passwordId"/>
        <button onclick="hm('box');requestSignatureFromCheckbox(document.getElementById('passwordId').value, checkboxObject);">OK</button>
    </div>
</div>
<!-- section tabs here -->
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="right" valign="middle" style="padding-left: 12px; display: none" id="TabsBack">
    <a href="javascript:TabsBack()"><img src="images/arrow_back.gif" border="0" style="margin-top:10px"></a></td>
<td align="right" style="padding-left: 12px" id="TabsBackDis">
    <img src="images/arrow_back_dis.gif" border="0"/></td>


<script type="text/JavaScript" language="JavaScript">
<!--

// Total number of tabs (one for each CRF)
var TabsNumber = <c:out value="${sectionNum}"/>;


// Number of tabs to display at a time
var TabsShown = 3;


// Labels to display on each tab (name of CRF)
var TabLabel = new Array(TabsNumber)
var TabFullName = new Array(TabsNumber)
var TabSectionId = new Array(TabsNumber)
<c:set var="count" value="0"/>
<c:forEach var="section" items="${toc.sections}">
<c:set var="completedItems" value="${section.numItemsCompleted}"/>
<c:if test="${toc.eventDefinitionCRF.doubleEntry}">
<c:set var="completedItems" value="${section.numItemsNeedingValidation}"/>
</c:if>
TabFullName[<c:out value="${count}"/>]="<c:out value="${section.label}"/> (<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)";

TabSectionId[<c:out value="${count}"/>]= <c:out value="${section.id}"/>;

TabLabel[<c:out value="${count}"/>]="<c:out value="${section.label}"/>";
if (TabLabel[<c:out value="${count}"/>].length>8) {
    var shortName = TabLabel[<c:out value="${count}"/>].substring(0,7);
    TabLabel[<c:out value="${count}"/>]= shortName + '...' + "<span id='secNumItemsCom<c:out value="${count}"/>' style='font-weight: normal;'>(<c:out value="${completedItems}"/>/<c:out value="${section.numItems}" />)</span>";
} else {
    TabLabel[<c:out value="${count}"/>]="<c:out value="${section.label}"/> " + "<span id='secNumItemsCom<c:out value="${count}"/>' style='font-weight: normal;'>(<c:out value="${completedItems}"/>/<c:out value="${section.numItems}" />)</span>";
}

<c:set var="count" value="${count+1}"/>
</c:forEach>
DisplaySectionTabs()

selectTabs(${tabId},${sectionNum},'crfHeaderTabs');


function DisplaySectionTabs()
{
    TabID=1;

    while (TabID<=TabsNumber)

    {
        sectionId = TabSectionId[TabID-1];
        url = "InitialDataEntry?eventCRFId=" + <c:out value="${section.eventCRF.id}"/> + "&sectionId=" + sectionId + "&tab=" + TabID;
        currTabID = <c:out value="${tabId}"/>;

        if (TabID<=TabsShown)
        {
            document.write('<td class="crfHeaderTabs" valign="bottom" id="Tab' + TabID + '">');
        }
        else
        {
            document.write('<td class="crfHeaderTabs" valign="bottom" id="Tab' + TabID + '">');
        }
        if (TabID != currTabID) {
            document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
            document.write('<a class="tabtext" title="' + TabFullName[(TabID-1)] + '" href=' + url + ' onclick="return checkSectionStatus();">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
            document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
            document.write('</td>');
        }
        else {
            document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">');
            document.write('<span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
            document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
            document.write('</td>');
        }

        TabID++

    }
}

function checkDataStatus() {

    objImage=document.getElementById('status_top');
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        return confirm('<fmt:message key="you_have_unsaved_data" bundle="${resword}"/>');
    }

    return true;
}
function gotoLink() {

    var OptionIndex=document.crfForm.sectionName.selectedIndex;
    if (checkDataStatus()) {
        window.location = document.crfForm.sectionName.options[OptionIndex].value;
    }
}

function pageWidth() {return window.innerWidth != null? window.innerWidth: document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth:document.body != null? document.body.clientWidth:null;}
function pageHeight() {return window.innerHeight != null? window.innerHeight: document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight:document.body != null? document.body.clientHeight:null;}
function posLeft() {return typeof window.pageXOffset != 'undefined' ? window.pageXOffset:document.documentElement && document.documentElement.scrollLeft? document.documentElement.scrollLeft:document.body.scrollLeft? document.body.scrollLeft:0;}
function posTop() {return typeof window.pageYOffset != 'undefined' ? window.pageYOffset:document.documentElement && document.documentElement.scrollTop? document.documentElement.scrollTop: document.body.scrollTop?document.body.scrollTop:0;}
function $(x){return document.getElementById(x);}
function scrollFix(){var obol=$('ol');obol.style.top=posTop()+'px';obol.style.left=posLeft()+'px'}
function sizeFix(){var obol=$('ol');obol.style.height=pageHeight()+'px';obol.style.width=pageWidth()+'px';}
function kp(e){ky=e?e.which:event.keyCode;if(ky==88||ky==120)hm();return false}
function inf(h){tag=document.getElementsByTagName('select');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;tag=document.getElementsByTagName('iframe');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;tag=document.getElementsByTagName('object');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;}
function sm(obl, chkbox, wd, ht){if(chkbox.checked==false){checkboxObject=chkbox;return;} checkboxObject=chkbox;  var h='hidden';var b='block';var p='px';var obol=$('ol'); var obbxd = $('mbd');obbxd.innerHTML = $(obl).innerHTML;obol.style.height=pageHeight()+p;obol.style.width=pageWidth()+p;obol.style.top=posTop()+p;obol.style.left=posLeft()+p;obol.style.display=b;var tp=posTop()+((pageHeight()-ht)/2)-12;var lt=posLeft()+((pageWidth()-wd)/2)-12;var obbx=$('mbox');obbx.style.top=(tp<0?0:tp)+p;obbx.style.left=(lt<0?0:lt)+p;obbx.style.width=wd+p;obbx.style.height=ht+p;inf(h);obbx.style.display=b;return false;}
function hm(){var v='visible';var n='none';$('ol').style.display=n;$('mbox').style.display=n;inf(v);document.onkeypress=''}
function initmb(){var ab='absolute';var n='none';var obody=document.getElementsByTagName('body')[0];var frag=document.createDocumentFragment();var obol=document.createElement('div');obol.setAttribute('id','ol');obol.style.display=n;obol.style.position=ab;obol.style.top=0;obol.style.left=0;obol.style.zIndex=998;obol.style.width='100%';frag.appendChild(obol);var obbx=document.createElement('div');obbx.setAttribute('id','mbox');obbx.style.display=n;obbx.style.position=ab;obbx.style.zIndex=999;var obl=document.createElement('span');obbx.appendChild(obl);var obbxd=document.createElement('div');obbxd.setAttribute('id','mbd');obl.appendChild(obbxd);frag.insertBefore(obbx,obol.nextSibling);obody.insertBefore(frag,obody.firstChild);
    window.onscroll = scrollFix; window.onresize = sizeFix;
}
window.onload = initmb;

//-->
</script>

<td align="right"id="TabsNextDis" style="display: none"><img src="images/arrow_next_dis.gif" border="0"/></td>
<td align="right" id="TabsNext"><a href="javascript:TabsForward()"><img src="images/arrow_next.gif" border="0" style=
  "margin-top:10px;margin-right:6px"/></a></td>
<td>&nbsp;
    <div class="formfieldM_BG_noMargin">
        <select id="sectionTabSelectElement" class="formfieldM" name="sectionName" size="1" onchange="gotoLink();">
        <c:set var="tabCount" value="1"/>
        <option selected>-- <fmt:message key="select_to_jump" bundle="${resword}"/> --</option>
        <c:forEach var="sec" items="${toc.sections}" >
            <c:set var="tabUrl" value = "InitialDataEntry?eventCRFId=${section.eventCRF.id}&sectionId=${sec.id}&tab=${tabCount}"/>
            <option value="<c:out value="${tabUrl}"/>"><c:out value="${sec.name}"/></option>
            <c:set var="tabCount" value="${tabCount+1}"/>
        </c:forEach>
    </select>
    </div>
</td>
</tr>
</table>
<input type="hidden" name="submitted" value="1" />

<script type="text/javascript" language="JavaScript">
    <!--
    function checkSectionStatus() {

        objImage=document.getElementById('status_top');
    //alert(objImage.src);
        if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
            return confirm('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>');
        }

        return true;
    }


    function checkEntryStatus(strImageName) {
        objImage = MM_findObj(strImageName);
    //alert(objImage.src);
        if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
            return confirm('<fmt:message key="you_have_unsaved_data_exit" bundle="${resword}"/>');
        }
        return true;
    }
    //-->
</script>

<c:set var="stage" value="${param.stage}"/>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<div style="width:100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B">
<div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<c:set var="currPage" value="" />
<c:set var="curCategory" value="" />

<!--   include return to top table-->
<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0">
<c:set var="displayItemNum" value="${0}" />
<c:set var="itemNum" value="${0}" />
<c:set var="numOfTr" value="0"/>
<c:set var="numOfDate" value="1"/>
<c:if test='${section.section.title != ""}'>
    <tr class="aka_stripes">
        <td class="aka_header_border"><b><fmt:message key="title" bundle="${resword}"/>:&nbsp;<c:out value="${section.section.title}" escapeXml="false"/></b> </td>
    </tr>
</c:if>
<c:if test='${section.section.subtitle != ""}'>
    <tr class="aka_stripes">
        <td class="aka_header_border"><fmt:message key="subtitle" bundle="${resword}"/>:&nbsp;<c:out value="${section.section.subtitle}" escapeXml="false"/> </td>
    </tr>
</c:if>
<c:if test='${section.section.instructions != ""}'>
    <tr class="aka_stripes">
        <td class="aka_header_border"><fmt:message key="instructions" bundle="${resword}"/>:&nbsp;<c:out value="${section.section.instructions}" escapeXml="false"/> </td>
    </tr>
</c:if>
<c:set var="repeatCount" value="1"/>
<c:forEach var="displayItem" items="${section.displayItemGroups}" varStatus="itemStatus">

<c:if test="${displayItemNum ==0}">
    <!-- always show the button and page above the first item-->
    <!-- to handle the case of no pageNumLabel for all the items-->
    <%--  BWP: corrected "column span="2" "--%>
    <tr class="aka_stripes">
            <%--  <td class="aka_header_border" colspan="2">width="100%"--%>
        <td class="aka_header_border" colspan="2">
            <table border="0" cellpadding="0" cellspacing="0" style="margin-bottom: 6px;">
                <tr>

                    <td valign="bottom" nowrap="nowrap" style="padding-right: 50px">

                        <a name="top"><fmt:message key="page" bundle="${resword}"/>: <c:out value="${displayItem.pageNumberLabel}" escapeXml="false"/></a>
                    </td>
                    <td align="right" valign="bottom">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <c:choose>
                                    <c:when test="${stage !='adminEdit' && section.lastSection}">
                                        <c:choose>
                                            <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                             onclick="sm('box', this, 730,100);">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                             onclick="displayMessageFromCheckbox(this)">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <td colspan="2">&nbsp;</td>
                                    </c:otherwise>
                                </c:choose>
                                <td><input type="submit" name="submittedResume" value="<fmt:message key="save" bundle="${resword}"/>" class=
                                  "button_medium" /></td>
                                <td><input type="submit" name="submittedExit" value="<fmt:message key="exit" bundle="${resword}"/>" class=
                                  "button_medium" onClick="return checkEntryStatus('DataStatus_top');" /></td>

                                <td valign="bottom"><img name=
                                  "DataStatus_top" id="status_top" alt="<fmt:message key="data_status" bundle="${resword}"/>" src="images/icon_UnchangedData.gif"></td>

                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</c:if>

<c:if test="${currPage != displayItem.pageNumberLabel && displayItemNum >0}">
    <!-- show page number and buttons -->
    <%--  BWP: corrected "column span="2" "  width="100%"--%>
    <tr class="aka_stripes">
        <td class="aka_header_border" colspan="2">
            <table border="0" cellpadding="0" cellspacing="0" style="margin-bottom: 6px;">
                <tr>

                    <td valign="bottom" nowrap="nowrap" style="padding-right: 50px">
                        <fmt:message key="page" bundle="${resword}"/>: <c:out value="${displayItem.pageNumberLabel}" escapeXml="false"/>
                    </td>
                    <td align="right" valign="bottom">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <c:choose>
                                    <c:when test="${stage !='adminEdit' && section.lastSection}">
                                        <c:choose>
                                            <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                             onclick="sm('box', this, 730,100);">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                             onclick="displayMessageFromCheckbox(this)">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <td colspan="2">&nbsp;</td>
                                    </c:otherwise>
                                </c:choose>

                                    <%-- below removed tbh, 102007 --%>
                                    <%--

                                            <td><input type="submit" name="submittedResume" value="Save" class="button_medium" /></td>
                                            <td><input type="submit" name="submittedExit" value="Exit" class="button_medium" onClick="return checkEntryStatus('DataStatus_top');" /></td>

                                    --%>

                                    <%--<td valign="bottom"><img name="DataStatus_top" src="images/icon_UnchangedData.gif"></td>--%>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <!-- end of page number and buttons-->

</c:if>

<c:choose>

<c:when test="${displayItem.inGroup == true}">
<c:set var="currPage" value="${displayItem.pageNumberLabel}" />
<%-- the section borders property value --%>
<c:set var="sectionBorders" value="${section.section.borders}" />
<tr><td>
<c:set var="uniqueId" value="0"/>
<c:set var="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.name}"/>

<c:set var="repeatNumber" value="${displayItem.itemGroup.groupMetaBean.repeatNum}"/>
<c:if test="${groupHasData}">
    <!-- there are already item data for an item group, repeat number just be 1-->
    <c:set var="repeatNumber" value="1"/>
</c:if>
<c:set var="repeatMax" value="${displayItem.itemGroup.groupMetaBean.repeatMax}"/>
<c:set var="totalColsPlusSubcols" value="0" />
<c:set var="questionNumber" value=""/>
    <%--the itemgroups include a group for orphaned items, in the order they should appear,
but the custom tag uses that, not this jstl code--%>
<c:if test="${! (repeatParentId eq 'Ungrouped')}">
<%-- implement group header--%>
<c:if test="${! (displayItem.itemGroup.groupMetaBean.header eq '')}">
    <div class="aka_group_header">
        <strong><c:out value="${displayItem.itemGroup.groupMetaBean.header}" escapeXml="false"/></strong>
    </div>
</c:if>
<table border="0" cellspacing="0" cellpadding="0" class="aka_form_table" width="100%">
<thead>
<tr>
        <%-- if there are horizontal checkboxes or radios anywhere in the group...--%>
    <c:set var="isHorizontal" scope="request" value="${false}"/>
    <c:forEach var="thItem" items="${displayItem.itemGroup.items}">
        <c:set var="questionNumber" value="${thItem.metadata.questionNumberLabel}"/>
        <%-- We have to add a second row of headers if the response_layout property is
     horizontal for checkboxes. --%>
        <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
        <c:if test="${thItem.metadata.responseLayout eq 'horizontal' ||
      thItem.metadata.responseLayout eq 'Horizontal'}">
            <c:set var="isHorizontal" scope="request" value="${true}"/>
            <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
            <c:set var="optionsLen" value="0"/>
            <c:forEach var="optn" items="${thItem.metadata.responseSet.options}">
                <c:set var="optionsLen" value="${optionsLen+1}"/>
            </c:forEach>
        </c:if>
        <c:choose>
            <c:when test="${isHorizontalCellLevel && sectionBorders == 1 &&
        (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                <th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" />
            </c:when>
            <c:when test="${isHorizontalCellLevel &&
        (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                <th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" />
            </c:when>
            <c:when test="${sectionBorders == 1}">
                <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" />
            </c:when>
            <c:otherwise>
                <th class="aka_headerBackground aka_padding_large aka_cellBorders">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" />
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${thItem.metadata.header == ''}">
                <c:if test="${! (empty questionNumber)}">
                    <span style="margin-right:1em"><c:out value="${questionNumber}" escapeXml="false"/></span></c:if><c:out value="${thItem.metadata.leftItemText}" escapeXml="false"/>
            </c:when>
            <c:otherwise>
                <c:if test="${! (empty questionNumber)}">
                    <span style="margin-right:1em"><c:out value="${questionNumber}" escapeXml="false"/></span></c:if><c:out value="${thItem.metadata.header}" escapeXml="false"/>
            </c:otherwise>
        </c:choose>
        </th>
    </c:forEach>
    <c:choose>
        <c:when test="${sectionBorders == 1}">
            <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark" />

        </c:when>
        <c:otherwise>
            <th class="aka_headerBackground aka_padding_large aka_cellBorders" />
        </c:otherwise>
    </c:choose>
</tr>
<c:if test="${isHorizontal}">
    <%-- create another row --%>
    <tr>
        <c:forEach var="thItem" items="${displayItem.itemGroup.items}">
            <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
            <c:if test="${thItem.metadata.responseLayout eq 'horizontal' ||
      thItem.metadata.responseLayout eq 'Horizontal'}">
                <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
            </c:if>
            <c:choose>
                <c:when test="${isHorizontalCellLevel && sectionBorders == 1 &&
                    (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                    <c:forEach var="respOpt" items="${thItem.metadata.responseSet.options}">
                        <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
                            <c:out value="${respOpt.text}" /></th>
                    </c:forEach>
                </c:when>
                <c:when test="${isHorizontalCellLevel &&
                    (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                    <c:forEach var="respOpt" items="${thItem.metadata.responseSet.options}">
                        <th class="aka_headerBackground aka_padding_large aka_cellBorders">
                            <c:out value="${respOpt.text}" /></th>
                    </c:forEach>
                </c:when>
                <c:when test="${sectionBorders == 1}">
                    <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark"/>
                </c:when>
                <c:otherwise>
                    <th class="aka_headerBackground aka_padding_large aka_cellBorders"/>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <th />
    </tr>
</c:if>
</thead>

<tbody>

<c:set var="uniqueId" value="${0}"/>
<c:set var="repeatRowCount" value="0"/>

<c:forEach var="bodyItemGroup" items="${displayItem.itemGroups}">
    <c:set var="repeatRowCount" value="${repeatRowCount+1}"/>
</c:forEach>
<!-- there are data posted already -->
<c:if test="${repeatRowCount>1}">
    <c:set var="repeatNumber" value="1"/>
</c:if>
<c:forEach var="bodyItemGroup" items="${displayItem.itemGroups}"  varStatus="status">
<c:set var="columnNum"  value="1"/>
<!-- hasError is set to true when validation error happens-->
<c:choose>
<c:when test="${status.last}">
<!-- for the last but not the first row and only row, we need to use [] so the repetition javascript can copy it to create new row-->
<tr id="<c:out value="${repeatParentId}"/>" repeat="template" repeat-start="<c:out value="${repeatNumber}"/>" repeat-max="<c:out value="${repeatMax}"/>">
    <c:forEach var="bodyItem" items="${bodyItemGroup.items}">
        <c:set var="itemNum" value="${itemNum + 1}" />
        <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
        <c:if test="${bodyItem.metadata.responseLayout eq 'horizontal' ||
      bodyItem.metadata.responseLayout eq 'Horizontal'}">
            <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
        </c:if>
        <c:choose>
            <c:when test="${isHorizontalCellLevel && sectionBorders == 1 && (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
                <%-- For horizontal checkboxes, radio buttons--%>
                <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                    <td class="aka_padding_norm aka_cellBorders_dark">
                        <c:set var="displayItem" scope="request" value="${bodyItem}" />
                        <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                        <c:import url="../submit/showGroupItemInput.jsp">
                            <c:param name="repeatParentId" value="${repeatParentId}"/>
                            <c:param name="rowCount" value="${uniqueId}"/>
                            <c:param name="key" value="${numOfDate}" />
                            <c:param name="isLast" value="${true}"/>
                            <c:param name="tabNum" value="${itemNum}"/>
                            <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                            <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                            <c:param name="originJSP" value="initialDataEntry"/>
                        </c:import>
                    </td>
                </c:forEach>
            </c:when>
            <c:when test="${isHorizontalCellLevel &&
                (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
                <%-- For horizontal checkboxes, radio buttons--%>
                <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                    <td class="aka_padding_norm aka_cellBorders">
                        <c:set var="displayItem" scope="request" value="${bodyItem}" />
                        <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                        <c:import url="../submit/showGroupItemInput.jsp">
                            <c:param name="repeatParentId" value="${repeatParentId}"/>
                            <c:param name="rowCount" value="${uniqueId}"/>
                            <c:param name="key" value="${numOfDate}" />
                            <c:param name="isLast" value="${true}"/>
                            <c:param name="tabNum" value="${itemNum}"/>
                            <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                            <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                            <c:param name="originJSP" value="initialDataEntry"/>
                        </c:import>
                    </td>
                </c:forEach>
            </c:when>
            <c:when test="${sectionBorders == 1}">
                <td class="aka_padding_norm aka_cellBorders_dark">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${true}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="initialDataEntry"/>
                    </c:import>
                </td>
            </c:when>
            <%-- could be a radio or checkbox that is not horizontal --%>
            <c:otherwise>
                <td class="aka_padding_norm aka_cellBorders">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${true}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="initialDataEntry"/>
                    </c:import>
                </td>
            </c:otherwise>
        </c:choose>
        <c:set var="columnNum" value="${columnNum+1}"/>
    </c:forEach>
    <c:choose>
        <c:when test="${sectionBorders == 1}">
            <td class="aka_padding_norm aka_cellBorders_dark">
                <input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
                <button type="remove" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
            </td>
        </c:when>

        <c:otherwise>
            <td class="aka_padding_norm aka_cellBorders">
                <input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
                <button type="remove" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
            </td>
        </c:otherwise>
    </c:choose>
</tr>

</c:when>
<c:otherwise>
<!--  not the last row -->
<tr repeat="0" />
<c:set var="columnNum"  value="1"/>
<c:forEach var="bodyItem" items="${bodyItemGroup.items}">
    <c:set var="itemNum" value="${itemNum + 1}" />
    <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
    <c:if test="${bodyItem.metadata.responseLayout eq 'horizontal' ||
      bodyItem.metadata.responseLayout eq 'Horizontal'}">
        <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
    </c:if>
    <c:choose>
        <c:when test="${isHorizontalCellLevel &&
            sectionBorders == 1 && (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
            <%-- For horizontal checkboxes, radio buttons--%>
            <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                <td class="aka_padding_norm aka_cellBorders_dark">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
                    <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${false}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="initialDataEntry"/>
                    </c:import>
                </td>
            </c:forEach>
        </c:when>
        <c:when test="${isHorizontalCellLevel &&
           (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
            <%-- For horizontal checkboxes, radio buttons--%>
            <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                <td class="aka_padding_norm aka_cellBorders">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
                    <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${false}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="initialDataEntry"/>
                    </c:import>
                </td>
            </c:forEach>
        </c:when>
        <c:when test="${sectionBorders == 1}">
            <td class="aka_padding_norm aka_cellBorders_dark">
                <c:set var="displayItem" scope="request" value="${bodyItem}" />
                <c:import url="../submit/showGroupItemInput.jsp">
                    <c:param name="repeatParentId" value="${repeatParentId}"/>
                    <c:param name="rowCount" value="${uniqueId}"/>
                    <c:param name="key" value="${numOfDate}" />
                    <c:param name="isLast" value="${false}"/>
                    <c:param name="tabNum" value="${itemNum}"/>
                    <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                    <c:param name="originJSP" value="initialDataEntry"/>
                </c:import>
            </td>
        </c:when>
        <%-- could be a radio or checkbox that is not horizontal --%>
        <c:otherwise>
            <td class="aka_padding_norm aka_cellBorders">
                <c:set var="displayItem" scope="request" value="${bodyItem}" />
                <c:import url="../submit/showGroupItemInput.jsp">
                    <c:param name="repeatParentId" value="${repeatParentId}"/>
                    <c:param name="rowCount" value="${uniqueId}"/>
                    <c:param name="key" value="${numOfDate}" />
                    <c:param name="isLast" value="${false}"/>
                    <c:param name="tabNum" value="${itemNum}"/>
                    <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                    <c:param name="originJSP" value="initialDataEntry"/>
                </c:import>
            </td>
        </c:otherwise>
    </c:choose>
    <c:set var="columnNum" value="${columnNum+1}"/>
</c:forEach>
<c:choose>
    <c:when test="${sectionBorders == 1}">
        <td class="aka_padding_norm aka_cellBorders_dark">
                <%-- check for manual in the input name; if rowCount > 0 then manual
           will be in the name --%>
            <c:choose>
                <c:when test="${uniqueId ==0}">
                    <input type="hidden" name="<c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>.newRow" value="yes">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="<c:out value="${repeatParentId}"/>_manual<c:out value="${uniqueId}"/>.newRow" value="yes">
                </c:otherwise>
            </c:choose>
            <button type="remove" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
        </td>
    </c:when>
    <c:otherwise>
        <td class="aka_padding_norm aka_cellBorders">
                <%-- check for manual in the input name; if rowCount > 0 then manual
           will be in the name --%>
            <c:choose>
                <c:when test="${uniqueId ==0}">
                    <input type="hidden" name="<c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>.newRow" value="yes">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="<c:out value="${repeatParentId}"/>_manual<c:out value="${uniqueId}"/>.newRow" value="yes">
                </c:otherwise>
            </c:choose>
            <button type="remove" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
        </td>

    </c:otherwise>
</c:choose>
</tr>
</c:otherwise>
</c:choose>
<c:set var="uniqueId" value="${uniqueId +1}"/>
</c:forEach>

<tr>
    <c:choose>
        <c:when test="${sectionBorders == 1}">
            <%-- Add 1 to the totalColsPlusSubcols variable to accomodate the cell
            containing the remove button--%>
            <td class="aka_padding_norm aka_cellBorders_dark" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
                <button type="add" template="<c:out value="${repeatParentId}"/>" class="button_search"><fmt:message key="add" bundle="${resword}"/></button></td>
        </c:when>
        <c:otherwise>
            <td class="aka_padding_norm aka_cellBorders" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
                <button type="add" template="<c:out value="${repeatParentId}"/>" class="button_search"><fmt:message key="add" bundle="${resword}"/></button></td>
        </c:otherwise>
    </c:choose>
</tr>
</tbody>

</table>
<%--test for itemgroup named Ungrouped --%>
</c:if>
</td></tr>


</c:when>

<c:otherwise>


<c:set var="currPage" value="${displayItem.singleItem.metadata.pageNumberLabel}" />

    <%-- SHOW THE PARENT FIRST --%>
<c:if test="${displayItem.singleItem.metadata.parentId == 0}">

<!--ACCORDING TO COLUMN NUMBER, ARRANGE QUESTIONS IN THE SAME LINE-->

<c:if test="${displayItem.singleItem.metadata.columnNumber <=1}">
<c:if test="${numOfTr > 0 }">
</tr>
</table>
</td>

</tr>

</c:if>
<c:set var="numOfTr" value="${numOfTr+1}"/>
<c:if test="${!empty displayItem.singleItem.metadata.header}">
    <tr class="aka_stripes">
            <%--<td class="table_cell_left" bgcolor="#F5F5F5">--%>
        <td class="table_cell_left aka_stripes"><b><c:out value=
          "${displayItem.singleItem.metadata.header}" escapeXml="false"/></b></td>
    </tr>
</c:if>
<c:if test="${!empty displayItem.singleItem.metadata.subHeader}">
    <tr class="aka_stripes">
        <td class="table_cell_left"><c:out value="${displayItem.singleItem.metadata.subHeader}" escapeXml=
          "false"/></td>
    </tr>
</c:if>
<tr>
    <td class="table_cell_left">
        <table border="0" >
            <tr>
                <td valign="top">
                    </c:if>

                    <c:if test="${displayItem.singleItem.metadata.columnNumber >1}">
                <td valign="top">
                    </c:if>
                    <table border="0">
                        <tr>
                            <td valign="top" class="aka_ques_block"><c:out value="${displayItem.singleItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
                            <td valign="top" class="aka_text_block"><c:out value="${displayItem.singleItem.metadata.leftItemText}" escapeXml="false"/></td>

                            <td valign="top" nowrap="nowrap">
                                    <%-- display the HTML input tag --%>
                                <c:set var="displayItem" scope="request" value="${displayItem.singleItem}" />
                                <c:import url="../submit/showItemInput.jsp">
                                    <c:param name="key" value="${numOfDate}" />
                                    <c:param name="tabNum" value="${itemNum}"/>
                                    <%-- add default value from the crf --%>
                                    <c:param name="defaultValue" value="${displayItem.singleItem.metadata.defaultValue}"/>
                                    <c:param name="respLayout" value="${displayItem.singleItem.metadata.responseLayout}"/>
                                    <c:param name="originJSP" value="initialDataEntry"/>
                                </c:import>

                            </td>
                            <c:if test='${displayItem.singleItem.item.units != ""}'>
                                <td valign="top">
                                    <c:out value="(${displayItem.singleItem.item.units})" escapeXml="false"/>
                                </td>
                            </c:if>
                            <td valign="top"><c:out value="${displayItem.singleItem.metadata.rightItemText}" escapeXml="false" /></td>
                        </tr>
                            <%--try this, displaying error messages in their own row--%>
                            <%--We won't need this if the error messages are not embedded in the form:
                            <tr>
                              <td valign="top" colspan="4" style="text-align:right">
                                <c:import url="../showMessage.jsp"><c:param name="key" value=
                                  "input${displayItem.singleItem.item.id}" /></c:import> </td>
                            </tr>--%>
                    </table>
                </td>
                <c:if test="${itemStatus.last}">
            </tr>
        </table>
    </td>

</tr>
</c:if>

<c:if test="${displayItem.singleItem.numChildren > 0}">
    <tr>
            <%-- indentation --%>
        <!--<td class="table_cell">&nbsp;</td>-->
            <%-- NOW SHOW THE CHILDREN --%>

        <td class="table_cell">
            <table border="0">
                <c:set var="notFirstRow" value="${0}" />
                <c:forEach var="childItem" items="${displayItem.singleItem.children}">


                <c:set var="currColumn" value="${childItem.metadata.columnNumber}" />
                <c:if test="${currColumn == 1}">
                <c:if test="${notFirstRow != 0}">
                    </tr>
                </c:if>
                <tr>
                    <c:set var="notFirstRow" value="${1}" />
                        <%-- indentation --%>
                    <td valign="top">&nbsp;</td>
                    </c:if>
                        <%--
                          this for loop "fills in" columns left blank
                          e.g., if the first childItem has column number 2, and the next one has column number 5,
                          then we need to insert one blank column before the first childItem, and two blank columns between the second and third children
                        --%>
                    <c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
                        <td valign="top">&nbsp;</td>
                    </c:forEach>

                    <td valign="top">
                        <table border="0">
                            <tr>
                                    <%--          <td valign="top" class="text_block">
                                  <c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/>
                                  <c:out value="${childItem.metadata.leftItemText}" escapeXml="false"/></td>--%>
                                <td valign="top" class="aka_ques_block"><c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
                                <td valign="top" class="aka_text_block"><c:out value="${childItem.metadata.leftItemText}" escapeXml="false"/></td>
                                <td valign="top" nowrap="nowrap">
                                        <%-- display the HTML input tag --%>
                                    <c:set var="itemNum" value="${itemNum + 1}" />
                                    <c:set var="displayItem" scope="request" value="${childItem}" />
                                    <c:import url="../submit/showItemInput.jsp" >
                                        <c:param name="key" value="${numOfDate}" />
                                        <c:param name="tabNum" value="${itemNum}"/>
                                        <c:param name="defaultValue" value="${childItem.metadata.defaultValue}"/>
                                        <c:param name="respLayout" value="${childItem.metadata.responseLayout}"/>
                                        <c:param name="originJSP" value="initialDataEntry"/>
                                    </c:import>
                                        <%--	<br />--%><%--<c:import url="../showMessage.jsp"><c:param name="key" value="input${childItem.item.id}" /></c:import>--%>
                                </td>
                                <c:if test='${childItem.item.units != ""}'>
                                    <td valign="top"> <c:out value="(${childItem.item.units})" escapeXml="false"/> </td>
                                </c:if>
                                <td valign="top"> <c:out value="${childItem.metadata.rightItemText}" escapeXml="false"/> </td>
                            </tr>
                                <%--BWP: try this--%>
                            <tr>
                                <td valign="top" colspan="4" style="text-align:right">
                                    <c:import url="../showMessage.jsp"><c:param name="key" value=
                                      "input${childItem.item.id}" /></c:import> </td>
                            </tr>
                        </table>
                    </td>
                    </c:forEach>
                </tr>
            </table>
        </td>
    </tr>
</c:if>
</c:if>

</c:otherwise>
    <%--end comment here to see problem with this part of the JSP, and
  include an <c:otherwise></c:otherwise>--%>

</c:choose>

<c:set var="displayItemNum" value="${displayItemNum + 1}" />
<c:set var="itemNum" value="${itemNum + 1}" />

</c:forEach>
</table>

<!--   return to top table:
possibly, stick the upcoming section as a new row in the above table, because it sometimes displays beneath this
table-->

<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
    <!--   style="padding-right: 50px"-->
    <tr>
        <td valign="bottom" nowrap="nowrap">
            <a href="#top">&nbsp;&nbsp;<fmt:message key="return_to_top" bundle="${resword}"/></a>
        </td>
        <td align="right" valign="bottom">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <c:choose>
                        <c:when test="${stage !='adminEdit' && section.lastSection}">
                            <c:choose>
                                <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
                                    <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                 onclick="sm('box', this, 730,100);">
                                    </td>
                                    <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                </c:when>
                                <c:otherwise>
                                    <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                 onclick="displayMessageFromCheckbox(this)">
                                    </td>
                                    <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <td colspan="2">&nbsp;</td>
                        </c:otherwise>
                    </c:choose>
                    <td><input type="submit" name="submittedResume" value="<fmt:message key="save" bundle="${resword}"/>" class=
                      "button_medium" /></td>
                    <td><input type="submit" name="submittedExit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium" onClick="return checkEntryStatus('DataStatus_bottom');" /></td>

                    <td valign="bottom"><img name="DataStatus_bottom" alt="<fmt:message key="data_status" bundle="${resword}"/>" src="images/icon_UnchangedData.gif">&nbsp;</td>


                </tr>
            </table>
        </td>
    </tr>
</table>

<!-- End Table Contents -->

</form>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<div id="testdiv1" style=
  "position:absolute;visibility:hidden;background-color:white"></div>
</div>

</body>
</html>