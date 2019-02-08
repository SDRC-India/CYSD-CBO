<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="org.sdrc.cysdcbo.util.Constants"%>
<!-- @author Harsh Pratyush (harsh@sdrc.co.in)
 -->

<html lang="en" ng-app="dashboardMainApp">
<%-- <%
	if (request.getSession().getAttribute(Constants.USER_PRINCIPAL) == null) {
		response.sendRedirect("");
	}
%> --%>
<head>
<title>CYSD-CBO-Monitoring| Dashboard</title>
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
ul.dropdown-menu{
	max-height: 300px;
    overflow-y: auto;
    right: 0;
    left: auto;
    width: 384px;
   border-radius: 0px;
}

#dataTable.table-bordered{
		border: none;
	}
	#dataTable th{
		border-top: 1px solid #ddd;
		border-bottom: none;
		background-color: #f4f4f4;
	}
	
	#dataTable th:first-child{
		text-align:left;
	}
	#dataTable.table-no-total td{
		text-align:center;
	}
	#dataTable.table-no-total td:first-child{
		background-color: #f4f4f4;
		color: #000;
		text-align:left;
	}
	
	#dataTable.table-no-total tr:last-child{
	font-weight: normal !important;
	font-size: 13px;
	}
	
</style>
<body ng-controller="DashboardController" ng-cloak>
	<!-- spinner -->
	<div id="spinner" class="loader" style="display: none;"></div>
	<div id="loader-mask" class="loader" style="display: none;"></div>
	<!-- /spinner -->
	<jsp:include page="fragments/header.jsp"></jsp:include>

	<div id="wrapper">
		<div id="containerId" class="content">
			<!-- 			<div class="container"> -->
			<div class="container-fluid">
				<div class="row mar-bot-10" style="margin: auto;">
					<div class="col-md-12">
						<div class="row">
							<div class="col-md-2 page-title-head">
								<h3 class="page_header">Dashboard</h3>
							</div>
							
					 		<div class="col-md-10">
							<ul class="numberOfCbosList">
							<li ng-repeat="sector in sectorKeys  track by $index">
							Number of {{sector}} Assessed : {{cboAssessed[sector]}} &nbsp; &nbsp;
							</li>
							</ul>
							</div>  

						</div>	
					<div class="row">
							<div class="col-md-12">
							<div style="position: fixed; top: 115px; right: -114px;; z-index: 1000;"
								class="download-container" ng-hide="roleId == 5 || !tableData.length"
								ng-class="{'remove': !chartData.length &amp;&amp; !pieChartData.length}">
								<button type="button" id="pdfDownloadBtn"
									class="btn pdfDownloadBtn" title="Download PDF"
									ng-click="sdrc_export()">
									<i class="fa fa-lg fa-file-pdf-o"></i> &nbsp; Download PDF
								</button>
							</div>
								<div style="position: fixed; top: 175px; right: -121px; z-index: 1000;"
									class="download-container-excel" ng-hide="roleId == 5 || !tableData.length"
									ng-class="{'remove': !chartData.length &amp;&amp; !pieChartData.length}">
									<button type="button" id="excelDownloadBtn"
										class="btn excelDownloadBtn" title="Download Excel"
										ng-click="sdrc_export()">
										<i class="fa fa-lg fa-file-excel-o"></i> &nbsp; Download Excel
									</button>
								</div>
								<div class="row">
								<div class="col-md-12 ">
									<div class="row">
									<div class="col-md-4">
										<div class="select-container text-center">
											<div class="input-group">
												<input type="text" placeholder="Time Period *"
													id="timeperiod" class="form-control not-visible-input"
													ng-class="allTimePeriods.length < 2 ?'':'input-text'"
													name="indicator" readonly=""
													ng-model="selectedTimePeriod.timePeriod">
												<div class="input-group-btn" style="position: relative;">
													<button data-toggle="dropdown"
														class="btn btn-color dropdown-toggle" type="button" ng-disabled="allTimePeriods.length < 2">
														<i class="fa fa-calendar"></i>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li ng-repeat="timeperiod in allTimePeriods"
															ng-click="resetPushpinDataCallDone(); selectTimePeriod(timeperiod)"><a href="">{{timeperiod.timePeriod}}</a></li>
													</ul>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="select-container text-center">
											<div class="input-group">
												<input type="text" placeholder="Indicator *" id="indicator"
													class="form-control not-visible-input input-text" name="indicator"
													readonly="" ng-model="selectedSector.label">
												<div class="input-group-btn" style="position: relative;">
													<button data-toggle="dropdown"
														class="btn btn-color dropdown-toggle" type="button">
														<i class="fa fa-list"></i>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li ng-repeat="sector in sectors"
															ng-click="resetPushpinDataCallDone(); selectSector(sector)"><a
															href="">{{sector.label}}</a></li>
													</ul>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="select-container dist-list text-center">
											<div class="input-group">
												<input type="text" ng-class="{'noBorder': roleId == 3 }"
													placeholder="Districts *" id="Dist"
													class="form-control not-visible-input input-text" name="Dist"
													readonly="" ng-model="selectedDistrict.areaName">
												<div class="input-group-btn" style="position: relative;">
													<button data-toggle="dropdown" ng-if="roleId != 3"
														class="btn btn-color dropdown-toggle" type="button">
														<i class="fa fa-list"></i>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li ng-repeat="district in allDistricts"
															ng-class="{'allDistDropdown': $index == 0}"
															ng-click="resetPushpinDataCallDone(); selectDistrict(district)"><a
															href="">{{district.areaName}}</a></li>
													</ul>
												</div>
											</div>
										</div>
									</div>
								
								</div>
								</div>
							</div>
						</div>
				
						<div class="row">
							<div class="col-md-12" style="margin: 10px auto 25px;">
								<div id="mapContainerId" style="max-width: 98%;margin-left: 13px;">

									<div class="col-md-4 area_level" data-html2canvas-ignore="true">
										<!-- 									<h4 class="bar_style">Vector View</h4> -->
										<ul class="list-unstyled list-inline arealist">
											<li
												ng-click="resetPushpinDataCallDone(); selectParentSector(parentSector)"
												ng-repeat="parentSector in parentSectors"><a
												ng-class="{active:selectedParentSector.formXpathScoreId == parentSector.formXpathScoreId}"
												href="#"> <i ng-if="parentSector.formXpathScoreId == 1"
													class="fa fa-map-marker"></i> <i
													ng-if="parentSector.formXpathScoreId == 15" class="fa fa-plus"></i> <i
													ng-if="parentSector.formXpathScoreId == 29" class="fa fa-star"></i>
													{{parentSector.label}}
											</a></li>

											<!-- <li ng-click="selectSector('27', 'CHC')"><a
										ng-class="{active:selectedSector == 27}" href="#"> <i
											class="fa fa-map-marker"></i> CHC
									</a></li>

									<li ng-click="selectSector('30', 'PHC')"><a
										ng-class="{active:selectedSector == 30}" href="#"> <i
											class="fa fa-map-marker"></i> PHC
									</a></li> -->
										</ul>
									</div>
									<div style="position: relative;">
										<section class="legends" style=" min-width: 210px; max-width: 400px;">

											<ul>
												<li class="legend_list">
													<h4>{{selectedSector.label}}</h4>
												</li>
												
												<li class="legend_list" style="margin-bottom: 4px;">
													<h5>CBO Assessed : {{noOfFacilities}}</h5>
												</li>
												
												<li class="legend_list ng-scope"><span
													class="legend_key ">below 60</span> <span
													class="firstslices legnedblock"> </span> (<span
													style="color: red;">{{redMarkers}}</span>)</li>
												<!-- end ngRepeat: legend in legends -->
												<li class="legend_list "><span class="legend_key ">60
														to 79</span> <span class="secondslices legnedblock"> </span> (<span
													style="color: orange;">{{orangeMarkers}}</span>)</li>
												<!-- end ngRepeat: legend in legends -->
												<li class="legend_list "><span
													class="legend_key ng-binding">80 and above</span> <span
													class="fourthslices legnedblock"> </span> (<span
													style="color: green;">{{greenMarkers}}</span>)</li>
											</ul>
										</section>
										<google-map center="map.center" zoom="map.zoom"
											draggable="true"  > <markers class="pushpin"
											models="map.markers" coords="'self'" icon="'icon'"
											events="map.events" click='onClick()'
											 > <windows show="'showWindow'"
											closeClick="'closeClick'" options='pixelOffset'>
										<p ng-non-bindable
											style="width: 130px; height: 30px; font-size: 15px; color: #313e4d; display: inline;">
											<strong>{{title}}</strong><br> <strong>Score:{{dataValue}}%</strong>
										</p>
										</windows> </markers> <polygon static="true"
											ng-repeat="p in polygons track by p.id" path="p.path"
											stroke="p.stroke" visible="p.visible" geodesic="p.geodesic"
											fill="p.fill" fit="false" editable="p.editable"
											draggable="p.draggable"></polygon> </google-map>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12" style="margin-bottom: 25px;">
								<div class="">
									<div class="col-md-12 selection-bar">
										<h5>
											{{selectedParentSector.label}} &nbsp;&nbsp; <i
												class="fa fa-angle-double-right color-green"
												aria-hidden="true"></i> &nbsp;&nbsp;
											{{selectedDistrict.areaName}} &nbsp;&nbsp; <i
												class="fa fa-angle-double-right color-green"
												aria-hidden="true"></i> &nbsp;&nbsp; {{selectedPushpin}}
										</h5>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12" id="charts">
								<div class="col-md-6">
									<div id="myTabContent" class="tab-content">
										<div id="home" class="tab-pane fade active in">

											<sdrc-spider></sdrc-spider>

										</div>
									</div>
								</div>
								<div class="col-md-6 ">
						<div class=" table-responsive" style="width: 100%; margin-top: 35px;">
				<table items="tableData" show-filter="true" cellpadding="0"
					cellspacing="0" border="0" class="assign-height-similar-spider dataTable table-no-total table table-bordered"
					id="dataTable" >
					<thead>
						<tr>
							<th ng-repeat="column in columns"
								ng-click="order(column)" style="position: relative;">{{column}}
								
							</th>
						</tr>
					</thead>
					<tbody>
						<tr
							ng-repeat="rowData in tableData">
							<td ng-repeat="column in columns" 
							ng-class="column=='Section/Sub-Section'?'':rowData[column]<60?'firstslices':rowData[column]<80?'secondslices':'fourthslices'"
								>{{rowData[column]}}</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
								</div>
							</div>
						</div>
						<div class="modal fade" id="noDataModall" role="dialog"
							style="margin-top: 12%;">
							<div class="modal-dialog">

								<!-- Modal content-->
								<div class="modal-content modal-info">
									<div class="modal-header"
										style="background-color: #2f515b; color: #fff;">
										<button type="button" class="close" data-dismiss="modal">&times;</button>
										<h4 class="modal-title" style="text-align: center;">Info</h4>
									</div>
									<div class="modal-body">
										<p
											style="text-align: center; font-size: 20px; margin-bottom: -10px;">NO
											DATA AVAILABLE</p>
									</div>
									<div class="modal-footer" style="text-align: center;">
										<button type="button"
											class="btn btn-default btn-view text-center"
											data-dismiss="modal">Ok</button>
									</div>
								</div>

							</div>
						</div>

						<!-- //end  -->
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="fragments/footer.jsp"></jsp:include>
	<script
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBedTd_YjXAiOM8I34K2MRUzqso2wu0wlA&v=3.31"
		type="text/javascript"></script>
	<script src="resources/js/lodash.min.js" type="text/javascript"></script>
	<spring:url value="/webjars/angularjs/1.2.16/angular.min.js"
		var="angularmin" />
	<script src="${angularmin}" type="text/javascript"></script>
	<spring:url value="/webjars/angularjs/1.2.16/angular-animate.min.js"
		var="angularaAnimatemin" />
	<script src="${angularaAnimatemin}" type="text/javascript"></script>
