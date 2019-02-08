<%@ page import="org.sdrc.cysdcbo.util.Constants"%>
<%@ page import="org.sdrc.cysdcbo.model.CollectUserModel"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.sdrc.cysdcbo.model.FeatureModel"%>
<%@page
	import="org.sdrc.cysdcbo.model.UserRoleFeaturePermissionMappingModel"%>

<%
	CollectUserModel user = null;
	List<String> features = new ArrayList<String>();
	List<String> permissions = new ArrayList<String>();
	List<FeatureModel> featureModels=new ArrayList<FeatureModel>();
	Integer roleId=0;
	if (request.getSession().getAttribute(Constants.USER_PRINCIPAL) == null) {
	} else if (request.getSession().getAttribute(Constants.USER_PRINCIPAL) != null) {
		user = (CollectUserModel) request.getSession().getAttribute(Constants.USER_PRINCIPAL);

		List<UserRoleFeaturePermissionMappingModel> ursMappings = new ArrayList<UserRoleFeaturePermissionMappingModel>();
		ursMappings = user != null ? user.getUserRoleFeaturePermissionMappings() : null;
		if (ursMappings != null && !ursMappings.isEmpty()) {
	for (UserRoleFeaturePermissionMappingModel ursm : ursMappings) {
		FeatureModel featureModel=new FeatureModel();
		
		
		features.add(ursm.getRoleFeaturePermissionSchemeModel().getFeaturePermissionMapping()
		.getFeature().getFeatureName());
		
		
		featureModel.setFeatureName(ursm.getRoleFeaturePermissionSchemeModel().getFeaturePermissionMapping()
		.getFeature().getFeatureName());
		featureModel.setDescription(ursm.getRoleFeaturePermissionSchemeModel().getFeaturePermissionMapping()
		.getFeature().getDescription());
		permissions.add(ursm.getRoleFeaturePermissionSchemeModel().getFeaturePermissionMapping()
		.getPermission().getPermissionName());
		
	featureModels.add(featureModel);

		}
		}
	
 	roleId=user.getUserRoleFeaturePermissionMappings().get(0).getRoleFeaturePermissionSchemeModel().getRole().getRoleId(); 
	}
%>
<script>
	var roleId =
<%=roleId%>
	
</script>
				<style>
					.navHeaderCollapse2{
						width: 41.66666667%;
					}
				</style>
<div class="container-fluid header-nav">
	<!-- <div class="col-md-6 logoresize">
		<div class="heading_partDesktop heading_part">
			<h2 class="headerinfo">District Gap Analysis<span style="font-size: 12px; color: #22b369;">&nbsp; 1.0.1</span></h2>
		</div>
	</div> -->

</div>
<!--logo part end-->
<nav class="navbar nav-menu-container">
	<button class="navbar-toggle custom-navbar-mobile" style="z-index: 777"
		data-toggle="collapse" data-target=".navHeaderCollapse2">
		<span class="icon-bar"></span> <span class="icon-bar"></span> <span
			class="icon-bar"></span>
	</button>
	<div class="container-fluid">
		<div class="">
		<div class="col-md-5 navbar-header">
			<div class="logoresize">
				<div class="heading_partDesktop heading_part">
				<a href="home"><img src="resources/images/cysd_logo.png" class="logoImage img-responsive"></img></a>
					<h2 class="headerinfo">
						CYSD CBO Monitoring<span
							style="font-size: 12px; color: #79bdf8;">&nbsp; 1.0.0</span>
					</h2>
				</div>
			</div>
		</div>
		<div class="col-md-2 user-welcome text-right">
		<%
				if (user != null) {
			%>
			
				<h5>
					Welcome<span>&nbsp; <%=user.getName()
						/* + "("
						+ user.getUserRoleFeaturePermissionMappings().get(0)
								.getRoleFeaturePermissionSchemeModel()
								.getSchemeName() + ")"*/%></span>
				</h5>
			
			<%
				}
			%>
			</div>
			<div
			class="col-md-5 collapse navbar-collapse navHeaderCollapse2">
			<ul class="nav navbar-nav navbar-right nav-submenu nav-place-right">
				<% 
					if (user == null) {
				%>
				<li><a href="home">Login</a></li>
				<%
					}%>
				<!-- <li class="active"><a href="home">Home</a></li> -->
				<%
					if (features.contains("dashboard")) {
				%>
				<li><a href="dashboard">Dashboard</a></li>
				<%
					}
				%>
				<%
					if (features.contains("dataTree")) {
				%>
				<li><a href="dataTree">DataTree</a></li>
				<%
					}
				%>
				
				<%
					if (features.contains("report")) {
				%>
				<li onClick="getRawData()"><a href="#">Report</a></li>
				<%
					}
				%>
				
				<li><a href="contactUS">Contact</a></li>
				<li><a href="resource">Resources</a></li>
				<%

					if (user != null) {
				%>
				<li><a href="logout">Logout</a></li>
				<%
					}%>
			</ul>
		</div>
	</div>
	</div>
</nav>

	
<script src="resources/js/angularcontrollers/commonCtrl.js" type="text/javascript"></script>
