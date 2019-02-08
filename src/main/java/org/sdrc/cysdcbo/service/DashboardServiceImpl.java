package org.sdrc.cysdcbo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.cysdcbo.domain.Area;
import org.sdrc.cysdcbo.domain.FacilityPlanning;
import org.sdrc.cysdcbo.domain.FacilityScore;
import org.sdrc.cysdcbo.domain.FormXpathScoreMapping;
import org.sdrc.cysdcbo.domain.LastVisitData;
import org.sdrc.cysdcbo.domain.TimePeriod;
import org.sdrc.cysdcbo.model.AreaModel;
import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.model.FacilityPlanningModel;
import org.sdrc.cysdcbo.model.FormXpathScoreMappingModel;
import org.sdrc.cysdcbo.model.GoogleMapDataModel;
import org.sdrc.cysdcbo.model.ScoreModel;
import org.sdrc.cysdcbo.model.SpiderDataCollection;
import org.sdrc.cysdcbo.model.SpiderDataModel;
import org.sdrc.cysdcbo.model.TimePeriodModel;
import org.sdrc.cysdcbo.repository.AreaRepository;
import org.sdrc.cysdcbo.repository.FacilityPlanningRepository;
import org.sdrc.cysdcbo.repository.FacilityScoreRepository;
import org.sdrc.cysdcbo.repository.FormXpathScoreMappingRepository;
import org.sdrc.cysdcbo.repository.LastVisitDataRepository;
import org.sdrc.cysdcbo.repository.TimePeriodRepository;
import org.sdrc.cysdcbo.util.Constants;
import org.sdrc.cysdcbo.util.HeaderFooter;
import org.sdrc.cysdcbo.util.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 
 * @author Harekrishna Panigrahi
 * @author Sarita Panigrahi
 *  @author Harsh Pratyush
 *
 */

