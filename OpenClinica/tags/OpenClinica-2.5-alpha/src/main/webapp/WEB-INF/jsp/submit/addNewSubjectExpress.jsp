<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
    <%=org.akaza.openclinica.i18n.util.HtmlUtils.getCalendarPopupCode("cal1","testdiv1")%>
  </SCRIPT>
  </table>
  <c:set var="tabCount" value="${1}"/>
  <table border="0" cellpadding="0" cellspacing="0" width="100%"
         style="background-color:#CCCCCC">
    <tr id="AddSubjectRow1" style="display:none">
		<form id="subjectForm" name="subjectForm" action="AddNewSubject" method="post">
		   
			<td class="table_cell_left" colspan="2" nowrap>
			  <jsp:include page="../include/showSubmitted.jsp" />
			  <input type="hidden" name="addWithEvent" value="1"/>
				<c:choose>
			      <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto non-editable'}">
			       <input type="text" value="<c:out value="${label}"/>" tabindex ="<c:out value="${tabCount}"/>" size="12" class="formfield" disabled>
			       <input type="hidden" name="label" value="<c:out value="${label}"/>">
			      </c:when>
			      <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto editable'}">
			       <input onfocus="this.select()" onclick = "if (this.value == '<fmt:message key="study_subject_ID" bundle="${resword}"/>'){ this.value =''}" type="text" name="label" tabindex ="<c:out value="${tabCount}"/>" value="<c:out value="${label}"/>" size="12" class="formfield">
			      </c:when>
			      <c:otherwise>
			        <!--<input type="text" name="label" value="<c:out value="${label}"/>" size="5" class="formfield">-->
			        <input onfocus="this.select()" onclick = "if (this.value == '<fmt:message key="study_subject_ID" bundle="${resword}"/>'){ this.value =''}" type="text" name="label" tabindex ="<c:out value="${tabCount}"/>" value="<fmt:message key="study_subject_ID" bundle="${resword}"/>" size="12" class="formfield">
			      </c:otherwise>
			    </c:choose>
			<span class="formlabel">*</span></td>
			<c:set var="tabCount" value="${tabCount+1}"/>
			<c:choose>
			<c:when test="${study.studyParameterConfig.genderRequired !='false'}">						
			<td valign="top" class="table_cell" nowrap>
			<select name="gender" class="formfield" tabindex="<c:out value="${tabCount}"/>">
	
				<option value=""><fmt:message key="gender" bundle="${resword}"/>:</option>
				<option value="m"><fmt:message key="male" bundle="${resword}"/></option>
				<option value="f"><fmt:message key="female" bundle="${resword}"/></option>
			</select><span class="formlabel">*</span></td>
			 <c:set var="tabCount" value="${tabCount+1}"/>
			</c:when>
			<c:otherwise>
		    	<input type="hidden" name="gender" value="">
			</c:otherwise>
			</c:choose> 
			 
			<c:set var="count" value="0"/>
			<c:forEach var="groupClass" items="${studyGroupClasses}">			
					
			<td valign="top" class="table_cell" nowrap>
	
			  <select name="studyGroupId<c:out value="${count}"/>" tabindex="<c:out value="${tabCount}"/>" class="formfield">
	
				<option value=""><c:out value="${groupClass.name}"/>:</option>
				 <c:forEach var="studyGroup" items="${groupClass.studyGroups}">
				   <option value="<c:out value="${studyGroup.id}"/>"><c:out value="${studyGroup.name}"/></option>
				 </c:forEach>
			  </select>
			</td>
				<c:set var="count" value="${count+1}"/>	
				<c:set var="tabCount" value="${tabCount+1}"/>
			</c:forEach>
					
			<td valign="top" colspan="6" class="table_cell" nowrap>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td valign="top">
	
					<select name="studyEventDefinition" class="formfield" tabindex="<c:out value="${tabCount}"/>">
	                   
						<option value=""><fmt:message key="event" bundle="${resword}"/>:</option>
						<c:forEach var="event" items="${allDefsArray}">
						  <option value="<c:out value="${event.id}"/>"><c:out value="${event.name}"/></option>
						</c:forEach>											
						</select>
					</td>
					 <c:set var="tabCount" value="${tabCount+1}"/>
					<td valign="top" align="right">
					 <input onfocus="if (this.value == '<fmt:message key="location" bundle="${resword}"/>'){ this.value =''}" type="text" name="location" tabindex="<c:out value="${tabCount}"/>" size="8" value="<fmt:message key="location" bundle="${resword}"/>" class="formfield" />*
					</td>
					 <c:set var="tabCount" value="${tabCount+1}"/>
					<td valign="top">
					<input onfocus="if (this.value == '<fmt:message key="eventMMDDYYYY" bundle="${resword}"/>'){ this.value =''}" type="text" name="enrollmentDate" size="15" value="<fmt:message key="eventMMDDYYYY" bundle="${resword}"/>" tabindex="<c:out value="${tabCount}"/>" class="formfield" />
					</td>
					 <c:set var="tabCount" value="${tabCount+1}"/>				
				     <td valign="top">*<A HREF="#" onClick="cal1.select(document.subjectForm.enrollmentDate,'anchor1','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" TITLE="cal1.select(document.subjectForm.enrollmentDate,'anchor1','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" /></a> 
					</td>
				</tr>
			</table>
			</td>
			<td valign="top" align="right" class="table_cell" nowrap>
				<input type="submit" name="addSubject" value="<fmt:message key="add" bundle="${resword}"/>" tabindex="999" class="button_search" />
			</td>
			
		</tr>
		<tr id="AddSubjectRow2" style="display:none">
			<td valign="top" align="left" colspan="2" class="table_cell_left" nowrap>
			 <c:choose>
	           <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='required'}">
			    &nbsp;<input onfocus="if (this.value == '<fmt:message key="person_ID" bundle="${resword}"/>'){ this.value =''}" type="text" name="uniqueIdentifier" value="<fmt:message key="person_ID" bundle="${resword}"/>" size="12" tabindex="<c:out value="${tabCount}"/>" class="formfield"><span class="formlabel">*</span>
			   </c:when>
			   <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='optional'}">
			    &nbsp;<input onfocus="if (this.value == '<fmt:message key="person_ID" bundle="${resword}"/>'){ this.value =''}" type="text" name="uniqueIdentifier" value="<fmt:message key="person_ID" bundle="${resword}"/>" size="12" tabindex="<c:out value="${tabCount}"/>" class="formfield">
			   </c:when>
			   <c:otherwise>
			    &nbsp;<input type="hidden" name="uniqueIdentifier" value="">
			   </c:otherwise>
			 </c:choose> 
			  <c:set var="tabCount" value="${tabCount+1}"/> 
			</td>
			<td valign="top" align="left" colspan="3" class="table_cell" nowrap>
			<table border="0">
				<tr>
					<td valign="top" align="right">
					 <c:choose>
	                    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
						&nbsp;<input onfocus="if (this.value == '<fmt:message key="DOB" bundle="${resword}"/>'){ this.value =''}" type="text" name="dob" size="20" value="<fmt:message key="DOB" bundle="${resword}"/>" tabindex="<c:out value="${tabCount}"/>" class="formfield"><span class="formlabel">*</span>
						</td>
						 <td valign="top" align="left"><a href="#" onClick="cal1.select(document.subjectForm.dob,'anchor2','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" TITLE="cal1.select(document.subjectForm.dob,'anchor2','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" NAME="anchor2" ID="anchor2"> <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" /></a>
					    </c:when>
					    <c:when test="${study.studyParameterConfig.collectDob == '2'}">
					     &nbsp;<input onfocus="if (this.value == '<fmt:message key="YOB" bundle="${resword}"/>'){ this.value =''}" type="text" name="yob" size="15" value="YOB: YYYY" class="formfield" tabindex="<c:out value="${tabCount}"/>"/><span class="formlabel">*</span>
					    </c:when>
					    <c:otherwise>
					      &nbsp;<input type="hidden" name="dob" value="" />
					    </c:otherwise>
					  </c:choose>
					    
					 <c:set var="tabCount" value="${tabCount+1}"/>
					</td>
				</tr>
			</table>
			</td>
			<td valign="top" nowrap align="right" colspan="8" class="table_cell"><a href="AddNewSubject"><fmt:message key="enter_full_record_details" bundle="${resword}"/></a></td>
		</tr>
	    </form>
	   
								
	 
										
		<!-- End Data -->
					
				
			
							
		</table>

		<!-- End Table Contents -->			
		</td>

	</tr>
	
		
		
 

  </table>
	
			
	
 <!-- End Table 0 -->	
</div>
</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>


<DIV ID="testdiv1" STYLE="position:absolute;z-index:5;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
