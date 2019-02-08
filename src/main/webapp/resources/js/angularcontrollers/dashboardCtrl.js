function ValueObject(key, value) {
	this.key = key;
	this.value = value;
};
/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 */
myAppConstructor.controller("DashboardController", DashboardController);

function DashboardController($scope, $http, $window, $timeout, $filter,
		 allServices) {

	$scope.roleId = $window.roleId;
	$scope.selectedGranularitySpider = "";
	$scope.selectedSectorName = "CHC";
	$scope.isPushpinClicked = false;
	$scope.lastVisiDataId = 0;
	$scope.isShowTable = false;
	$scope.isShowChart = true;
	$scope.lastVisitDataId = 0;
	$scope.allDistricts = [];
	$scope.sectors = [];
	$scope.pushpinDataCallDone = false;
	$scope.percentileFacility = 0;
	// $scope.map = "[]";
	// $scope.map.markers ="[]";
	$scope.progressBarUpdateCalled = false;
	$scope.noOfFacilities = 0;
	$scope.hoverwindow = [];
	$scope.noOfFacilitiesPlanned = 0;
	$scope.todayDate = new Date().toLocaleDateString();
	var w = angular.element($window);
	$scope.getWindowDimensions = function() {
		return {
			"h" : w.height(),
			"w" : (w.width() * 90 / 100)
		};
	};
	// this is to make sure that scope gets changes as window get resized.
	w.on("resize", function() {
		if (!$scope.$$phase)
		$scope.$apply();
	});
	$(".loader").show();
	$scope.pixelOffset = {

		pixelOffset : new google.maps.Size(0, -28)
	};

	function convert(array) {
		var map = {};
		for (var i = 0; i < array.length; i++) {
			var obj = array[i];
			if (obj.parentXpathScoreId == -1)
				obj.parentXpathScoreId = null;
			if (!(obj.formXpathScoreId in map)) {
				map[obj.formXpathScoreId] = obj;
				map[obj.formXpathScoreId].children = [];
			}

			if (typeof map[obj.formXpathScoreId].name == 'undefined') {
				map[obj.formXpathScoreId].formXpathScoreId = String(obj.formXpathScoreId);
				map[obj.formXpathScoreId].name = obj.name;
				map[obj.formXpathScoreId].parentXpathScoreId = String(obj.parentXpathScoreId);
				map[obj.formXpathScoreId].score = String(obj.score);
				map[obj.formXpathScoreId].maxScore = String(obj.maxScore);
				map[obj.formXpathScoreId].percentScore = obj.percentScore;
			}

			var parent = obj.parentXpathScoreId || '-';
			if (!(parent in map)) {
				map[parent] = {};
				map[parent].children = [];
			}

			map[parent].children.push(map[obj.formXpathScoreId]);
		}
		return map['-'];
	}

	$scope.map = {
		center : {
			latitude : 20.516689,
			longitude : 84.355523
		},
		bounds : {},
		clickMarkers : [],
		zoom : 7,
		events : {
			"mouseover" : function(mapModel, eventName, originalEventArgs) {
				for (var i = 0; i < $scope.map.markers.length; i++) {
					if ($scope.map.markers[i].id == originalEventArgs.id) {
						$scope.map.markers[i].showWindow = true;
						break;
					}
				}
				if (!$scope.$$phase)
				$scope.$apply();
			},
			"mouseout" : function(mapModel, eventName, originalEventArgs) {
				for (var i = 0; i < $scope.map.markers.length; i++) {
					if ($scope.map.markers[i].id == originalEventArgs.id) {
						$scope.map.markers[i].showWindow = false;
						break;
					}
				}
				if (!$scope.$$phase)
				$scope.$apply();
			},
			"click" : function(mapModel, eventName, originalEventArgs) {
				$(".loader").show();
				$scope.selectedPushpin = '';
				$scope.isPushpinClicked = true;
				$scope.selectedPushpin = originalEventArgs.title;
				$scope.lastVisiDataId = originalEventArgs.id;

				$scope.getSpiderData($scope.selectedParentSector.formId,
						originalEventArgs.id, $scope.selectedDistrict.areaId,$scope.selectedParentSector.type);
				$('html, body').animate({
					scrollTop : $("#charts").offset().top
				}, 1000);
				if (!$scope.$$phase)
				$scope.$apply();

			}
		}
	};
	/*
	 * $scope.getChhatisgarhMap = function() {
	 * allServices.getChhatisgarhMap().then(function(data){ $scope.polygons =
	 * JSON.parse(data.selectedRegion); }); };
	 */

	$scope.polygons = [ {
		id : 1,
		path : [{longitude :  84.7760009765625, latitude : 19.0828843693401 },
		        {longitude :  84.869384765625, latitude : 19.2178029596679 },
		        {longitude :  84.9847412109375, latitude : 19.3215112268171 },
		        {longitude :  85.111083984375, latitude : 19.4044304968127 },
		        {longitude :  85.1934814453125, latitude : 19.4614129968328 },
		        {longitude :  85.3143310546875, latitude : 19.5390841355093 },
		        {longitude :  85.5010986328125, latitude : 19.6218921803193 },
		        {longitude :  85.5615234375, latitude : 19.6943142418257 },
		        {longitude :  85.7208251953125, latitude : 19.7356835786294 },
		        {longitude :  85.869140625, latitude : 19.7718729607967 },
		        {longitude :  85.9844970703125, latitude : 19.8183900938449 },
		        {longitude :  86.15478515625, latitude : 19.8648936205131 },
		        {longitude :  86.319580078125, latitude : 19.9113835141555 },
		        {longitude :  86.46240234375, latitude : 20.1023648329449 },
		        {longitude :  86.5557861328125, latitude : 20.1797235027651 },
		        {longitude :  86.72607421875, latitude : 20.28280869133 },
		        {longitude :  86.7974853515625, latitude : 20.3703772563476 },
		        {longitude :  86.7755126953125, latitude : 20.4167169889457 },
		        {longitude :  86.748046875, latitude : 20.432160468556 },
		        {longitude :  86.7425537109375, latitude : 20.4784816000905 },
		        {longitude :  86.77001953125, latitude : 20.5453652115108 },
		        {longitude :  86.8853759765625, latitude : 20.6173610033977 },
		        {longitude :  86.978759765625, latitude : 20.6636260541528 },
		        {longitude :  87.03369140625, latitude : 20.7047387200555 },
		        {longitude :  87.022705078125, latitude : 20.7355659052186 },
		        {longitude :  86.98974609375, latitude : 20.7766590518788 },
		        {longitude :  86.9732666015625, latitude : 20.8434115649864 },
		        {longitude :  86.94580078125, latitude : 20.9203969139718 },
		        {longitude :  86.868896484375, latitude : 21.0434912168035 },
		        {longitude :  86.824951171875, latitude : 21.1459921649578 },
		        {longitude :  86.84692382812499, latitude : 21.2125797906305 },
		        {longitude :  86.9293212890625, latitude : 21.3149636417175 },
		        {longitude :  86.9732666015625, latitude : 21.407048123461 },
		        {longitude :  87.132568359375, latitude : 21.5144067200302 },
		        {longitude :  87.20947265625, latitude : 21.5399566230854 },
		        {longitude :  87.33032226562499, latitude : 21.5501753255569 },
		        {longitude :  87.4017333984375, latitude : 21.5859351147885 },
		        {longitude :  87.4566650390625, latitude : 21.6063653172033 },
		        {longitude :  87.47314453125, latitude : 21.6880572567954 },
		        {longitude :  87.4346923828125, latitude : 21.7646014057439 },
		        {longitude :  87.3687744140625, latitude : 21.7901070598078 },
		        {longitude :  87.2808837890625, latitude : 21.8309066650697 },
		        {longitude :  87.242431640625, latitude : 21.8920842515614 },
		        {longitude :  87.1875, latitude : 21.958330359927 },
		        {longitude :  87.1490478515625, latitude : 21.9328547363353 },
		        {longitude :  87.099609375, latitude : 21.9226632093259 },
		        {longitude :  87.07763671875, latitude : 21.8767923124383 },
		        {longitude :  87.0172119140625, latitude : 21.9073745508283 },
		        {longitude :  87.0281982421875, latitude : 22.0143606531032 },
		        {longitude :  86.99523925781249, latitude : 22.0500047670364 },
		        {longitude :  86.94580078125, latitude : 22.080549718325 },
		        {longitude :  86.868896484375, latitude : 22.1059987997505 },
		        {longitude :  86.7974853515625, latitude : 22.1467077800126 },
		        {longitude :  86.69860839843749, latitude : 22.1874049913987 },
		        {longitude :  86.6876220703125, latitude : 22.2280904167844 },
		        {longitude :  86.5777587890625, latitude : 22.289096418723 },
		        {longitude :  86.451416015625, latitude : 22.3551562185897 },
		        {longitude :  86.407470703125, latitude : 22.339914425562 },
		        {longitude :  86.3470458984375, latitude : 22.3602364457993 },
		        {longitude :  86.32507324218749, latitude : 22.4211847103318 },
		        {longitude :  86.253662109375, latitude : 22.4516488191262 },
		        {longitude :  86.1932373046875, latitude : 22.4618020353339 },
		        {longitude :  86.1163330078125, latitude : 22.4871818211392 },
		        {longitude :  86.0723876953125, latitude : 22.5480743154181 },
		        {longitude :  86.033935546875, latitude : 22.5632932447077 },
		        {longitude :  86.0064697265625, latitude : 22.5379274299472 },
		        {longitude :  85.9625244140625, latitude : 22.4922572200851 },
		        {longitude :  85.968017578125, latitude : 22.4262625262058 },
		        {longitude :  86.0064697265625, latitude : 22.3907139168385 },
		        {longitude :  85.98999023437499, latitude : 22.3297523043764 },
		        {longitude :  86.011962890625, latitude : 22.2992614997412 },
		        {longitude :  85.98999023437499, latitude : 22.2433444092356 },
		        {longitude :  86.0064697265625, latitude : 22.1467077800126 },
		        {longitude :  85.9844970703125, latitude : 22.090729901489 },
		        {longitude :  85.9185791015625, latitude : 22.0143606531032 },
		        {longitude :  85.836181640625, latitude : 21.9838014173846 },
		        {longitude :  85.7757568359375, latitude : 22.039821650237 },
		        {longitude :  85.80322265625, latitude : 22.0856399016503 },
		        {longitude :  85.78125, latitude : 22.1110880653077 },
		        {longitude :  85.7537841796875, latitude : 22.0856399016503 },
		        {longitude :  85.6768798828125, latitude : 22.0703688013492 },
		        {longitude :  85.660400390625, latitude : 22.1059987997505 },
		        {longitude :  85.616455078125, latitude : 22.1110880653077 },
		        {longitude :  85.53955078125, latitude : 22.1009093505727 },
		        {longitude :  85.4681396484375, latitude : 22.1161771472106 },
		        {longitude :  85.4241943359375, latitude : 22.1416198007387 },
		        {longitude :  85.36376953125, latitude : 22.1416198007387 },
		        {longitude :  85.2264404296875, latitude : 22.0550960505758 },
		        {longitude :  85.1275634765625, latitude : 22.0703688013492 },
		        {longitude :  85.05615234375, latitude : 22.1161771472106 },
		        {longitude :  85.0341796875, latitude : 22.1467077800126 },
		        {longitude :  85.067138671875, latitude : 22.217920166311 },
		        {longitude :  85.1055908203125, latitude : 22.2738474111046 },
		        {longitude :  85.1055908203125, latitude : 22.3246709661332 },
		        {longitude :  85.0836181640625, latitude : 22.3602364457993 },
		        {longitude :  85.078125, latitude : 22.4110285215587 },
		        {longitude :  85.0726318359375, latitude : 22.4516488191262 },
		        {longitude :  85.045166015625, latitude : 22.4770304649333 },
		        {longitude :  84.979248046875, latitude : 22.4668783645284 },
		        {longitude :  84.87487792968749, latitude : 22.4516488191262 },
		        {longitude :  84.6771240234375, latitude : 22.4262625262058 },
		        {longitude :  84.6221923828125, latitude : 22.4262625262058 },
		        {longitude :  84.462890625, latitude : 22.4110285215587 },
		        {longitude :  84.42993164062499, latitude : 22.3805555014215 },
		        {longitude :  84.287109375, latitude : 22.3449952084378 },
		        {longitude :  84.232177734375, latitude : 22.3856348018571 },
		        {longitude :  84.1387939453125, latitude : 22.4465719321789 },
		        {longitude :  84.1058349609375, latitude : 22.4770304649333 },
		        {longitude :  84.0673828125, latitude : 22.5227057034824 },
		        {longitude :  84.012451171875, latitude : 22.5277797986945 },
		        {longitude :  83.9794921875, latitude : 22.5277797986945 },
		        {longitude :  83.9794921875, latitude : 22.4922572200851 },
		        {longitude :  84.012451171875, latitude : 22.4821062360776 },
		        {longitude :  84.0234375, latitude : 22.4364176007631 },
		        {longitude :  84.0179443359375, latitude : 22.3805555014215 },
		        {longitude :  83.968505859375, latitude : 22.3653164877205 },
		        {longitude :  83.90808105468749, latitude : 22.3551562185897 },
		        {longitude :  83.8421630859375, latitude : 22.3500758061248 },
		        {longitude :  83.78173828125, latitude : 22.3195894428339 },
		        {longitude :  83.7542724609375, latitude : 22.2738474111046 },
		        {longitude :  83.69384765625, latitude : 22.2433444092356 },
		        {longitude :  83.6279296875, latitude : 22.2331752654027 },
		        {longitude :  83.61145019531249, latitude : 22.2026634080092 },
		        {longitude :  83.56201171875, latitude : 22.1619706143679 },
		        {longitude :  83.57299804687499, latitude : 22.1212660454257 },
		        {longitude :  83.551025390625, latitude : 22.0652780677658 },
		        {longitude :  83.529052734375, latitude : 22.0092679044937 },
		        {longitude :  83.5345458984375, latitude : 21.9787075713862 },
		        {longitude :  83.5675048828125, latitude : 21.9430455334381 },
		        {longitude :  83.583984375, latitude : 21.9175671721907 },
		        {longitude :  83.5784912109375, latitude : 21.8716946351427 },
		        {longitude :  83.5784912109375, latitude : 21.8156081756626 },
		        {longitude :  83.5400390625, latitude : 21.820707853875 },
		        {longitude :  83.507080078125, latitude : 21.8003080509725 },
		        {longitude :  83.46313476562499, latitude : 21.7901070598078 },
		        {longitude :  83.46313476562499, latitude : 21.7646014057439 },
		        {longitude :  83.4521484375, latitude : 21.7186798057031 },
		        {longitude :  83.4136962890625, latitude : 21.6829528654782 },
		        {longitude :  83.4136962890625, latitude : 21.6216860759719 },
		        {longitude :  83.38623046875, latitude : 21.5910429357242 },
		        {longitude :  83.353271484375, latitude : 21.5348470020487 },
		        {longitude :  83.3367919921875, latitude : 21.4632934418992 },
		        {longitude :  83.3477783203125, latitude : 21.4223899052313 },
		        {longitude :  83.375244140625, latitude : 21.4121622297254 },
		        {longitude :  83.3917236328125, latitude : 21.3507811506797 },
		        {longitude :  83.33129882812499, latitude : 21.3507811506797 },
		        {longitude :  83.287353515625, latitude : 21.3865899091328 },
		        {longitude :  83.25439453125, latitude : 21.3047284621516 },
		        {longitude :  83.2708740234375, latitude : 21.248422235627 },
		        {longitude :  83.232421875, latitude : 21.2637806158378 },
		        {longitude :  83.2049560546875, latitude : 21.1818507662661 },
		        {longitude :  83.1719970703125, latitude : 21.1152493099637 },
		        {longitude :  83.08959960937499, latitude : 21.1101248811478 },
		        {longitude :  83.03466796874999, latitude : 21.1152493099637 },
		        {longitude :  82.97973632812499, latitude : 21.1459921649578 },
		        {longitude :  82.935791015625, latitude : 21.1664838582065 },
		        {longitude :  82.891845703125, latitude : 21.1664838582065 },
		        {longitude :  82.8533935546875, latitude : 21.1613612008072 },
		        {longitude :  82.7435302734375, latitude : 21.1459921649578 },
		        {longitude :  82.7105712890625, latitude : 21.151115354148 },
		        {longitude :  82.650146484375, latitude : 21.1613612008072 },
		        {longitude :  82.6336669921875, latitude : 21.0947505331401 },
		        {longitude :  82.6171875, latitude : 21.0281099786428 },
		        {longitude :  82.5567626953125, latitude : 20.9563098919867 },
		        {longitude :  82.45788574218749, latitude : 20.8896075104043 },
		        {longitude :  82.45788574218749, latitude : 20.8331438720399 },
		        {longitude :  82.408447265625, latitude : 20.853678557426 },
		        {longitude :  82.37548828125, latitude : 20.8793429719578 },
		        {longitude :  82.3260498046875, latitude : 20.8485451487872 },
		        {longitude :  82.353515625, latitude : 20.7869305925702 },
		        {longitude :  82.3480224609375, latitude : 20.7047387200555 },
		        {longitude :  82.36450195312499, latitude : 20.632784250388 },
		        {longitude :  82.3480224609375, latitude : 20.5556524037733 },
		        {longitude :  82.3699951171875, latitude : 20.4939188716188 },
		        {longitude :  82.408447265625, latitude : 20.4476023975941 },
		        {longitude :  82.3809814453125, latitude : 20.3652275374124 },
		        {longitude :  82.3919677734375, latitude : 20.3137209038779 },
		        {longitude :  82.40295410156249, latitude : 20.2621971242465 },
		        {longitude :  82.408447265625, latitude : 20.2106562344898 },
		        {longitude :  82.3919677734375, latitude : 20.1694112276102 },
		        {longitude :  82.386474609375, latitude : 20.0559312651944 },
		        {longitude :  82.5347900390625, latitude : 20.0043222959987 },
		        {longitude :  82.6226806640625, latitude : 19.9991604673702 },
		        {longitude :  82.6885986328125, latitude : 19.9888363023807 },
		        {longitude :  82.72705078125, latitude : 19.9733487861106 },
		        {longitude :  82.69958496093749, latitude : 19.9062186444808 },
		        {longitude :  82.705078125, latitude : 19.8648936205131 },
		        {longitude :  82.6666259765625, latitude : 19.8338927825377 },
		        {longitude :  82.60620117187499, latitude : 19.7873801819862 },
		        {longitude :  82.562255859375, latitude : 19.792548920244 },
		        {longitude :  82.5897216796875, latitude : 19.8442270679451 },
		        {longitude :  82.46337890625, latitude : 19.895888399365 },
		        {longitude :  82.386474609375, latitude : 19.895888399365 },
		        {longitude :  82.342529296875, latitude : 19.8493939584227 },
		        {longitude :  82.298583984375, latitude : 19.8855574801372 },
		        {longitude :  82.265625, latitude : 19.9475328779893 },
		        {longitude :  82.210693359375, latitude : 19.9888363023807 },
		        {longitude :  82.1832275390625, latitude : 19.9991604673702 },
		        {longitude :  82.0953369140625, latitude : 20.0249679172227 },
		        {longitude :  82.0404052734375, latitude : 20.0249679172227 },
		        {longitude :  81.990966796875, latitude : 20.0198067659828 },
		        {longitude :  81.9525146484375, latitude : 20.0817293899715 },
		        {longitude :  81.8756103515625, latitude : 20.0456108274397 },
		        {longitude :  81.84814453125, latitude : 19.9888363023807 },
		        {longitude :  81.8426513671875, latitude : 19.9268771112092 },
		        {longitude :  81.8701171875, latitude : 19.8907230239969 },
		        {longitude :  81.9140625, latitude : 19.8752258870892 },
		        {longitude :  81.9635009765625, latitude : 19.8390600093046 },
		        {longitude :  81.9964599609375, latitude : 19.7977174907047 },
		        {longitude :  82.0513916015625, latitude : 19.7667035517169 },
		        {longitude :  82.0513916015625, latitude : 19.7098289973884 },
		        {longitude :  82.0458984375, latitude : 19.6374139455962 },
		        {longitude :  82.0458984375, latitude : 19.549437468141 },
		        {longitude :  82.02941894531249, latitude : 19.502842244396 },
		        {longitude :  82.100830078125, latitude : 19.502842244396 },
		        {longitude :  82.12280273437499, latitude : 19.456233596018 },
		        {longitude :  82.16125488281249, latitude : 19.4147924380995 },
		        {longitude :  82.1502685546875, latitude : 19.3629761333418 },
		        {longitude :  82.1722412109375, latitude : 19.347428027887 },
		        {longitude :  82.15576171875, latitude : 19.25410831644 },
		        {longitude :  82.166748046875, latitude : 19.1451681962052 },
		        {longitude :  82.1942138671875, latitude : 19.0932666360897 },
		        {longitude :  82.232666015625, latitude : 18.9478557812941 },
		        {longitude :  82.1502685546875, latitude : 18.8543103618898 },
		        {longitude :  82.1392822265625, latitude : 18.7919177442344 },
		        {longitude :  82.11181640625, latitude : 18.7451080999854 },
		        {longitude :  82.06787109374999, latitude : 18.6670631919266 },
		        {longitude :  82.0074462890625, latitude : 18.6462451426706 },
		        {longitude :  81.9305419921875, latitude : 18.6410402313999 },
		        {longitude :  81.8756103515625, latitude : 18.6410402313999 },
		        {longitude :  81.947021484375, latitude : 18.6046013884552 },
		        {longitude :  81.947021484375, latitude : 18.578568865536 },
		        {longitude :  81.88110351562499, latitude : 18.5212833254962 },
		        {longitude :  81.8206787109375, latitude : 18.5056566630405 },
		        {longitude :  81.8206787109375, latitude : 18.448346702932 },
		        {longitude :  81.749267578125, latitude : 18.4014425048482 },
		        {longitude :  81.6943359375, latitude : 18.3336694457713 },
		        {longitude :  81.617431640625, latitude : 18.3023806040251 },
		        {longitude :  81.5570068359375, latitude : 18.2502199770656 },
		        {longitude :  81.5020751953125, latitude : 18.1719496799106 },
		        {longitude :  81.49108886718749, latitude : 18.0361980634148 },
		        {longitude :  81.4691162109375, latitude : 17.983957957423 },
		        {longitude :  81.4306640625, latitude : 17.9160227038776 },
		        {longitude :  81.43615722656249, latitude : 17.8846591795428 },
		        {longitude :  81.40869140625, latitude : 17.8951143037491 },
		        {longitude :  81.3702392578125, latitude : 17.8480613983963 },
		        {longitude :  81.40869140625, latitude : 17.785304836705 },
		        {longitude :  81.4471435546875, latitude : 17.7957657972559 },
		        {longitude :  81.5130615234375, latitude : 17.8062261447828 },
		        {longitude :  81.58447265624999, latitude : 17.8219155159688 },
		        {longitude :  81.6448974609375, latitude : 17.853290114098 },
		        {longitude :  81.7108154296875, latitude : 17.853290114098 },
		        {longitude :  81.76025390625, latitude : 17.8898868186253 },
		        {longitude :  81.8316650390625, latitude : 17.9369286375494 },
		        {longitude :  81.925048828125, latitude : 17.9735080790687 },
		        {longitude :  81.968994140625, latitude : 17.9996316149118 },
		        {longitude :  82.0184326171875, latitude : 18.03097474989 },
		        {longitude :  82.0513916015625, latitude : 18.0570897666486 },
		        {longitude :  82.08984375, latitude : 18.0623123045467 },
		        {longitude :  82.12280273437499, latitude : 18.03097474989 },
		        {longitude :  82.166748046875, latitude : 18.0257512813562 },
		        {longitude :  82.1942138671875, latitude : 18.0153038794171 },
		        {longitude :  82.24365234375, latitude : 17.9891826646305 },
		        {longitude :  82.276611328125, latitude : 18.004855857908 },
		        {longitude :  82.265625, latitude : 18.0518670735476 },
		        {longitude :  82.298583984375, latitude : 18.0414212218919 },
		        {longitude :  82.3370361328125, latitude : 18.0518670735476 },
		        {longitude :  82.3370361328125, latitude : 18.1093081551014 },
		        {longitude :  82.3590087890625, latitude : 18.1249706393865 },
		        {longitude :  82.3260498046875, latitude : 18.1667304102219 },
		        {longitude :  82.30957031249999, latitude : 18.2136982162104 },
		        {longitude :  82.3480224609375, latitude : 18.2658698115581 },
		        {longitude :  82.408447265625, latitude : 18.3023806040251 },
		        {longitude :  82.37548828125, latitude : 18.3232404604433 },
		        {longitude :  82.3480224609375, latitude : 18.3232404604433 },
		        {longitude :  82.36450195312499, latitude : 18.3753790940318 },
		        {longitude :  82.36450195312499, latitude : 18.4014425048482 },
		        {longitude :  82.4853515625, latitude : 18.5421166544489 },
		        {longitude :  82.496337890625, latitude : 18.5108657090913 },
		        {longitude :  82.529296875, latitude : 18.4952380954332 },
		        {longitude :  82.5347900390625, latitude : 18.4535574905395 },
		        {longitude :  82.5457763671875, latitude : 18.4222903942559 },
		        {longitude :  82.5567626953125, latitude : 18.4014425048482 },
		        {longitude :  82.6007080078125, latitude : 18.3649526265391 },
		        {longitude :  82.6116943359375, latitude : 18.3284550317128 },
		        {longitude :  82.5787353515625, latitude : 18.3075958037538 },
		        {longitude :  82.5787353515625, latitude : 18.2658698115581 },
		        {longitude :  82.6336669921875, latitude : 18.2189160800174 },
		        {longitude :  82.6666259765625, latitude : 18.2606533567583 },
		        {longitude :  82.705078125, latitude : 18.2815182353089 },
		        {longitude :  82.7325439453125, latitude : 18.3284550317128 },
		        {longitude :  82.7874755859375, latitude : 18.3701659390446 },
		        {longitude :  82.7874755859375, latitude : 18.4118667652028 },
		        {longitude :  82.8094482421875, latitude : 18.4431357572309 },
		        {longitude :  82.8369140625, latitude : 18.4118667652028 },
		        {longitude :  82.88635253906249, latitude : 18.4118667652028 },
		        {longitude :  82.9193115234375, latitude : 18.3649526265391 },
		        {longitude :  82.97973632812499, latitude : 18.3597391565536 },
		        {longitude :  83.023681640625, latitude : 18.3858049312974 },
		        {longitude :  83.0731201171875, latitude : 18.406654713919 },
		        {longitude :  83.045654296875, latitude : 18.4535574905395 },
		        {longitude :  83.067626953125, latitude : 18.5160745965893 },
		        {longitude :  83.0511474609375, latitude : 18.5473245898274 },
		        {longitude :  83.023681640625, latitude : 18.578568865536 },
		        {longitude :  83.0126953125, latitude : 18.6254245407012 },
		        {longitude :  83.045654296875, latitude : 18.6618589190626 },
		        {longitude :  83.0841064453125, latitude : 18.6878786860341 },
		        {longitude :  83.12805175781249, latitude : 18.7451080999854 },
		        {longitude :  83.1610107421875, latitude : 18.7451080999854 },
		        {longitude :  83.2049560546875, latitude : 18.7242996450782 },
		        {longitude :  83.2159423828125, latitude : 18.7659139906274 },
		        {longitude :  83.2489013671875, latitude : 18.7503098131406 },
		        {longitude :  83.2818603515625, latitude : 18.8023181216881 },
		        {longitude :  83.3477783203125, latitude : 18.8075180694086 },
		        {longitude :  83.3917236328125, latitude : 18.8439132011341 },
		        {longitude :  83.34228515625, latitude : 18.895892559415 },
		        {longitude :  83.3477783203125, latitude : 18.9374644296418 },
		        {longitude :  83.3258056640625, latitude : 18.9686365434022 },
		        {longitude :  83.353271484375, latitude : 18.9894147152393 },
		        {longitude :  83.408203125, latitude : 18.9894147152393 },
		        {longitude :  83.4521484375, latitude : 18.9634415956189 },
		        {longitude :  83.4576416015625, latitude : 18.9894147152393 },
		        {longitude :  83.4906005859375, latitude : 19.0205771109668 },
		        {longitude :  83.4796142578125, latitude : 19.0569258555425 },
		        {longitude :  83.5125732421875, latitude : 19.0569258555425 },
		        {longitude :  83.5345458984375, latitude : 19.010190294396 },
		        {longitude :  83.56201171875, latitude : 19.0569258555425 },
		        {longitude :  83.5894775390625, latitude : 19.1036482516636 },
		        {longitude :  83.616943359375, latitude : 19.1399787738344 },
		        {longitude :  83.6444091796875, latitude : 19.119219453414 },
		        {longitude :  83.6553955078125, latitude : 19.0880755840931 },
		        {longitude :  83.6553955078125, latitude : 19.0621178835146 },
		        {longitude :  83.69384765625, latitude : 19.041348796589 },
		        {longitude :  83.7213134765625, latitude : 19.0153837837967 },
		        {longitude :  83.73779296875, latitude : 18.9686365434022 },
		        {longitude :  83.7652587890625, latitude : 18.9270724313261 },
		        {longitude :  83.75976562499999, latitude : 18.9790259532552 },
		        {longitude :  83.770751953125, latitude : 19.0049966428023 },
		        {longitude :  83.78173828125, latitude : 19.0075934888709},
		        {longitude :  83.78173828125, latitude : 19.0075934888709 },
		        {longitude :  83.8037109375, latitude : 18.9842204152497 },
		        {longitude :  83.8092041015625, latitude : 18.955648870479 },
		        {longitude :  83.8201904296875, latitude : 18.9192780083978 },
		        {longitude :  83.8421630859375, latitude : 18.867305906826 },
		        {longitude :  83.8641357421875, latitude : 18.8309158446774 },
		        {longitude :  83.88885498046875, latitude : 18.8153176896249 },
		        {longitude :  83.935546875, latitude : 18.8023181216881 },
		        {longitude :  84.00970458984374, latitude : 18.8023181216881 },
		        {longitude :  84.05364990234375, latitude : 18.7893175494793 },
		        {longitude :  84.10308837890624, latitude : 18.7425071833055 },
		        {longitude :  84.15252685546875, latitude : 18.771115062337 },
		        {longitude :  84.20196533203125, latitude : 18.7737155380231 },
		        {longitude :  84.24591064453125, latitude : 18.7789163690341 },
		        {longitude :  84.31732177734375, latitude : 18.7815167243497 },
		        {longitude :  84.35028076171875, latitude : 18.8283162526983 },
		        {longitude :  84.3255615234375, latitude : 18.8543103618898 },
		        {longitude :  84.3475341796875, latitude : 18.8699048949648 },
		        {longitude :  84.385986328125, latitude : 18.8854979774628 },
		        {longitude :  84.4244384765625, latitude : 18.9062864959109 },
		        {longitude :  84.42993164062499, latitude : 18.9478557812941 },
		        {longitude :  84.43267822265625, latitude : 18.9868175854974 },
		        {longitude :  84.42169189453125, latitude : 19.0075934888709 },
		        {longitude :  84.4683837890625, latitude : 18.9842204152497 },
		        {longitude :  84.49859619140625, latitude : 19.0153837837967 },
		        {longitude :  84.55078125, latitude : 19.0387524779531 },
		        {longitude :  84.583740234375, latitude : 19.0647138365397 },
		        {longitude :  84.58648681640624, latitude : 19.0127870593728 },
		        {longitude :  84.61669921875, latitude : 19.0387524779531 },
		        {longitude :  84.649658203125, latitude : 19.0543297806049 },
		        {longitude :  84.66064453125, latitude : 19.067309748918 },
		        {longitude :  84.6441650390625, latitude : 19.0828843693401 },
		        {longitude :  84.61669921875, latitude : 19.0776929918682 },
		        {longitude :  84.5947265625, latitude : 19.119219453414 },
		        {longitude :  84.63592529296875, latitude : 19.1166243549583 },
		        {longitude :  84.67437744140625, latitude : 19.1270045042905 },
		        {longitude :  84.66888427734375, latitude : 19.1555465514036 },
		        {longitude :  84.70184326171875, latitude : 19.1477628462048 },
		        {longitude :  84.70458984375, latitude : 19.1218145111244 },
		        {longitude :  84.72381591796875, latitude : 19.0984575252923 },
		        {longitude :  84.74029541015625, latitude : 19.0854799970588 },
		        {longitude :  84.77325439453124, latitude : 19.0776929918682 }],
		stroke : {
			color : 'rgb(220,70,57)',
			weight : 2,
		},
		editable : true,
		draggable : false,
		geodesic : false,
		visible : true,
		fill : {
			color : 'rgb(220,70,57)',
			opacity : 0.08
		}
	} ];
	$scope.map.markers = [];
	$scope.treeData = [];
	
	$scope.selectParentSector = function(parentSector) {
		$scope.selectedPushpin = "";
		$scope.lastVisiDataId = 0;
		$scope.isShowChart = true;
		$scope.isShowTable = false;
		$scope.isPushpinClicked = false;
		$scope.selectedParentSector = parentSector;
//		$scope.noOfFacilitiesPlanned = $scope.selectedParentSector.formId == 43 ? 165
//				: $scope.selectedParentSector.formId == 44 ? 26 : 492;
		$scope.selectedDistrict.areaId = 0;
		$scope.getSectors(parentSector.formXpathScoreId);
		$scope.getSpiderData($scope.selectedParentSector.formId, 0,
				$scope.selectedDistrict.areaId,$scope.selectedParentSector.type);
		/*$scope.getPlannedFacilities($scope.selectedParentSector.formId, $scope.selectedTimePeriod.timePeriod_Nid,
				$scope.selectedDistrict.areaId);*/

	};
	$scope.selectSector = function(sector) {
		$scope.selectedSector = sector;
		if ($scope.allDistricts.length && !$scope.pushpinDataCallDone) {
			$scope.map.markers = [];
			$scope.getPushpinData($scope.selectedParentSector.formId,
					$scope.selectedSector.formXpathScoreId,
					$scope.selectedDistrict.areaId);
			$scope.pushpinDataCallDone = true;
		}

	};

	$scope.selectDistrict = function(District) {
		$scope.selectedPushpin = "";
		$scope.lastVisiDataId = 0;
		$scope.selectedDistrict = District;
		if ($scope.sectors.length && !$scope.pushpinDataCallDone) {
			$scope.map.markers = [];
			$scope.getPushpinData($scope.selectedParentSector.formId,
					$scope.selectedSector.formXpathScoreId,
					$scope.selectedDistrict.areaId);
			$scope.pushpinDataCallDone = true;
		}
		$scope.getSpiderData($scope.selectedParentSector.formId, 0,
				$scope.selectedDistrict.areaId,$scope.selectedParentSector.type);
		/*$scope.getPlannedFacilities($scope.selectedParentSector.formId, $scope.selectedTimePeriod.timePeriod_Nid,
				$scope.selectedDistrict.areaId);*/
	};
	$scope.selectTimePeriod = function(timeperiod) {
		$scope.selectedPushpin = "";
		$scope.lastVisiDataId = 0;
		$scope.selectedTimePeriod = timeperiod;
		if ($scope.sectors.length && !$scope.pushpinDataCallDone) {
			$scope.map.markers = [];
			$scope.getPushpinData($scope.selectedParentSector.formId,
					$scope.selectedSector.formXpathScoreId,
					$scope.selectedDistrict.areaId);
			$scope.pushpinDataCallDone = true;
		}
		if($scope.selectedParentSector){
		$scope.getSpiderData($scope.selectedParentSector.formId, 0,
				$scope.selectedDistrict.areaId,$scope.selectedParentSector.type);
/*		$scope.getPlannedFacilities($scope.selectedParentSector.formId, $scope.selectedTimePeriod.timePeriod_Nid,
				$scope.selectedDistrict.areaId);*/
		}
	}
	$scope.resetPushpinDataCallDone = function() {
		$scope.pushpinDataCallDone = false;
	}
	/*
	 * $scope.getTreeData = function() { $http.get('getAllData?formId=' +
	 * $scope.selectedSector).success( function(data) { $scope.treeData =
	 * convert(data).children[0];
	 * 
	 * $('html, body').animate({ scrollTop : $('#treeChartDiv').offset().top },
	 * 1000);
	 * 
	 * }); };
	 */
	
	$scope.getPushpinData = function(selectedParentSectorFormId,
			selectedSectorFormXpathScoreId, areaId) {
		$scope.map.markers = [];
		$scope.mapLoading=false;
		$(".loader").show();
		$http.get(
				'googleMapData?formId=' + selectedParentSectorFormId
						+ '&sector=' + selectedSectorFormXpathScoreId
						+ '&areaId=' + areaId
						+ '&timePeriodId='+$scope.selectedTimePeriod.timePeriod_Nid).success(function(data) {
			$scope.mapLoading=true;
			$(".loader").fadeOut();
			checkSessionOut(data);
			$scope.noOfFacilities = data.length;
			$scope.tempNoOfFacilities = JSON.parse(JSON.stringify($scope.noOfFacilities));
			
			$scope.map.markers = data;
			$scope.greenMarkers = 0;
			$scope.redMarkers = 0;
			$scope.orangeMarkers = 0;
			$scope.noOfFacilitiesSuccessful = true;
			if($scope.map.markers.length == 0){
				$("#noDataModall").modal("show");
			}
			
			if($scope.noOfFacilitiesPlannedSuccessful && $scope.noOfFacilitiesSuccessful){
				$scope.progressBarUpdate();
				$scope.noOfFacilitiesPlannedSuccessful = false;	
				$scope.noOfFacilitiesSuccessful = false;
			}
			for (var i = 0; i < $scope.map.markers.length; i++) {
				if (parseFloat($scope.map.markers[i].dataValue) >= 80)
					$scope.greenMarkers++;
				else if (parseFloat($scope.map.markers[i].dataValue) < 60)
					$scope.redMarkers++;
				else
					$scope.orangeMarkers++;
			}
			if (!$scope.$$phase)
			$scope.$apply();
		});
	};
	$scope.getSpiderData = function(selectedParentSectorFormId,
			lastVisitDataId, districtId,cboType) {
		allServices.getSpiderData(selectedParentSectorFormId, lastVisitDataId,
				districtId,cboType).then(function(data) {
			$scope.spiderdata = data;
			$scope.tableData=data.tableData
			$scope.columns=[];
			$scope.columns=Object.keys($scope.tableData[0]);
//			//console.log($scope.spiderdata);
			if($scope.mapLoading)
			$(".loader").fadeOut();
		});
	};

	
	$scope.getAllDistricts = function() {
		allServices.getDashboardDistricts().then(function(data) {
			$scope.allDistricts = data;
			$scope.selectDistrict($scope.allDistricts[0]);
			$scope.getAllDistrictsSuccessful = true;
			$scope.checkBasicDataSuccessful();
		});
	};
	$scope.getSectors = function(parentId) {
		allServices.getSectors(parentId).then(function(data) {
			$scope.sectors = data;
			$scope.selectSector($scope.sectors[0]);
			$scope.getAllDistricts();
		});
	};
	$scope.getParentSectors = function() {
		allServices.getParentSectors().then(function(data) {
			$scope.parentSectors = data;
			$scope.selectedParentSector = $scope.parentSectors[0];
			$scope.getSpiderData($scope.selectedParentSector.formId, 0, 0,$scope.selectedParentSector.type);
			$scope.getSectors($scope.selectedParentSector.formXpathScoreId);
			$scope.getParentSectorSuccessful = true;
			$scope.checkBasicDataSuccessful();
		});
	};
	$scope.getParentSectors();
	$scope.getAllTimePeriods = function() {
		allServices.getAllTimeperiod().then(function(data) {
			$scope.allTimePeriods = data;
			// //console.log($scope.allTimePeriods);
			$scope.selectTimePeriod($scope.allTimePeriods[0]);
			$scope.getAllTimePeriodsSuccessful = true;
			$scope.checkBasicDataSuccessful();
		});
	};
	$scope.getAllTimePeriods();
	$scope.checkBasicDataSuccessful = function(){
		if($scope.getAllTimePeriodsSuccessful && $scope.getParentSectorSuccessful && $scope.getAllDistrictsSuccessful){
//			$scope.getPlannedFacilities($scope.selectedParentSector.formId, $scope.selectedTimePeriod.timePeriod_Nid,
//					$scope.selectedDistrict.areaId);
			$scope.getAllTimePeriodsSuccessful = false;
			$scope.getParentSectorSuccessful = false;
			$scope.getAllDistrictsSuccessful = false;
		}
	};
	
	$scope.getCboAsessed =function()
	{
		allServices.getCboAsessed().then(function(data) {
			$scope.cboAssessed = data;
			$scope.sectorKeys=Object.keys(data);
		});
	}
	$scope.getCboAsessed();
	

		};


$(document).ready(function() {
	$(".dist-list ul.dropdown-menu input").click(function(e) {
		e.preventDefault();
	});
});