@SuppressWarnings("deprecation")
@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private FacilityScoreRepository facilityScoreRepository;

	private static DecimalFormat df = new DecimalFormat(".#");

	@Autowired
	private LastVisitDataRepository lastVisitDataRepository;

	@Autowired
	private FormXpathScoreMappingRepository formXpathScoreMappingRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private ResourceBundleMessageSource messages;

	@Autowired
	private StateManager stateManager;

	@Autowired
	private ServletContext context;
	
	@Autowired
	private FacilityPlanningRepository facilityPlanningRepository;
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	private SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");

	@Override  @Transactional(readOnly=true)
	public List<ScoreModel> getAllAggregatedData(Integer formId) {

		List<Object[]> avgPercentValues = facilityScoreRepository
				.findAvgByFormId(formId);
		List<ScoreModel> scoreModels = new ArrayList<ScoreModel>();

		for (int i = 0; i < avgPercentValues.size(); i++) {

			// Map<String, Object> firstChildMap = new HashMap<>();
			Object[] objs = avgPercentValues.get(i);

			Integer formXpathScoreId = (Integer) objs[0];
			String label = (String) objs[1];
			Integer parentXpathId = (Integer) objs[2];
			Double percentScore = (Double) objs[3];
			Double maxScore = (Double) objs[4];
			Double score = percentScore != null ? (percentScore * maxScore) / 100
					: null;

			ScoreModel scoreModel = new ScoreModel();
			scoreModel.setFormXpathScoreId(formXpathScoreId);
			scoreModel.setMaxScore(maxScore);
			scoreModel.setName(label);
			scoreModel.setParentXpathScoreId(parentXpathId);
			scoreModel.setPercentScore(percentScore != null
					&& percentScore == 0.0 ? "0.0" : percentScore != null ? df
					.format(percentScore) : "NA");
			scoreModel.setScore(score);
			scoreModels.add(scoreModel);
		}
		return scoreModels;
	}

	@Override  @Transactional(readOnly=true)
	public List<GoogleMapDataModel> fetchAllGoogleMapData(Integer formId,
			Integer sectorId, Integer areaId,int timeperiodId) throws Exception {

		List<Object[]> objects;
		//if areaId is 0 then overall score of the state will shown
		if (areaId == 0) {
			objects = lastVisitDataRepository.getDataByFormId(formId, sectorId,timeperiodId);
		}
		// if areaId is Choosen and then if formId is 44 means DH Type then this will get Executed
		else if (formId == Integer.parseInt( messages.getMessage("DH_FormID",null,null))) {
			objects = lastVisitDataRepository.getDataByFormIdAndAreaId(formId,
					sectorId, areaId,timeperiodId);
		}
		// if district filter is done or a district login is done and form type is not of DH then this code will get executed
		else {
			objects = lastVisitDataRepository.getDataByFormIdAndDistrictAreaId(
					formId, sectorId, areaId,timeperiodId);
		}
		List<GoogleMapDataModel> googleMapDataModels = new ArrayList<>();
		
		String cboType=null;
		for (Object[] obj : objects) {
			GoogleMapDataModel mapDataModel = new GoogleMapDataModel();

			Double dataValue = new Double(0.0);
			// Double numerator = new Double(0.0);
			// Double denominator = new Double(0.0);

			if (obj[0] instanceof LastVisitData) {
				
				LastVisitData lastVisitData = (LastVisitData) obj[0];
				mapDataModel.setId(lastVisitData.getLastVisitDataId());
				mapDataModel.setAreaID(lastVisitData.getArea().getAreaId()
						.toString());
				cboType=lastVisitData.getFacilityScores().get(0).getFormXpathScoreMapping().getCboType();
				mapDataModel.setLatitude(lastVisitData.getLatitude());
				mapDataModel.setLongitude(lastVisitData.getLongitude());
				mapDataModel.setShowWindow(false);
				mapDataModel.setDateOfVisit(null != lastVisitData
						.getDateOfVisit() ? lastVisitData.getDateOfVisit()
						.toString() : null);
				mapDataModel.setTitle(lastVisitData.getArea().getAreaName());

				StringBuffer finalList = new StringBuffer();
				if (lastVisitData.getImageFileNames() != null) {
					String[] listOfImages = lastVisitData.getImageFileNames()
							.split(",");
					for (String img : listOfImages) {
						finalList.append("resources/images/facilities/" + img
								+ ",");
					}
					if (finalList.length() > 0) {
						finalList.replace(finalList.length() - 1,
								finalList.length(), "");
					}
					mapDataModel.setImages(finalList.toString());
				}

			}

			// if (obj[1] instanceof FacilityScore) {
			// FacilityScore facilityScore = (FacilityScore) obj[1];
			// numerator = null != facilityScore.getScore() ?
			// facilityScore.getScore() : 0.0;
			// }
			//
			// if(obj[3] instanceof FormXpathScoreMapping){
			// FormXpathScoreMapping formXpathScoreMapping =
			// (FormXpathScoreMapping) obj[3];
			// denominator = null != formXpathScoreMapping.getMaxScore() ?
			// formXpathScoreMapping.getMaxScore() : 0.0;
			// }
			if (obj[4] instanceof Double) {
				dataValue = (Double) obj[4];
			}
			// dataValue = ((numerator * 1.0)/(denominator * 1.0))*100;
			mapDataModel
					.setDataValue(dataValue != null && dataValue == 0.0 ? "0.0"
							: dataValue != null ? df.format(dataValue) : "NA");

			switch (cboType) {
			case "Intermediary":// CHC
				if (dataValue >= 80) {
					mapDataModel
							.setIcon("resources/images/pushpins/CHC_green.png");
				} else if (dataValue >= 60 && dataValue < 80) {
					mapDataModel
							.setIcon("resources/images/pushpins/CHC_orange.png");
				} else if (dataValue >= 0 && dataValue < 60) {
					mapDataModel
							.setIcon("resources/images/pushpins/CHC_red.png");
				}
				break;
			case "Village CP Committee": // DH
				if (dataValue >= 80) {
					mapDataModel
							.setIcon("resources/images/pushpins/DH_green.png");
				} else if (dataValue >= 60 && dataValue < 80) {
					mapDataModel
							.setIcon("resources/images/pushpins/DH_orange.png");
				} else if (dataValue >= 0 && dataValue < 60) {
					mapDataModel
							.setIcon("resources/images/pushpins/DH_red.png");
				}
				break;
			case "Village CBO":// PHC
				if (dataValue >= 80) {
					mapDataModel
							.setIcon("resources/images/pushpins/PHC_green.png");
				} else if (dataValue >= 60 && dataValue < 80) {
					mapDataModel
							.setIcon("resources/images/pushpins/PHC_orange.png");
				} else if (dataValue >= 0 && dataValue < 60) {
					mapDataModel
							.setIcon("resources/images/pushpins/PHC_red.png");
				}
				break;
			default:
				break;
			}

			googleMapDataModels.add(mapDataModel);
		}
		return googleMapDataModels;
	}

	//Not used
	@Override  @Transactional(readOnly=true)
	public List<ScoreModel> fetchLabelFromLastVisitData(Integer lastVisitDataId)
			throws Exception {

		List<Object[]> objects = lastVisitDataRepository
				.getByLastVisitData(lastVisitDataId);
		List<ScoreModel> facilityScoreMappingLabelModels = new ArrayList<>();
		for (Object[] obj : objects) {

			ScoreModel facilityScoreMappingLabelModel = new ScoreModel();

			if (obj[1] instanceof FacilityScore) {
				FacilityScore facilityScore = (FacilityScore) obj[1];
				facilityScoreMappingLabelModel.setScore(null != facilityScore
						.getScore() ? facilityScore.getScore() : null);
			}
			if (obj[2] instanceof FormXpathScoreMapping) {
				FormXpathScoreMapping formXpathScoreMapping = (FormXpathScoreMapping) obj[2];
				facilityScoreMappingLabelModel
						.setFormXpathScoreId(formXpathScoreMapping
								.getFormXpathScoreId());
				facilityScoreMappingLabelModel
						.setMaxScore(null != formXpathScoreMapping
								.getMaxScore() ? formXpathScoreMapping
								.getMaxScore() : null);
				facilityScoreMappingLabelModel
						.setName(null != formXpathScoreMapping.getLabel()
								&& !formXpathScoreMapping.getLabel().equals("") ? formXpathScoreMapping
								.getLabel() : null);
				facilityScoreMappingLabelModel
						.setParentXpathScoreId(formXpathScoreMapping
								.getParentXpathId());
			}
			if (obj[3] instanceof Double) {
				Double percentScore = (Double) obj[3];
				facilityScoreMappingLabelModel
						.setPercentScore(percentScore != null
								&& percentScore == 0.0 ? "0.0"
								: percentScore != null ? df
										.format(percentScore) : "NA");
			}
			// facilityScoreMappingLabelModel.setLastVisitDataId(lastVisitDataId);
			facilityScoreMappingLabelModels.add(facilityScoreMappingLabelModel);

		}
		return facilityScoreMappingLabelModels;
	}

	// Not used
	@Override  @Transactional(readOnly=true)
	public Map<String, List<ScoreModel>> getGridTableData(Integer formId,
			Integer lastVisitDataId,int timeperiodId) throws Exception {

		Map<String, List<ScoreModel>> headerScoreMap = new HashMap<String, List<ScoreModel>>();
		List<ScoreModel> scoreModels = new ArrayList<ScoreModel>();

		scoreModels = lastVisitDataId != 0 ? fetchLabelFromLastVisitData(lastVisitDataId)
				: getAllAggregatedData(formId);

		headerScoreMap.put("Score (%)", scoreModels);

		return headerScoreMap;
	}

	@Override  @Transactional(readOnly=true)
	public SpiderDataCollection getfetchSpiderData(Integer formId,
			Integer lastVisitDataId, Integer areaId,String cboType) {
		//getting user details from the state manager
		CollectUserModel collectUserModel = (CollectUserModel) stateManager
				.getValue(Constants.USER_PRINCIPAL);
		Integer areaLevelId = collectUserModel
				.getUserRoleFeaturePermissionMappings().get(0)
				.getRoleFeaturePermissionSchemeModel().getAreaModel()
				.getAreaLevelId();
		Integer parentAreaId = collectUserModel
				.getUserRoleFeaturePermissionMappings().get(0)
				.getRoleFeaturePermissionSchemeModel().getAreaModel()
				.getParentAreaId();
		
		List<Object []> maxMinTimePeriodId=new ArrayList<Object[]>();
		//If in google map data if pushpin is not clicked then lastVisit Data will be 0
		if(lastVisitDataId==0)
		{
			//if areaID=0 the we will check if logged in user is of district Level
	        if (areaLevelId > 3 && parentAreaId != -1) {
					areaId = collectUserModel
							.getUserRoleFeaturePermissionMappings().get(0)
							.getRoleFeaturePermissionSchemeModel().getAreaModel()
							.getAreaId();
					
//					if (formId == Integer.parseInt( messages.getMessage("DH_FormID",null,null)))
//					{
//				maxMinTimePeriodId=lastVisitDataRepository.findMaxMinTimePeriodIdForADistrict(areaId,formId);
//					}
//					else
//					{
						maxMinTimePeriodId=lastVisitDataRepository.findMaxMinTimePeriodIdForADistrictPHCCHC(areaId,formId);
//					}
					}
	    	//if district filter is done then areaId will contain the Id of that district.In case of State select Id will be 0
			else	if (areaId != 0) {
//				if (formId == Integer.parseInt( messages.getMessage("DH_FormID",null,null)))
//				{
//			maxMinTimePeriodId=lastVisitDataRepository.findMaxMinTimePeriodIdForADistrict(areaId,formId);
//				}
//				else
//				{
					maxMinTimePeriodId=lastVisitDataRepository.findMaxMinTimePeriodIdForADistrictPHCCHC(areaId,formId);
//				}
			}
	     // for the state level or national Level User
			else
			{
				maxMinTimePeriodId=lastVisitDataRepository.findMaxMinTimePeriodIdForState();
	        }
		}
		// if pushpin clicked
		else
		{
			maxMinTimePeriodId=lastVisitDataRepository.findMaxMinTimePeriodIdForAFacility(lastVisitDataId);
		}
		
		// setting max and min timeperiod for a area
		
		List<Integer> maxMinTime=new ArrayList<Integer>();
		if(maxMinTimePeriodId.size()>0)
		{
		if(maxMinTimePeriodId.get(0)[0]!=null)
		{
			maxMinTime.add(Integer.parseInt(maxMinTimePeriodId.get(0)[0].toString()));
		}
		
		if(maxMinTimePeriodId.get(0)[1]!=null && !maxMinTime.contains(Integer.parseInt(maxMinTimePeriodId.get(0)[1].toString())) )
		{
			maxMinTime.add(Integer.parseInt(maxMinTimePeriodId.get(0)[1].toString()));
		}
		Collections.reverse(maxMinTime);
		}
		
		List<List<SpiderDataModel>> spiderDataModelsLists = new ArrayList<List<SpiderDataModel>>();
		SpiderDataCollection spiderDataCollection = new SpiderDataCollection();
		List<Map<String,String>> gridData = new ArrayList<Map<String,String>>();
		Map<String,Map<String,String>> tableData=new LinkedHashMap<String, Map<String,String>>();
		for(int timeperiodId:maxMinTime)
		{
		
		List<SpiderDataModel> spiderDataModels = new ArrayList<SpiderDataModel>();

		List<Object[]> spiderDatas = new ArrayList<Object[]>();;

		String cboTypeFormXpath=cboType.replace(" ", ".");
		//getting parentXpath Id of sector according to cbo type from messages.properties
		Integer parenXpathId = Integer.parseInt(messages.getMessage(
				cboTypeFormXpath, null, null));
		
//		System.out.println(cboType);
		
		//If in google map data if pushpin is not clicked then lastVisit Data will be 0
		if (lastVisitDataId == 0) {
		
			//if areaID=0 the we will check if logged in user is of district Level
        if (areaLevelId > 3 && parentAreaId != -1) {
				areaId = collectUserModel
						.getUserRoleFeaturePermissionMappings().get(0)
						.getRoleFeaturePermissionSchemeModel().getAreaModel()
						.getAreaId();

//				//checking form is of DH
//				if (formId == Integer.parseInt( messages.getMessage("DH_FormID",null,null))) {
//					spiderDatas =facilityScoreRepository
//							.findSpiderDataChartByFormIdForDistrictForDGA(
//									formId, -1, areaId,timeperiodId);
//					spiderDatas .addAll( facilityScoreRepository
//							.findSpiderDataChartByFormIdForDistrictForDGA(
//									formId, parenXpathId, areaId,timeperiodId));
//				} 
//				// if form is of CHC,DH
//				else 
				{
					spiderDatas = facilityScoreRepository
							.findSpiderDataChartByFormIdForDistrict(formId,
									-1, areaId,timeperiodId,cboType);
					spiderDatas .addAll(facilityScoreRepository
							.findSpiderDataChartByFormIdForDistrict(formId,
									parenXpathId, areaId,timeperiodId,cboType));
				}

			}
    	//if district filter is done then areaId will contain the Id of that district.In case of State select Id will be 0
			else	if (areaId != 0) {
				
				// if formaID is of DH
				if (formId == Integer.parseInt( messages.getMessage("DH_FormID",null,null))) {
					spiderDatas = facilityScoreRepository
							.findSpiderDataChartByFormIdForDistrictForDGA(
									formId, -1, areaId,timeperiodId);
					
					spiderDatas .addAll( facilityScoreRepository
							.findSpiderDataChartByFormIdForDistrictForDGA(
									formId, parenXpathId, areaId,timeperiodId));
				} 
				//if formId is of PHC,CHC
				else {
					spiderDatas = facilityScoreRepository
							.findSpiderDataChartByFormIdForDistrict(formId,
									-1, areaId,timeperiodId,cboType);
					spiderDatas.addAll(facilityScoreRepository
							.findSpiderDataChartByFormIdForDistrict(formId,
									parenXpathId, areaId,timeperiodId,cboType));
				}

			} 

			// for the state level or national Level User
			else {
				spiderDatas = facilityScoreRepository
						.findSpiderDataChartByFormId(formId, -1,timeperiodId,cboType);
				
				spiderDatas .addAll( facilityScoreRepository
						.findSpiderDataChartByFormId(formId, parenXpathId,timeperiodId,cboType));
			}

		} 
		// if on google map pushpin is clicked
		else {
			spiderDatas = facilityScoreRepository
					.findSpiderDataChartByLasatVisitDatAndFormIdAndTimePeriodId(formId,
							lastVisitDataId, -1,timeperiodId);
			spiderDatas .addAll(facilityScoreRepository
					.findSpiderDataChartByLasatVisitDatAndFormIdAndTimePeriodId(formId,
							lastVisitDataId, parenXpathId,timeperiodId));
		}
		for (Object[] spiderData : spiderDatas) {
			
			SpiderDataModel spiderDataModel = new SpiderDataModel();

			
			spiderDataModel.setAxis(spiderData[0].toString().contains("Total score")?"Overall Score":spiderData[0].toString());
			spiderDataModel.setValue(spiderData[1] == null|| spiderData[1].equals("0.00") ? "0.0" :spiderData[1].toString().equalsIgnoreCase("0.0")?"0.0": df
					.format((Double) spiderData[1]));
			spiderDataModel.setFxmId(Integer.parseInt(spiderData[3].toString()));

			spiderDataModel.setTimePeriod(spiderData[2].toString());
			Map<String,String> mapData=new LinkedHashMap<String, String>();
			
			// putting the data for tables.Setting the value in this format <IndicatorName,<TimeperiodName,Value>>
			if(!tableData.containsKey(spiderDataModel.getAxis()))
			{
				mapData.put(spiderDataModel.getTimePeriod(), spiderDataModel.getValue());
				tableData.put(spiderDataModel.getAxis(), mapData);
			}
			else
			{
				tableData.get(spiderDataModel.getAxis()).put(spiderDataModel.getTimePeriod(), spiderDataModel.getValue());
			}
		
			
			
			
			spiderDataModels.add(spiderDataModel);
		}
		spiderDataModelsLists.add(spiderDataModels);
	}
		spiderDataCollection.setDataCollection(spiderDataModelsLists);
		
		tableData.forEach((k, v) -> {
			Map<String,String> gridMapTableData = new LinkedHashMap<String,String>();
			
			
			// setting the table data in following format <Indicator,IndicatorName>,<Timeperiod,Value>
			gridMapTableData.put("Indicator", k);
			v.forEach((i, j) ->{
				gridMapTableData.put(i, j);
			});
			gridData.add(gridMapTableData);

		});
		spiderDataCollection.setTableData(gridData);
		return spiderDataCollection;
	}

	@Override  @Transactional(readOnly=true)
	public List<FormXpathScoreMappingModel> getParentSectors() {
		List<FormXpathScoreMapping> formXpathScoreMappings = formXpathScoreMappingRepository
				.findByParentXpathId(-1);
		/* formXpathScoreMapping */
		List<FormXpathScoreMappingModel> formXpathScoreMappingModels = new ArrayList<FormXpathScoreMappingModel>();
		
		//Getting the parent Sectors
		for (FormXpathScoreMapping formXpathScoreMapping : formXpathScoreMappings) {
			FormXpathScoreMappingModel formXpathScoreMappingModel = new FormXpathScoreMappingModel();
			formXpathScoreMappingModel
					.setFormXpathScoreId(formXpathScoreMapping
							.getFormXpathScoreId());
			//Slicing the name as DB Consist the name as Total SCore OF DH for every Sector
			formXpathScoreMappingModel.setLabel(formXpathScoreMapping
					.getLabel().split("Total score for")[1]);
			formXpathScoreMappingModel.setFormId(formXpathScoreMapping
					.getForm().getFormId());
			
			formXpathScoreMappingModel.setType(formXpathScoreMapping.getCboType());

			formXpathScoreMappingModels.add(formXpathScoreMappingModel);
		}
		return formXpathScoreMappingModels;
	}

	@Override  @Transactional(readOnly=true)
	public List<FormXpathScoreMappingModel> getSectors(Integer parentId) {
		List<FormXpathScoreMapping> formXpathScoreMappings = formXpathScoreMappingRepository
				.findByParentXpathId(parentId);
		/* formXpathScoreMapping */
		List<FormXpathScoreMappingModel> formXpathScoreMappingModels = new ArrayList<FormXpathScoreMappingModel>();

		FormXpathScoreMappingModel formXpathScoreMappingModel = new FormXpathScoreMappingModel();
		formXpathScoreMappingModel.setFormXpathScoreId(parentId);
		formXpathScoreMappingModel.setLabel("Overall Score");
		formXpathScoreMappingModel.setFormId(0);
		formXpathScoreMappingModels.add(formXpathScoreMappingModel);

		for (FormXpathScoreMapping formXpathScoreMapping : formXpathScoreMappings) {
			formXpathScoreMappingModel = new FormXpathScoreMappingModel();
			formXpathScoreMappingModel
					.setFormXpathScoreId(formXpathScoreMapping
							.getFormXpathScoreId());
			formXpathScoreMappingModel.setLabel(formXpathScoreMapping
					.getLabel());
			formXpathScoreMappingModel.setFormId(formXpathScoreMapping
					.getForm().getFormId());
			formXpathScoreMappingModel.setParentXpathId(formXpathScoreMapping
					.getParentXpathId());
			formXpathScoreMappingModel.setMaxScore(formXpathScoreMapping
					.getMaxScore());
			formXpathScoreMappingModels.add(formXpathScoreMappingModel);
		}
		return formXpathScoreMappingModels;
	}

	@Override  @Transactional(readOnly=true)
	public List<AreaModel> getAllDistricts() {
		CollectUserModel collectUserModel = (CollectUserModel) stateManager
				.getValue(Constants.USER_PRINCIPAL);
		Integer areaLevelId = collectUserModel
				.getUserRoleFeaturePermissionMappings().get(0)
				.getRoleFeaturePermissionSchemeModel().getAreaModel()
				.getAreaLevelId();
		Integer parentAreaId = collectUserModel
				.getUserRoleFeaturePermissionMappings().get(0)
				.getRoleFeaturePermissionSchemeModel().getAreaModel()
				.getParentAreaId();

		//if Logged in  user of District type then only its own District will be displayed
		List<AreaModel> districtModels = new ArrayList<AreaModel>();
		if (areaLevelId > 3 && parentAreaId != -1) {
			districtModels.add(collectUserModel
					.getUserRoleFeaturePermissionMappings().get(0)
					.getRoleFeaturePermissionSchemeModel().getAreaModel());
			return districtModels;
		}
// if of national or guest or admin type then all the areas
		List<Area> districts = areaRepository.findByAreaLevelAreaLevelId(4);

		AreaModel areaModel = new AreaModel();

		areaModel.setAreaId(0);
		areaModel.setAreaName("ODISHA");

		districtModels.add(areaModel);

		for (Area district : districts) {
			areaModel = new AreaModel();
			areaModel.setAreaId(district.getAreaId());
			areaModel.setAreaName(district.getAreaName());
			areaModel.setAreaLevelId(district.getAreaLevel().getAreaLevelId());
			areaModel.setParentAreaId(district.getParentAreaId());

			districtModels.add(areaModel);
		}
		return districtModels;
	}

	@SuppressWarnings("resource")
	@Override  @Transactional(readOnly=true)
	public String exportToPdf(String spiderChart, String columnChart,
			Integer formId, Integer lastVisitDataId, Integer areaId,
			HttpServletResponse response,int noOfFacilities,int timeperiodId,String cboType) throws Exception{
			SpiderDataCollection spiderDataCollection = getfetchSpiderData(
					formId, lastVisitDataId, areaId,cboType);
			
			Map<String,List<SpiderDataModel>> spiderDataMap=new HashMap<String,List<SpiderDataModel>>();
			List<Object[]> childDatas=new ArrayList<Object[]>();
			for(List<SpiderDataModel> spiderDataModels:spiderDataCollection.getDataCollection())
			{
			for(SpiderDataModel spiderDataModel:spiderDataModels)
			{
				childDatas=new ArrayList<Object[]>();
				if(spiderDataModel.getAxis().equalsIgnoreCase("Overall Score")) continue;
				
			if(lastVisitDataId!=0)
			{
				childDatas.addAll(facilityScoreRepository.findSpiderDataChartByLasatVisitDatAndFormIdAndTimePeriodId(formId, lastVisitDataId, spiderDataModel.getFxmId(), timeperiodId));
			}
			else
			{
				if(areaId==0)
				{
					childDatas.addAll(facilityScoreRepository.findSpiderDataChartByFormId(formId, spiderDataModel.getFxmId(), timeperiodId, cboType));
				}
				else
				{
					childDatas.addAll(facilityScoreRepository.findSpiderDataChartByFormIdForDistrict(formId, spiderDataModel.getFxmId(), areaId, timeperiodId, cboType));
				}
			}
			
			for(Object[] childData:childDatas)
			{
				SpiderDataModel spiderDataModel1=new SpiderDataModel();
				spiderDataModel1.setAxis(childData[0].toString());
				spiderDataModel1.setValue(childData[1]==null?"0.0":df.format(Double.parseDouble(childData[1].toString())));
				if(spiderDataMap.containsKey(childData[2].toString()+"_"+spiderDataModel.getFxmId()))
				{
					List<SpiderDataModel> spiderDataModels1=spiderDataMap.get(childData[2].toString()+"_"+spiderDataModel.getFxmId());
					spiderDataModels1.add(spiderDataModel1);
					spiderDataMap.put(childData[2].toString()+"_"+spiderDataModel.getFxmId(), spiderDataModels1);
				}
				else
				{
					List<SpiderDataModel> spiderDataModels1=new ArrayList<SpiderDataModel>();
					spiderDataModels1.add(spiderDataModel1);
					spiderDataMap.put(childData[2].toString()+"_"+spiderDataModel.getFxmId(), spiderDataModels1);
				}
				
			}
			}
			}
			
			

			
			new FileOutputStream(new File(context.getRealPath("")
					+ "\\resources\\spider.svg")).write(spiderChart.getBytes());

			String area;

			
			if (lastVisitDataId!=0)
			{
				noOfFacilities=1;
			}
			//getting sector Name
	
			
			// If pushpin is clicked the we will set FaciliTy name in area
			if (lastVisitDataId != 0) {
				area = lastVisitDataRepository
						.findByLastVisitDataIdAndIsLiveTrue(lastVisitDataId)
						.getArea().getAreaName();
			} 
			//for the district filter
			else if(areaId!=0){
				area = areaRepository.findByAreaId(areaId).getAreaName();
			}
			else
				area="Odisha";

			Font smallBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
			Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
			Font boldFont = new Font(Font.FontFamily.HELVETICA,12,Font.BOLD);

			Document document = new Document(PageSize.A4.rotate());
			String outputPath = messages.getMessage("outputPath", null, null)
					+ area + "_" + cboType +sdf.format(new java.util.Date()) +".pdf";
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(outputPath));
			
			/**
			 * setting Header Footer.{@link HeaderFooter}
			 */
			HeaderFooter headerFooter=new HeaderFooter(context, ResourceBundle.getBundle("spring/app").getString("domain.name"));
			writer.setPageEvent(headerFooter);

			document.open();

