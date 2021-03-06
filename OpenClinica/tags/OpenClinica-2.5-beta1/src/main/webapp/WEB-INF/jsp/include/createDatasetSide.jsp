<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
	
 <c:choose>
 <c:when test="${panel.orderedData}">
  <b><fmt:message key="study_events" bundle="${resword}"/>:</b><br>
	   <c:set var="count" value="0"/>
	   <c:set var="newEvent" value="0"/>
	   <c:set var="eventCount" value="0"/>		 
	   <c:forEach var='line' items="${panel.userOrderedData}">
			<c:if test="${line.colon}">
			 <c:choose>
			 <c:when test="${line.title=='Definition'}">
			  
			  <c:if test="${count >0 && eventCount>0}">
			      </table>
	             </td>
               </tr>
			  </c:if>
			  <c:if test="${eventCount==0}">
			   <table border="0" cellpadding="0" cellspacing="0" width="120">
			  </c:if>
			  <c:set var="count" value="${count+1}"/>
			  <c:set var="newEvent" value="1"/>
			  <c:set var="eventCount" value="${eventCount+1}"/>
			  <tr>
	           <td valign="top" width="10" class="leftmenu"><a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${eventCount}"/>'); 
		          javascript:setImage('ExpandGroup<c:out value="${eventCount}"/>','images/bt_Collapse.gif');"><img 
		          name="ExpandGroup<c:out value="${eventCount}"/>" src="images/bt_Expand.gif" border="0"></a></td>
	              <td valign="top" class="leftmenu"><a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${eventCount}"/>'); 
		            javascript:setImage('ExpandGroup<c:out value="${eventCount}"/>','images/bt_Collapse.gif');"><b><c:out value="${line.info}" escapeXml="false"/></b></a>
		       </td>
             </tr>  
             </c:when>
             <c:otherwise>
               <b><c:out value="${line.title}" escapeXml="false"/>: <c:out value="${line.info}" escapeXml="false"/></b>
                <br/>             
                <br/>
             </c:otherwise>
             </c:choose>           
             </c:if>
             <c:if test="${!line.colon}">
               <c:if test="${newEvent==1}">
                  <tr id="leftnavSubRow_SubSection<c:out value="${eventCount}"/>" style="display:none" valign="top">
	               <td colspan="3">
	               <table border="0" cellpadding="0" cellspacing="0" width="110">
               </c:if>
                <c:set var="newEvent" value="0"/>
                <c:set var="count" value="${count+1}"/>
                <tr>
                 <c:choose>
                   <c:when test="${line.lastCRF}">                  
		             <td valign="top" class="vline_B">
		           </c:when>
		           <c:otherwise>
		             <td valign="top" class="vline">
		          </c:otherwise> 
		         </c:choose>
		         <img src="images/leftbar_hline.gif"></td>
		         <td valign="top" class="leftmenu" style="font-size:11px; color:#789EC5"><c:out value="${line.info}" escapeXml="false"/></td>
	           </tr>
             </c:if>
			  
		  </c:forEach>
		   <c:if test="${count>0}">
		     </table>
	         </td>
            </tr>
		  </table>  
		  </c:if>
		  
		 <br><a href='SelectItems?eventAttr=1'><b><fmt:message key="event_attributes" bundle="${resword}"/></b></a><br><br>
        <a href='SelectItems?subAttr=1'><b><fmt:message key="subject_attributes" bundle="${resword}"/></b></a><br><br>
        <a href='SelectItems?CRFAttr=1'><b>CRF Attributes</b></a><br><br>
        <a href='SelectItems?groupAttr=1'><b>Group Attributes</b></a><br><br>
        <%--
        	<a href='SelectItems?discAttr=1'><b>Discrepancy Note Attributes</b></a><br><br>
        --%>
        <a href='ViewSelected'><b><fmt:message key="view_selected_items" bundle="${resword}"/></b></a>
	</c:when>
	<c:otherwise>
	  <c:if test="${newDataset.id<=0}">
	  <c:forEach var='line' items="${panel.data}">
		 <b><c:out value="${line.key}" escapeXml="false"/>:</b>&nbsp;
		  <c:out value="${line.value}" escapeXml="false"/>
	    <br/><br/>
	 </c:forEach>
	 </c:if>
	  <a href="javascript: openDocWindow('ViewSelected?status=html')"><b><fmt:message key="view_selected_items" bundle="${resword}"/></b></a><br><br>
	</c:otherwise>
 </c:choose>
