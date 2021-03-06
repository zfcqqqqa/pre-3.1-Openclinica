<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:import url="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>
<jsp:useBean scope='request' id='auditUserBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
 <h1><span class="title_Admin">
 <fmt:message key="view_user_log_for" bundle="${resword}"/>
 <c:out value="${auditUserBean.name}"/> (<c:out value="${auditUserBean.firstName}"/>&nbsp;<c:out value="${auditUserBean.lastName}"/>)
 </span></h1>

<jsp:include page="../include/alertbox.jsp" />
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showAuditEventRow.jsp" /></c:import>
<jsp:include page="../include/footer.jsp"/>
