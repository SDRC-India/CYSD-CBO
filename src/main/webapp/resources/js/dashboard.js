// Angular app creation


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
			config.url =  "http://localhost:8080/DGA/api/" + config.url;
//			config.url =  "http://192.168.1.158:8080/DGA/api/" + config.url;
//			config.url =  "http://prod4.sdrc.co.in/dgacg/api/" + config.url;
			return config;
		}
	};

});


app.directive("sdrcDataTree", function($window) {
	function link(scope, el) {
		var el = el[0];
		scope.drawDataTree = function(nodes) {
			var margin = {
				top : 20,
				right : 20,
				bottom : 20,
				left : 100
			};
			d3.select(el).selectAll("*").remove();
			var w = scope.getWindowDimensions();
			var width = w.w - 50;
			var height = w.h ; //height = w.h * 63.8 / 100;

			var i = 0, duration = 750, root;

			var svg = d3.select(el)
			.append("div")
			.classed("svg-container", true)
			.append("svg")
//			.attr("width", width).attr(
//				"height", height + margin.bottom).append("g").attr(
//				"transform",
//				"translate(" + margin.left + "," + margin.top + ")");
			 .attr("preserveAspectRatio", "xMinYMin meet")
			 .attr("viewBox","-125 0 " + width + " " + height)
			 //class to make it responsive
			 .classed("svg-content-responsive", true)
			  .append("g"); 
			
			var tree = d3.layout.tree().separation(function(a,b){return 3;}).size([ height, width ]);

			var diagonal = d3.svg.diagonal().projection(function(d) {
				return [ d.y, d.x ];
			});

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
				if (d.name != "B.Basic Services") {
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
				var nodes = tree.nodes(root).reverse(), links = tree
						.links(nodes);
				// Normalize for fixed-depth.
				nodes.forEach(function(d) {
					d.y = d.depth * 350;
				});

				// Update the nodes
				var node = svg.selectAll("g.node").data(nodes, function(d) {
					return d.id || (d.id = ++i);
				});
				
//		          ================ wrap function=================
                
                
                function wrap(text, width) {
					  text.each(function(d) {
					    var text = d3.select(this),
					        words = text.text().split(/(.{60}[^\s]*)\s/),
					        word,
					        line = [],
					        x = text.attr("x"),
					        y = text.attr("y"),
					        dy = text.attr("dy") ? text.attr("dy") : 0;
					        tspan = text.text(null).append("tspan").attr("x", x).attr("y", 9);
					    while (word = words.pop()) {
					      line.push(word);
					      tspan.text(line.join(" "));
					      if (tspan.node().getComputedTextLength() > width) {
					        line.pop();
					        tspan.text(line.join(" "));
					        line = [word];
					        tspan = text.append("tspan").attr("x", x).attr("y", y).text(word);
					      }
//					    }
						  }});
					}
                
                
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
								$(this).popover(

								{
									title : '',
									placement : 'auto left',
									container : 'body',
									trigger : 'manual',
									html : true,
									content : function() {
//										return d.name;
										return "Name: " + d.name
										+ "<br/>Value: "
										+ (d.percentScore == "NA" || d.percentScore == null ? "NA" : (d.percentScore +"%"));
									}
								});
								$(this).popover('show');
						}).on("mouseout", function removePopovers(d) {
							$('.popover').each(function() {
								$(this).remove();
							});
						});

				nodeEnter.append("circle").attr("r", 1e-6).style("fill", function(d){
					return (d.percentScore == null || d.percentScore == "NA")  ? "grey" :
						d.percentScore < 60 ? "#d13f43" : d.percentScore >= 60 && d.percentScore < 80 ? "#f19537" : "#22b369";
				}).style("z-index", "-1");
//						"#D0D0D0").style("z-index", "-1");
				// append text on the nodes
				nodeEnter.append("text").style("fill", function(d) {
					return "black";
				}).style("font-weight", "bold").style("font-size", function(d) {
					if (!d.parent) {
						return "15px";
					} else {
						return "12px";
					}
				}).attr("x", function(d) {
					if (!d.parent) {
						return -80;
					} else {
						return d.children || d._children ? -3 : 20;
					}

				}).attr("dy", ".35em")

				.text(function(d) {
					return((d.name) + " ("+(d.percentScore == "NA" || d.percentScore == null ? "NA" : d.percentScore +"%")+")");
				}).style("fill-opacity", 1e-6);
//				.call(wrap, 250);
				

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
				link.enter().insert("path", "g").attr("class", "link").style(
						"stroke", "#D0D0D0").style("stroke-width", 3)
						 .style("stroke-dasharray", "5,5") .style("opacity",
								1).attr("d", function(d) {
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

				if (source.name == 'Total score for PHC' || source.name == 'Category') {
					click(source.children[0]);
				}
			}
			
			   function zoom() {
			        var scale = d3.event.scale,
			            translation = d3.event.translate,
			            tbound = - 100,
			            bbound = height * scale,
			            lbound = (-5) * scale,
			            rbound = (width - margin.bottom) * scale;
			        // limit translation to thresholds
			        translation = [
			        Math.max(Math.min(translation[0], rbound), lbound),
			        Math.max(Math.min(translation[1], bbound), tbound)];
			        d3.select("g")
			        .attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
			    } 
			   
			 d3.select("svg").call(d3.behavior.zoom().scaleExtent([1, 5]).on("zoom", function () {
		            zoom();
		        }));

			function click(d) {
				sear = true;
				/*
				 * ############# clear the previous highlight in every new click
				 */$('.popover').each(function() {
					$(this).remove();
				});

				ui.animGroup.selectAll("path.selected").data([]).exit()
						.remove();
//				scope.setBackFalse();
//				scope.setColorTrue();
//				scope.setValueTrue();
				/*
				 * ###########Toggle children on click######### if children
				 * exits then do nothing if children are not showing but exits
				 * then show the children by closing others if it is the child
				 * node itself and no further child available then call draw
				 * bubble function
				 */

				/*if (!d.children) {
					if (d._children) {
						d.children = d._children;
						d._children = null;
//						tog._children = tog.children;
//						tog.children = null;
//						updateTree(tog);
//						tog = d;
						updateTree(d);
					}
				}*/
				// Toggle children on click.
				  if (d.children) {
				    d._children = d.children;
				    d.children = null;
				  } else {
				    d.children = d._children;
				    d._children = null;
				  }
				  updateTree(d);
				/*
				 * ######## Highlight the specified path###########
				 * highlightEvent is defined here it only chooses the the
				 * selected path(from root to child) and pass it to the event
				 * animateLink
				 */

				ancestors = [];

				var parent;

				if (d.children) {
					if (d.children.length > 0) {
						parent = d.children[0];
					}
				} else {
					parent = d;
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
								"fill", "none").style("stroke", "#00FF00")
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
						selected.style("fill", function(d){
							return (d.percentScore == null || d.percentScore  == "NA") ? "grey" :
								d.percentScore < 60 ? "#d13f43" : d.percentScore >= 60 && d.percentScore < 80 ? "#f19537" : "#22b369";
						}).style("stroke",
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
							return p === d;
						});

					});
					var selectedText = selected[0][0].nextSibling;
					var selectedTextDe = selected[0][1].nextSibling;
					var selectedid = selected[0][0].nextSibling.innerHTML;
//					if (selectedid != "SVS" && selectedid != "RVS"
//							&& selectedid != "DVS"
//							&& selectedid != "Health Facility" && selectedid != "DIV-VS") {
//						selectedText.style.fill = "white";
//					} else {
//						selectedTextDe.style.fill = "white";
//					}

					selected
//					.style("fill", "#997a00")
							.style("stroke", "#00FF00").style("z-index", "-1");
				}

			}

		};

		scope.$watch('treeData', function() {

			if (scope.treeData && scope.treeData.children && scope.treeData.children.length > 0) {
				scope.drawDataTree(scope.treeData);
			}
		}, false);

	}
	return {
		link : link,
		restrict : "E"
	};

});
