var sdrc_export = new function() {
	"use strict";
	this.root = "http://localhost/";
	this.init = function(rootUri) {
		this.root = rootUri;
		console.log("in init");
	};
	// Please give container Id and export pdf btn ids
	this.export_pdf = function(containerId, exportPdfbtn) {
		var serverURL;
		$("#" + exportPdfbtn).on("click",function(event) {
			var $scope = angular.element("body").scope();
			event.preventDefault();
			d3.selectAll("svg").attr("version", 1.1).attr("xmlns",
					"http://www.w3.org/2000/svg");
			var spiderSvg = $("sdrc-spider").html();
			var columnSvg = $("sdrc-bar-chart").html();
			var chartSvgs = [];
			chartSvgs.push(spiderSvg); //chartSvgs.push(columnSvg);
			$(".loader").show();
			
			var serverURL = "exportToPdf?formId=" + $scope.selectedParentSector.formId + 
			"&lastVisitDataId=" + $scope.lastVisiDataId +
			"&areaId=" + $scope.selectedDistrict.areaId + 
			"&noOfFacilities=" + $scope.noOfFacilities
			+ '&timePeriodId='+$scope.selectedTimePeriod.timePeriod_Nid
			+'&cboType='+$scope.selectedParentSector.type;
			
	
			$.ajax({
				url : serverURL, 
				method : 'POST',
				data : JSON.stringify(chartSvgs),
				contentType : 'application/json',
				success : function(data) {
					var fileName = {
							"fileName" : data
						};
					if(typeof data == 'string' && data.indexOf("You are not authorized to view this page") != -1){
						$("body").append('<div id="sessionOutMessage" class="modal fade" role="dialog"><div class="modal-dialog"><div class="modal-content modal-info"><div class="modal-header"style="background-color: #2f515b; color: #fff;"><h4 class="modal-title" style="text-align: center;">Error</h4> </div> <div class="modal-body text-center"><h3>Session has been expired</h3></div><div class="modal-footer" style="text-align: center;"><a href="home" class="btn btn-default btn-view text-center" type="submit">OK</a></div></div></div></div>');
						$(".loader").css("display", "none");
						$("#sessionOutMessage").modal("show");
					}
					else
						$.download("downloadFile/", fileName, 'POST');
				}
			});
		});
	};
	this.export_excel = function(containerId, exportExcelbtn) {
		var serverURL;
		$("#" + exportExcelbtn).on("click",function(event) {
			var $scope = angular.element("body").scope();
			event.preventDefault();
			d3.selectAll("svg").attr("version", 1.1).attr("xmlns",
					"http://www.w3.org/2000/svg");
			var spiderSvg = $("sdrc-spider").html();
			var columnSvg = $("sdrc-bar-chart").html();
			var chartSvgs = [];
			chartSvgs.push(spiderSvg); ///chartSvgs.push(columnSvg);
			$(".loader").show();
			
			var serverURL = "exportToExcel?formId=" + $scope.selectedParentSector.formId + 
			"&lastVisitDataId=" + $scope.lastVisiDataId +
			"&areaId=" + $scope.selectedDistrict.areaId + 
			"&noOfFacilities=" + $scope.noOfFacilities
			+'&timePeriodId='+$scope.selectedTimePeriod.timePeriod_Nid
			+'&cboType='+$scope.selectedParentSector.type;
			
	
			$.ajax({
				url : serverURL, 
				method : 'POST',
				data : JSON.stringify(chartSvgs),
				contentType : 'application/json',
				success : function(data) {
					var fileName = {
							"fileName" : data
						};
					if(typeof data == 'string' && data.indexOf("You are not authorized to view this page") != -1){
						$("body").append('<div id="sessionOutMessage" class="modal fade" role="dialog"><div class="modal-dialog"><div class="modal-content modal-info"><div class="modal-header"style="background-color: #2f515b; color: #fff;"><h4 class="modal-title" style="text-align: center;">Error</h4> </div> <div class="modal-body text-center"><h3>Session has been expired</h3></div><div class="modal-footer" style="text-align: center;"><a href="home" class="btn btn-default btn-view text-center" type="submit">OK</a></div></div></div></div>');
						$(".loader").css("display", "none");
						$("#sessionOutMessage").modal("show");
					}
					else
						$.download("downloadFile/", fileName, 'POST');
				}
			});
		});
	};
	// download a file
	$.download = function(url, data, method) {
		// url and data options required
		if (url && data) {
			// data can be string of parameters or array/object
			data = typeof data == 'string' ? data : jQuery.param(data);
			// split params into form inputs
			var inputs = '';
			jQuery.each(data.split('&'), function() {
				var pair = this.split('=');
				inputs += '<input type="hidden" name="' + pair[0] + '" value="'	+ pair[1] + '" />';
			});
			// send request
			jQuery(
					'<form action="' + url + '" method="' + (method || 'post')
							+ '">' + inputs + '</form>').appendTo('body')
					.submit().remove();
			$(".loader").css("display", "none");
		}
		;
	this.export_excel = function() {
		alert("excel exported");
	};
	};
};

$(document).ready(function(){
	$("#pdfDownloadBtn").hover(function(){
		
	});
});