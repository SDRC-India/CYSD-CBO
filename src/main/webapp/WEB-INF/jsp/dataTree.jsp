<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<!-- @author Harsh Pratyush (harsh@sdrc.co.in)
 -->
<html>
<head>
<title>CYSD-CBO-Monitoring| Datatree</title>
<link rel="shortcut icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<link rel="icon" href="resources/images/favicon.ico"
	type="image/x-icon">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<link rel="stylesheet" href="resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="resources/css/bootstrap-dropdownhover.min.css">
<link rel="stylesheet" href="resources/css/font-awesome.min.css">
<spring:url value="/webjars/jquery-ui/1.10.3/themes/base/jquery-ui.css"
	var="jQueryUiCss" />
<link href="${jQueryUiCss}" rel="stylesheet"></link>
<link rel="stylesheet" href="resources/css/customLoader.css">
<link href='http://fonts.googleapis.com/css?family=Ubuntu:400,300'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="resources/css/style.css">
<spring:url value="/webjars/jquery/2.0.3/jquery.min.js" var="jQuery" />
<script src="${jQuery}"></script>
<spring:url value="/webjars/bootstrap/3.1.1/js/bootstrap.min.js"
	var="bootstrapjs" />
<script src="${bootstrapjs}"></script>
<spring:url value="/webjars/font-awesome/4.6.1/css/font-awesome.min.css"
	var="fontawesomeCss" />
<link href="${fontawesomeCss}" rel="stylesheet" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
<!-- <script src="resources/js/angular.min.js"></script> -->
<script src="resources/js/bootstrap-dropdownhover.min.js"></script>
</head>