//			BaseColor cellColor = WebColors.getRGBColor("#E8E3E2");



			Paragraph blankSpace = new Paragraph();
			blankSpace.setAlignment(Element.ALIGN_CENTER);
			blankSpace.setSpacingAfter(10);
			Chunk blankSpaceChunk = new Chunk(" ");
			blankSpace.add(blankSpaceChunk);

			
			Paragraph areaParagrap = new Paragraph();
			areaParagrap.setAlignment(Element.ALIGN_LEFT);
			areaParagrap.setSpacingAfter(10);
			Chunk areaParagrapChunk = new Chunk(lastVisitDataId!=0?"CBO: " :"Area: ",boldFont );
			Chunk selectedAreaParagrapChunk = new Chunk(area);
			areaParagrap.add(areaParagrapChunk);
			areaParagrap.add(selectedAreaParagrapChunk);
			
			
			Paragraph cboTypeParagrap = new Paragraph();
			cboTypeParagrap.setAlignment(Element.ALIGN_LEFT);
			cboTypeParagrap.setSpacingAfter(10);
			Chunk cboTypeParagrapChunk = new Chunk("CBO Type: ",boldFont );
			Chunk selectedcboTypeParagrapChunk = new Chunk(cboType);
			cboTypeParagrap.add(areaParagrap);
			cboTypeParagrap.add(cboTypeParagrapChunk);
			cboTypeParagrap.add(selectedcboTypeParagrapChunk);
			
			Paragraph numberParagrap = new Paragraph();
			numberParagrap.setAlignment(Element.ALIGN_RIGHT);
			numberParagrap.setSpacingAfter(10);
			Chunk numberParagrapChunk = new Chunk("Number of CBO Assessed : ",boldFont );
			Chunk selectednumberParagrapChunk = new Chunk(String.valueOf(noOfFacilities));
			numberParagrap.add(numberParagrapChunk);
			numberParagrap.add(selectednumberParagrapChunk);
			
			
			Paragraph TimperiodParagrap = new Paragraph();
			TimperiodParagrap.setAlignment(Element.ALIGN_RIGHT);
			TimperiodParagrap.setSpacingAfter(10);
			Chunk TimePeriodParagrapChunk = new Chunk("Time Period: ",boldFont );
			Chunk selectedTimePeriodParagrapChunk = new Chunk(spiderDataCollection.getTableData().get(0).keySet().toArray()[1].toString());
			TimperiodParagrap.add(numberParagrap);
			TimperiodParagrap.add(TimePeriodParagrapChunk);
			TimperiodParagrap.add(selectedTimePeriodParagrapChunk);
			
			
			PdfPTable selectionTable=new PdfPTable(2);
			selectionTable.setWidthPercentage(100);
			/*float [] selectionTablecolumnWidths = new float[] { 10f,10f,10f };
			selectionTable.setWidths(selectionTablecolumnWidths);*/
			
			PdfPCell areaCell=new PdfPCell();
			areaCell.addElement(cboTypeParagrap);
			areaCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			areaCell.setBorderColor(BaseColor.WHITE);
			selectionTable.addCell(areaCell);
			
			
			PdfPCell numberCell=new PdfPCell();
			numberCell.addElement(TimperiodParagrap);
			numberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			numberCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			numberCell.setBorderColor(BaseColor.WHITE);
			selectionTable.addCell(numberCell);
		

			// for Image



			String css = "svg {" + "shape-rendering: geometricPrecision;"
					+ "text-rendering:  geometricPrecision;"
					+ "color-rendering: optimizeQuality;"
					+ "image-rendering: optimizeQuality;" + "}";
			File cssFile = File.createTempFile("batik-default-override-",
					".css");
			FileUtils.writeStringToFile(cssFile, css);

			String svg_URI_input = Paths
					.get(new File(context.getRealPath("")
							+ "\\resources\\spider.svg").getPath()).toUri()
					.toURL().toString();
			TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
			// Step-2: Define OutputStream to PNG Image and attach to
			// TranscoderOutput
			ByteArrayOutputStream png_ostream = new ByteArrayOutputStream();
			TranscoderOutput output_png_image = new TranscoderOutput(
					png_ostream);
			// Step-3: Create PNGTranscoder and define hints if required
			PNGTranscoder  my_converter = new PNGTranscoder ();
			// Step-4: Convert and Write output
			my_converter.transcode(input_svg_image, output_png_image);
			png_ostream.flush();

			PdfPTable contentTable = new PdfPTable(2);
			contentTable.setWidthPercentage(100);
			
			Image spiderDataImage = Image.getInstance(png_ostream.toByteArray());
