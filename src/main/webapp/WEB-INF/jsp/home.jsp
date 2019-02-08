<!-- 
@author Laxman (laxman@sdrc.co.in)
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>

<html>
<head>

<title>CYSD-CBO-Monitoring|Home</title>
<link rel="shortcut icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<link rel="icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<link rel="stylesheet" href="resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="resources/css/bootstrap-dropdownhover.min.css">
<link rel="stylesheet" href="resources/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/css/jquery-ui.css">
<link rel="stylesheet" href="resources/css/style.css">
<%@taglib prefix="serror" uri="/WEB-INF/ErrorDescripter.tld"%>
<spring:url value="/webjars/jquery/2.0.3/jquery.min.js" var="jQuery" />
<script src="${jQuery}"></script>
<spring:url value="/webjars/bootstrap/3.1.1/js/bootstrap.min.js"
	var="bootstrapjs" />
<script src="${bootstrapjs}"></script>
<!-- <script src="resources/js/angular.min.js"></script> -->
<script src="resources/js/bootstrap-dropdownhover.min.js"></script>
</head>

<body>
	<jsp:include page="fragments/header.jsp"></jsp:include>
	<div id="errMsg" class="text-center">
		<serror:Error id="msgBox" errorList="${formError}"
			cssInfClass="${className}">
		</serror:Error>
	</div>
	<div id="mymain">
		<section id="homeslide">
		<jsp:include page="login.jsp"></jsp:include>
		</section>
	</div>
	
	<!--end of thematic and chklist  -->
	<jsp:include page="fragments/footer.jsp"></jsp:include>

</body>
<!-- <script src="resources/js/angularController/loginController.js"></script> -->
<script type="text/javascript">
	$("#msgBox").show().delay(2000).fadeOut(400);
</script>
</html>