<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="session" id="newUserBean" class="org.akaza.openclinica.bean.login.UserAccountBean"/>
<c:choose>
<c:when test="${userBean != null && userRole != null && userRole.role.name != 'invalid'}">
<jsp:include page="../include/home-header.jsp"/>
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
</c:when>
<c:otherwise>
<jsp:include page="../login-include/login-header.jsp"/>

<jsp:include page="../login-include/request-sidebar.jsp"/>
</c:otherwise>
</c:choose>


<!-- Main Content Area -->

<h1>Contact OpenClinica Administrator</h1>
<P>Please fill out the following form to contact OpenClinica Administrator and ask questions.<p>
<jsp:include page="../login-include/login-alertbox.jsp"/>

<form action="Contact" method="post"> 
All fields are required.<br>
<input type="hidden" name="action" value="submit"> 
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="0">
  <tr><td class="formlabel">Your Name:</td><td>

<div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${name}"/>" class="formfieldXL"></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include>
</td></tr>
  
 <tr><td class="formlabel">Your Email:</td><td>

<div class="formfieldXL_BG"><input type="text" name="email" value="<c:out value="${email}"/>" class="formfieldXL"></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email"/></jsp:include>
</td></tr>
  
 <tr><td class="formlabel">Subject of your question:</td><td>

<div class="formfieldXL_BG"><input type="text" name="subject" value="<c:out value="${subject}"/>" class="formfieldXL"></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subject"/></jsp:include>
</td></tr>  
<tr><td class="formlabel">
Your Message:</td>
 <td><div class="formtextareaXL4_BG">
<textarea name="message" rows="4" cols="50" class="formtextareaXL4"><c:out value="${message}"/></textarea>
</div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="message"/></jsp:include>
</td></tr> 

</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
 <table border="0" cellpadding="0">
 <tr><td>
 <input type="submit" name="submit" value="Submit" class="button_medium">
 </td>
 <td><input type="button" name="cancel" value="Cancel" class="button_medium" onclick="javascript:window.location.href='MainMenu'"></td>
 </tr> 
 </table>
</form>

<jsp:include page="../login-include/login-footer.jsp"/>
