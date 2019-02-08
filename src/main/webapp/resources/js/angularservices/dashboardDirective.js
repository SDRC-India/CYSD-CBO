//Angular app creation


app.config([ "$httpProvider", function($httpProvider) {
	$httpProvider.interceptors.push('apis');
} ]);
//app.config(function(cfpLoadingBarProvider) {
//	// true is the default, but I left this here as an example:
//	cfpLoadingBarProvider.includeSpinner = true;
//});
app.factory('apis', function() {
	return {
		request : function(config) {

			// need more controlling when there is more than 1 domain involved
			config.url =  "" + config.url;
//			config.url =  "http://192.168.1.114:8080/DGA/api/" + config.url;
//			config.url =  "http://prod4.sdrc.co.in/dgacg/api/" + config.url;
			return config;
		}
	};

});


app
		.directive(
				"sdrcSpider",
				function($window) {
					function link(scope, el) {
						var el = el[0];

						var RadarChart = {
							draw : function(el, d, options) {
								d3.select(el).select("svg").remove();
								var w = scope.getWindowDimensions();
//								var width = (w.w * 30) / 100;
								var width = $(window).width() > 768 ? $("#"+el.parentElement.getAttribute("id")).width()-200:$("#"+el.parentElement.getAttribute("id")).width()-100;
								var cfg = {
									radius : 5,
									w : width,
									h : width,
									factor : 1,
									factorLegend : .85,
									levels : 5,
									maxValue : 0,
									radians : 2 * Math.PI,
									opacityArea : 0.5,
									ToRight : 5,
									TranslateX : 80,
									TranslateY : 30,
									ExtraWidthX : 200,
									ExtraWidthY : 200,
									color : d3.scale.category10()
								};
								if($(window).width() < 565){
									cfg.TranslateX = 38;
								}
								/*if ('undefined' !== typeof options) {
									for ( var i in options) {
										if ('undefined' !== typeof options[i]) {
											cfg[i] = options[i];
										}
									}
								}*/
								// cfg.maxValue = Math.max(cfg.maxValue,
								// d3.max(d,
								// function(i) {
								// return d3.max(i.map(function(o) {
								// return o.value;
								// }));
								// }));
								cfg.maxValue = 100;
								var allAxis = (d[0].map(function(i, j) {
									return i.axis;
								}));
								var mouseOutcolor = [ "#8FBBD9", "#FFBF87" ];
								var hoverColor = [ "#1F77B4", "#FF8C26" ];

								var total = allAxis.length;
								var radius = cfg.factor
										* Math.min(cfg.w / 2, cfg.h / 2);
								var Format = d3.format('%');
								

								var g = d3.select(el).append("svg").style("overflow", "visible").attr(
										"width", cfg.w + cfg.ExtraWidthX).attr(
										"height", cfg.h + cfg.ExtraWidthY)
										.append("g").attr(
												"transform",
												"translate(" + cfg.TranslateX
														+ "," + cfg.TranslateY
														+ ")");
								;

								var tooltip;

								// Circular segments
								for (var j = 0; j <= cfg.levels - 1; j++) {
									var levelFactor = cfg.factor * radius
											* ((j + 1) / cfg.levels);
									g
											.selectAll(".levels")
											.data(allAxis)
											.enter()
											.append("svg:line")
											.attr(
													"x1",
													function(d, i) {
														return levelFactor
																* (1 - cfg.factor
																		* Math
																				.sin(i
																						* cfg.radians
																						/ total));
													})
											.attr(
													"y1",
													function(d, i) {
														return levelFactor
																* (1 - cfg.factor
																		* Math
																				.cos(i
																						* cfg.radians
																						/ total));
													})
											.attr(
													"x2",
													function(d, i) {
														return levelFactor
																* (1 - cfg.factor
																		* Math
																				.sin((i + 1)
																						* cfg.radians
																						/ total));
													})
											.attr(
													"y2",
													function(d, i) {
														return levelFactor
																* (1 - cfg.factor
																		* Math
																				.cos((i + 1)
																						* cfg.radians
																						/ total));
													})
											.attr("class", "line")
											.style("stroke", "grey")
											.style("stroke-opacity", "0.75")
											.style("stroke-width", "0.3px")
											.style("stroke-dasharray", 0)
											.attr(
													"transform",
													"translate("
															+ (cfg.w / 2 - levelFactor)
															+ ", "
															+ (cfg.h / 2 - levelFactor)
															+ ")");
								}

								// Text indicating at what % each level is
								for (var j = 0; j < cfg.levels; j++) {
									var levelFactor = cfg.factor * radius
											* ((j + 1) / cfg.levels);
									g
											.selectAll(".levels")
											.data([ 1 ])
											// dummy data
											.enter()
											.append("svg:text")
											.attr(
													"x",
													function(d) {
														return levelFactor
																* (1 - cfg.factor
																		* Math
																				.sin(0));
													})
											.attr(
													"y",
													function(d) {
														return levelFactor
																* (1 - cfg.factor
																		* Math
																				.cos(0));
													})
											.attr("class", "legend")
											.style("font-family", "sans-serif")
											.style("font-size", "10px")
											.attr(
													"transform",
													"translate("
															+ (cfg.w
																	/ 2
																	- levelFactor + cfg.ToRight)
															+ ", "
															+ (cfg.h / 2 - levelFactor)
															+ ")").attr("fill",
													"#737373").text(
													Math.round((j + 1) * 100
															/ cfg.levels));
								}
								series = 0;

								var axis = g.selectAll(".axis").data(allAxis)
										.enter().append("g").attr("class",
												"axis");

								axis
										.append("line")
										.attr("x1", cfg.w / 2)
										.attr("y1", cfg.h / 2)
										.attr(
												"x2",
												function(d, i) {
													return cfg.w
															/ 2
															* (1 - cfg.factor
																	* Math
																			.sin(i
																					* cfg.radians
																					/ total));
												})
										.attr(
												"y2",
												function(d, i) {
													return cfg.h
															/ 2
															* (1 - cfg.factor
																	* Math
																			.cos(i
																					* cfg.radians
																					/ total));
												}).attr("class", "line").style(
												"stroke", "grey").style(
												"stroke-width", "1px");

								axis
										.append("text")
										.attr("class", "legend")
										.text(function(d) {
											if(d.split(' ').length > 3)
												return d.split(' ').slice(0, 3).join(" ") + " ...";
											else
												return d;
										})
										.style("font-family", "sans-serif")
										.style("font-size", "10px")
										.attr("text-anchor", "start")
										.attr("dy", "1.5em")
										.attr("transform", function(d, i) {
											return "translate(0, -10)";
										})

										.attr(
												"x",
												function(d, i) {
													return cfg.w
															/ 2
															* (1 - cfg.factorLegend
																	* Math
																			.sin(i
																					* cfg.radians
																					/ total))
															- 60
															* Math
																	.sin(i
																			* cfg.radians
																			/ total);
												})
										.attr(
												"y",
												function(d, i) {
													if(d == "J1. New-Born Care Stabilization Unit (NBSU)" && i==7)
														return cfg.h
															/ 2
															* (1 - Math
																	.cos(i
																			* cfg.radians
																			/ total))
															- 20
															* Math
																	.cos(i
																			* cfg.radians
																			/ total) - 15;
													else
														return cfg.h
														/ 2
														* (1 - Math
																.cos(i
																		* cfg.radians
																		/ total))
														- 20
														* Math
																.cos(i
																		* cfg.radians
																		/ total);
												});

								d
										.forEach(function(y, x) {
											dataValues = [];
											g
													.selectAll(".nodes")
													.data(
															y,
															function(j, i) {
																dataValues
																		.push([
																				cfg.w
																						/ 2
																						* (1 - (parseFloat(Math
																								.max(
																										j.value,
																										0)) / cfg.maxValue)
																								* cfg.factor
																								* Math
																										.sin(i
																												* cfg.radians
																												/ total)),
																				cfg.h
																						/ 2
																						* (1 - (parseFloat(Math
																								.max(
																										j.value,
																										0)) / cfg.maxValue)
																								* cfg.factor
																								* Math
																										.cos(i
																												* cfg.radians
																												/ total)) ]);
															});
											
											dataValues.push(dataValues[0]);
											g
													.selectAll(".area")
													.data([ dataValues ])
													.enter()
													.append("polygon")
													.attr(
															"class",
															"radar-chart-serie"
																	+ series)
													.style("stroke-width",
															"2px")
													.style("stroke",
															cfg.color(series))
													.attr(
															"points",
															function(d) {
																var str = "";
																for (var pti = 0; pti < d.length; pti++) {
																	str = str
																			+ d[pti][0]
																			+ ","
																			+ d[pti][1]
																			+ " ";
																}
																return str;
															})
													.style(
															"fill",
															function(j, i) {
																return cfg
																		.color(series);
															})
													.style("fill-opacity",
															cfg.opacityArea)
													.on(
															'mouseover',
															function(d) {
																z = "polygon."
																		+ d3
																				.select(
																						this)
																				.attr(
																						"class");
																g
																		.selectAll(
																				"polygon")
																		.transition(
																				200)
																		.style(
																				"fill-opacity",
																				0.1);
																g
																		.selectAll(
																				z)
																		.transition(
																				200)
																		.style(
																				"fill-opacity",
																				.7);
															})
													.on(
															'mouseout',
															function() {
																g
																		.selectAll(
																				"polygon")
																		.transition(
																				200)
																		.style(
																				"fill-opacity",
																				cfg.opacityArea);
															});
											d3.select(el).select("svg g").append("rect").attr({
												"x": width/2 -40 ,
												"y": width + 50 + x * 30,
												"width": "20px",
												"height": "20px"
											}).style({
												"width": "20px",
												"height": "20px",
												"fill": cfg.color(series)
											})
											d3.select(el).select("svg g").append("text").attr({
												"x": width/2 - 10 ,
												"y": width + 65 + x * 30
											}).text(y[0].timePeriod);
											series++;
										});
								series = 0;

								d
										.forEach(function(y, x) {
											g
													.selectAll(".nodes")
													.data(y)
													.enter()
													.append("svg:circle")
													.attr(
															"class",
															"radar-chart-serie"
																	+ series)
													.attr('r', cfg.radius)
													.attr(
															"alt",
															function(j) {
																return Math
																		.max(
																				j.value,
																				0);
															})
													.attr(
															"cx",
															function(j, i) {
																dataValues
																		.push([
																				cfg.w
																						/ 2
																						* (1 - (parseFloat(Math
																								.max(
																										j.value,
																										0)) / cfg.maxValue)
																								* cfg.factor
																								* Math
																										.sin(i
																												* cfg.radians
																												/ total)),
																				cfg.h
																						/ 2
																						* (1 - (parseFloat(Math
																								.max(
																										j.value,
																										0)) / cfg.maxValue)
																								* cfg.factor
																								* Math
																										.cos(i
																												* cfg.radians
																												/ total)) ]);
																return cfg.w
																		/ 2
																		* (1 - (Math
																				.max(
																						j.value,
																						0) / cfg.maxValue)
																				* cfg.factor
																				* Math
																						.sin(i
																								* cfg.radians
																								/ total));
															})
													.attr(
															"cy",
															function(j, i) {
																return cfg.h
																		/ 2
																		* (1 - (Math
																				.max(
																						j.value,
																						0) / cfg.maxValue)
																				* cfg.factor
																				* Math
																						.cos(i
																								* cfg.radians
																								/ total));
															})
													.attr("data-id",
															function(j) {
																return j.axis;
															})
													.style("fill",
															cfg.color(series))
													.style("fill-opacity", .9)
													.on(
															'mouseover',
															function(d) {
																showPopover
																		.call(
																				this,
																				d);
																d3
																		.select(
																				this)
																		.attr(
																				'fill',
																				function(
																						d,
																						i) {
																					return hoverColor[i];
																				});
															},
															function(d) {
																newX = parseFloat(d3
																		.select(
																				this)
																		.attr(
																				'cx')) - 10;
																newY = parseFloat(d3
																		.select(
																				this)
																		.attr(
																				'cy')) - 5;

																z = "polygon."
																		+ d3
																				.select(
																						this)
																				.attr(
																						"class");
																g
																		.selectAll(
																				"polygon")
																		.transition(
																				200)
																		.style(
																				"fill-opacity",
																				0.1);
																g
																		.selectAll(
																				z)
																		.transition(
																				200)
																		.style(
																				"fill-opacity",
																				.7);
															})
													.on(
															'mouseout',
															function(d) {
																removePopovers();
																d3
																		.select(
																				this)
																		.attr(
																				'fill',
																				function(
																						d,
																						i) {
																					return color[i];
																				});
															});

											series++;
										});

								function removePopovers() {
									$('.popover').each(function() {
										$(this).remove();
									});
								}
								function showPopover(d) {
									$(this).popover({
										title : '',
										placement : 'auto top',
										container : 'body',
										trigger : 'manual',
										html : true,
										content : function() {
											return "<div style='color: #257ab6;'>" + d.axis + "</div>" + "Score : " + d.value + "%";
										}
									});
									$(this).popover('show');
								}
//								var spiderLegend = d3.select(el).select("svg g").append("div").attr({
//									"x": width/2,
//									"y": width
//								}).style({
//									"fill": "#CCC"
//								});
//								d3.select(el).select("svg g").append("rect").attr({
//									"x": width/2 -90,
//									"y": width + 50
//								}).style({
//									"width": "20px",
//									"height": "20px",
//									"fill": "rgb(31, 119, 180)"
//								})
//								d3.select(el).select("svg g").append("text").attr({
//									"x": width/2 - 60 ,
//									"y": width + 65
//								}).text("Phase 1");
//								d3.select(el).select("svg g").append("rect").attr({
//									"x": width/2 + 15,
//									"y": width + 50
//								}).style({
//									"width": "20px",
//									"height": "20px",
//									"fill": "rgb(255, 127, 14)"
//								})
//								d3.select(el).select("svg g").append("text").attr({
//									"x": width/2+45 ,
//									"y": width + 65
//								}).text("Phase 2");
								if($(window).width() > 991)
								{
									if(scope.tableData.length > 4)
								$("table.assign-height-similar-spider").height($("#home").width()-300);
									else
										$("table.assign-height-similar-spider").height($("#home").width()-300);
								}
							}
						};

						var w = scope.getWindowDimensions();
						var width = (w.w * 30) / 100;
						// Options for the Radar chart, other than default
						var mycfg = {
							w : width,
							h : width,
							maxValue : 0.6,
							levels : 10,
							ExtraWidthX : 300
						};

						scope.$watch('spiderdata', function() {
							if (scope.spiderdata
									&& scope.spiderdata.dataCollection) {
								var d = scope.spiderdata.dataCollection;
								RadarChart.draw(el, d, mycfg);
							}
						}, true);

					}
					return {
						link : link,
						restrict : "E"
					};

				});