//			int indentation1 = 0;
/*			float scaler1 = ((document.getPageSize().getWidth()
					- document.leftMargin() - document.rightMargin() - indentation1) / spiderDataImage
					.getWidth()) * 62;*/
			spiderDataImage.scalePercent(60);
			spiderDataImage.setAbsolutePosition(12, 70);
			
			
			
			PdfPCell imageCell=new PdfPCell(spiderDataImage);
			imageCell.setBorderColor(BaseColor.WHITE);
			imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);


			BaseColor siNoColor=WebColors.getRGBColor("#f4f4f4");
			BaseColor redColor=WebColors.getRGBColor("#D7191C");
			BaseColor orangeColor = WebColors.getRGBColor("#FF8000");
			BaseColor greenColor=WebColors.getRGBColor("#1A9642");
			BaseColor borderColor=WebColors.getRGBColor("#ddd");
			PdfPTable spiderDataTable = null;
			
			if (spiderDataCollection.getDataCollection().size() > 1) {
				spiderDataTable = new PdfPTable(4);
			} else {
				spiderDataTable = new PdfPTable(3);
			}
			
			spiderDataTable.setKeepTogether(true);

			spiderDataTable.setWidthPercentage(100);
			// Spider Datas Table
			float[] spiderDatacolumnWidths;
			if (spiderDataCollection.getDataCollection().size() > 1) {
				spiderDatacolumnWidths = new float[] { 2f,10f,4f,4f };

			} else {
				spiderDatacolumnWidths = new float[] { 2f,10f,4f };
			}
			spiderDataTable.setHeaderRows(1);
			spiderDataTable.setWidths(spiderDatacolumnWidths);
			

		
			PdfPCell spiderDataCell0 = new PdfPCell(new Paragraph("Sl No.",
					smallBold));
			
			PdfPCell spiderDataCell1 = new PdfPCell(new Paragraph("Indicators",
					smallBold));
			PdfPCell spiderDataCell3 = new PdfPCell(new Paragraph(spiderDataCollection.getTableData().get(0).keySet().toArray()[1].toString(), smallBold));

			
			spiderDataCell0.setBackgroundColor(siNoColor);
			spiderDataCell0.setBorderColor(borderColor);
			
			spiderDataCell1.setBackgroundColor(siNoColor);
			spiderDataCell3.setBackgroundColor(siNoColor);
			spiderDataCell3.setBorderColor(borderColor);
			
			spiderDataCell1.setBorderColor(borderColor);
			
			spiderDataCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			spiderDataCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			spiderDataCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
	
			spiderDataTable.addCell(spiderDataCell0);
			spiderDataTable.addCell(spiderDataCell1);
			spiderDataTable.addCell(spiderDataCell3);
			if (spiderDataCollection.getDataCollection().size() > 1) {
				PdfPCell spiderDataCell4 = new PdfPCell(new Paragraph(new Paragraph(spiderDataCollection.getTableData().get(0).keySet().toArray()[2].toString(), smallBold)));
				spiderDataCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				spiderDataCell4.setBackgroundColor(siNoColor);
				spiderDataCell4.setBorderColor(borderColor);
				spiderDataTable.addCell(spiderDataCell4);
			}

			int i=1;
			if (spiderDataCollection.getDataCollection().size() > 0
					&& spiderDataCollection.getDataCollection().get(0) != null
					&& !spiderDataCollection.getDataCollection().get(0).isEmpty()) {
				List<SpiderDataModel> listSpiderDataModel = spiderDataCollection
						.getDataCollection().get(0);

				for (SpiderDataModel spiderDataModel : listSpiderDataModel) {

					
					PdfPCell data0 = new PdfPCell(new Paragraph(String.valueOf(i),dataFont));
					PdfPCell data1 = new PdfPCell(new Paragraph(
							spiderDataModel.getAxis(),
							dataFont));
					
					data0.setFixedHeight(spiderDataCell3.getHeight());
					data0.setHorizontalAlignment(Element.ALIGN_CENTER);
					
					data1.setFixedHeight(spiderDataCell3.getHeight());
					data1.setHorizontalAlignment(Element.ALIGN_LEFT);

					PdfPCell data3 = new PdfPCell(new Paragraph(
							spiderDataModel.getValue(), dataFont));
					data3.setHorizontalAlignment(Element.ALIGN_CENTER);
					data3.setFixedHeight(spiderDataCell3.getHeight());
					
					
					
						data0.setBorderColor(borderColor);
						data0.setBackgroundColor(siNoColor);
						data1.setBorderColor(borderColor);
						data3.setBorderColor(borderColor);
						data1.setBackgroundColor(siNoColor);
						
						if(Double.parseDouble(spiderDataModel.getValue())<60)
						{
							data3.setBackgroundColor(redColor);
						}
						
						else if(Double.parseDouble(spiderDataModel.getValue())<80)
						{
							data3.setBackgroundColor(orangeColor);
						}
						else
						{
							data3.setBackgroundColor(greenColor);
						}
						
					spiderDataTable.addCell(data0);
					spiderDataTable.addCell(data1);
					spiderDataTable.addCell(data3);
					
					if(spiderDataMap.containsKey((spiderDataModel.getTimePeriod()+"_"+(spiderDataModel.getFxmId()))))
					{
						int j=1;
						
						for(SpiderDataModel spiDataModel:spiderDataMap.get((spiderDataModel.getTimePeriod()+"_"+(spiderDataModel.getFxmId()))))
						{


							
							 data0 = new PdfPCell(new Paragraph(i+"."+j++,dataFont));
							 data1 = new PdfPCell(new Paragraph(
									 spiDataModel.getAxis(),
									dataFont));
							
							data0.setFixedHeight(spiderDataCell3.getHeight());
							data0.setHorizontalAlignment(Element.ALIGN_CENTER);
							
							data1.setFixedHeight(spiderDataCell3.getHeight());
							data1.setHorizontalAlignment(Element.ALIGN_LEFT);
							data1.setIndent(20);

							 data3 = new PdfPCell(new Paragraph(
									 spiDataModel.getValue(), dataFont));
							data3.setHorizontalAlignment(Element.ALIGN_CENTER);
							data3.setFixedHeight(spiderDataCell3.getHeight());
							
							
							
								data0.setBorderColor(borderColor);
								data0.setBackgroundColor(siNoColor);
								data1.setBorderColor(borderColor);
								data3.setBorderColor(borderColor);
								data1.setBackgroundColor(siNoColor);
								
								if(Double.parseDouble(spiDataModel.getValue())<60)
								{
									data3.setBackgroundColor(redColor);
								}
								
								else if(Double.parseDouble(spiDataModel.getValue())<80)
								{
									data3.setBackgroundColor(orangeColor);
								}
								else
								{
									data3.setBackgroundColor(greenColor);
								}
								
							spiderDataTable.addCell(data0);
							spiderDataTable.addCell(data1);
							spiderDataTable.addCell(data3);
							
						}
					}
					
					if (spiderDataCollection.getDataCollection().size() > 1) {
						for (SpiderDataModel spiderDataModelTime2 : spiderDataCollection
								.getDataCollection().get(1)) {
							if (spiderDataModelTime2.getAxis().equalsIgnoreCase(
									spiderDataModel.getAxis())) {
								PdfPCell data4 = new PdfPCell(new Paragraph(
										spiderDataModelTime2.getValue(), dataFont));
								data4.setFixedHeight(spiderDataCell3.getHeight());
								data4.setBorderColor(borderColor);
								data4.setHorizontalAlignment(Element.ALIGN_CENTER);
								
								if(Double.parseDouble(spiderDataModelTime2.getValue())<60)
								{
									data4.setBackgroundColor(redColor);
								}
								
								else if(Double.parseDouble(spiderDataModelTime2.getValue())<80)
								{
									data4.setBackgroundColor(orangeColor);
								}
								else
								{
									data4.setBackgroundColor(greenColor);
								}
								
								spiderDataTable.addCell(data4);
							}
						}
					}

					i++;
				}

			}
			spiderDataTable.setComplete(true);
			PdfPCell tableCell=new PdfPCell();
			tableCell.setVerticalAlignment(Element.ALIGN_CENTER);
			tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			tableCell.setBorderColor(BaseColor.WHITE);
			if(spiderDataCollection.getDataCollection().get(0).size()>3)
			tableCell.setPaddingTop(20);
			else
				tableCell.setPaddingTop(50);
			tableCell.setFixedHeight(spiderDataTable.getTotalHeight());
			tableCell.addElement(spiderDataTable);
	
			
			contentTable.addCell(imageCell);
			contentTable.addCell(tableCell);
			
			 document.add( blankSpace );
