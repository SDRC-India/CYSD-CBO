<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="org.sdrc.cysdcbo.util.Constants"%>


<html lang="en">
<head>
<title>CYSD-CBO-Monitoring|Contact</title>
<link rel="shortcut icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<link rel="icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<meta name="viewport" content="width=device-width, initial-scale=1">
<spring:url value="/webjars/jquery/2.0.3/jquery.min.js" var="jQuery" />
<script src="${jQuery}"></script>
<spring:url value="/webjars/bootstrap/3.1.1/js/bootstrap.min.js"
	var="bootstrapjs" />
<script src="${bootstrapjs}"></script>
<script src="resources/js/bootstrap-dropdownhover.min.js"></script>
<spring:url value="/webjars/bootstrap/3.1.1/css/bootstrap.min.css"
	var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<link rel="stylesheet" href="resources/css/bootstrap-dropdownhover.min.css">
<link rel="stylesheet" href="resources/css/customLoader.css">
<link href='http://fonts.googleapis.com/css?family=Ubuntu:400,300'
	rel='stylesheet' type='text/css'>
<spring:url value="resources/css/style.css" var="styleCss" />
<link href="${styleCss}" rel="stylesheet" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
<spring:url value="/webjars/jquery-ui/1.10.3/themes/base/jquery-ui.css"
	var="jQueryUiCss" />
<link href="${jQueryUiCss}" rel="stylesheet"></link>
<spring:url value="/webjars/font-awesome/4.6.1/css/font-awesome.min.css"
	var="fontawesomeCss" />
<link href="${fontawesomeCss}" rel="stylesheet" />
</head>

<style type="text/css">
.node {
	cursor: pointer;
}

.node circle {
	fill: #fff;
	stroke: steelblue;
	stroke-width: 1.5px;
}

.node text {
	cursor: pointer;
	font: 10px sans-serif;
}

.link {
	fill: none;
	stroke: #ccc;
	stroke-width: 1.5px;
}
</style>
<body>
	<jsp:include page="fragments/header.jsp"></jsp:include>
	<div class="container-fluid content-section">
		<div class="col-md-12 heading-contact"><h3>Contact</h3></div>
		<div class="col-md-6" style="margin-bottom: 50px;">

			<h5> Centre for Youth and Social Development</h5>
			E-1, Institutional Area,<br>
			 Gangadhar Meher Marg,Bhubaneswar-751013, India <br><br>
			<b>Mail to: </b> <a href="mailto:info@cysd.org">info@cysd.org</a>/<a href="mailto:cysd@cysd.org">cysd@cysd.org</a><br>
		</div>
		<div class="col-md-6 text-center">
			<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3741.8019385169287!2d85.81861321449075!3d20.30846368639297!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3a1909b904217745%3A0x4f5d082bb59ce9ba!2sCentre+for+Youth+and+Social+Development!5e0!3m2!1sen!2sin!4v1518271708919" width="100%" height="300"  style="border:0 "></iframe>
		</div>
	</div>
</body>
<jsp:include page="fragments/footer.jsp"></jsp:include>
</html>