app.directive("sdrcBarChart",
		function($window) {
			function link(scope, el) {
				var el = el[0];
				draw = function(el, data) {

					d3.select("#columnbarChart").remove();
					var n = 2, // number of layers
					m = 10, // number of samples per layer
					stack = d3.layout.stack(), layers = stack(data),

					yGroupMax = d3.max(layers, function(layer) {
						return d3.max(layer, function(d) {

							return d.value;
						});
					}), yStackMax = d3.max(layers, function(layer) {
						return d3.max(layer, function(d) {
							return d.y0 + d.value;
						});
					});
					var w = scope.getWindowDimensions();
					var relativewidth = $(window).width() > 768 ? $("#"+el.parentElement.getAttribute("id")).width():$("#"+el.parentElement.getAttribute("id")).width();
					var margin = {
						top : 20,
						right : 55,
						bottom : 40, // // bottom height
						left : 40
					}, width = $("#"+el.parentElement.getAttribute("id")).width(), height = 400 // //
							// height
							- margin.top - margin.bottom;
					/*if($(window).width()< 768){
						margin.left = 0;
						margin.right = 0;
					}*/
					var x = d3.scale.ordinal().domain(data[0].map(function(d) {
						return d.axis;
					})).rangeRoundBands([ 0, width ], .1);
					;

					var y = d3.scale.linear().domain([ 0, 100 ]).range(
							[ height, 0 ]);

					// var color = d3.scale.linear().domain([ 0, n - 1 ])
					// .range([ "#8FBBD9", "#FFBF87" ]);
					// var color = [ "#8FBBD9", "#FFBF87","#FF8C26" ];
					var color = [ "#1a9641", "#FF8000", "#d7191c" ];

					// var hoverColor = d3.scale.linear().domain([ 0, n - 1 ])
					// .range([ "#1F77B4", "#FF8C26" ]);
					var hoverColor = [ "#017A27", "#FF5900", "#b7191c" ];

					var formatTick = function(d) {
						return d.split(".")[0];
					};
					var xAxis = d3.svg.axis().scale(x).orient("bottom")
							.tickFormat(formatTick);

					var svg = d3.select(el).append("svg").attr("id",
							"columnbarChart").attr("width",
							width + margin.left + margin.right).attr("height",
							height + margin.top + margin.bottom).append("g")
							.attr(
									"transform",
									"translate(" + margin.left + ","
											+ margin.top + ")");

					var layer = svg.selectAll(".layer").data(layers).enter()
							.append("g").attr("class", "layer").style("fill",
									function(d, i) {
										return color[i];
									}).attr("id", function(d, i) {
								return i;
							});
					// var layer = svg.selectAll(".layer").data(layers)
					// .enter().append("g").attr("class", "layer").style("fill",
					// function(d, i) {
					// if(80 <= d[i].value ){
					// return color[0];}
					// else if(61 <= d[i].value && d[i].value <= 79 ){
					// return color[1];}
					// else if(d[i].value <= 60 ){
					// return color[2];}
					// }).attr("id", function(d,i) {
					// return i;
					// });

					var rect = layer.selectAll("rect").data(function(d) {
						return d;
					}).enter().append("rect").attr("x", function(d) {
						return x(d.axis);
					}).attr("y", height).attr("width", x.rangeBand()).attr(
							"height", 0).style("fill", function(d, i) {
						if (80 <= d.value) {
							return color[0];
						} else if (60 <= d.value && d.value < 80) {
							return color[1];
						} else if (d.value < 60) {
							return color[2];
						}
					}).on("mouseover", function(d) {
						showPopover.call(this, d);
						// d3.select(this).attr('fill', function(d, i) {
						// return hoverColor[$(this).parent().attr('id')];
						// });
						d3.select(this).style('fill', function(d, i) {

							if (80 <= d.value) {
								return hoverColor[0];
							} else if (60 <= d.value && d.value < 80) {
								return hoverColor[1];
							} else if (d.value < 60) {
								return hoverColor[2];
							}
						});

					}).on("mouseout", function(d) {
						removePopovers();
						// d3.select(this).attr('fill', function(d, i) {
						// return color[$(this).parent().attr('id')];
						// });
						d3.select(this).style('fill', function(d, i) {
							if (80 <= d.value) {
								return color[0];
							} else if (60 <= d.value && d.value < 80) {
								return color[1];
							} else if (d.value < 60) {
								return color[2];
							}
						});
					});
					;

					svg.append("g").attr("class", "x axis").attr("transform",
							"translate(0," + height + ")").call(xAxis)
							.selectAll("text").style("text-anchor", "end")
							.attr("class",  function(d,i){return  "evmtext"+i})
//							.attr("id", function(d,i){return "chartid"+i})
							.attr("dx", "-.2em").attr("dy", ".70em");
					// start
					var xAxis = d3.svg.axis().scale(x).orient("bottom");

					var yAxis = d3.svg.axis().scale(y).orient("left");

					svg.append("g").attr("class", "x axis").attr("transform",
							"translate(0," + height + ")").call(xAxis).attr(
							"x", width / 2).attr("y", margin.bottom).attr("dx",
							"1em").style("text-anchor", "middle").text(
							"Time Period");
					// .style("fill", "#FFFFFF");

					svg.append("g").attr("class", "y axis").call(yAxis).append(
							"text").attr("transform", "rotate(-90)").attr("y",
							0 - margin.left).attr("x", 0 - (height / 2)).attr(
							"dy", "1em").style("text-anchor", "end").text(
							"Score(%)");

					// END
					// legend

					// end of legend
					
					function transitionGrouped() {
						y.domain([ 0, 100 ]);

						rect.transition().duration(500).delay(function(d, i) {
							return i * 10;
						}).attr("x", function(d, i, j) {
							return x(d.axis) + x.rangeBand() / n * j; // function(d)
							// {return
							// x(d.axis);
							// //
							// for
							// Group
							// bar
							// chat
						}).attr("width", x.rangeBand() / n).transition().attr(
								"y", function(d) {
									return y(d.value);
								}).attr("height", function(d) {
							return height - y(d.value);
						});
					}

					transitionGrouped();
					function removePopovers() {
						$('.popover').each(function() {
							$(this).remove();
						});
					}
					function showPopover(d) {
						$(this).popover(
								{
									title : '',
									placement : 'auto top',
									container : 'body',
									trigger : 'manual',
									html : true,
									content : function() {
										if(d.axis != "")
										return "<div style='color: #257ab6;'>" + d.axis + "</div>" + "Score : "
												+ d.value + "%";
										else
											return "<div style='color: #257ab6;'> Average: " + d.value + "%</div>" ;
									}
								});
						$(this).popover('show');
						$('.popover.fade.top.in').css('top', parseFloat($('.popover.fade.top.in').css('top').slice(0, -2)) - 15);
					}
					
					//NEW CODE FOR DATA VALUE TEXT ON EACH BAR-----------------
					var e0Arr = [];
					for (var i = 0; i < data.length; i++) {
						e0Arr.push(data[i][0].value);
						layer.selectAll("evmtext" + i).data(data[i]).enter()
								.append("text").attr(
										"x",
										function(d) {
											return i == 0 ? x(d.axis)
													+ x.rangeBand() / 2 - 9
													: x(d.axis) + x.rangeBand()
															/ 2 + 12;
										}) // changes acc to no of bars in one
											// chart
								.attr("y", function(d) {
									return y(d.value) - 3;
								}).style("text-anchor", "middle").style("fill",
										"#000000").text(function(d) {
									return Math.round(d.value);
								});
					}
					//END=================
					//calculate average
						var sumTotal = 0; var avg = 0;
						for(var i=0; i<data[0].length; i++){
							sumTotal = sumTotal+parseFloat(data[0][i].value);
						}
						avg = sumTotal/data[0].length;
						scope.averageScore = Math.round(avg);
					//Draw a horizontal line for overall score of latest time period
						svg.append("g").attr("class", "y axis").call(yAxis).append("line")          // attach a line
						.attr("stroke-dasharray", "5,5")
					    .attr("stroke", "#1F77B4") 		// colour the line
					    .attr("stroke-width", 2)
					    .attr("fill", "none")
					    .attr("x1", 0)     				// x position of the first end of the line
					    .attr("y1", y(avg.toString()))      // y position of the first end of the line
					    .attr("x2", width)     				// x position of the second end of the line
					    .attr("y2", y(avg.toString()))
					    .style("cursor", "pointer").on("mouseover", function(d) {
							showPopover.call(this, {axis: "", value: Math.round(avg.toString())}
										);})    // y position of the second end of the line
						.on("mouseout", function() {
						removePopovers();});				
				};
				scope.$watch('spiderdata', function() {
					if (scope.spiderdata && scope.spiderdata.dataCollection) {
						var d = scope.spiderdata.dataCollection;
						draw(el, d);
					}
				}, true);

			}
			return {
				link : link,
				restrict : "E"
			};

		});

/*******************************extra js **********************************/

$(".download-container").hover(function(){
	if(parseFloat($(this).css("right").slice(0, -2)) < -50)
	$(this).animate({right: 0});
}, function(){
	var self = this;
	if(parseFloat($(this).css("right").slice(0, -2)) > -50)
		$(this).animate({right: '-107px'});
	else
		setTimeout(function(){
			$(self).animate({right: '-107px'});
		}, 2000);
});
$(".download-container-excel").hover(function(){
	if(parseFloat($(this).css("right").slice(0, -2)) < -50)
	$(this).animate({right: 0});
}, function(){
	var self = this;
	if(parseFloat($(this).css("right").slice(0, -2)) > -50)
	$(this).animate({right: '-114px'});
	else
		setTimeout(function(){
			$(self).animate({right: '-114px'});
		}, 2000);
});