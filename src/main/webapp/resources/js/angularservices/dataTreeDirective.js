//Angular app creation

app.config([ "$httpProvider", function($httpProvider) {
	$httpProvider.interceptors.push('apis');
} ]);
// app.config(function(cfpLoadingBarProvider) {
// // true is the default, but I left this here as an example:
// cfpLoadingBarProvider.includeSpinner = true;
// });
app.factory('apis', function() {
	return {
		request : function(config) {

			// need more controlling when there is more than 1 domain involved
			// config.url = "http://localhost:8080/DGA/api/" + config.url;
			config.url = "" + config.url;
			// config.url = "http://prod4.sdrc.co.in/dgacg/api/" + config.url;
			return config;
		}
	};

});
// ///====================== Draw bubbles according to scores
// ======================
var sear = true;
var firstLoad = true;
app
		.directive(
				"bubbleChart",
				function($window, $http) {

					function link(scope, el) {
						var el = el[0];
						scope.drawBubble = function(nodes) {
							var rScale = d3.scale.linear().domain(
									[ 0, nodes.length ]).range(
									[ 0, d3.max(nodes, function(d) {
										return d.size;
									}) ]);

							d3.select(el).selectAll("*").remove();
							var w = scope.getWindowDimensions();
							var width = $(window).width > 768 ? $("#home1")
									.width() : 500, height = w.h * 64.4 / 100;// height
							// =
							// w.h
							// * 59
							// /
							// 100;
							// //
							var force = d3.layout
									.force()
									.nodes(nodes)
									// .charge(-200)
									.charge(
											function(d) {
												if (nodes.length > 200) {
													return -1
															* (Math
																	.pow(
																			d.size / 1.5,
																			3.8) / nodes.length);
												}
												if (nodes.length > 60) {
													return -1
															* (Math
																	.pow(
																			d.size * 3.0,
																			2.0) / nodes.length);
												} else
													return -1
															* (Math
																	.pow(
																			d.size * 3.0,
																			2.0) / nodes.length);
											}).size([ width, height ]).on(
											"tick", tick).start();

							var svg = d3.select(el).append("svg").attr("width",
									width).attr("height", height);

							var bubbleNode = svg.selectAll(".bubbleNode").data(
									nodes).enter().append("circle").attr(
									"class", "bubbleNode").attr("cx",
									function(d) {
										return d.x;
									}).attr("cy", function(d) {
								return d.y;
							}).attr(
									"r",
									function(d) {
										if (nodes.length >= 200) {
											return rScale(d.value * 1.5) + 1;
										}
//										if (nodes.length >= 100) {
//											return rScale(d.value * 1.5) + 1;
//										}
//										
//										if (nodes.length >= 60) {
//											return rScale(d.value * 1) + 1;
//										} // +1 to draw bubbles having 0
//										// values
//										else 
										else if (nodes.length >= 40) {
											return rScale(d.value) / 2.0 + 1;
										} else
											if (nodes.length >= 10
												&& nodes.length < 40) {
											return rScale(d.size) *2 + 1;
										} else if (nodes.length == 1) {
											return rScale(d.size) / 2.2 + 1;
										} 
										else {
											return rScale(d.size) / 1.5 + 1;
										}
									}).style("fill", function(d, i) {
								return d.color;
							})
							// .style("fill", 'url(#gradient)')
							// .style("stroke", "#515151")
							.style("cursor", "pointer").call(force.drag).on(
									"dblclick", bubbleClickHandler).on(
									"mousemove", onmousemove).on("mouseout",
									onmouseout).on("mouseover", onover).on(
									"mousedown", function() {
										d3.event.stopPropagation();
									});

							svg.style("opacity", 1e-6).transition().duration(
									1000).style("opacity", 1);

							d3.select(el).on("mousedown", mousedown);

							function tick() {

								bubbleNode.attr("cx", function(d) {
									return d.x;
								}).attr("cy", function(d) {
									return d.y;
								});

							}

							function mousedown() {
								nodes.forEach(function(o, i) {
									o.x += (Math.random() - .5) * 150;
									o.y += (Math.random() - .5) * 150;
								});
								force.resume();
							}
							;

							function removePopovers() {
								$('.popover').each(function() {
									$(this).remove();
								});
							}
							function onmouseout(d) {
								colormouseout.call(this);
								removePopovers();

							}

							function onover(d) {
								$(this)
										.popover(
												{
													title : '',
													placement : 'auto top',
													container : 'body',
													trigger : 'manual',
													html : true,
													content : function() {
														/*"<span style='color:#009999'>"
														+ "<strong>"
														+ "District Name : "
														+ "</strong>"
														+ "</span>"
														+ d.districtName
														
														+ "<br/>"
														+ */
														return  "<span style='color:#009999'>"
																+ "<strong>"
																+ "CBO Name : "
																+ "</strong>"
																+ "</span>"
																+ d.name

																+ "<br/>"
																+ "<span style='color:#009999'>"
																+ "<strong>"
																+ "Score : "
																+ "</strong>"
																+ "</span>"
																+ d.value + "%";
													}
												});
								$(this).popover('show');
								colormouseover.call(this, d);
								// }

							}

							var hoverColor = [ "#017A27", "#fb7c21", "#b7191c" ];

							function colormouseover(d) {
								d3.select(this).style('fill', function(d, i) {

									if (80 <= d.value) {
										return hoverColor[0];
									} else if (60 <= d.value && d.value <= 79) {
										return hoverColor[1];
									} else if (d.value < 60) {
										return hoverColor[2];
									}
								});
							}
							function colormouseout(d) {
								d3.select(this).style('fill', function(d, i) {
									return d.color;
								});
							}

							// // end of draw bubbles

							function bubbleClickHandler(d) {
								scope.isBubbleVisible = false;
								sear = false;
								$('.popover').each(function() {
									$(this).remove();
								});

								// //console.log(scope.selectedSector + "<>"
								// + scope.selectedAreaBubble);
								// Http calls

								if (scope.selectedSector.parent.name != 'E0'
										&& scope.selectedSector.parent.name != 'C0') {
									// showModal();
									$(".loader").show();
									scope.selectedAreaBubble = d;
									// //console.log(scope.selectedAreaBubble);
									scope.isGranularityVisible = true;
									$http
											.get(
													'forceLayoutData?sectorId='
															+ scope.selectedSector.Id
															+ '&areaId='
															+ scope.selectedAreaBubble.areaId
															+'&timeperiodId='+scope.selectedTimePeriod.timePeriod_Nid)

											.success(
													function(data) {
														// back button
														checkSessionOut(data);
														scope.setBackTrue();
														scope.setColorFalse();
														scope.setValueFalse();

														sear = false;
														d3.select(el)
																.selectAll("*")
																.remove();
														$("#helpImg")
																.attr('src',
																		"resources/images/SingleClickHelp.png");
														var force = d3.layout
																.force()
																.linkDistance(5)
																// .charge(-330)
																// .gravity(0.05)
																// .linkStrength(0.1)
																// .friction(0.9)
																// .friction(function(){
																// if
																// (scope.selectedSector.parent.name
																// == 'C4')
																// return 0.45;
																// else return
																// 0.9;
																// })
																.charge(
																		function(
																				d) {
																			if (scope.selectedSector.parent.name == 'C4')
																				return -300;
																			else
																				return -400;
																		})
																.size(
																		[
																				width,
																				height ])
																.linkDistance(
																		function(
																				d) {
																			if (scope.selectedSector.parent.name == 'E6'
																					|| scope.selectedSector.parent.name == 'E4') {

																				return d.target._children ? 70
																						: 40;
																			}

																			else if (scope.selectedSector.parent.name == 'C4') {

																				return d.target._children ? 40
																						: 20;
																			}

																			else {
																				return d.target._children ? 100
																						: 90;
																			}
																		}).on(
																		"tick",
																		tick);

														var drag = force
																.drag()
																.on(
																		"dragstart",
																		dragstart)
																.on("dragend",
																		dragend);

														var svg = d3.select(el)
																.append("svg")
																.attr("width",
																		width)
																.attr("height",
																		height);

														var tog1 = null;

														var link = svg
																.selectAll(".link"), node = svg
																.selectAll(".forceNode");

														root = data;

														function collapse(d) {
															if (d.children) {
																d._children = d.children;
																d._children
																		.forEach(collapse);
																d.children = null;
															}
														}
														root.children
																.forEach(collapse);
														updateForce();

														function updateForce() {
															var nodes = flatten(root), links = d3.layout
																	.tree()
																	.links(
																			nodes);
															// Restart the force
															// layout.
															force
																	.nodes(
																			nodes)
																	.links(
																			links)
																	.start();

															// Update the links
															link = link
																	.data(
																			links,
																			function(
																					d) {
																				return d.target.id;
																			});

															link.exit()
																	.remove();
															// Enter any new
															// links.
															link
																	.enter()
																	.insert(
																			"line",
																			".forceNode")
																	.attr(
																			"class",
																			"link");

															// Update the nodes
															node = node
																	.data(
																			nodes,
																			function(
																					d) {
																				return d.id;
																			});

															node.exit()
																	.remove();
															// Enter any new
															// nodes.
															var nodeEnter = node
																	.enter()
																	.append("g")
																	.attr(
																			"class",
																			"forceNode")
																	.on(
																			"click",
																			click)
																	.on(
																			"dblclick",
																			dblclick)
																	.style(
																			"cursor",
																			"pointer")
																	.on(
																			"mouseover",
																			onover)
																	.on(
																			"mousemove",
																			onmousemove)
																	.on(
																			"mouseout",
																			onmouseout)
																	.call(drag);

															nodeEnter
																	.append(
																			"circle")
																	.attr(
																			"r",
																			function(
																					d) {
																				return Math
																						.sqrt(d.size);
																			})
																	.style(
																			"fill",
																			function(
																					d) {

																				return d.color;
																			});

															nodeEnter
																	.append(
																			"text")
																	.style(
																			"text-anchor",
																			"start")
																	.style(
																			"font-size",
																			function(
																					d) {
																				if (scope.selectedSector.parent.name == 'C4')
																					return "10px";
																				else
																					return "10px";
																			})
																	.style(
																			"font-weight",
																			"bold")
																	.text(
																			function(
																					d) {
																				var removedDesc = d.name
																						.split(" ");
																				if(d['_children'] != undefined && d['_children'].length){
																					return "*" + removedDesc
																					.splice(
																							0,
																							1)
																					.join(
																							".");
																				}
																				else{
																					return removedDesc
																					.splice(
																							0,
																							1)
																					.join(
																							".");
																				}
																				
																			});

																	// node.select("circle")
																	// .style("fill",
																	// d.color);

																	link = svg
																			.selectAll(".link"),
																	node = svg
																			.selectAll("g.forceNode");
														}

														function tick() {
															link
																	.style(
																			"stroke-width",
																			function(
																					d) {
																				return "3px";
																			})
																	.attr(
																			"x1",
																			function(
																					d) {
																				return d.source.x;
																			})
																	.attr(
																			"y1",
																			function(
																					d) {
																				return d.source.y;
																			})
																	.attr(
																			"x2",
																			function(
																					d) {
																				if (d.target.x > width)
																					return width - 20;
																				else if (d.target.x < 5) {
																					return 5;
																				} else
																					return d.target.x;
																			})
																	.attr(
																			"y2",
																			function(
																					d) {
																				if (d.target.y > height)
																					return height - 20;
																				else if (d.target.y < 5) {
																					return 5;
																				} else
																					return d.target.y;
																			});

															node
																	.attr(
																			"transform",
																			function(
																					d) {
																				// {return
																				// "translate("+
																				// d.x+
																				// ","+
																				// d.y+
																				// ")";}
																				if (d.y > height)
																					return "translate("
																							+ d.x
																							+ ","
																							+ (height - 30)
																							+ ")";
																				else if (d.y < 5)
																					return "translate("
																							+ d.x
																							+ ","
																							+ 10
																							+ ")";
																				else if (d.x > width)
																					return "translate("
																							+ (width - 30)
																							+ ","
																							+ d.y
																							+ ")";
																				else if (d.x < 5)
																					return "translate("
																							+ 10
																							+ ","
																							+ d.y
																							+ ")";
																				else {
																					return "translate("
																							+ d.x
																							+ ","
																							+ d.y
																							+ ")";
																				}
																			});
														}

														function onmouseout() {
															scope.closeViz();
														}
														function onover(d) {

															scope.showViz();
															// $(".trend-viz").effect('slide',
															// { direction:
															// "right" });
															// $(".trend-viz").effect('slide',
															// { direction:
															// "left"
															// }).animate({duration:
															// "700"});
															// $(".trend-viz").slideToggle("slow");
															// $(".trend-viz").fadeToggle('slow');
															//                                                      

															if (d.wt >= 0 && !d.response) {
																var value = d.value == "NaN" ? ""
																		: d.value
																				;
																if(d.percentile != undefined){
																d3
																		.select(
																				".datatree_popover_content")
																		.html(
																				"<span style='color:#009999'>"
																						+ "Area Name : "
																						+ "</span>"
																						+ "<span>"
																						+ scope.selectedAreaBubble.name
																						+ "</span>"
																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Question Name : "
																						+ "</span>"
																						+ "<span>"
																						+ d.qsname
																						+ "</span>"
																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Value : "
																						+ "</span>"
																						+ "<span>"
																						+ value
																						+ "</span>"
																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Max : "
																						+ "</span>"
																						+ "<span>"
																						+ d.wt
																						+ "</span>"
																						+ "<br/><span style='color:#009999'>Percentage : </span><span>"
																						+ d.percentile + "%"
																						+ "</span>");
																}
																else{
																	d3
																	.select(
																			".datatree_popover_content")
																	.html(
																			"<span style='color:#009999'>"
																					+ "Area Name : "
																					+ "</span>"
																					+ "<span>"
																					+ scope.selectedAreaBubble.name
																					+ "</span>"
																					+ "<br/>"
																					+ "<span style='color:#009999'>"
																					+ "Question Name : "
																					+ "</span>"
																					+ "<span>"
																					+ d.qsname
																					+ "</span>"
																					+ "<br/>"
																					+ "<span style='color:#009999'>"
																					+ "Value : "
																					+ "</span>"
																					+ "<span>"
																					+ value
																					+ "</span>"
																					+ "<br/>"
																					+ "<span style='color:#009999'>"
																					+ "Max : "
																					+ "</span>"
																					+ "<span>"
																					+ d.wt
																					+ "</span>"
																					);
																}
																d3
																		.select(
																				"#scrollingDiv")
																		.style(
																				"position",
																				"absolute")
																		.style(
																				"left",
																				d3.event.pageX
																						- 400
																						+ 'px')
																		.style(
																				"top",
																				d3.event.pageY
																						+ 'px')
																		.style(
																				"width",
																				"35%");

															}
															
															
															else if(d.response)
																{

																d3
																		.select(
																				".datatree_popover_content")
																		.html(
																				"<span style='color:#009999'>"
																						+ "Area Name : "
																						+ "</span>"
																						+ scope.selectedAreaBubble.name

																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Question : "
																						+ "</span>"
																						+ d.qsname

																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Response : "
																						+ "</span>"
																						+ "<span>"
																						+ d.value
																						+ "</span>")
																		.style(
																				"cursor",
																				"pointer");
																d3
																		.select(
																				"#scrollingDiv")
																		.style(
																				"width",
																				d.name.length > 2 ? "35%"
																						: "20%");

															}

															else {

																d3
																		.select(
																				".datatree_popover_content")
																		.html(
																				"<span style='color:#009999'>"
																						+ "Area Name : "
																						+ "</span>"
																						+ scope.selectedAreaBubble.name

																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Question : "
																						+ "</span>"
																						+ d.qsname

																						+ "<br/>"
																						+ "<span style='color:#009999'>"
																						+ "Percentage : "
																						+ "</span>"
																						+ "<span>"
																						+ d.value
																						+ "%</span>")
																		.style(
																				"cursor",
																				"pointer");
																d3
																		.select(
																				"#scrollingDiv")
																		.style(
																				"width",
																				d.name.length > 2 ? "35%"
																						: "20%");

															}
														}

														function click(d) {
															// /scope.isShowDatatableBtn
															// = false;
															// $("#gridBtn").hide();
															if (d3.event.defaultPrevented)
																return;

															if (d.size == 300
																	|| d.size == 110) {
																// scope.isShowDatatableBtn
																// = false;
																// $("#gridBtn").hide();
																updateForce(d);
															} else {

																if (d.children) {
																	// scope.isShowDatatableBtn
																	// = false;
																	$(
																			"#gridBtn")
																			.hide();
																	if(!d.lastLevel&&tog1 && tog1.lastLevel && tog1.children)
																	{
																		tog1._children = tog1.children;
																		tog1.children = null;
																		updateForce(tog1);

																	}
																	scope.isShowTable = false;
																	d._children = d.children;
																	d.children = null;
																	tog1 = null;
																	updateForce(d);

																} else if (d._children
																		&& !tog1) {
																	if (scope.parentArea.value == "Uttar Pradesh") {
																		$(
																				"#gridBtn")
																				.show();
																	}
																	d.children = d._children;
																	d._children = null;
																	tog1 = d;
																	updateForce(d);
																	// scope.getTableData(d.children,d.questionXpath,d.qsname);
																}

																else if(d._children && tog1 && d.lastLevel)
																{
																	d.children = d._children;
																	d._children = null;
																	if(tog1.lastLevel && tog1.children)
																		{
																			// scope.isShowDatatableBtn
																			// = false;
																			$(
																					"#gridBtn")
																					.hide();
																			scope.isShowTable = false;
																			tog1._children = tog1.children;
																			tog1.children = null;
																			updateForce(tog1);

																		}
																	else if(!tog1.lastLevel)
																		{
																			parentTog=tog1;
																		}
																	tog1 = d;
																	updateForce(d);
																}
																else if (d._children
																		&& tog1) {
																	if (tog1.size != 300) {
																		if (scope.parentArea.value == "Uttar Pradesh") {
																			$(
																					"#gridBtn")
																					.show();
																		}
																		
																		tog1._children = tog1.children;
																		tog1.children = null;
																		updateForce(tog1);
																		if(tog1.lastLevel && tog1._children)
																		{
																			parentTog._children=parentTog.children;
																			parentTog.children=null;
																			updateForce(parentTog);
																		}
																		d.children = d._children;
																		d._children = null;
																		tog1 = d;
																		updateForce(d);
																		// scope.getTableData(d.children,d.questionXpath,d.qsname);
																	}

																}
//															
															}
															// }
															d3
																	.select(
																			this)
																	.classed(
																			"fixed",
																			d.fixed = false);
															updateForce();

														}

														// Returns a list of all
														// nodes under the root.
														function flatten(root) {
															var nodes = [], i = 0;

															function recurse(
																	node) {
																if (node.children)
																	node.children
																			.forEach(recurse);
																node.id = ++i;
																nodes
																		.push(node);
															}
															recurse(root);
															return nodes;
														}

														function dblclick(d) {
															d3
																	.select(
																			this)
																	.classed(
																			"fixed",
																			d.fixed = false);
															scope.showViz();
														}

														function dragstart(d) {
															d3
																	.select(
																			this)
																	.classed(
																			"fixed",
																			d.fixed = true);
														}

														function dragend(d, i) {

															if (d.x > 4
																	&& d.x < width
																	&& d.y > 4
																	&& d.y < height) {
																d.fixed = true; // of
																// course
																// set
																// the
																// node
																// to
																// fixed
																// so
																// the
																// force
																// doesn't
																// include
																// the
																// node
																// in
																// its
																// auto
																// positioning
																// stuff

															} else {
																d.fixed = false;
															}
															force.resume();
														}
														// hideModal();
														$(".loader").fadeOut();
													});
								}
							}
							// //================== Group bubbles of same color
							// according to the range =====================
							if (firstLoad) {
								$("#btnColorBubble")
										.click(
												function() {
													d3.select(el)
															.selectAll("*")
															.remove();
													console
															.log("show modal.........");
													// showModal();
													$(".loader").show();
													var root = {
														"name" : "category",
														"children" : scope.bubbleDataModel
													};
													nodes = root.children;
													var rScale = d3.scale
															.linear()
															.domain(
																	[
																			0,
																			nodes.length ])
															.range(
																	[
																			0,
																			d3
																					.max(
																							nodes,
																							function(
																									d) {
																								return d.size;
																							}) ]);
													var foci = {
														"#D13F43" : {
															x : 200,
															y : 200
														},
														"#F19537" : {
															x : 300,
															y : 200
														},
														"#22B369" : {
															x : 400,
															y : 200
														}
													};
													var force = d3.layout
															.force()
															.nodes(nodes)
															.gravity(0)
															// .charge(-70)
															.charge(
																	function(d) {
																		if (nodes.length > 60) {
																			return -1
																					* (Math
																							.pow(
																									d.size * 3,
																									2.0) / nodes.length);
																		} else
																			return -1
																					* (Math
																							.pow(
																									d.size * 2.0,
																									2.0) / nodes.length);
																	})
															.size(
																	[ width,
																			height ])
															.on("tick", tickk)
															.start();
													var svg = d3.select(el)
															.append("svg")
															.attr("width",
																	width)
															.attr("height",
																	height);

													var bubbleNode = svg
															.selectAll(
																	".bubbleNode")
															.data(nodes)
															.enter()
															.append("circle")
															.attr("class",
																	"bubbleNode")
															.attr(
																	"cx",
																	function(d) {
																		return d.x - 50;
																	})
															.attr(
																	"cy",
																	function(d) {
																		return d.y;
																	})
															// .attr("r",
															// function(d) {
															// return
															// d.size; })
															.attr(
																	"r",
																	function(d) {
																		if (nodes.length > 60) {
																			return rScale(d.value) + 1;
																		} else if (nodes.length > 40) {
																			return rScale(d.value) / 2.5 + 1;
																		} else if (nodes.length > 10
																				&& nodes.length < 40) {
																			return rScale(d.value) / 4 + 1;
																		} else if (nodes.length == 1) {
																			return rScale(d.size) / 2.2 + 1;
																		} else {
																			return rScale(d.size) / 1.5 + 1;
																		}
																	})
															.style(
																	"fill",
																	function(d,
																			i) {
																		return d.color;
																	})
															// .style("stroke",
															// "#515151")
															.style("cursor",
																	"pointer")
															.call(force.drag)
															.on("dblclick",
																	bubbleClickHandler)
															.on("mousemove",
																	onmousemove)
															.on("mouseout",
																	onmouseout)
															.on("mouseover",
																	onover)
															.on(
																	"mousedown",
																	function() {
																		d3.event
																				.stopPropagation();
																	});

													svg
															.style("opacity",
																	1e-6)
															.transition()
															.duration(1000)
															.style("opacity", 1);

													d3.select(el).on(
															"mousedown",
															mousedown);

													function tickk(e) {
														// Push different nodes
														// in
														// different directions
														// for
														// clustering.
														var k = .1 * e.alpha;
														nodes
																.forEach(function(
																		o, i) {

																	o.y = o.y
																			+ (foci[o.color].y - o.y)
																			* k;
																	o.x = o.x
																			+ (foci[o.color].x - o.x)
																			* k;
																});
														bubbleNode
																.attr(
																		"cx",
																		function(
																				d) {
																			return d.x - 50;
																		})
																.attr(
																		"cy",
																		function(
																				d) {
																			return d.y;
																		});
														// hideModal();
														$(".loader").fadeOut();
														sear=true;
														searchNode();
													}

													function mousedown() {
														nodes
																.forEach(function(
																		o, i) {
																	o.x += (Math
																			.random() - .5) * 50;
																	o.y += (Math
																			.random() - .5) * 50;
																});
														force.resume();
													}
														
													// /scope.loadBarStop();
												}); // end of group bubbles

								// //===================== Sorting of bubbles
								// according to their scores ==================

								$("#btnSortBubble")
										.click(
												function() {
													// showModal();
													$(".loader").show();
													setTimeout(function(){
														searchNode();
												
													{
														d3.select(el)
																.selectAll("*")
																.remove();

														var root = {
															"name" : "category",
															"children" : scope.bubbleDataModel
														};
														nodes = root.children;

														var rScale = d3.scale
																.linear()
																.domain(
																		[
																				0,
																				nodes.length ])
																.range(
																		[
																				0,
																				d3
																						.max(
																								nodes,
																								function(
																										d) {
																									return d.size / 1.5;
																								}) ]);

														function compare(a, b) {
															if (a.name < b.name)
																return -1;
															if (a.name > b.name)
																return 1;
															return 0;
														}

														var bubble = d3.layout
																.pack()
																.sort(
																		comparator)
																.size(
																		[
																				width,
																				height ])
																.padding(10);

														function comparator(a,
																b) {
															return (a.value - b.value);
														}

														var svg = d3.select(el)
																.append("svg")
																.attr("width",
																		width)
																.attr("height",
																		height);
														var layer1 = svg
																.append('g');
														var layer2 = svg
																.append('g');

														var node = layer2
																.selectAll(
																		".bubbleNode")
																.data(
																		bubble
																				.nodes(
																						classes(root))
																				.filter(
																						function(
																								d) {
																							return !d.children;
																						}))
																.enter()
																.append(
																		"circle")
																.attr("class",
																		"bubbleNode")
																.attr(
																		"cx",
																		function(
																				d) {
																			return d.x;
																		})
																.attr(
																		"cy",
																		function(
																				d) {
																			return d.y;
																		})

																.attr(
																		"r",
																		function(
																				d) {
																			if (nodes.length > 60) {
																				return rScale(d.value * 1.5)
																						- (rScale(d.value * 1.5));
																			} else if (nodes.length > 40) {
																				return rScale(d.value)
																						/ 1.5
																						- (rScale(d.value) / 1.5);
																			} else if (nodes.length > 10
																					&& nodes.length < 40) {
																				return rScale(d.value)
																						/ 2
																						- (rScale(d.value) / 2);
																			} else {
																				return rScale(d.size)
																						/ 1.5
																						- (rScale(d.size) / 1.5);
																			}
																		})
																.style(
																		"opacity",
																		0)
																.style(
																		"fill",
																		function(
																				d,
																				i) {
																			return d.color;
																		})
																.style(
																		"stroke",
																		"#099")
																.on("dblclick",
																		bubbleClickHandler)
																.on(
																		"mousemove",
																		onmousemove)
																.on("mouseout",
																		onmouseout)
																.on(
																		"mouseover",
																		onover);
														node
																.each(function() {
																	d3
																			.select(
																					this)
																			.transition()
																			.duration(
																					1000)
																			.ease(
																					"linear")
																			.attr(
																					"r",
																					function(
																							d) {
																						if (nodes.length >= 200) {
																							return rScale(d.value * 2.5) + 1;
																						}
//																						if (nodes.length >= 100) {
//																							return rScale(d.value * 1.5) + 1;
//																						}
//																						
//																						if (nodes.length >= 60) {
//																							return rScale(d.value * 1) + 1;
//																						} // +1 to draw bubbles having 0
//																						// values
//																						else 
																						if (nodes.length >= 40) {
																							return rScale(d.value) / 1.5 + 1;
																						} else if (nodes.length >= 10
																								&& nodes.length < 40) {
																							return rScale(d.size) * 1.5 + 1;
																						} else if (nodes.length == 1) {
																							return rScale(d.size) / 2.2 + 1;
																						} 
																						else {
																							return rScale(d.size) / 1.5 + 1;
																						}
																						
//																						if (nodes.length > 60) {
//																							return rScale(d.value * 1.5) + 1;
//																						} else if (nodes.length > 40) {
//																							return rScale(d.value) / 1.5 + 1;
//																						} else if (nodes.length > 10
//																								&& nodes.length < 40) {
//																							return rScale(d.value) / 4 + 1;
//																						} else if (nodes.length == 1) {
//																							return rScale(d.size) / 2.2 + 1;
//																						} else {
//																							return rScale(d.size) / 1.5 + 1;
//																						}

																					})
																			.style(
																					"opacity",
																					1)
																			.style(
																					"cursor",
																					"pointer");
																	node
																			.append(
																					"text")
																			.attr(
																					"dy",
																					".3em")
																			.style(
																					"text-anchor",
																					"middle")
																			.text(
																					function(
																							d) {
																						return d.name;
																					});
																	;
																	;
																});
														var lineData = [];

														var sortedNode = [];

														function sortNode(a, b) {
															return (a.__data__.value - b.__data__.value);
														}

														sortedNode = node[0]
																.sort(sortNode);
														sortedNode
																.forEach(function(
																		d) {
																	lineData
																			.push({
																				'x' : d.cx.animVal.value,
																				'y' : d.cy.animVal.value
																			});
																});
														// //console.log(lineData[0].x);
														var lineFunction = d3.svg
																.line()
																.x(function(d) {
																	return d.x;
																})
																.y(function(d) {
																	return d.y;
																})
																.interpolate(
																		"linear");

														var pathDraw = layer1
																.append("path")
																.attr(
																		"d",
																		lineFunction(lineData))
																.style(
																		"opacity",
																		0)
																.attr("stroke",
																		"#099")
																.attr(
																		"stroke-width",
																		0)
																.attr("fill",
																		"none");
														pathDraw
																.each(function() {
																	d3
																			.select(
																					this)
																			.transition()
																			.duration(
																					1000)
																			.ease(
																					"linear")
																			.style(
																					"opacity",
																					1)
																			.attr(
																					"stroke-width",
																					2);
																});

														function classes(root) {
															var classes = [];
															function recurse(
																	name, node) {

																if (node.children)
																	node.children
																			.forEach(function(
																					child) {
																				recurse(
																						node.name,
																						child);
																			});
																else
																	classes
																			.push({
																				name : node.name,
																				value : node.value,
																				weight : node.weight,
																				size : node.size,
																				color : node.color,
																				areaId : node.areaId
																			});
															}
															recurse(null, root);
															return {
																children : classes
															};
														}
														searchNode();
															
													}
													
													sear = true;
														
															
															$(".loader").fadeOut();
														}, 100)
													
														

												}); // end of sorting
								// //////=============== end of bubble
								// functionalities ===============

								$("#btnBack").click(function() {
									scope.getbubbledata(scope.selectedSector);
									sear = true;
								}); // Back to selected sector
								firstLoad = false;
							}
						};
						scope.$watch('bubbleDataModel', function() {

							if (scope.bubbleDataModel.length) {
								scope.drawBubble(scope.bubbleDataModel);
								searchNode();
							}
							else{
								d3.select(el).selectAll("*").remove();
							}
						}, false);

					}
					return {
						link : link,
						restrict : "E"
					};

				});

