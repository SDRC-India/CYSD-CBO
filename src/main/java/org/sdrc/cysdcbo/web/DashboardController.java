package org.sdrc.cysdcbo.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.cysdcbo.core.Authorize;
import org.sdrc.cysdcbo.model.AreaModel;
import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.model.FormXpathScoreMappingModel;
import org.sdrc.cysdcbo.model.GoogleMapDataModel;
import org.sdrc.cysdcbo.model.ScoreModel;
import org.sdrc.cysdcbo.model.SpiderDataCollection;
import org.sdrc.cysdcbo.model.TimePeriodModel;
import org.sdrc.cysdcbo.service.DashboardService;
import org.sdrc.cysdcbo.service.MasterRawDataService;
import org.sdrc.cysdcbo.util.Constants;
import org.sdrc.cysdcbo.util.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private StateManager stateManager;
	
	@Autowired
	ResourceBundleMessageSource applicationMessageSource;

	@Autowired
	private ServletContext context;
	
	@Autowired
	private MasterRawDataService masterRawDataService;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	
	@RequestMapping("/getAllData")
	@ResponseBody
	public List<ScoreModel> getAllAggregatedData(
			@RequestParam("formId") Integer formId) {
		return dashboardService.getAllAggregatedData(formId);
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping(value = "/googleMapData", method = { RequestMethod.GET })
	@ResponseBody
	public List<GoogleMapDataModel> fetchAllGoogleMapData(
			@RequestParam("formId") Integer formId,
			@RequestParam("sector") Integer sectorId,
			@RequestParam("areaId") Integer areaId,
			@RequestParam("timePeriodId")int timePeriodId) throws Exception {

		
		CollectUserModel collectUserModel = (CollectUserModel) stateManager
				.getValue(Constants.USER_PRINCIPAL);
		//On load of page if the user is of district level then areaId will be set as its district id
		if (collectUserModel.getUserRoleFeaturePermissionMappings()
						.get(0).getRoleFeaturePermissionSchemeModel()
						.getAreaModel().getAreaLevelId() > 3
				&& collectUserModel.getUserRoleFeaturePermissionMappings()
						.get(0).getRoleFeaturePermissionSchemeModel()
						.getAreaModel().getParentAreaId() != -1) {
			areaId = collectUserModel.getUserRoleFeaturePermissionMappings()
					.get(0).getRoleFeaturePermissionSchemeModel()
					.getAreaModel().getAreaId();
		}
		return dashboardService.fetchAllGoogleMapData(formId, sectorId, areaId,timePeriodId);
	}

	@RequestMapping(value = "/fetchLabelAndScore", method = { RequestMethod.GET })
	@ResponseBody
	public List<ScoreModel> fetchLabelFromLastVisitData(
			@RequestParam("lastVisitDataId") Integer lastVisitDataId)
			throws Exception {
		return dashboardService.fetchLabelFromLastVisitData(lastVisitDataId);
	}

	@RequestMapping(value = "/fetchGridData", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, List<ScoreModel>> fetchGridData(
			@RequestParam(value = "formId", required = false) Integer formId,
			@RequestParam(value = "lastVisitDataId", required = false) Integer lastVisitDataId
			,@RequestParam("timePeriodId")int timePeriodId)
			throws Exception {
		return dashboardService.getGridTableData(formId, lastVisitDataId,timePeriodId);
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping(value = "/spiderData")
	@ResponseBody
	public SpiderDataCollection fetchSpiderData(
			@RequestParam(value = "formId", required = true) Integer formId,
			@RequestParam(value = "lastVisitDataId", required = true) Integer lastVisitDataId,
			@RequestParam(value = "areaId", required = true) Integer areaId
			,@RequestParam(value = "cboType", required = true) String cboType
			)
			throws Exception {
		return dashboardService.getfetchSpiderData(formId, lastVisitDataId,
				areaId,cboType);
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping(value = "/getParentSectors")
	@ResponseBody
	public List<FormXpathScoreMappingModel> getParentSectors() {
		return dashboardService.getParentSectors();
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping(value = "/getSectors")
	@ResponseBody
	public List<FormXpathScoreMappingModel> getSectors(
			@RequestParam("parentId") Integer parentId) {
		return dashboardService.getSectors(parentId);
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping(value = "/getAllDistrict")
	@ResponseBody
	public List<AreaModel> getAllDistricts() {
		return dashboardService.getAllDistricts();
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping("dashboard")
	public String dataTreePage() {
		return "Dashboard";
	}

	@Authorize(feature = "dashboard", permission = "View")
	@RequestMapping("/getAllTimePeriod")
	@ResponseBody
	List<TimePeriodModel> getTimePeriods() {
		return dashboardService.getAllPlanningTimePeriod();
	}

	@RequestMapping("/exportToPdf")
	@Authorize(feature = "dashboard", permission = "View")
	@ResponseBody
	public String exportToPdf(
			@RequestBody List<String> svgs,
			@RequestParam("noOfFacilities")int noOfFacilities,
			@RequestParam(value = "formId", required = false) Integer formId,
			@RequestParam(value = "lastVisitDataId", required = false) Integer lastVisitDataId,
			@RequestParam(value = "areaId", required = false) Integer areaId,
			@RequestParam("timePeriodId")int timePeriodId,
			@RequestParam(value = "cboType", required = true) String cboType,
			HttpServletResponse response) throws Exception {

		String pdfPath = dashboardService.exportToPdf(svgs.get(0), svgs.get(0),
				formId, lastVisitDataId, areaId, response,noOfFacilities,timePeriodId,cboType);

		return pdfPath;

	}
	
	@RequestMapping("/exportToExcel")
	@Authorize(feature = "dashboard", permission = "View")
	@ResponseBody
	public String exportToExcel(
			@RequestBody List<String> svgs,
			@RequestParam("noOfFacilities")int noOfFacilities,
			@RequestParam(value = "formId", required = false) Integer formId,
			@RequestParam(value = "lastVisitDataId", required = false) Integer lastVisitDataId,
			@RequestParam(value = "areaId", required = false) Integer areaId,
			@RequestParam("timePeriodId")int timePeriodId,
			@RequestParam(value = "cboType", required = true) String cboType,
			HttpServletResponse response) throws Exception {

		String pdfPath = dashboardService.exportToExcel(svgs.get(0), svgs.get(0),
				formId, lastVisitDataId, areaId, response,noOfFacilities,timePeriodId,cboType);

		return pdfPath;

	}
	
	@RequestMapping(value = "/downloadFile", method=RequestMethod.POST)
	public void downLoad(@RequestParam("fileName") String name,HttpServletResponse response) throws IOException {
		InputStream inputStream;
		String fileName = "";
		try {
			fileName=name.replaceAll("%3A", ":").replaceAll("%2F", "/")
						 .replaceAll("%5C", "/").replaceAll("%2C",",")
						 .replaceAll("\\+", " ").replaceAll("%22", "")
						 .replaceAll("%3F", "?").replaceAll("%3D", "=");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream"); //for all file type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			new File(fileName).delete();
		}
	}
	
	
	
	@Authorize(feature = "report", permission = "View")
	@RequestMapping(value = "/downloadReport", method=RequestMethod.POST)
	public void downloadReport(@RequestParam("fileName") String name,HttpServletResponse response) throws IOException {
		InputStream inputStream;
		String fileName = "";
		try {
			fileName=name.replaceAll("%3A", ":").replaceAll("%2F", "/")
						 .replaceAll("%5C", "/").replaceAll("%2C",",")
						 .replaceAll("\\+", " ").replaceAll("%22", "")
						 .replaceAll("%3F", "?").replaceAll("%3D", "=");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					new java.io.File(fileName).getName().split(".xlsx")[0]+" "+sdf.format(new java.util.Date())+".xlsx");
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream"); //for all file type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			new File(fileName).delete();
		}
	}
	
	
	@SuppressWarnings({"unused","resource"})
	@Authorize(feature = "report", permission = "View")
	@RequestMapping("getRawExcel")
	@ResponseBody
	String getRawExcel()throws Exception
	{
		try
		{
			InputStream input =new FileInputStream( context.getRealPath("resources\\"
					+applicationMessageSource.getMessage("rawDataOuputPath", null,
							null)));
			new java.io.File( context.getRealPath("resources\\"
					+applicationMessageSource.getMessage("rawDataOuputPath", null,
							null))).getName();
		}
		catch (Exception e)
		{
			try {
			return masterRawDataService.generateExcel();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return context.getRealPath("resources\\"
				+applicationMessageSource.getMessage("rawDataOuputPath", null,
						null));
	}
	
	@RequestMapping("/cboAsessed")
	@ResponseBody
	Map<String,Integer> getCboAssessed()
	{
		return dashboardService.getCBOAssessed();
	}
}