//			 document.add(areaParagrap);
//			 document.add(cboTypeParagrap);
			 
			 document.add(selectionTable);

			
			
			document.add(Chunk.NEWLINE);
			document.add(contentTable);
			
			document.close();

	
			return outputPath;
	
	}

	@SuppressWarnings({ "resource" })
	@Override  @Transactional(readOnly=true)
	public String exportToExcel(String spiderChart, String columnChart, Integer formId,
			Integer lastVisitDataId, Integer areaId,
			HttpServletResponse response,int noOfFacilities,int timeperiodId,String cboType) throws Exception 
	{
		XSSFWorkbook workbook=new XSSFWorkbook();
		XSSFSheet sheet=workbook.createSheet("Score Card");
		CreationHelper creationHelper=workbook.getCreationHelper();
		XSSFHyperlink link = (XSSFHyperlink)creationHelper
			      .createHyperlink(Hyperlink.LINK_URL);
		link.setAddress( ResourceBundle.getBundle("spring/app").getString("domain.name"));
		link.setTooltip( ResourceBundle.getBundle("spring/app").getString("domain.name"));
		link.setLabel( ResourceBundle.getBundle("spring/app").getString("domain.name"));
		POIXMLProperties xmlProps = workbook.getProperties();
		POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
		coreProps.setCreator( ResourceBundle.getBundle("spring/app").getString("domain.name"));
		
		new FileOutputStream(new File(context.getRealPath("")
				+ "\\resources\\spider.svg")).write(spiderChart.getBytes());
		
		new FileOutputStream(new File(context.getRealPath("")
				+ "\\resources\\column.svg")).write(columnChart.getBytes());
		if (lastVisitDataId!=0)
		{
			noOfFacilities=1;
		}
		
		SpiderDataCollection spiderDataCollection = getfetchSpiderData(
				formId, lastVisitDataId, areaId,cboType); 
		Map<String,List<SpiderDataModel>> spiderDataMap=new HashMap<String,List<SpiderDataModel>>();
		List<Object[]> childDatas=new ArrayList<Object[]>();
		for(List<SpiderDataModel> spiderDataModels:spiderDataCollection.getDataCollection())
		{
		for(SpiderDataModel spiderDataModel:spiderDataModels)
		{
			childDatas=new ArrayList<Object[]>();
			if(spiderDataModel.getAxis().equalsIgnoreCase("Overall Score")) continue;
			
		if(lastVisitDataId!=0)
		{
			childDatas.addAll(facilityScoreRepository.findSpiderDataChartByLasatVisitDatAndFormIdAndTimePeriodId(formId, lastVisitDataId, spiderDataModel.getFxmId(), timeperiodId));
		}
		else
		{
			if(areaId==0)
			{
				childDatas.addAll(facilityScoreRepository.findSpiderDataChartByFormId(formId, spiderDataModel.getFxmId(), timeperiodId, cboType));
			}
			else
			{
				childDatas.addAll(facilityScoreRepository.findSpiderDataChartByFormIdForDistrict(formId, spiderDataModel.getFxmId(), areaId, timeperiodId, cboType));
			}
		}
		
		for(Object[] childData:childDatas)
		{
			SpiderDataModel spiderDataModel1=new SpiderDataModel();
			spiderDataModel1.setAxis(childData[0].toString());
			spiderDataModel1.setValue(childData[1]==null?"0.0":df.format(Double.parseDouble(childData[1].toString())));
			if(spiderDataMap.containsKey(childData[2].toString()+"_"+spiderDataModel.getFxmId()))
			{
				List<SpiderDataModel> spiderDataModels1=spiderDataMap.get(childData[2].toString()+"_"+spiderDataModel.getFxmId());
				spiderDataModels1.add(spiderDataModel1);
				spiderDataMap.put(childData[2].toString()+"_"+spiderDataModel.getFxmId(), spiderDataModels1);
			}
			else
			{
				List<SpiderDataModel> spiderDataModels1=new ArrayList<SpiderDataModel>();
				spiderDataModels1.add(spiderDataModel1);
				spiderDataMap.put(childData[2].toString()+"_"+spiderDataModel.getFxmId(), spiderDataModels1);
			}
			
		}
		}
		}
		
		
		String area;


		if (lastVisitDataId != 0) {
			area = lastVisitDataRepository
					.findByLastVisitDataIdAndIsLiveTrue(lastVisitDataId)
					.getArea().getAreaName();
		} else if(areaId!=0){
			area = areaRepository.findByAreaId(areaId).getAreaName();
		}
		else
			area="Odisha";

		int rowId=0;
		int colId=0;
		Row row=sheet.createRow(rowId);
		Cell col=row.createCell(colId);
		XSSFCellStyle headCellStyle=workbook.createCellStyle();
		   
		   XSSFFont headFont = workbook.createFont();
		   headFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		   headFont.setColor(HSSFColor.BLACK.index);
		   headFont.setFontHeight(18);
		   
		   headCellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		   headCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   headCellStyle.setFont(headFont);
		   headCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		 
		   
		col.setCellValue((lastVisitDataId != 0?"CBO: " + area :"Area: " + area )+ "\t  \t  CBO Type: "
					+ cboType +"\t \t Timeperiod: "+spiderDataCollection.getTableData().get(0).keySet().toArray()[1].toString());
		col.setHyperlink(link);
		col.setCellStyle(headCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(rowId,rowId,colId,19));
		rowId=3;
		
		row=sheet.createRow(rowId++);
		   colId=9;
		   col=row.createCell(colId);
		   XSSFCellStyle headerCellStyle=workbook.createCellStyle();
		   
		   XSSFFont headerFont = workbook.createFont();
		   headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		   headerFont.setColor(HSSFColor.BLACK.index);
		   
		   headerCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		   headerCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   headerCellStyle.setFont(headerFont);
		   headerCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		   headerCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		   headerCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		   headerCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
		   
		   XSSFCellStyle leftHeaderCellStyle=workbook.createCellStyle();
		   leftHeaderCellStyle.setFont(headerFont);
		   leftHeaderCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		   leftHeaderCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   leftHeaderCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		   leftHeaderCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		   leftHeaderCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		   leftHeaderCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
		   leftHeaderCellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		   
		   XSSFFont font = workbook.createFont();
		   XSSFCellStyle cellStyle=workbook.createCellStyle();
		   font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		   font.setColor(HSSFColor.GREY_80_PERCENT.index);
		   
		   XSSFFont cellFont = workbook.createFont();
		   cellFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		   cellFont.setColor(HSSFColor.BLACK.index);
		   
		   XSSFCellStyle redCellStyle=workbook.createCellStyle();
		   redCellStyle.setFont(cellFont);
		   redCellStyle.setFillForegroundColor(HSSFColor.RED.index);
		   redCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   redCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		   redCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		   redCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		   redCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
		   redCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   
		   
		   
		   XSSFCellStyle orangeCellStyle=workbook.createCellStyle();
		   orangeCellStyle.setFont(cellFont);		 
		   orangeCellStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
		   orangeCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   orangeCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		   orangeCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		   orangeCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		   orangeCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
		   orangeCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   
		   
		   XSSFCellStyle greenCellStyle=workbook.createCellStyle();
		   greenCellStyle.setFont(cellFont);
		   greenCellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		   greenCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   greenCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		   greenCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		   greenCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		   greenCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
		   greenCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   
		   
		   col.setCellValue("Sl No.");
		   headerCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   col.setCellStyle(headerCellStyle);
		   sheet.autoSizeColumn(colId);
		   colId++;
		   
		   
		   col=row.createCell(colId);
		   col.setCellValue("Indicator");
		   headerCellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		   col.setCellStyle(leftHeaderCellStyle);
		   sheet.autoSizeColumn(colId);
		   colId++;
		   
		   col=row.createCell(colId);
		   headerCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   col.setCellValue(spiderDataCollection.getTableData().get(0).keySet().toArray()[1].toString());
		   col.setCellStyle(headerCellStyle);
		   sheet.autoSizeColumn(colId);
		   colId++;
		   if (spiderDataCollection.getDataCollection().size() > 1) {
		   col=row.createCell(colId);
		   sheet.autoSizeColumn(colId);
		   
		   sheet.autoSizeColumn(colId);
		   headerCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   col.setCellValue(spiderDataCollection.getTableData().get(0).keySet().toArray()[2].toString());
		   col.setCellStyle(headerCellStyle);
	   sheet.autoSizeColumn(colId);
	   colId++;
		   }
		   
		   int i=1;
		   for(SpiderDataModel spiderData:spiderDataCollection.getDataCollection().get(0))
		   {
			   
			   
			   
			   cellStyle.setFont(font);
			   cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   cellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   cellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   cellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   cellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			   
			   XSSFCellStyle leftCellStyle=workbook.createCellStyle();
			   leftCellStyle.setFont(font);
			   leftCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   leftCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   leftCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			   
			   
			   XSSFCellStyle centreCellStyle=workbook.createCellStyle();
			   centreCellStyle.setFont(font);
			   centreCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   centreCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   centreCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   centreCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   centreCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   centreCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   centreCellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			   
			 
			   colId=9;
			   row=sheet.createRow(rowId++);
			   col=row.createCell(colId);
			   col.setCellValue(i);
			   col.setCellStyle(centreCellStyle);
			   sheet.autoSizeColumn(colId);
			   colId++;
			   
			   col=row.createCell(colId);
			   col.setCellValue(spiderData.getAxis());
			   col.setCellStyle(leftCellStyle);
			   
			   sheet.autoSizeColumn(colId);
			   colId++;
			   col=row.createCell(colId);
			   col.setCellValue(spiderData.getValue());
			   
			   if(Double.parseDouble(spiderData.getValue())<60)
			   col.setCellStyle(redCellStyle);
			   else if(Double.parseDouble(spiderData.getValue())<80)
				   col.setCellStyle(orangeCellStyle);   
			   else
				   col.setCellStyle(greenCellStyle);
			   
			   sheet.autoSizeColumn(colId);
			   colId++;
			   
			   
			   if (spiderDataCollection.getDataCollection().size() > 1) {
					for (SpiderDataModel spiderDataModelTime2 : spiderDataCollection
							.getDataCollection().get(1)) {
						if (spiderDataModelTime2.getAxis().equalsIgnoreCase(
								spiderData.getAxis())) {
							
							 col=row.createCell(colId);
							   col.setCellValue(spiderDataModelTime2.getValue());
							   
							   if(Double.parseDouble(spiderDataModelTime2.getValue())<60)
								   col.setCellStyle(redCellStyle);
								   else if(Double.parseDouble(spiderDataModelTime2.getValue())<80)
									   col.setCellStyle(orangeCellStyle);   
								   else
									   col.setCellStyle(greenCellStyle);
							   sheet.autoSizeColumn(colId);
						}
					}
			   }
			   
			  
				if(spiderDataMap.containsKey((spiderData.getTimePeriod()+"_"+(spiderData.getFxmId()))))
				{
					int j=1;
					
					for(SpiderDataModel spiDataModel:spiderDataMap.get((spiderData.getTimePeriod()+"_"+(spiderData.getFxmId()))))
					{
						
						colId=9;
						   row=sheet.createRow(rowId++);
						   col=row.createCell(colId);
						   col.setCellValue(i+"."+j);
						   col.setCellStyle(centreCellStyle);
						   sheet.autoSizeColumn(colId);
						   colId++;
						   
						   col=row.createCell(colId);
						   col.setCellValue(spiDataModel.getAxis());
						   col.setCellStyle(leftCellStyle);
						   
						   sheet.autoSizeColumn(colId);
						   colId++;
						   col=row.createCell(colId);
						   col.setCellValue(spiDataModel.getValue());
						   
						   if(Double.parseDouble(spiDataModel.getValue())<60)
						   col.setCellStyle(redCellStyle);
						   else if(Double.parseDouble(spiDataModel.getValue())<80)
							   col.setCellStyle(orangeCellStyle);   
						   else
							   col.setCellStyle(greenCellStyle);
						   
						   sheet.autoSizeColumn(colId);
						   colId++;
						   j++;
					}
					}
				i++;
			   
			}
			 row=sheet.createRow(1);
			 
			 col=row.createCell(7);
			 
			   XSSFCellStyle normalCellStyle=workbook.createCellStyle();
			   
			   
			   
			   normalCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   normalCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   normalCellStyle.setFont(font);
			   normalCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			  
			 col.setCellValue("Number of CBO Assessed = "+noOfFacilities);
			 col.setCellStyle(normalCellStyle);
		FileInputStream fileInputStream;
		
		String svg_URI_input = Paths 
				.get(new File(context.getRealPath("")
						+ "\\resources\\spider.svg").getPath()).toUri()
				.toURL().toString();
		String path=createImgFromFile(svg_URI_input);
	
		
		 fileInputStream=new FileInputStream(path);
		 byte[]  imageBytes = IOUtils.toByteArray(fileInputStream);
		 int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);

			
		 CreationHelper   helper = workbook.getCreationHelper();
		 Drawing    drawing = sheet.createDrawingPatriarch();
		 ClientAnchor   anchor = helper.createClientAnchor();

		    anchor.setCol1(0);
			   anchor.setRow1(3);
			   anchor.setCol2(8);
			   anchor.setRow2(22);
			   Picture   pict=  drawing.createPicture(anchor, pictureureIdx);
			  pict.resize(1);
			   
		   String outputPath = messages.getMessage("outputPath", null, null)
					+ area + "_" + cboType +sdf.format(new java.util.Date())+".xlsx";
		   FileOutputStream fileOut = null;
		   fileOut = new FileOutputStream(outputPath);
		   workbook.write(fileOut);
		   fileOut.close();
		// End of image
		return outputPath;
	}

	 public String createImgFromFile(String path) throws Exception{
		 
		 JPEGTranscoder t = new JPEGTranscoder();

		 t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
		
			 
		 TranscoderInput input = new TranscoderInput(path);

		 String fileName = ResourceBundle.getBundle("spring/app").getString("output.filepath");
		 OutputStream ostream = new FileOutputStream(fileName+"/CHART_"+".jpg");
		 TranscoderOutput output = new TranscoderOutput(ostream);

		 t.transcode(input, output);

		 ostream.flush();
		 ostream.close();
		 
		 return fileName+"/CHART_"+".jpg";
	 	}

	@Override  @Transactional(readOnly=true)
	public FacilityPlanningModel getPlannedFacilities(int formId, int areaId,
			int timePeriodId) {
		
		CollectUserModel collectUserModel = (CollectUserModel) stateManager
				.getValue(Constants.USER_PRINCIPAL);
		
		//checking if user is of District Level
		if (areaId == 0
				&& collectUserModel.getUserRoleFeaturePermissionMappings()
						.get(0).getRoleFeaturePermissionSchemeModel()
						.getAreaModel().getAreaLevelId() > 3
				&& collectUserModel.getUserRoleFeaturePermissionMappings()
						.get(0).getRoleFeaturePermissionSchemeModel()
						.getAreaModel().getParentAreaId() != -1) {
			areaId = collectUserModel.getUserRoleFeaturePermissionMappings()
					.get(0).getRoleFeaturePermissionSchemeModel()
					.getAreaModel().getAreaId();
		}
// if areaId is 0 and user is national or state type the areaId will set as areaId of Chhattisgarh
		else if(areaId==0)
		{
			areaId=864;
		}
		
		// facilities planned for a selected area and form type
		FacilityPlanning facilityPlanning=facilityPlanningRepository.findByAreaAreaIdAndXFormFormIdAndTimPeriodTimePeriodId(areaId, formId,timePeriodId);
		FacilityPlanningModel facilityPlanningModel=new FacilityPlanningModel();
		
		
		if(facilityPlanning!=null)
		{
			facilityPlanningModel.setXformId(facilityPlanning.getxForm().getFormId());
			facilityPlanningModel.setStartDate(facilityPlanning.getTimPeriod().getStartDate());
			facilityPlanningModel.setEndDate(facilityPlanning.getTimPeriod().getEndDate());
			facilityPlanningModel.setFacilityPlanned(facilityPlanning.getFacilityPlanned());
			AreaModel areaModel=new AreaModel();
			
			areaModel.setAreaId(facilityPlanning.getArea().getAreaId());
			areaModel.setAreaName(facilityPlanning.getArea().getAreaName());
			areaModel.setParentAreaId(facilityPlanning.getArea().getParentAreaId());
			
			facilityPlanningModel.setAreaModel(areaModel);
		}
		return facilityPlanningModel;
	}

	@Override  @Transactional(readOnly=true)
	public List<TimePeriodModel> getAllPlanningTimePeriod() 
	{
		List<TimePeriodModel> timePeriodModels=new ArrayList<TimePeriodModel>();
		List<TimePeriod> timePeriods=timePeriodRepository.findAll();
		Collections.reverse(timePeriods);
		for(TimePeriod timeperiod:timePeriods)
		{
			TimePeriodModel periodModel=new TimePeriodModel();
			periodModel.setTimePeriod(timeperiod.getTimeperiod());
			periodModel.setTimePeriod_Nid(timeperiod.getTimePeriodId());
			
			timePeriodModels.add(periodModel);
		}
		
		return timePeriodModels;
	}

	@Override
	public Map<String, Integer> getCBOAssessed() {
		List<Object[]> cbosAssesed=facilityScoreRepository.getCBOAssessed();
		Map<String,Integer> cboAessedMap=new LinkedHashMap<String, Integer>();
		
		for(Object[] cboAssessed:cbosAssesed)
		{
			cboAessedMap.put(cboAssessed[1].toString(), Integer.parseInt(cboAssessed[0].toString()));
		}
		return cboAessedMap;
	}
}