// //////===============START of Tree functionalities ===============

app.directive("sdrcDataTree", function($window) {
	function link(scope, el) {
		var el = el[0];
		scope.drawDataTree = function(nodes) {
			var margin = {
				top : 20,
				right : 120,
				bottom : 20,
				left : 100
			};
			d3.select(el).selectAll("*").remove();
			var w = scope.getWindowDimensions();
			var width = 500;
			var height = w.h * 67.4 / 100; // height = w.h * 63.8 / 100;

			var i = 0, duration = 750, root;

			var tree = d3.layout.tree().size([ height, width ]);

			var diagonal = d3.svg.diagonal().projection(function(d) {
				return [ d.y, d.x ];
			});

			var svg = d3.select(el).append("svg").attr("width", width).attr(
					"height", height + margin.bottom).append("g").attr(
					"transform",
					"translate(" + margin.left + "," + margin.top + ")");

			// d3.json("/d/4063550/flare.json", function(error, flare) {
			root = nodes;
			root.x0 = height / 2;
			root.y0 = 0;
			var source;
			var tog = null;
			var ui;
			var textr = [];
			var ndarr = [];
			var ancestors = [];

			svg.append("svg:clipPath").attr("id", "clipper").append("svg:rect")
					.attr('id', 'clip-rect');

			var animGroup = svg.append("svg:g").attr("clip-path",
					"url(#clipper)");

			function collapse(d) {
				if (d.Id != 1) {
					if (d.children) {
						d._children = d.children;
						d._children.forEach(collapse);
						d.children = null;
					}
				} else {
					tog = d;
				}

			}

			root.children.forEach(collapse);
			updateTree(root);

			d3.select(self.frameElement).style("height", "400px");

			function updateTree(source) {
				// Compute the new tree layout.
				var nodes = tree.nodes(root).reverse();
				/*
				 * for(var i=0; i<nodes.length; i++){ nodes[i].x0 =
				 * nodes[i].x0/2; nodes[i].x = nodes[i].x/2; nodes[i].y0 =
				 * nodes[i].y0/2; nodes[i].y = nodes[i].y/2; }
				 */
				var links = tree.links(nodes);
				// Normalize for fixed-depth.
				nodes.forEach(function(d) {
					d.y = d.depth * 180;
				});

				// Update the nodes
				var node = svg.selectAll("g.node").data(nodes, function(d) {
					return d.id || (d.id = ++i);
				});
				// Enter any new nodes at the parent's previous position.
				var nodeEnter = node.enter().append("g").attr("class", "node")
						.attr(
								"transform",
								function(d) {
									return "translate(" + source.y0 + ","
											+ source.x0 + ")";
								}).on("click", function(d) {
							if (d.parent) {
								return click(d);
							}
						})

						.on("mouseover", function showPopover(d) {
							if (d.depth == 2) {
								$(this).popover(

								{
									title : '',
									placement : 'auto left',
									container : 'body',
									trigger : 'manual',
									html : true,
									content : function() {
										return d.fullName;
									}
								});
								$(this).popover('show');
							}
							else if(d.depth == 1)
								{

								$(this).popover(

								{
									title : '',
									placement : 'auto left',
									container : 'body',
									trigger : 'manual',
									html : true,
									content : function() {
										return d.fullName;
									}
								});
								$(this).popover('show');
								}
						}).on("mouseout", function removePopovers(d) {
							$('.popover').each(function() {
								$(this).remove();
							});
						});

				nodeEnter.append("circle").attr("r", 1e-6).style("fill",
						"#D0D0D0").style({
					"z-index" : "-1",
					"cursor" : "pointer"
				});
				// append text on the nodes
				nodeEnter.append("text").style("fill", function(d) {
					return "black";
				}).style("font-weight", "bold").style("font-size", function(d) {
					if (!d.parent) {
						return "10px";
					} else if (d.Id == 1 || d.Id == 15 || d.Id == 29) {
						return "8px";
					} else {
						return "10px";
					}
				}).attr("x", function(d) {
					if (!d.parent) {
						return -80;
					} else {
						return d.children || d._children ? -7 : 20;
					}

				}).attr("dy", function(d){
					if(!d.parent)
						return ".35em"
					else
						return ".35em"
					})

				.text(function(d) {
//					if(d.name.includes("."))
//						return d.name.split(".")[0];
//					else
						return d.name
				}).style("fill-opacity", 1e-6);

				// Transition nodes to their new position.
				var nodeUpdate = node.transition().duration(duration).attr(
						"transform", function(d) {
							return "translate(" + d.y + "," + d.x + ")";
						});

				nodeUpdate.select("circle").attr("id", "nodeEmp").attr("r", 13)
						.style("stroke", "none");

				nodeUpdate.select("text").style("fill-opacity", 1);

				// Transition exiting nodes to the parent's new position.
				var nodeExit = node.exit().transition().duration(duration)
						.attr(
								"transform",
								function(d) {
									return "translate(" + source.y + ","
											+ source.x + ")";
								}).remove();

				nodeExit.select("circle").attr("r", 1e-6);

				nodeExit.select("text").style("fill-opacity", 1e-6);

				// Update the links
				var link = svg.selectAll("path.link").attr("id", "pathform")
						.data(links, function(d) {
							return d.target.id;
						});

				// Enter any new links at the parent's previous position.#####
				link.enter().insert("path", "g").attr("class", "link").style({
					"stroke" : "#D0D0D0",
					"fill" : "none"
				}).style("stroke-width", 3)
				/* .style("stroke-dasharray", "5,5") */.style("opacity", 1)
						.attr("d", function(d) {
							var o = {
								x : source.x0,
								y : source.y0
							};
							return diagonal({
								source : o,
								target : o
							});
						});

				// Transition links to their new position.
				link.transition().duration(duration).attr("d", diagonal);

				// Transition exiting nodes to the parent's new position.
				link.exit().transition().duration(duration).attr("d",
						function(d) {
							var o = {
								x : source.x,
								y : source.y
							};
							return diagonal({
								source : o,
								target : o
							});
						}).remove();

				// clear the old positions for transition.
				nodes.forEach(function(d) {
					d.x0 = d.x;
					d.y0 = d.y;
				});

				ui = {
					svgRoot : svg,
					nodeGroup : node,
					linkGroup : link,
					animGroup : animGroup
				};

				if (source.name == 'CYSD') {
					click(source.children[0]);

				}
			}

			function click(d) {
			/*	if (d.name != "E0" && d.name != "C0" && d.name != "SVS"
						&& d.name != "RVS" && d.name != "DVS"
						&& d.name != "Health Facility") {*/
					$("#helpImg").attr('src',
							"resources/images/DoubleClickHelp.png");
			/*	}*/
				sear = true;
				$("#gridBtn").hide();
				scope.isShowTable = false;
				/*
				 * ############# clear the previous highlight in every new click
				 */$('.popover').each(function() {
					$(this).remove();
				});

				ui.animGroup.selectAll("path.selected").data([]).exit()
						.remove();
				scope.setBackFalse();
				scope.setColorTrue();
				scope.setValueTrue();
				/*
				 * ###########Toggle children on click######### if children
				 * exits then do nothing if children are not showing but exits
				 * then show the children by closing others if it is the child
				 * node itself and no further child available then call draw
				 * bubble function
				 */

				if (!d.children) {
					if (d._children) {
						d.children = d._children;
						d._children = null;
						tog._children = tog.children;
						tog.children = null;
						updateTree(tog);
						tog = d;
						updateTree(d);
					}
				}

				/*
				 * ######## Highlight the specified path###########
				 * highlightEvent is defined here it only chooses the the
				 * selected path(from root to child) and pass it to the event
				 * animateLink
				 */

				ancestors = [];

				var parent;

				if (d.children) {
				/*	if (d.children.length > 1) {
						parent = d.children[1];
						scope.selectedSector = parent;
						scope.getbubbledata(parent);

					} else {*/
						parent = d.children[0];
						scope.selectedSector = parent;
						scope.getbubbledata(parent);
					/*}*/
				} else {
					parent = d;
					scope.selectedSector = parent;
					scope.getbubbledata(parent);
				}

				while (!_.isUndefined(parent)) {
					ancestors.push(parent);
					parent = parent.parent;

				}

				// Get the matched links

				var matchedLinks = [];
				d3.select(el).selectAll('path.link').filter(function(d, i) {
					return _.any(ancestors, function(p) {
						return p === d.target;
					});
				})

				// Push the matched links into an array of objects

				.each(function(d) {
					matchedLinks.push(d);
				});
				animateNode(ancestors[0]);
				animateLink(matchedLinks);
				// animateText(d);

			}

			/*
			 * highlightEvent(links) highlighting the path in the tree here it
			 * creates a new path for the link defined in highlightEvent
			 */

			function animateLink(links) {

				var linkRenderer = d3.svg.diagonal().projection(function(d) {
					return [ d.y, d.x ];
				});

				// Links defined in highlightEvent
				ui.animGroup.selectAll("path.selected").data([]).exit()
						.remove();

				ui.animGroup.selectAll("path.selected").data(links).enter()
						.append("svg:path").attr("class", "selected").style(
								"fill", "none").style("stroke", "#009999")
						.style("stroke-width", 3).style("opacity", 1).attr("d",
								linkRenderer);

				// Animate the path selected
				var overlayBox = ui.svgRoot.node().getBBox();

				ui.svgRoot.select("#clip-rect")

				.attr("x", overlayBox.x).attr("width", 0).transition()
						.duration(1200).attr("y",
								overlayBox.y + overlayBox.width).attr("height",
								overlayBox.height).attr("width",
								overlayBox.width).attr("y", overlayBox.y);

			}

			function animateNode(node) {
				var nodes = d3.select(el).selectAll("circle");

				if (ndarr.length > 1) {
					for (var i = 0; i < ndarr.length; i++) {
						var selected = nodes.filter(function(d) {
							return _.any(ndarr, function(p) {
								return p === d;
							});

						});
						var selectedText = selected[0][0].nextSibling;
						var selectedTextDe = selected[0][1].nextSibling;
						selectedText.style.fill = "black";
						selectedTextDe.style.fill = "black";
						selected.style("fill", "#D0D0D0").style("stroke",
								"#D0D0D0").style("z-index", "-1");

					}
				}

				ndarr = [];
				while (node) {
					ndarr.push(node);
					node = node.parent;
				}

				for (var i = 0; i < ndarr.length; i++) {
					var selected = nodes.filter(function(d) {
						return _.any(ndarr, function(p) {
							return p === d ;
						});

					});
					console.log(selected);
					var selectedText = selected[0][0].nextSibling;
					var selectedTextDe = selected[0][1].nextSibling;
					var selectedid = selected[0][0].nextSibling.innerHTML;
					if (selectedid != "Gov." && selectedid != "Admin."
							&& selectedid != "Fin. Mgmt."
							&& selectedid != "Eff"
							&& selectedid != "DIV-VS" && selectedid!="HR") {
						selectedText.style.fill = "white";
					} else {
						selectedTextDe.style.fill = "white";
					}

					selected.style("fill", "#009999")
							.style("stroke", "#009999").style("z-index", "-1");
				}

			}

		};

		scope.$watch('treeData', function() {

			if (scope.treeData) {
				scope.drawDataTree(scope.treeData);
			}
		}, false);

	}
	return {
		link : link,
		restrict : "E"
	};

});
// searchNode() to search
function searchNode() {

	// find the node

	// alert("Hello Rohit");

	// $(document).ready(function(){
	// $('input').keyup(function(){

	var selectedVal = document.getElementById('search').value;

	if (sear == true) {
		var node = d3.selectAll(".bubbleNode");
		// var searchInput = $(this).val();
		node.style("opacity", "1");
		optArray = [];
		if (selectedVal == "") {
			// node.style("stroke", "white")
			node.style("stroke-width", "1");
		}

		else {
			node.style("opacity", "0.2");
			var selected = node.filter(function(d, i) {
				// if(d.name.toUpperCase().match(selectedVal.toUpperCase())){

				// }
				if (d.name.toUpperCase().match(selectedVal.toUpperCase())) {
					optArray.push(d.name);
				}
				return d.name.toUpperCase().match(selectedVal.toUpperCase());
			});
			selected.style("opacity", "1");

		}
	} else {
		var node = d3.selectAll(".forceNode");
		// var searchInput = $(this).val();
		node.style("opacity", "1");
		optArray = [];
		if (selectedVal == "") {
			// node.style("stroke", "white")
			node.style("stroke-width", "1");
		}

		else {
			node.style("opacity", "0.2");
			var selected = node.filter(function(d, i) {
				// if(d.name.toUpperCase().match(selectedVal.toUpperCase())){

				// }
				if (d.name.toUpperCase().match(selectedVal.toUpperCase())) {
					optArray.push(d.name);
				}
				return d.name.toUpperCase().match(selectedVal.toUpperCase());
			});
			selected.style("opacity", "1");

		}
	}

}
$(document).ready(function(){
	setTimeout(function(){
		if($(window).width() > 800)
			$(".chart-container-tab").height($("#myTabContent").height());
			$("section.legendsDataTree").css("top", $("#myTabContent").height()-42)
		if($(window).width() < 560){
			$(".chart-container-tab #home").scrollLeft($("#home").width());
			$(".chart-container-tab #home1").scrollLeft($("#home1").width()/2 - 50);
		}
	}, 2000)
	
});