<body ng-app="dataTreeApp" ng-controller="DataTreeController" ng-cloak>
	<!-- spinner -->
	<div id="spinner" class="loader" style="display: none;"></div>
	<div id="loader-mask" class="loader" style="display: none;"></div>
	<!-- /spinner -->
	<jsp:include page="fragments/header.jsp"></jsp:include>
	<div id="mymain" class="container-fluid" >
		<div class="row mar-bot-10" style="margin: auto; margin-bottom:36px;">
		<div class="col-md-12 ">
		<div class="row" style="margin-left: -1px;">
				<div class="col-md-2 page-title-head">
								<h3 class="page_header">Data Tree</h3>
							</div>
							
								 		<div class="col-md-8">
							<ul class="numberOfCbosList">
							<li ng-repeat="sector in sectorKeys  track by $index">
							Number of {{sector}} Assessed : {{cboAssessed[sector]}} &nbsp; &nbsp;
							</li>
							</ul>
							</div> 				
		<div class="disclaimerText"
						style="margin: 15px; cursor: pointer; float: right;">
						<i class="fa fa-lg fa-info-circle"
							data-toggle="modal" data-target="#helpModal"
							data-backdrop="static" data-keyboard="false"></i>
					</div>
		</div>
			<div class="row" style="margin-left: -29px; margin-right: -29px;">
				<div class="col-md-12 col-sm-12" style="margin-bottom: 10px;">
					<div class="select-container text-center col-md-4 dist-list">
						<div class="input-group">
							<input type="text" placeholder="Timeperiod" 
								id="timeperiod" class="form-control not-visible-input"
								ng-class="(!isColor && !isValue) || allTimePeriods.length<2 ?'':'input-text'"
								name="district" readonly="" ng-model="selectedTimePeriod.timePeriod" ng-disabled="true">
							<div class="input-group-btn" style="position: relative;">
								<button data-toggle="dropdown"
									class="btn btn-color dropdown-toggle" type="button" ng-disabled="(!isColor && !isValue) || allTimePeriods.length<2">
									<i class="fa fa-calendar"  ng-disabled="!isColor && !isValue"></i>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li ng-repeat="timeperiod in allTimePeriods"
										ng-click="selectTimePeriod(timeperiod)"><a href="">{{timeperiod.timePeriod}}</a></li>
								</ul>
							</div>
						</div>
					</div>
					
									<div class="select-container text-center col-md-4 dist-list">
						<div class="input-group">
							<input type="text" placeholder="Filter District" 
								id="district" class="form-control not-visible-input"
								ng-class="!isColor && !isValue ?'':'input-text'"
								name="district" readonly="" ng-model="selectedDistrict.areaName" ng-disabled="!isColor && !isValue">
							<div class="input-group-btn" style="position: relative;">
								<button data-toggle="dropdown"
									class="btn btn-color dropdown-toggle" type="button" ng-disabled="!isColor && !isValue">
									<i class="fa fa-list"  ng-disabled="!isColor && !isValue"></i>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li ng-repeat="district in allDistricts"
										ng-class="{'allDistDropdown': $index == 0}"
										ng-click="selectDistrict(district)"><a href="">{{district.areaName}}</a></li>
								</ul>
							</div>
						</div>
					</div>
					
					<div class="select-container select-container text-center col-md-4" >
								<div class="input-group" id="searchDiv">
									<input class="form-control not-visible-input"
										ng-class="!isColor && !isValue ?'':'input-text'"
										type="text" id="search" placeholder="Search CBO"
										onkeyup="searchNode()" autocomplete="off" ng-disabled="!isColor && !isValue">
									<div class="input-group-btn" style="position: relative;">
									<button class="btn btn-color  dropdown-toggle" id="searchButton"
										 onclick="searchNode()" ng-disabled="!isColor && !isValue">
										<i class="fa fa-lg fa-search search-button-dataTree"></i>
									</button>
									</div>
								</div>
					</div>
					
				</div>
			</div>
			<div class="tree-bubble-container">
				<div class="col-md-6 tree-tab">
					<div id="myTabContent" class="tab-content chart-container-tab">
						<div id="home"
							class="tab-pane fade active in tree-chart-container">
							<sdrc-data-tree></sdrc-data-tree>
						</div>
					</div>
				</div>

				<div class="col-md-6 bubble-tab">


					<div class="chart-container-tab">
						<div class="text-right">

							<button id="btnColorBubble" type="button"
								class="btn btn-default bubble-chart-btn button mar-top-10"
								ng-show="isColor" ng-click="setBackTrue()">By Color</button>
							<button id="btnSortBubble" type="button"
								style="margin-right: 3px"
								class="btn btn-default bubble-chart-btn button mar-top-10"
								ng-show="isValue" ng-click="setBackTrue()">By Value</button>
							<button id="btnBack" type="button" style="margin-right: 3px"
								class="btn btn-default bubble-chart-btn button mar-top-10"
								ng-show="isBkBtn"
								ng-click="setBackFalse();setColorTrue();setValueTrue()">
								<i class="fa fa-lg fa-arrow-circle-o-left"></i>&nbsp; Back
							</button>


						</div>






						<section class="legendsDataTree dataTreeLegend">
						
						<div class="bubble-legend">
								<div>Total</div>
								<div class="totalSliceTree"></div>
								<div class="bubble-counter"
									ng-class="{'novisible': !isColor && !isValue}">{{bubbleDataModel.length}}</div>
						</div>
							
							<!-- 							<ul> -->
							<div class="bubble-legend">
								<div>below 60</div>
								<div class="firstslicesTree"></div>
								<div class="bubble-counter"
									ng-class="{'novisible': !isColor && !isValue}">{{redBubbleCount}}</div>
							</div>
							<div class="bubble-legend">
								<div>60 - 79</div>
								<div class="secondslicesTree"></div>
								<div class="bubble-counter"
									ng-class="{'novisible': !isColor && !isValue}">{{yellowBubbleCount}}</div>
							</div>
							<div class="bubble-legend">
								<div>80 and above</div>
								<div class="thirdslicesTree"></div>
								<div class="bubble-counter"
									ng-class="{'novisible': !isColor && !isValue}">{{greenBubbleCount}}</div>
							</div>
							<!-- 							</ul> -->
						</section>



						<div class="mar-top-5"></div>
						<div class="">
							<div id="myTabContent1" class="tab-content">
								<div id="home1" class="tab-pane fade active in">
									<bubble-chart></bubble-chart>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
	<div class="cboHelpDiv">
			<ul class="cboHelpList">
			<li ng-repeat="sections in treeDatas.children">
			<span class="sectionName">{{sections.name}} : </span> {{sections.fullName}} &nbsp; &nbsp;
			</li>
			</ul>
			
			</div>
			
				<div class="cboHelpDiv">
			<ul class="cboHelpList" ng-repeat="section in treeDatas.children">
			<li ng-repeat="sections in section.children">
			<span class="sectionName">{{sections.name}} : </span> {{sections.fullName}} &nbsp; &nbsp;
			</li>
			</ul>
			
			</div>
	<div class="trend-viz bubbleDetailView animate-show pull-right"
		style="width: 35%; position: absolute; left: 466px; top: 410px;"
		id="scrollingDiv" ng-animate=" 'animate' " ng-show="isTrendVisible">

		<div class="datatree_popover_content"></div>
	</div>
	<div id="helpModal" class="modal fade" role="dialog">
		<div class="modal-dialog helpModaldialog">

			<!-- Modal content-->
			<div class="modal-content helpModalContent">
				<div class="modal-body">
					<i style="float: right;"
						class="fa fa-2x fa-times-circle helpCloseBtn" data-dismiss="modal"></i>
					<img id="helpImg" alt="" src="resources/images/SingleClickHelp.png"
						style="width: 100%; height: auto;">
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
	<jsp:include page="fragments/footer.jsp"></jsp:include>
	<spring:url value="/webjars/angularjs/1.2.16/angular.min.js"
		var="angularmin" />
	<script src="${angularmin}" type="text/javascript"></script>
	<spring:url value="/webjars/angularjs/1.2.16/angular-animate.min.js"
		var="angularaAnimatemin" />
	<script src="${angularaAnimatemin}" type="text/javascript"></script>
	<script src="resources/js/ng-table.min.js" type="text/javascript"></script>
	<script src="resources/js/ng-table-export.js" type="text/javascript"></script>
	<spring:url
		value="/webjars/angular-loading-bar/0.4.3/loading-bar.min.js"
		var="loadingbarmin" />
	<script src="${loadingbarmin}" type="text/javascript"></script>
	<spring:url value="/webjars/d3js/3.4.6/d3.min.js" var="d3js" />
	<script src="${d3js}"></script>
	<script type="text/javascript">
		var app = angular.module("dataTreeApp", [ 'ngAnimate' ]);
		var myAppConstructor = angular.module("dataTreeApp");
	</script>
	<script src="resources/js/angularcontrollers/dataTreeCtrl.js"
		type="text/javascript"></script>
	<script src="resources/js/angularservices/dataTreeDirective.js"
		type="text/javascript"></script>
	<script src="resources/js/angularservices/services.js"
		type="text/javascript"></script>
	<script src="resources/js/underscore.js" type="text/javascript"></script>

	<!-- <script src="resources/js/angularController/loginController.js"></script> -->
	<script type="text/javascript">
		$("#msgBox").show().delay(2000).fadeOut(400);
	</script>
</body>

</html>