<!-- 	<script src="resources/js/ng-table.min.js" type="text/javascript"></script>
	<script src="resources/js/ng-table-export.js" type="text/javascript"></script> -->
	<spring:url
		value="/webjars/angular-loading-bar/0.4.3/loading-bar.min.js"
		var="loadingbarmin" />
	<script src="${loadingbarmin}" type="text/javascript"></script>
	<spring:url value="/webjars/d3js/3.4.6/d3.min.js" var="d3js" />
	<script src="${d3js}"></script>
	<spring:url value="/webjars/jquery-ui/1.10.3/ui/jquery.ui.core.js"
		var="jQueryUiCore" />
	<script src="${jQueryUiCore}"></script>
	<script src="resources/js/html2canvas.js"></script>
	<!-- 	end -->
	<script src="resources/js/angular-google-maps.min.js"
		type="text/javascript"></script>
	<script type="text/javascript">
		var app = angular.module("dashboardMainApp", [
				'google-maps']);
		var myAppConstructor = angular.module("dashboardMainApp");
	</script>
	<%--  <spring:url value="resources/js/dashboard.js" var="app" />
	<script src="${app}" type="text/javascript"></script>  --%>
	<spring:url value="resources/js/angularcontrollers/dashboardCtrl.js"
		var="ctrl" />
	<script src="${ctrl}" type="text/javascript"></script>
	<script src="resources/js/angularservices/services.js"
		type="text/javascript"></script>
	<script src="resources/js/angularservices/dashboardDirective.js"
		type="text/javascript"></script>
	<script src="resources/js/angularservices/sdrc.export.js"
		type="text/javascript"></script>
	<script src="resources/js/underscore.js" type="text/javascript"></script>
	<spring:url value="/resources/js/angular_bootstrap_ui.js"
		var="bootstrapUI" />
	<script src="${bootstrapUI}"></script>

	<spring:url value="/resources/js/ui-bootstrap-tpls-1.3.2.min.js"
		var="bootstraPtpls" />
	<script src="${bootstraPtpls}"></script>
	<script>
		$(document).ready(function() {
			sdrc_export.export_pdf("", "pdfDownloadBtn");
			sdrc_export.export_excel("", "excelDownloadBtn");
		});
	</script>
</html>
