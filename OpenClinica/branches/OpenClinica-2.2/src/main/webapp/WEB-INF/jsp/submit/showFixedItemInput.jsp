<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />

<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="defValue" value="${param.defaultValue}" />
<c:set var="respLayout" value="${param.respLayout}" />

<c:if test="${(respLayout eq 'Horizontal' || respLayout eq 'horizontal')}">
  <c:set var="isHorizontal" value="${true}" />
</c:if>

<c:choose>
  <c:when test="${empty displayItem.metadata.responseSet.value}">
    <c:set var="inputTxtValue" value="${defValue}"/>
  </c:when>
  <c:otherwise>
     <c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/>
  </c:otherwise>
</c:choose>

<c:if test='${inputType == "text"}'>
	<input type="text" name="input<c:out value="${itemId}" />" value="<c:out value="${inputTxtValue}"/>" style="background:white;color:#4D4D4D;"/>
</c:if>
<c:if test='${inputType == "textarea"}'>
	<textarea name="input<c:out value="${itemId}" />" rows="5" cols="40" style="background:white;color:#4D4D4D;"><c:out value="${inputTxtValue}"/></textarea>
</c:if>
<c:if test='${inputType == "checkbox"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			 <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="checked" />
      </c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<input type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
	</c:forEach>
</c:if>
<c:if test='${inputType == "radio"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			 <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="checked" />
      </c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<input type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
	</c:forEach>
</c:if>
<c:if test='${inputType == "single-select"}'>
	<select name="input<c:out value="${itemId}"/>" class="formfield" style="background:white;color:#4D4D4D;">
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
			<c:choose>
				<c:when test="${option.selected}"><c:set var="checked" value="selected" /></c:when>
				 <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="selected" />
      </c:when>
        <c:otherwise><c:set var="checked" value="" /></c:otherwise>
			</c:choose>
			<option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
		</c:forEach>
	</select>
</c:if>
<c:if test='${inputType == "multi-select"}'>
	<select multiple name="input<c:out value="${itemId}"/>" style="background:white;color:#4D4D4D;">
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
			<c:choose>
				<c:when test="${option.selected}"><c:set var="checked" value="selected" /></c:when>
				 <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="selected" />
      </c:when>
        <c:otherwise><c:set var="checked" value="" /></c:otherwise>
			</c:choose>
			<option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
		</c:forEach>
	</select>
</c:if>
