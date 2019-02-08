

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 */
myAppConstructor.controller("DataTreeController", function($scope, $http, $window, allServices){
	$scope.parentArea = {value: "Uttar Pradesh"};
	$scope.bubbleDataModel = [];
	$scope.setBackTrue= function(){
		$scope.isBkBtn = true;
	};
	$scope.setBackFalse= function(){
		$scope.isBkBtn = false;
	};
	
	
	$scope.setColorTrue= function(){
		$scope.isColor = true;
	};
	
	$scope.setColorFalse= function(){
		$scope.isColor=false;
	};
	
	$scope.setColorFalse= function(){
		$scope.isColor=false;
	};
	
	$scope.setValueTrue= function(){
		$scope.isValue=true;
	};
	
	$scope.setValueFalse= function(){
		$scope.isValue=false;
	};
	$scope.closeViz = function() {
		$scope.isTrendVisible = false;
		$scope.$apply();
	};
	
	$scope.showViz = function() {
		$scope.isTrendVisible = true;
		$scope.$apply();
	};
	allServices.getDashboardDistricts().then(function(data){
		$scope.allDistricts = data;
		$scope.selectDistrict($scope.allDistricts[0]);
	});
	
		allServices.getAllTimeperiod().then(function(data) {
			$scope.allTimePeriods = data;
			$scope.selectTimePeriod($scope.allTimePeriods[0]);
			$scope.getAllTimePeriodsSuccessful = true;
		});

	
	$scope.selectDistrict = function(district){
		$scope.selectedDistrict = district;
//		$scope.getbubbledata($scope.selectedSector);
		if($scope.treeData == undefined)
			allServices.getTreeData().then(function(data){
				$scope.treeData = data;
				$scope.treeDatas=data;
			});
		else
			$scope.getbubbledata($scope.selectedSector);
	};
	
	$scope.selectTimePeriod = function(timeperiod) {
		$scope.selectedTimePeriod = timeperiod;
		if($scope.treeData == undefined)
			allServices.getTreeData().then(function(data){
				$scope.treeData = data;
				$scope.treeDatas=data;
			});
		else
			$scope.getbubbledata($scope.selectedSector);
	}
	
	$scope.getbubbledata = function(d) {
			$(".loader").show();
			$scope.bubbleDataModel = [];
			$scope.isGranularityVisible = false;
			if(d)
			$scope.selectedIusNid=d.parent.Id;
			$scope.selectedSector = d;
			if($scope.selectedDistrict.areaId != undefined && d)
			allServices.getBubbleChartData(d.Id, $scope.selectedDistrict.areaId,$scope.selectedTimePeriod.timePeriod_Nid)
			.then(function(data) {
				$(".loader").fadeOut();
				$("#helpImg").attr('src', "resources/images/DoubleClickHelp.png");
				$scope.isBubbleVisible=true;
				$scope.bubbleDataModel = data;
				$scope.redBubbleCount = 0;
				$scope.yellowBubbleCount = 0;
				$scope.greenBubbleCount = 0;
				if($scope.bubbleDataModel.length)
					{
				for(var i=0; i<$scope.bubbleDataModel.length; i++){
					if($scope.bubbleDataModel[i].value < 60){
						$scope.redBubbleCount++;
					}
					else if($scope.bubbleDataModel[i].value >= 60 && $scope.bubbleDataModel[i].value < 80){
						$scope.yellowBubbleCount++;
					}
					else{
						$scope.greenBubbleCount++;
					}
				}
					}
				else
					{
					$("#noDataModall").modal("show");
					}
			});	
	};
	
	$scope.getCboAsessed =function()
	{
		allServices.getCboAsessed().then(function(data) {
			$scope.cboAssessed = data;
			$scope.sectorKeys=Object.keys(data);
		});
	}
	$scope.getCboAsessed();
	
	var w = angular.element($window);
	$scope.getWindowDimensions = function() {
		return {
			"h" : w.height(),
			"w" : (w.width() * 90 / 100)
		};
	};
	

});