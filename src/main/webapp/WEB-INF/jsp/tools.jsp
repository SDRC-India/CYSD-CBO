<!-- 
@author Harsh Pratyush (harsh@sdrc.co.in)
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>

<html >
<head>
<title>CYSD-CBO-Monitoring| Resources</title>
<link rel="shortcut icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<link rel="icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<link rel="stylesheet" href="resources/css/bootstrap.min.css">
<link href="https://fonts.googleapis.com/css?family=Questrial"
	rel="stylesheet">
<link rel="stylesheet" href="resources/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/css/customLoader.css">
<link rel="stylesheet" href="resources/css/style.css">

<%-- <spring:url value="${pageContext.request.contextPath}/webjars/jquery/2.0.3/jquery.min.js" var="jQuery" /> --%>
<script src="${pageContext.request.contextPath}/webjars/jquery/2.0.3/jquery.min.js"></script>
<%-- <spring:url value="/webjars/bootstrap/3.1.1/js/bootstrap.min.js" --%>
<%-- 	var="bootstrapjs" /> --%>
<%-- <script src="${bootstrapjs}"></script> --%>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<%-- <spring:url value="${pageContext.request.contextPath}/webjars/angularjs/1.2.16/angular.min.js"
	var="angularmin" /> --%>
<script src="${pageContext.request.contextPath}/webjars/angularjs/1.2.16/angular.min.js" type="text/javascript"></script>
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
</head>

<body>
	<jsp:include page="fragments/header.jsp"></jsp:include>
	<div class="container-fluid content-section">
	 <div class="col-md-12 heading-contact ">
          <h3> Resources </h3>
              </div>

      <div class="col-md-12 ">
          <table class="table table-responsive table-striped factsheet">
            <thead class="toolsContent">
              <tr>
                <th>Files </th>
                <th style="text-align:center;">Download</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="fontSIze">CYSD Section Score</td>
                <td style="text-align:center;"><a href="resources/files/Cysd_section_score.pdf" download><i class="fa fa-2x fa-file-pdf-o"></i></a></td>
              </tr>
               <tr>
                <td class="fontSIze">User Guide</td>
                <td style="text-align:center;"><a href="resources/files/CYSDCBO User Guide.pdf" download><i class="fa fa-2x fa-file-pdf-o"></i></a></td>
              </tr>
           </tbody>
          </table>
          

	  <br/>
      </div>
    </div> <br>
</body>
<jsp:include page="fragments/footer.jsp"></jsp:include>
</html>