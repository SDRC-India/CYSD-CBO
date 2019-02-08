/*
 * @Harsh Pratyush (harsh@sdrc.co.in)
 * 
 * */
function checkSessionOut(data){
	if(typeof data == 'string' && data.indexOf("You are not authorized to view this page") != -1){
		$("body").append('<div id="sessionOutMessage" class="modal fade" role="dialog"><div class="modal-dialog"><div class="modal-content modal-info"><div class="modal-header"style="background-color: #2f515b; color: #fff;"><h4 class="modal-title" style="text-align: center;">Error</h4> </div> <div class="modal-body text-center"><h3>Session has been expired</h3></div><div class="modal-footer" style="text-align: center;"><a href="home" class="btn btn-default btn-view text-center" type="submit">OK</a></div></div></div></div>');
		$(".loader").fadeOut();
		$("#sessionOutMessage").modal("show");
	}
}

myAppConstructor
    .service('allServices', allServices)
 
  function allServices($http, $q) {
	


	
	this.getAllTimeperiods = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get("getAllTimePeriods")
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	
	

	this.getAllTimeperiod = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get("getAllTimePeriod")
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	
	this.getAllSectors = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get("getAllSectors")
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getSummary = function(facilityId, sectionId, timeperiodId){
		var deferred = $q.defer();
        // get posts from database
		$http.get("getSummary?checklistId=" + facilityId +
							"&sectionId=" + sectionId + 
							"&timperiodNid=" + timeperiodId
				)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getRawData = function(division, checklistId, sectionId, timperiodNid){
		var deferred = $q.defer();
        // get posts from database
		$http.get("getRawData?division=" + division +
							"&checklistId=" + checklistId + 
							"&sectionId=" + sectionId +
							"&timperiodNid=" + timperiodNid
				)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getSpiderData = function(formId, lastVisitDataId, districtId,cboType){
		var deferred = $q.defer();
        // get posts from database
		$http.get('spiderData?formId=' + formId +
							"&lastVisitDataId=" + lastVisitDataId +
							"&areaId=" + districtId +"&cboType="+cboType)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getPlannedFacilities = function(formId, timePeriodId, districtId){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getPlannedFacilities?formId=' + formId +
							"&timeperiodId=" + timePeriodId +
							"&areaId=" + districtId)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getChhatisgarhMap = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('../resources/json/chhatisgarh-polygon.json')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getParentSectors = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getParentSectors')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getSectors = function(parentId){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getSectors?parentId='+parentId)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getDashboardDistricts = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getAllDistrict')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getTreeData = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('treeData')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getBubbleChartData = function(sectorId, areaId,timeperiodId){
		var deferred = $q.defer();
        // get posts from database
		$http.get('bubbleChartData?sectorId='+sectorId + '&areaId=' + areaId + '&timeperiodId='+timeperiodId)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	/*this.exportToPdf = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('exportToPdf')
          .then(function(result) {
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};*/
	this.getFacilitiesForCrossTab = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getFacilitiesForCrossTab')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getAllDistricts = function(){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getAllDistricts')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getCrossTabData = function(crosstab, areaId){
		var deferred = $q.defer();
        // get posts from database
		$http.get('getCrossTabData?id=' + crosstab.parentid +
									'&colid=' + crosstab.colid + 
									'&rowid=' + crosstab.rowid +
									'&areaId=' + areaId)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	this.getFacilityImprovementSelection = function(){
		$(".loader").show();
		var deferred = $q.defer();
        // get posts from database
		$http.get('getAllFacilityList')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	
	
	this.getCrossTabDropDownData = function(){
		$(".loader").show();
		var deferred = $q.defer();
        // get posts from database
		$http.get('crossTabDropDownData')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	
	this.getCrossTabTableData=function(crossTabDataModel){
		$(".loader").show();
		var deferred = $q.defer();
        // get posts from database
		$http.post('getCrossTabTableData',crossTabDataModel)
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
	
	this.getRawData=function()
	{
		$(".loader").show();
		$http.get('database.getRawExcel').then(function(result) {
      	  checkSessionOut(result.data);
    	  jsonData = result.data;
        deferred.resolve(jsonData);
      }, function(error) {
    	  jsonData = error;
        deferred.reject(error);
      });
    jsonData = deferred.promise;
    return $q.when(jsonData);
	}
	
	this.getCboAsessed=function(){
		$(".loader").show();
		var deferred = $q.defer();
        // get posts from database
		$http.post('cboAsessed')
          .then(function(result) {
        	  checkSessionOut(result.data);
        	  jsonData = result.data;
            deferred.resolve(jsonData);
          }, function(error) {
        	  jsonData = error;
            deferred.reject(error);
          });
        jsonData = deferred.promise;
        return $q.when(jsonData);
	};
};