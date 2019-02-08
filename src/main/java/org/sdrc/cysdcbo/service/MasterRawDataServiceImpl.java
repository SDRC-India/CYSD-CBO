/**
 * 
 */
package org.sdrc.cysdcbo.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kxml2.io.KXmlSerializer;
import org.opendatakit.briefcase.model.DocumentDescription;
import org.opendatakit.briefcase.model.ServerConnectionInfo;
import org.opendatakit.briefcase.model.TerminationFuture;
import org.opendatakit.briefcase.util.AggregateUtils;
import org.opendatakit.briefcase.util.WebUtils;
import org.sdrc.cysdcbo.domain.Area;
import org.sdrc.cysdcbo.domain.AreaLevel;
import org.sdrc.cysdcbo.domain.ChoicesDetails;
import org.sdrc.cysdcbo.domain.CollectUser;
import org.sdrc.cysdcbo.domain.FacilityScore;
import org.sdrc.cysdcbo.domain.FormXpathScoreMapping;
import org.sdrc.cysdcbo.domain.LastVisitData;
import org.sdrc.cysdcbo.domain.RawDataScore;
import org.sdrc.cysdcbo.domain.RawFormXapths;
import org.sdrc.cysdcbo.domain.TimePeriod;
import org.sdrc.cysdcbo.domain.XForm;
import org.sdrc.cysdcbo.repository.AreaRepository;
import org.sdrc.cysdcbo.repository.ChoiceDetailsRepository;
import org.sdrc.cysdcbo.repository.FacilityScoreRepository;
import org.sdrc.cysdcbo.repository.FormXpathScoreMappingRepository;
import org.sdrc.cysdcbo.repository.IndicatorFormXpathMappingRepository;
import org.sdrc.cysdcbo.repository.LastVisitDataRepository;
import org.sdrc.cysdcbo.repository.RawDataScoreRepository;
import org.sdrc.cysdcbo.repository.RawFormXapthsRepository;
import org.sdrc.cysdcbo.repository.TimePeriodRepository;
import org.sdrc.cysdcbo.repository.XFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
@SuppressWarnings("deprecation")
@Service
public class MasterRawDataServiceImpl implements MasterRawDataService {
	@Autowired
	private XFormRepository xFormRepository;

	@Autowired
	private RawFormXapthsRepository rawFormXapthsRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private LastVisitDataRepository lastVisitDataRepository;


	@Autowired
	private ChoiceDetailsRepository choiceDetailsRepository;

	@Autowired
	private ResourceBundleMessageSource applicationMessageSource;

	@Autowired
	private FacilityScoreRepository facilityScoreRepository;

	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private RawDataScoreRepository rawDataScoreRepository;

	@Autowired
	private ServletContext context;
	
	@Autowired
	private FormXpathScoreMappingRepository formXpathScoreMappingRepository;
	
	@Autowired
	private IndicatorFormXpathMappingRepository indicatorFormXpathMappingRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
	

	/* (non-Javadoc)
	 * @see org.sdrc.cysdcbo.service.MasterRawDataService#generateXpath()
	 */
	@Override
	public boolean generateXpath() throws Exception {

		List<XForm> xforms = xFormRepository.findAllByIsLiveTrue();

    for(XForm xform : xforms)
    {

		String inputFilePath = "D:/cysdcbo/Excel Sheet/CYSD_CBO_Monitoring_060218_v1.xlsx";
		FileInputStream fileInputStream = new FileInputStream(inputFilePath);
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);

		XSSFSheet sheet = xssfWorkbook.getSheet("survey");

		StringBuilder queryString = new StringBuilder("/submission/data/");
		queryString.append(xform.getxFormId());
		String splittingStr = "";

		{
			for (int node_no = 0; node_no < 1; node_no++) {

				

				XSSFRow headRrow = sheet.getRow(0);
				XSSFCell headerValueCell = headRrow.createCell(node_no + 3);
				headerValueCell.setCellValue("Response_" + (node_no + 1));

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					RawFormXapths rawFormXapths = new RawFormXapths();

					XSSFRow row = sheet.getRow(i);
					if (null != row) {

						XSSFCell typeCell = row.getCell(0);
						XSSFCell nameCell = row.getCell(1);
						if (null != typeCell
								&& !typeCell.getStringCellValue().isEmpty()) {

							if (typeCell.getStringCellValue().equalsIgnoreCase(
									"begin group")) {
								rawFormXapths.setType(typeCell
										.getStringCellValue());
								queryString = queryString.append("/"
										+ nameCell.getStringCellValue());
							} else if (typeCell.getStringCellValue()
									.equalsIgnoreCase("end group")) {
								rawFormXapths.setType(typeCell
										.getStringCellValue());
								splittingStr = queryString.toString()
										.split("/")[queryString.toString()
										.split("/").length - 1];
								queryString = new StringBuilder(
										queryString.substring(
												0,
												queryString.lastIndexOf("/"
														+ splittingStr)));
							}

							if (!(typeCell.getStringCellValue()
									.equalsIgnoreCase("begin group") || typeCell
									.getStringCellValue().equalsIgnoreCase(
											"end group"))) {

								queryString = queryString.append("/"
										+ nameCell.getStringCellValue());

								rawFormXapths.setType(typeCell
										.getStringCellValue());

								rawFormXapths.setForm(xform);
								rawFormXapths.setXpath(queryString.toString());
								if (row.getCell(2) != null) {
									rawFormXapths.setLabel(row.getCell(2)
											.toString());
								}
								rawFormXapthsRepository.save(rawFormXapths);

								splittingStr = queryString.toString()
										.split("/")[queryString.toString()
										.split("/").length - 1];
								queryString = new StringBuilder(
										queryString.substring(
												0,
												queryString.lastIndexOf("/"
														+ splittingStr)));
							}

						}
					}

				}

			}
		}
		XSSFSheet choicesSheet = xssfWorkbook.getSheet("choices");

		for (int i = 1; i <= choicesSheet.getLastRowNum(); i++) {
			XSSFRow row = choicesSheet.getRow(i);

			if (null != row) {
				XSSFCell listNameCell = row.getCell(0);
				XSSFCell nameCell = row.getCell(1);
				XSSFCell labelCell = row.getCell(2);

				if (null != listNameCell && null != labelCell
						&& null != nameCell) {

					ChoicesDetails choicesDetails = new ChoicesDetails();
					String nameVal = nameCell.getCellType() == Cell.CELL_TYPE_STRING ? nameCell
							.getStringCellValue() : Integer
							.toString(((Double) nameCell.getNumericCellValue())
									.intValue());

					String labelVal = labelCell.getCellType() == Cell.CELL_TYPE_STRING ? labelCell
							.getStringCellValue()
							: Integer.toString(((Double) labelCell
									.getNumericCellValue()).intValue());

					choicesDetails.setChoicName(listNameCell
							.getStringCellValue());
					choicesDetails.setLabel(nameVal);
					choicesDetails.setChoiceValue(labelVal);
					choicesDetails.setForm(xform);
					choiceDetailsRepository.save(choicesDetails);

				}

			}

		}
		xssfWorkbook.close();
    }
		return false;
	}

	@Override
	public boolean updateAreaTable() throws Exception {
		
		List<ChoicesDetails> districtChoiceDetails=choiceDetailsRepository.findByChoicName("district");
		
		for(ChoicesDetails districtChoiceDetail:districtChoiceDetails)
		{
			Area area=new Area();
			area.setAreaCode(districtChoiceDetail.getLabel());
			area.setAreaName(districtChoiceDetail.getChoiceValue());
			area.setAreaLevel(new AreaLevel(4));
			area.setCreatedBy("Harsh");
			area.setIsLive(true);
			area.setParentAreaId(2);
			
			Area districtSaved = areaRepository.save(area);
		List<ChoicesDetails> blockChoiceDetails=choiceDetailsRepository.findByChoicNameAndLabelContaining("block","%"+districtChoiceDetail.getLabel()+"%");
		
		for(ChoicesDetails blockChoiceDetail:blockChoiceDetails)
		{
			
			Area blockArea = new Area();
			blockArea.setAreaCode(blockChoiceDetail.getLabel());
			blockArea.setAreaName(blockChoiceDetail.getChoiceValue());
			blockArea.setAreaLevel(new AreaLevel(5));
			blockArea.setCreatedBy("Harsh");
			blockArea.setIsLive(true);
			blockArea.setParentAreaId(districtSaved.getAreaId());
			
			areaRepository.save(blockArea);
		}
		
		Area notApplicableBlockArea=new Area();
			
		notApplicableBlockArea.setAreaCode("Not Applicable");
		notApplicableBlockArea.setAreaName("Not Applicable");
		notApplicableBlockArea.setAreaLevel(new AreaLevel(5));
		notApplicableBlockArea.setCreatedBy("Harsh");
		notApplicableBlockArea.setIsLive(true);
		notApplicableBlockArea.setParentAreaId(districtSaved.getAreaId());
			
		areaRepository.save(notApplicableBlockArea);
		}
		
		return true;
	}

	@Override
	@Transactional
	public boolean getRawDataFromOdk() throws Exception {

		List<Area> areaDetails = areaRepository.findAll();
		
		Map<String, Area> areaMap = new HashMap<String, Area>();

		for (Area area : areaDetails) {
			if(area.getAreaCode()!=null)
			areaMap.put(area.getAreaCode(), area);
		}

		for (Area area : areaDetails) {
			if (area.getAreaLevel().getAreaLevelId() == 4) {
				areaMap.put(area.getParentAreaId() + "_" + area.getAreaName(),
						area);
			}
		}

		List<LastVisitData> lastVisitDatas = lastVisitDataRepository.findAll();
		Map<String, LastVisitData> lastVisitDataMap = new HashMap<String, LastVisitData>();
		for (LastVisitData lastVisitData : lastVisitDatas) {
			lastVisitDataMap.put(lastVisitData.getInstanceId(), lastVisitData);

		}
		List<XForm> xfroms = xFormRepository.findAllByIsLiveTrue();

		for (XForm xform : xfroms) {
			String baseUrl = xform.getOdkServerURL().trim().concat(
					"view/submissionList");
			String serverURL = xform.getOdkServerURL().trim();
			String userName = xform.getUsername();
			String password = xform.getPassword();
			String submission_xml_url = xform.getOdkServerURL().trim().concat(
					"view/downloadSubmission");
			String base_xml_download_url = xform.getOdkServerURL().trim().concat(
					"formXml?formId=");
			String rootElement = xform.getxFormId().trim();

			StringWriter id_list = new StringWriter();
			AggregateUtils.DocumentFetchResult result = null;
			XmlSerializer serializer = new KXmlSerializer();

			String formRooTitle = "";

			StringWriter base_xlsForm = getXML(xform.getxFormId().trim(), serverURL.trim(),
					userName, password, base_xml_download_url);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document core_xml_doc = dBuilder.parse(new InputSource(
					new ByteArrayInputStream(base_xlsForm.toString().getBytes(
							"utf-8"))));
			if (core_xml_doc != null) {
				core_xml_doc.getDocumentElement().normalize();
				Element eElement = (Element) core_xml_doc.getElementsByTagName(
						"group").item(0);
				formRooTitle = eElement.getAttribute("ref").split("/")[1];
			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("formId", xform.getxFormId());
			params.put("cursor", "");
			params.put("numEntries", "");
			String fullUrl = WebUtils.createLinkWithProperties(baseUrl, params);

			ServerConnectionInfo serverInfo = new ServerConnectionInfo(
					serverURL, userName, password.toCharArray());
			DocumentDescription submissionDescription = new DocumentDescription(
					"Fetch of manifest failed. Detailed reason: ",
					"Fetch of manifest failed ", "form manifest",
					new TerminationFuture());
			result = AggregateUtils.getXmlDocument(fullUrl, serverInfo, false,
					submissionDescription, null);
			serializer.setOutput(id_list);
			result.doc.write(serializer);

			Document doc_id_list = dBuilder.parse(new InputSource(
					new ByteArrayInputStream(id_list.toString().getBytes(
							"utf-8"))));

			if (doc_id_list != null) {
				doc_id_list.getDocumentElement().normalize();

				NodeList nodeIdList = doc_id_list.getElementsByTagName("id");
				for (int node_no = 0; node_no < nodeIdList.getLength(); node_no++) {
					String instance_id = nodeIdList.item(node_no)
							.getFirstChild().getNodeValue().trim();
					if (!lastVisitDataMap.containsKey(instance_id)
							|| lastVisitDataMap.get(instance_id)
									.getFacilityScores() == null
							|| lastVisitDataMap.get(instance_id)
									.getFacilityScores().size() < 1
							|| lastVisitDataMap.get(instance_id)
									.getFacilityScores().isEmpty()) 
					{
						String link_formID = generateFormID(xform.getxFormId(),
								formRooTitle, instance_id);
						Map<String, String> submiteParams = new HashMap<String, String>();
						submiteParams.put("formId", link_formID);
						String full_url = WebUtils.createLinkWithProperties(
								submission_xml_url, submiteParams);

						serializer = new KXmlSerializer();
						StringWriter data_writer = new StringWriter();
						result = AggregateUtils.getXmlDocument(full_url,
								serverInfo, false, submissionDescription, null);
						serializer.setOutput(data_writer);
						result.doc.write(serializer);

						Document submission_doc = dBuilder
								.parse(new InputSource(
										new ByteArrayInputStream(data_writer
												.toString().getBytes("utf-8"))));
						XPath xPath = XPathFactory.newInstance().newXPath();
						submission_doc.getDocumentElement().normalize();

						String markedAsCompleteDate = xPath.compile(
								"/submission/data/" + rootElement
										+ "/@markedAsCompleteDate").evaluate(
								submission_doc);

						LastVisitData lvd = new LastVisitData();
						if (!lastVisitDataMap.containsKey(instance_id)) {
							lvd.setMarkedAsCompleteDate(new Timestamp((sdf
									.parse(markedAsCompleteDate)).getTime()));
							lvd.setInstanceId(instance_id);
							String areaCode=null;
							String areaPath = xform.getAreaXPath();
							{
								if(!xPath.compile(
										"/submission/data/" + rootElement
										+ areaPath)
								.evaluate(submission_doc).trim().equalsIgnoreCase(""))
								{
									areaCode=xPath.compile(
											"/submission/data/" + rootElement
													+ areaPath)
											.evaluate(submission_doc);

									{
										Area area = new Area();
										
										String districtName = xPath.compile("/submission/data/" + rootElement+ xform.getDistrictXpath()).evaluate(submission_doc);
										
										Area disArea=areaRepository.findByAreaCode(districtName);
										String blockName = xPath.compile("/submission/data/" + rootElement+ xform.getSecondaryAreaXPath()).evaluate(submission_doc);
										
										String otherBlock = xPath.compile("/submission/data/" + rootElement+ xform.getOtherBlockXpath()).evaluate(submission_doc);
										Area block=areaRepository.findByAreaCodeAndParentAreaId(blockName,disArea.getAreaId());
											
										if(block==null)
										block = areaRepository.findByAreaNameAndParentAreaId(otherBlock,disArea.getAreaId());
										
										if(block==null&&blockName.equalsIgnoreCase("Other"))
										{
											block = new Area();
											block.setParentAreaId(disArea.getAreaId());
											block.setIsLive(true);
											block.setCreatedBy("System");
											block.setAreaName(otherBlock);
											block.setAreaLevel(new AreaLevel(5));
											String areaCode1 = areaRepository
													.findTopOneByParentAreaIdOrderByAreaCodeDesc(
															block.getParentAreaId()).get(0)
													.getAreaCode();
											int areaCodeNumeric =0;
											try
											{
												areaCodeNumeric= Integer.parseInt(areaCode1
													.split("D")[1]) + 1;
											}
											catch (Exception e)
											{
												areaCodeNumeric= Integer.parseInt(areaCode1
														.split("C")[1]) + 1;
											}
											area.setAreaCode("IND" + areaCodeNumeric);
											block = areaRepository.save(block);
											areaMap.put(block.getAreaCode(),block);
										}
										area.setParentAreaId(block.getAreaId());
										String areaCode1 =null;

										List<Area> areaCodes=areaRepository
												.findTopOneByParentAreaIdOrderByAreaCodeDesc(
														area.getParentAreaId());
										if(areaCodes!=null&&areaCodes.size()>0)
										areaCode1= areaCodes.get(0)
												.getAreaCode();
										
										int areaCodeNumeric =0;
										if(areaCode1!=null)
										{
										try
										{
											areaCodeNumeric= Integer.parseInt(areaCode1
												.split("D")[1]) + 1;
										}
										catch (Exception e)
										{
											areaCodeNumeric= Integer.parseInt(areaCode1
													.split("C")[1]) + 1;
										}
										}
										else
										{
											try
											{
												areaCodeNumeric= Integer.parseInt(block.getAreaCode()
													.split("D")[1]) + 1;
											}
											catch (Exception e)
											{
												if(block.getAreaCode().contains("Not Applicable"))
													areaCodeNumeric= Integer.parseInt(disArea.getAreaCode()
															.split("D")[1]) + 001;
												else
												areaCodeNumeric= Integer.parseInt(block.getAreaCode()
														.split("C")[1]) + 1;
											}
										}
										area.setAreaCode("IND" + areaCodeNumeric);
										area.setIsLive(true);
										area.setCreatedBy("System");
										area.setAreaName(areaCode);
										area.setAreaLevel(new AreaLevel(7));
										
										area = areaRepository.save(area);
									
										lvd.setArea(area);
									}
								}
							}
							
							lvd.setLive(true);

							if (!xPath
									.compile(
											"/submission/data/" + rootElement
													+ xform.getLocationXPath())
									.evaluate(submission_doc).trim()
									.equalsIgnoreCase("")) {
								lvd.setLatitude(xPath
										.compile(
												"/submission/data/"
														+ rootElement
														+ xform.getLocationXPath())
										.evaluate(submission_doc).split(" ")[0]);
								lvd.setLongitude(xPath
										.compile(
												"/submission/data/"
														+ rootElement
														+ xform.getLocationXPath())
										.evaluate(submission_doc).split(" ")[1]);
							}
							lvd.setxForm(xform);
							lvd.setSubmissionFileName("");
							lvd.setSubmissionFileURL("");
							lvd.setImageFileNames("");
							lvd.setImageFileNames("");
							CollectUser collectUser = new CollectUser();
							collectUser.setUserId(1);
							lvd.setUser(collectUser);
							TimePeriod timePeriod = new TimePeriod();
							timePeriod.setTimePeriodId(1);
							lvd.setTimPeriod(timePeriod);
							lvd = lastVisitDataRepository.save(lvd);
							
							lastVisitDataMap.put(lvd.getInstanceId(), lvd);

						} else {
							lvd = lastVisitDataMap.get(instance_id);
						}
						String cbo="";
						// for test
						if(instance_id.equals("uuid:393de44a-55b3-4c78-a824-a740c693eca8"))
						{
							System.out.println("done");
						}
						//
						
						switch (Integer.parseInt(xPath
								.compile(
										"/submission/data/"
												+ rootElement 
												+ xform.getCboType())
								.evaluate(submission_doc))
								)
						{
						case 1: if(Integer.parseInt(xPath
								.compile(
										"/submission/data/"
												+ rootElement 
												+ "/bg1_s/q_1.1")
								.evaluate(submission_doc))!=5
								)
								cbo="Village CBO"; 
						else
							cbo="Village CP Committee";
						break;
							
						case 2:	cbo="Intermediary";break;
						}
						for (FormXpathScoreMapping formXpathScoreMapping : xform
								.getFormXpathScoreMappings()) {
							if(!formXpathScoreMapping.getCboType().equalsIgnoreCase(cbo) )
							{
								continue;
							}
							FacilityScore facilityScore = new FacilityScore();
							facilityScore
									.setFormXpathScoreMapping(formXpathScoreMapping);
						 {
								if (xPath
										.compile(
												"/submission/data/"
														+ rootElement
														+ "/"
														+ formXpathScoreMapping
																.getxPath())
										.evaluate(submission_doc).trim()
										.equalsIgnoreCase("")) {

								} else {
								{
									if(formXpathScoreMapping.getType().contains("multiple"))
									{
										facilityScore
										.setScore(Double
												.parseDouble(xPath
												.compile(
														"/submission/data/"
																+ rootElement
																+ "/"
																+ formXpathScoreMapping
																		.getxPath())
												.evaluate(
														submission_doc).trim()!="5"?"1":"0"));
									}
									
									else
									{
										facilityScore
										.setScore(Double
												.parseDouble(xPath
														.compile(
																"/submission/data/"
																		+ rootElement
																		+ "/"
																		+ formXpathScoreMapping
																				.getxPath())
														.evaluate(
																submission_doc)));
									}
									
									}

								}
							}
							facilityScore.setLastVisitData(lvd);
							facilityScoreRepository.save(facilityScore);
						}
						lastVisitDataMap.put(instance_id, lvd);
					}
				}

			}
		}
		return true;}

	private String generateFormID(String getxFormId, String formRooTitle,
			String instance_id) {

		return getxFormId + "[@version=null and @uiVersion=null]/"
				+ formRooTitle + "" + "[@key=" + instance_id + "]";
	}

	private StringWriter getXML(String Form, String serverURL, String userName,
			String password, String base_xml_download_url) throws Exception {
		AggregateUtils.DocumentFetchResult result = null;
		XmlSerializer serializer = new KXmlSerializer();
		StringWriter base_xml = new StringWriter();

		ServerConnectionInfo serverInfo = new ServerConnectionInfo(serverURL,

		userName, password.toCharArray());

		DocumentDescription submissionDescription = new DocumentDescription(
				"Fetch of manifest failed. Detailed reason: ",
				"Fetch of manifest failed ", "form manifest",
				new TerminationFuture());

		result = AggregateUtils.getXmlDocument(
				base_xml_download_url.concat(Form), serverInfo, false,
				submissionDescription, null);
		serializer.setOutput(base_xml);
		result.doc.write(serializer);

		return base_xml;
	}

	@Override
	public boolean uptoQuestionLevel() throws Exception {
		List<XForm> xfroms = xFormRepository.findAllByIsLiveTrue();
		XForm xform=xfroms.get(0);
		int maxId = formXpathScoreMappingRepository.findLastId();
		
		List<String> string = new ArrayList<String>();
		string.add("Village CBO");
		string.add("Intermediary");
		string.add("Village CP Committee");
		for(String type :string)
		{
			Map<String,FormXpathScoreMapping> formMap=new LinkedHashMap<String, FormXpathScoreMapping>();
			List<FormXpathScoreMapping> formXpathScoreMappings = formXpathScoreMappingRepository.findByFormIdAndWithNoChildren(xform.getFormId(),type);
			for(FormXpathScoreMapping formXpathScoreMapping:formXpathScoreMappings)
			{
				formMap.put(formXpathScoreMapping.getxPath().split(",")[0], formXpathScoreMapping);
			}
			List<String> xpathTypes=new ArrayList<String>();
			xpathTypes.add("note");
			xpathTypes.add("select_multiple social_vulnerable");
			xpathTypes.add("select_one yes_no");
			xpathTypes.add("integer");
			xpathTypes.add("select_one category");
			xpathTypes.add("select_one category1");
			xpathTypes.add("select_one gender");
			xpathTypes.add("select_one qualification");
			xpathTypes.add("select_one schedule");
			xpathTypes.add("select_one scheme_prog");
			xpathTypes.add("select_one social_group");
			xpathTypes.add("select_one social_vulnerable");
			xpathTypes.add("select_one times");
			
			
			xpathTypes.add("select_one type");
			xpathTypes.add("select_one type1");
			xpathTypes.add("select_one type10");
			xpathTypes.add("select_one type11");
			xpathTypes.add("select_one type12");
			xpathTypes.add("select_one type2");
			xpathTypes.add("select_one type3");
			xpathTypes.add("select_one type4");
			xpathTypes.add("select_one type5");
			
			xpathTypes.add("select_one type7");
			xpathTypes.add("select_one type8");
			xpathTypes.add("select_one type9");

			List<RawFormXapths> rawFormXapths=rawFormXapthsRepository.findByFormFormIdAndTypeIgnoreCaseIn(xform.getFormId(),xpathTypes);
				
		
			for(RawFormXapths rawXapth :rawFormXapths)
			{
				if(rawXapth.getxPathId()==1)
				continue;
					
				String excelXpath=rawXapth.getXpath().replace("/submission/data/"+xform.getxFormId()+"/", "");
				for (String key:formMap.keySet())
				{
					String modifiedKey = key.replace(key.split("/")[key.split("/").length-1],"");
					if(excelXpath.startsWith(modifiedKey) && !formMap.containsKey(excelXpath))
					{
			
						
						FormXpathScoreMapping formXpathScoreMapping = new FormXpathScoreMapping();
						
						formXpathScoreMapping.setFormXpathScoreId(++maxId);
						formXpathScoreMapping.setForm(xform);
						formXpathScoreMapping.setLabel(rawXapth.getLabel());
						formXpathScoreMapping.setParentXpathId(formMap.get(key).getFormXpathScoreId());
						formXpathScoreMapping.setType(rawXapth.getType());
						formXpathScoreMapping.setxPath(excelXpath);
						formXpathScoreMapping.setCboType(type);
						FormXpathScoreMapping formXpath=formXpathScoreMappingRepository.save(formXpathScoreMapping);
						formMap.put(formXpath.getxPath(), formXpath);
						
						
						break;
					}
				}
			}
			
			  
		}
		
		return false;
		}


	@Override
	public boolean updateXformMapping() throws Exception {
		List<XForm> xforms=xFormRepository.findAllByIsLiveTrue();
		
		XForm xform=xforms.get(0);
		
		List<String> string = new ArrayList<String>();
		string.add("Village CBO");
		string.add("Intermediary");
		string.add("Village CP Committee");
		for(String type :string)
		{
			Map<String,FormXpathScoreMapping> formMap=new LinkedHashMap<String, FormXpathScoreMapping>();
			
			List<FormXpathScoreMapping> formXpathScoreMappings = formXpathScoreMappingRepository.findByFormIdAndWithNoChildren(xform.getFormId(),type);
			
			for(FormXpathScoreMapping formXpathScoreMapping:formXpathScoreMappings)
			{
				System.out.println(formXpathScoreMapping.getLabel().split(Pattern.quote(".")).toString());
				formMap.put(formXpathScoreMapping.getLabel().replace(formXpathScoreMapping.getLabel().split(Pattern.quote("."))[formXpathScoreMapping.getLabel().split(Pattern.quote(".")).length-1],""), formXpathScoreMapping);
			}
			
			List<String> strings = new ArrayList<String>();
			for (String key:formMap.keySet())
			{
				if(!strings.contains(key))
				{
				for (String key1:formMap.keySet())
				{
				if((key1.startsWith(key))&& !key1.equalsIgnoreCase(key))
				{
					FormXpathScoreMapping formXpathScoreMapping = formMap.get(key1);
					formXpathScoreMapping.setParentXpathId(formMap.get(key).getFormXpathScoreId());
					formXpathScoreMappingRepository.save(formXpathScoreMapping);
					strings.add(key1);
				}
				}
				}
			}
			
		}
		return false;
	}

	@Override
	public boolean formatGeoCordinates() throws Exception {
		XSSFWorkbook geoJson = new XSSFWorkbook("D://jsonFormat.xlsx");
		
		XSSFSheet sheet=geoJson.getSheetAt(1);

		PrintWriter out = new PrintWriter("D://odishaJson.txt");
		PrintWriter out1 = new PrintWriter("D://odishaJson1.txt");
		
		for(int i=0;i<sheet.getLastRowNum();i++)
			{
			
			
			XSSFRow rows = sheet.getRow(i);
			
			
			XSSFCell cell=rows.getCell(0);
			if(cell==null || !cell.getStringCellValue().trim().startsWith("["))
			{
				continue;
			}			
			String cellValue= cell.getStringCellValue().replaceAll("[\\[\\]]", "").replaceAll("\\]", "");
			
					try
					{
						out1.println(cellValue);
						out.println("{longitude : "+cellValue.split(",")[0]+", latitude : "+cellValue.split(",")[1]+"},");	
					System.out.println("{longitude : "+cellValue.split(",")[0]+", latitude : "+cellValue.split(",")[1]+"},");
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
		out.close();
		out1.close();
		geoJson.close();
		
		return false;
	}

	@Override
	@Transactional
	public String generateExcel() throws Exception {
		
		Map<Integer,Area> areaMap=new HashMap<Integer, Area>();
		
		for(Area area:areaRepository.findAll())
		areaMap.put(area.getAreaId(), area);
		
		List<ChoicesDetails> choicesDetailsList = choiceDetailsRepository
				.findAll();

		List<RawFormXapths> formXapths = rawFormXapthsRepository.findAll();
		List<TimePeriod> timePeriods = timePeriodRepository
				.findByOrderByStartDateDesc();

		Map<Integer, TimePeriod> timePeriodMap = new HashMap<Integer, TimePeriod>();
		for (TimePeriod timePeriod : timePeriods) {
			timePeriodMap.put(timePeriod.getTimePeriodId(), timePeriod);
		}
		Map<String, ChoicesDetails> choicesMap = new HashMap<String, ChoicesDetails>();
		for (ChoicesDetails choicesDetails : choicesDetailsList) {
			choicesMap.put("select_one " + choicesDetails.getChoicName() + "_"
					+ choicesDetails.getLabel(), choicesDetails);
		}


		XSSFWorkbook rawDataWorkBook = new XSSFWorkbook();
		XSSFCell headerCell=null;
		XSSFRow headerRow = null;
		XSSFRow xpathRow = null;
		XSSFFont font =null;
		XSSFSheet rawDataSheet=null;
		

		XSSFCellStyle style = rawDataWorkBook.createCellStyle();
		style.setFont(font);

		style.setAlignment(CellStyle.ALIGN_GENERAL);
		style.setVerticalAlignment(CellStyle.ALIGN_GENERAL);

		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setWrapText(true);
		
		
		
		XSSFCellStyle scoreStyle = rawDataWorkBook.createCellStyle();
		scoreStyle.setFont(font);

		scoreStyle.setAlignment(CellStyle.ALIGN_GENERAL);
		scoreStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
		scoreStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		scoreStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);

		scoreStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		scoreStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		scoreStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		scoreStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		scoreStyle.setWrapText(true);
		
		
		int i;
		
		Map<String, Integer> xpathMap = new HashMap<String, Integer>();
		
		for(TimePeriod timePeriod:timePeriods)
		{
		 rawDataSheet = rawDataWorkBook.createSheet(timePeriod.getTimeperiod());

		 headerRow = rawDataSheet.createRow(0);
		 xpathRow = rawDataSheet.createRow(1);
		 font = rawDataWorkBook.createFont();
		font.setColor(HSSFColor.BLACK.index);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

		 headerCell = headerRow.createCell(0);
		headerCell.setCellValue("Serial Number");
		headerCell.setCellStyle(style);
//		rawDataSheet.autoSizeColumn(headerCell.getColumnIndex());
		rawDataSheet.setColumnWidth(headerCell.getColumnIndex(),8630);
		 i = 1;
		for (RawFormXapths rawFormXapth : formXapths) {
			if (rawFormXapth.getLabel()!=null&&!rawFormXapth.getLabel().trim().equalsIgnoreCase(""))
			{
			headerCell = headerRow.createCell(i);
			XSSFCell xpathCell = xpathRow.createCell(i);
			headerCell.setCellValue(rawFormXapth.getLabel());
			if(rawFormXapth.getLabel().startsWith("Score Secured")||rawFormXapth.getLabel().startsWith("Overall Score"))
			headerCell.setCellStyle(scoreStyle);
			else	
			headerCell.setCellStyle(style);
			rawDataSheet.autoSizeColumn(headerCell.getColumnIndex());
				

			xpathCell.setCellValue(rawFormXapth.getXpath());
			xpathMap.put(rawFormXapth.getXpath(), xpathCell.getColumnIndex());

			i++;
		}
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue("Date Of Submission");
			headerCell.setCellStyle(style);
			rawDataSheet.autoSizeColumn(headerCell.getColumnIndex());
		}

		XSSFRow valueRow;
		int rowIndex = 2;
		int responseNo = 1;
		XSSFCellStyle colStyle = rawDataWorkBook.createCellStyle();
		colStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		colStyle.setWrapText(true);
		for (LastVisitData lastVisitData : timePeriod.getLastVisitDatas()) {
			if (lastVisitData.isLive()&&(lastVisitData.getRawDataScore() != null
					|| lastVisitData.getRawDataScore().size() > 0)) {
					
				valueRow = rawDataSheet.createRow(rowIndex);
				XSSFCell responseCell = valueRow.createCell(0);
				responseCell.setCellValue(responseNo);
				responseCell.setCellStyle(colStyle);
				rawDataSheet.setColumnWidth(responseCell.getColumnIndex(),8630);
//				rawDataSheet.autoSizeColumn(responseCell.getColumnIndex());
				Map<String,RawDataScore> rawDataScoreMap=new HashMap<String, RawDataScore>();
				for (RawDataScore rawDataScore : lastVisitData
						.getRawDataScore()) {
					rawDataScoreMap.put(rawDataScore.getRawFormXapths().getXpath(),rawDataScore);

				}

				for (RawDataScore rawDataScore : lastVisitData
						.getRawDataScore()) {
				
					if(xpathMap.containsKey(rawDataScore.getRawFormXapths().getXpath()))
					{
					{
				responseCell = valueRow.createCell(xpathMap
							.get(rawDataScore.getRawFormXapths().getXpath()));
				
				if(rawDataScore.getRawFormXapths().getXpath().trim().endsWith(rawDataScore.getRawFormXapths().getForm().getAreaXPath().trim())
				  ||rawDataScore.getRawFormXapths().getXpath().trim().endsWith(rawDataScore.getRawFormXapths().getForm().getSecondaryAreaXPath().trim())
				  ||rawDataScore.getRawFormXapths().getXpath().trim().endsWith(rawDataScore.getRawFormXapths().getForm().getDistrictXpath().trim()))
				{
					
					if(rawDataScore.getRawFormXapths().getXpath().trim().endsWith(rawDataScore.getRawFormXapths().getForm().getAreaXPath().trim()))
					{
						responseCell.setCellValue(lastVisitData.getArea().getAreaName());
					}
						
					else if(rawDataScore.getRawFormXapths().getXpath().trim().endsWith(rawDataScore.getRawFormXapths().getForm().getSecondaryAreaXPath().trim()))
					{
						responseCell.setCellValue(areaMap.get(lastVisitData.getArea().getParentAreaId()).getAreaName());
					}
						
					else
					{
						responseCell.setCellValue(areaMap.get(areaMap.get(lastVisitData.getArea().getParentAreaId()).getParentAreaId()).getAreaName());
					}
						
					
				}
				
				else if (rawDataScore.getRawFormXapths().getType()
							.contains("select_one")) {
						try {
							Integer.parseInt(rawDataScore.getScore());
							responseCell.setCellValue(choicesMap.get(
									rawDataScore.getRawFormXapths().getType()
											+ "_" + rawDataScore.getScore())
									.getChoiceValue());

						} catch (Exception e) {
							responseCell.setCellValue(rawDataScore.getScore()==null?"":rawDataScore.getScore());
						}

					}
				
				else if (rawDataScore.getRawFormXapths().getType()
						.contains("select_multiple")) {
					try {
						
						String multipleAnsRespone=null;
						for(String response:Arrays.asList(rawDataScore.getScore().trim().split(" ")))
						{
							if(multipleAnsRespone==null)
							{
								String type=rawDataScore.getRawFormXapths().getType().replace("select_multiple ", "select_one ");
								multipleAnsRespone=choicesMap.get(
										type+ "_" + response)
										.getChoiceValue();	
							}
							else	
							{
								String type=rawDataScore.getRawFormXapths().getType().replace("select_multiple ", "select_one ");
							multipleAnsRespone = multipleAnsRespone + ","+choicesMap.get(
									type
											+ "_" + response)
									.getChoiceValue();
							
							}

						}
						responseCell.setCellValue(multipleAnsRespone);
						
						
					} catch (Exception e) {
						responseCell.setCellValue(rawDataScore.getScore()==null?"":rawDataScore.getScore());
					}

				}
				
				else {
						if(rawDataScore.getRawFormXapths().getCalXpaths()==null)
						{
							
							if(rawDataScore.getRawFormXapths().getType().equalsIgnoreCase("image") && rawDataScore.getScore()!=null && !rawDataScore.getScore().trim().equalsIgnoreCase(""))
							{
								String url="http://prod1.sdrc.co.in:8080/CBOM/view/binaryData?blobKey=CYSD_CBO_Monitoring_060218_v1%5B%40version%3Dnull+and+%40uiVersion%3Dnull%5D%2FCYSD_CBO_Monitoring_060218_v1%5B%40key%3D";
								String instanceId=rawDataScore.getLastVisitData().getInstanceId().replace(":","%3A");
								url+=instanceId+"%5D%2F"+rawDataScore.getRawFormXapths().getXpath().split("/")[rawDataScore.getRawFormXapths().getXpath().split("/").length-1];
								responseCell.setCellValue(url);
								CreationHelper creationHelper = rawDataWorkBook.getCreationHelper();
								XSSFHyperlink link = (XSSFHyperlink) creationHelper
										.createHyperlink(Hyperlink.LINK_URL);
								link.setAddress(url);
								responseCell.setHyperlink(link);
							}
							else if( rawDataScore.getRawFormXapths().getxPathId()==206 && !lastVisitData.getFacilityScores().get(0).getFormXpathScoreMapping().getCboType().equalsIgnoreCase("Village CP Committee"))
								{
									responseCell.setCellValue(rawDataScoreMap.get(rawDataScore.getRawFormXapths().getXpath().trim()+"1").getScore());
									
										
								}	
							else
							{	
						responseCell.setCellValue( rawDataScore.getScore()==null?"":rawDataScore.getScore());
							}
							}
						else
						{
							responseCell.setCellValue(rawDataScoreMap.get(rawDataScore.getRawFormXapths().getCalXpaths()).getScore());
						}
					}
					responseCell.setCellStyle(colStyle);
					}
					
					}
				}
				
				responseCell = valueRow.createCell(headerRow.getLastCellNum()-1);
				responseCell.setCellValue(sdf.format(lastVisitData.getMarkedAsCompleteDate()));
				responseCell.setCellStyle(colStyle);
				rawDataSheet.autoSizeColumn(responseCell.getColumnIndex());
				
				rowIndex++;
				responseNo++;
			}
			
		}

		removeRow(rawDataSheet, xpathRow.getRowNum());
		}
		String outputPath = context.getRealPath("resources\\"
				+applicationMessageSource.getMessage("rawDataOuputPath", null,
						null));
		FileOutputStream fileOut = null;
		fileOut = new FileOutputStream(outputPath);
		rawDataWorkBook.write(fileOut);
		fileOut.close();
		rawDataWorkBook.close();
		return outputPath;
	}

	private static void removeRow(XSSFSheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			XSSFRow removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}

	@Override
	@Transactional
	public boolean persistRawData() throws Exception {

		List<Area> areaDetails = areaRepository.findAll();
		
		Map<String, Area> areaMap = new HashMap<String, Area>();

		for (Area area : areaDetails) {
			if(area.getAreaCode()!=null)
			areaMap.put(area.getAreaCode(), area);
		}

		for (Area area : areaDetails) {
			if (area.getAreaLevel().getAreaLevelId() == 4) {
				areaMap.put(area.getParentAreaId() + "_" + area.getAreaName(),
						area);
			}
		}

		List<LastVisitData> lastVisitDatas = lastVisitDataRepository.findAll();
		Map<String, LastVisitData> lastVisitDataMap = new HashMap<String, LastVisitData>();
		for (LastVisitData lastVisitData : lastVisitDatas) {
			lastVisitDataMap.put(lastVisitData.getInstanceId(), lastVisitData);

		}
		List<XForm> xfroms = xFormRepository.findAllByIsLiveTrue();

		for (XForm xform : xfroms) {
			String baseUrl = xform.getOdkServerURL().trim().concat(
					"view/submissionList");
			String serverURL = xform.getOdkServerURL().trim();
			String userName = xform.getUsername();
			String password = xform.getPassword();
			String submission_xml_url = xform.getOdkServerURL().trim().concat(
					"view/downloadSubmission");
			String base_xml_download_url = xform.getOdkServerURL().trim().concat(
					"formXml?formId=");
			String rootElement = xform.getxFormId().trim();

			StringWriter id_list = new StringWriter();
			AggregateUtils.DocumentFetchResult result = null;
			XmlSerializer serializer = new KXmlSerializer();

			String formRooTitle = "";

			StringWriter base_xlsForm = getXML(xform.getxFormId().trim(), serverURL.trim(),
					userName, password, base_xml_download_url);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document core_xml_doc = dBuilder.parse(new InputSource(
					new ByteArrayInputStream(base_xlsForm.toString().getBytes(
							"utf-8"))));
			if (core_xml_doc != null) {
				core_xml_doc.getDocumentElement().normalize();
				Element eElement = (Element) core_xml_doc.getElementsByTagName(
						"group").item(0);
				formRooTitle = eElement.getAttribute("ref").split("/")[1];
			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("formId", xform.getxFormId());
			params.put("cursor", "");
			params.put("numEntries", "");
			String fullUrl = WebUtils.createLinkWithProperties(baseUrl, params);

			ServerConnectionInfo serverInfo = new ServerConnectionInfo(
					serverURL, userName, password.toCharArray());
			DocumentDescription submissionDescription = new DocumentDescription(
					"Fetch of manifest failed. Detailed reason: ",
					"Fetch of manifest failed ", "form manifest",
					new TerminationFuture());
			result = AggregateUtils.getXmlDocument(fullUrl, serverInfo, false,
					submissionDescription, null);
			serializer.setOutput(id_list);
			result.doc.write(serializer);

			Document doc_id_list = dBuilder.parse(new InputSource(
					new ByteArrayInputStream(id_list.toString().getBytes(
							"utf-8"))));

			if (doc_id_list != null) {
				doc_id_list.getDocumentElement().normalize();

				NodeList nodeIdList = doc_id_list.getElementsByTagName("id");
				for (int node_no = 0; node_no < nodeIdList.getLength(); node_no++) {
					String instance_id = nodeIdList.item(node_no)
							.getFirstChild().getNodeValue().trim();
					if (!lastVisitDataMap.containsKey(instance_id)
							|| lastVisitDataMap.get(instance_id)
									.getRawDataScore() == null
							|| lastVisitDataMap.get(instance_id)
									.getRawDataScore().size() < 1
							|| lastVisitDataMap.get(instance_id)
									.getRawDataScore().isEmpty()) {
						String link_formID = generateFormID(xform.getxFormId(),
								formRooTitle, instance_id);
						Map<String, String> submiteParams = new HashMap<String, String>();
						submiteParams.put("formId", link_formID);
						String full_url = WebUtils.createLinkWithProperties(
								submission_xml_url, submiteParams);

						serializer = new KXmlSerializer();
						StringWriter data_writer = new StringWriter();
						result = AggregateUtils.getXmlDocument(full_url,
								serverInfo, false, submissionDescription, null);
						serializer.setOutput(data_writer);
						result.doc.write(serializer);

						Document submission_doc = dBuilder
								.parse(new InputSource(
										new ByteArrayInputStream(data_writer
												.toString().getBytes("utf-8"))));
						XPath xPath = XPathFactory.newInstance().newXPath();
						submission_doc.getDocumentElement().normalize();

						String markedAsCompleteDate = xPath.compile(
								"/submission/data/" + rootElement
										+ "/@markedAsCompleteDate").evaluate(
								submission_doc);

						LastVisitData lvd = new LastVisitData();
						if (!lastVisitDataMap.containsKey(instance_id)) {
							lvd.setMarkedAsCompleteDate(new Timestamp((sdf
									.parse(markedAsCompleteDate)).getTime()));
							lvd.setInstanceId(instance_id);
							String areaCode=null;
							String areaPath = xform.getAreaXPath();
							{
								if(!xPath.compile(
										"/submission/data/" + rootElement
										+ areaPath)
								.evaluate(submission_doc).trim().equalsIgnoreCase(""))
								{
									areaCode=xPath.compile(
											"/submission/data/" + rootElement
													+ areaPath)
											.evaluate(submission_doc);

									{
										Area area = new Area();
										
										String districtName = xPath.compile("/submission/data/" + rootElement+ xform.getDistrictXpath()).evaluate(submission_doc);
										
										Area disArea=areaRepository.findByAreaCode(districtName);
										String blockName = xPath.compile("/submission/data/" + rootElement+ xform.getSecondaryAreaXPath()).evaluate(submission_doc);
										
										String otherBlock = xPath.compile("/submission/data/" + rootElement+ xform.getOtherBlockXpath()).evaluate(submission_doc);
										Area block=areaRepository.findByAreaCodeAndParentAreaId(blockName,disArea.getAreaId());
											
										if(block==null)
										block = areaRepository.findByAreaNameAndParentAreaId(otherBlock,disArea.getAreaId());
										
										if(block==null&&blockName.equalsIgnoreCase("Other"))
										{
											block = new Area();
											block.setParentAreaId(disArea.getAreaId());
											block.setIsLive(true);
											block.setCreatedBy("System");
											block.setAreaName(otherBlock);
											block.setAreaLevel(new AreaLevel(5));
											String areaCode1 = areaRepository
													.findTopOneByParentAreaIdOrderByAreaCodeDesc(
															block.getParentAreaId()).get(0)
													.getAreaCode();
											int areaCodeNumeric =0;
											try
											{
												areaCodeNumeric= Integer.parseInt(areaCode1
													.split("D")[1]) + 1;
											}
											catch (Exception e)
											{
												
												areaCodeNumeric= Integer.parseInt(areaCode1
														.split("C")[1]) + 1;
											}
											area.setAreaCode("IND" + areaCodeNumeric);
											block = areaRepository.save(block);
											areaMap.put(block.getAreaCode(),block);
										}
										area.setParentAreaId(block.getAreaId());
										String areaCode1 =null;

										List<Area> areaCodes=areaRepository
												.findTopOneByParentAreaIdOrderByAreaCodeDesc(
														area.getParentAreaId());
										if(areaCodes!=null&&areaCodes.size()>0)
										areaCode1= areaCodes.get(0)
												.getAreaCode();
										
										int areaCodeNumeric =0;
										if(areaCode1!=null)
										{
										try
										{
											areaCodeNumeric= Integer.parseInt(areaCode1
												.split("D")[1]) + 1;
										}
										catch (Exception e)
										{
											areaCodeNumeric= Integer.parseInt(areaCode1
													.split("C")[1]) + 1;
										}
										}
										else
										{
											try
											{
												areaCodeNumeric= Integer.parseInt(block.getAreaCode()
													.split("D")[1]) + 1;
											}
											catch (Exception e)
											{
												if(block.getAreaCode().contains("Not Applicable"))
													areaCodeNumeric= Integer.parseInt(disArea.getAreaCode()
															.split("D")[1]) + 001;
												areaCodeNumeric= Integer.parseInt(block.getAreaCode()
														.split("C")[1]) + 1;
											}
										}
										area.setAreaCode("IND" + areaCodeNumeric);
										area.setIsLive(true);
										area.setCreatedBy("System");
										area.setAreaName(areaCode);
										area.setAreaLevel(new AreaLevel(7));
										
										area = areaRepository.save(area);
									
										lvd.setArea(area);
									}
								}
							}
							
							lvd.setLive(true);
	

							if (!xPath
									.compile(
											"/submission/data/" + rootElement
													+ xform.getLocationXPath())
									.evaluate(submission_doc).trim()
									.equalsIgnoreCase("")) {
								lvd.setLatitude(xPath
										.compile(
												"/submission/data/"
														+ rootElement
														+ xform.getLocationXPath())
										.evaluate(submission_doc).split(" ")[0]);
								lvd.setLongitude(xPath
										.compile(
												"/submission/data/"
														+ rootElement
														+ xform.getLocationXPath())
										.evaluate(submission_doc).split(" ")[1]);
							}
							lvd.setxForm(xform);

							lvd.setSubmissionFileName("");
							lvd.setSubmissionFileURL("");
							lvd.setImageFileNames("");
							lvd.setImageFileNames("");
							CollectUser collectUser = new CollectUser();
							collectUser.setUserId(1);
							lvd.setUser(collectUser);
							TimePeriod timePeriod = new TimePeriod();
							timePeriod.setTimePeriodId(1);
							lvd.setTimPeriod(timePeriod);

							lvd = lastVisitDataRepository.save(lvd);
							
							lastVisitDataMap.put(lvd.getInstanceId(), lvd);

						} else {
							lvd = lastVisitDataMap.get(instance_id);
						}

						for (RawFormXapths rawFormXapths : xform
								.getRawXpaths()) {

							RawDataScore rawDataScore = new RawDataScore();
							rawDataScore.setRawFormXapths(rawFormXapths);
									
						 {
								if (xPath
										.compile(rawFormXapths.getXpath())
										.evaluate(submission_doc).trim()
										.equalsIgnoreCase("")) {

								} else {
								{
										
									rawDataScore
												.setScore((xPath
																.compile(rawFormXapths.getXpath())
																.evaluate(
																		submission_doc)));
									}

								}
							}
						 rawDataScore.setLastVisitData(lvd);
							rawDataScoreRepository.save(rawDataScore);
						}
						lastVisitDataMap.put(instance_id, lvd);
					}
				}

			}
		}
		return true;
		}

	@Override
	@Scheduled(cron="0 1 0 1/1 * ?")
	@Transactional
	public void updateDatasFacilityScoreScheduler() throws Exception {
		getRawDataFromOdk();
	}
	
	
	@Override
	@Scheduled(cron="0 5 0 1/1 * ?")
	@Transactional
	public void updateDatasRawDatasScheduler() throws Exception {
		persistRawData();
	}
	
	@Override
	@Scheduled(cron="0 12 0 1/1 * ?")
	@Transactional
	public void updateDatasExcelScheduler() throws Exception {
		generateExcel();
	}
	
	@Transactional
	@Override
	public String getSubmissionReport() throws Exception
	{
		String excel=context.getRealPath("resources\\"
				+applicationMessageSource.getMessage("rawDataOuputPath", null,
						null));
		
		List<Area> districts =areaRepository.findByAreaLevelAreaLevelId(4);
		
		XSSFWorkbook reportWorbook=new XSSFWorkbook();
		XSSFSheet reportSheet = reportWorbook.createSheet("District Report");
		
		XSSFWorkbook workbook=new XSSFWorkbook(excel);
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		
			XSSFCellStyle headCellStyle=reportWorbook.createCellStyle();
		   
		   XSSFFont headFont = reportWorbook.createFont();
		   headFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		   headFont.setColor(HSSFColor.BLACK.index);
		   headFont.setFontHeight(11);
		   
		   headCellStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		   headCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		   headCellStyle.setFont(headFont);
		   headCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		   headCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		   headCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		   headCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		   headCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
		   
		   
		   
			XSSFCellStyle headerCellStyle=reportWorbook.createCellStyle();
			   
			   XSSFFont headerFont = reportWorbook.createFont();
			   headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			   headerFont.setColor(HSSFColor.BLACK.index);
			   headFont.setFontHeight(11);
			   
			   headerCellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			   headerCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   headerCellStyle.setFont(headerFont);
			   headerCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			   headerCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   headerCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   headerCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   headerCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   
			   
			   XSSFFont font = reportWorbook.createFont();
			   font.setFontHeight(11);
			   
			   
			   
			   XSSFCellStyle leftCellStyle=reportWorbook.createCellStyle();
			   leftCellStyle.setFont(font);
			   leftCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   leftCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   leftCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   leftCellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			   
			   
			   XSSFCellStyle rightCellStyle=reportWorbook.createCellStyle();
			   rightCellStyle.setFont(font);
			   rightCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   rightCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   rightCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   rightCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   rightCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   rightCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   rightCellStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
			   
			   
			   
			   XSSFCellStyle boldleftCellStyle=reportWorbook.createCellStyle();
			   boldleftCellStyle.setFont(headerFont);
			   boldleftCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   boldleftCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   boldleftCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   boldleftCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   boldleftCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   boldleftCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   boldleftCellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			   
			   
			   XSSFCellStyle boldrightCellStyle=reportWorbook.createCellStyle();
			   boldrightCellStyle.setFont(headerFont);
			   boldrightCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			   boldrightCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			   boldrightCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
			   boldrightCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			   boldrightCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
			   boldrightCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
			   boldrightCellStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
			   
			   
		   
		
		Map<String,Map<String,Integer>> dataMap=new LinkedHashMap<String, Map<String,Integer>>();
		Map<String,Map<String,Integer>> blockDataMap=new LinkedHashMap<String, Map<String,Integer>>();
		
		for(int i=1;i<=sheet.getLastRowNum();i++)
		{
			Row row=sheet.getRow(i);
			
			Cell district=row.getCell(2);
			
			Cell cboType=row.getCell(3);
			
			Cell primary=row.getCell(4);
			
			Cell interdemiary=row.getCell(7);
			
			Cell block=row.getCell(16);
			
						/*=== for district wise ===*/
			if(dataMap.containsKey(district.getStringCellValue()))
			{
				if(dataMap.get(district.getStringCellValue()).containsKey(cboType.getStringCellValue()+"_"+
				(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
				 interdemiary.getStringCellValue():primary.getStringCellValue())))
				{
					int count=dataMap.get(district.getStringCellValue()).get(cboType.getStringCellValue()+"_"+
							(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
							 interdemiary.getStringCellValue():primary.getStringCellValue()))+1;
						
					dataMap.get(district.getStringCellValue()).put(cboType.getStringCellValue()+"_"+
							(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
									 interdemiary.getStringCellValue():primary.getStringCellValue()), count);
				}
				
				else
				{
					dataMap.get(district.getStringCellValue()).put(cboType.getStringCellValue()+"_"+
							(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
									 interdemiary.getStringCellValue():primary.getStringCellValue()), 1);
				}
			}
			else
			{
				Map<String,Integer> tempMap=new HashMap<>();
				tempMap.put(cboType.getStringCellValue()+"_"+
				(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
				 interdemiary.getStringCellValue():primary.getStringCellValue()), 1);
				
				dataMap.put(district.getStringCellValue(),tempMap);
			}
			
									/*=== For block wise ===*/
			
			if(blockDataMap.containsKey(district.getStringCellValue()+"_"+block.getStringCellValue()))
			{
				if(blockDataMap.get(district.getStringCellValue()+"_"+block.getStringCellValue()).containsKey(cboType.getStringCellValue()+"_"+
				(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
				 interdemiary.getStringCellValue():primary.getStringCellValue())))
				{
					int count=blockDataMap.get(district.getStringCellValue()+"_"+block.getStringCellValue()).get(cboType.getStringCellValue()+"_"+
							(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
							 interdemiary.getStringCellValue():primary.getStringCellValue()))+1;
						
					blockDataMap.get(district.getStringCellValue()+"_"+block.getStringCellValue()).put(cboType.getStringCellValue()+"_"+
							(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
									 interdemiary.getStringCellValue():primary.getStringCellValue()), count);
				}
				
				else
				{
					blockDataMap.get(district.getStringCellValue()+"_"+block.getStringCellValue()).put(cboType.getStringCellValue()+"_"+
							(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
									 interdemiary.getStringCellValue():primary.getStringCellValue()), 1);
				}
			}
			else
			{
				Map<String,Integer> tempMap=new HashMap<>();
				tempMap.put(cboType.getStringCellValue()+"_"+
				(primary.getCellType()==Cell.CELL_TYPE_BLANK||primary.getStringCellValue().trim().equalsIgnoreCase("")?
				 interdemiary.getStringCellValue():primary.getStringCellValue()), 1);
				
				blockDataMap.put(district.getStringCellValue()+"_"+block.getStringCellValue(),tempMap);
			}
			
			
				
		}
		
		int i=0;
		int colId=0;
		Row dateRow=reportSheet.createRow(i);
		
		dateRow.createCell(colId).setCellValue("Date of Report Generation: "+sdf2.format(new java.util.Date()));
		
		i=4;
		colId=0;
		Row headerRow=reportSheet.createRow(i++);
		Cell districtCol=headerRow.createCell(colId++);
		districtCol.setCellStyle(headCellStyle);
		Cell countCell=headerRow.createCell(colId++);
		countCell.setCellStyle(headCellStyle);
		
		districtCol.setCellValue("District");
		reportSheet.autoSizeColumn(districtCol.getColumnIndex());
		
		countCell.setCellValue("Number Of Submission");
		reportSheet.autoSizeColumn(countCell.getColumnIndex());
		
		Row districtRow = null;
		
		 int total=0;
		
		for(Area area:districts)
		{
			colId=0;
			districtRow=reportSheet.createRow(i++);
			 districtCol=districtRow.createCell(colId++);
			 
			 countCell=districtRow.createCell(colId++);

			
			districtCol.setCellValue(area.getAreaName());
			districtCol.setCellStyle(leftCellStyle);
			int count=0;
			if(dataMap.containsKey(area.getAreaName()))
			{
				for(String key:dataMap.get(area.getAreaName()).keySet())
				{
					count+=dataMap.get(area.getAreaName()).get(key);
					total+=dataMap.get(area.getAreaName()).get(key);
				}
				
			}
			
				countCell.setCellValue(count);
				countCell.setCellStyle(rightCellStyle);
				reportSheet.autoSizeColumn(districtCol.getColumnIndex());
				reportSheet.autoSizeColumn(countCell.getColumnIndex());
			
		}
		
		colId=0;
		districtRow=reportSheet.createRow(i++);
		 districtCol=districtRow.createCell(colId++);
		 countCell=districtRow.createCell(colId++);
		 districtCol.setCellValue("Total");
		 countCell.setCellValue(total);
		 districtCol.setCellStyle(boldleftCellStyle);
		 countCell.setCellStyle(boldrightCellStyle);
		 reportSheet.autoSizeColumn(districtCol.getColumnIndex());
		 reportSheet.autoSizeColumn(countCell.getColumnIndex());
		
		i+=3;
		
		Row submissionHeaderRow=reportSheet.createRow(i);
		
		colId=0;
		Cell subMissCell=submissionHeaderRow.createCell(colId++);
		subMissCell.setCellValue("Submission Details ");
		
		
		reportSheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
		subMissCell.setCellStyle(headerCellStyle);
		reportSheet.autoSizeColumn(subMissCell.getColumnIndex());
		
		
		i++;
		submissionHeaderRow=reportSheet.createRow(i++);
		
		colId=0;
		Cell districtCell=submissionHeaderRow.createCell(colId++);
		districtCell.setCellValue("District");
		districtCell.setCellStyle(headCellStyle);
		reportSheet.autoSizeColumn(districtCell.getColumnIndex());
		
		
		Cell cboCell=submissionHeaderRow.createCell(colId++);
		cboCell.setCellValue("CBO Type");
		cboCell.setCellStyle(headCellStyle);
		reportSheet.autoSizeColumn(cboCell.getColumnIndex());
		
		Cell submissionCell=submissionHeaderRow.createCell(colId++);
		submissionCell.setCellValue("Number Of Submission");
		submissionCell.setCellStyle(headCellStyle);
		reportSheet.autoSizeColumn(submissionCell.getColumnIndex());
		
		
		for(String key:dataMap.keySet())
		{
			
			for(String type:dataMap.get(key).keySet())
			{
				colId=0;
				Row submissionDataRow=reportSheet.createRow(i++);
				districtCell=submissionDataRow.createCell(colId++);
				districtCell.setCellValue(key);
				districtCell.setCellStyle(leftCellStyle);
				
				 cboCell=submissionDataRow.createCell(colId++);
				cboCell.setCellValue(type.replace("_", " - "));
				cboCell.setCellStyle(leftCellStyle);
				
				 submissionCell=submissionDataRow.createCell(colId++);
				submissionCell.setCellValue(dataMap.get(key).get(type));
				submissionCell.setCellStyle(rightCellStyle);
				reportSheet.autoSizeColumn(districtCell.getColumnIndex());
				reportSheet.autoSizeColumn(cboCell.getColumnIndex());
				reportSheet.autoSizeColumn(submissionCell.getColumnIndex());
			}
			
		}
		
		/**
		 * Block sheet report
		 */
		
		XSSFSheet blockReportSheet = reportWorbook.createSheet("Block Report");
		
		i=0;
		colId=0;
		 headerRow=blockReportSheet.createRow(i++);
		districtCol=headerRow.createCell(colId++);
		districtCol.setCellStyle(headCellStyle);
		
		Cell blockCol=headerRow.createCell(colId++);
		blockCol.setCellStyle(headCellStyle);
		
		countCell=headerRow.createCell(colId++);
		countCell.setCellStyle(headCellStyle);
		
		districtCol.setCellValue("District");
		blockReportSheet.autoSizeColumn(districtCol.getColumnIndex());
		
		blockCol.setCellValue("Block");
		blockReportSheet.autoSizeColumn(blockCol.getColumnIndex());
		
		
		countCell.setCellValue("Number Of Submission");
		blockReportSheet.autoSizeColumn(countCell.getColumnIndex());
		
		 districtRow = null;
		
		
		
		 for(String area:blockDataMap.keySet())
		{
			colId=0;
			districtRow=blockReportSheet.createRow(i++);
			 districtCol=districtRow.createCell(colId++);
			 blockCol = districtRow.createCell(colId++);
			 countCell=districtRow.createCell(colId++);

			
			districtCol.setCellValue(area.split("_")[0]);
			districtCol.setCellStyle(leftCellStyle);
			
			blockCol.setCellValue(area.split("_")[1]);
			blockCol.setCellStyle(leftCellStyle);
			
			int count=0;
			
				for(String key:blockDataMap.get(area).keySet())
				{
					count+=blockDataMap.get(area).get(key);				
//					total+=dataMap.get(area).get(key);
				}

			
				countCell.setCellValue(count);
				countCell.setCellStyle(rightCellStyle);
				reportSheet.autoSizeColumn(districtCol.getColumnIndex());
				reportSheet.autoSizeColumn(blockCol.getColumnIndex());
				reportSheet.autoSizeColumn(countCell.getColumnIndex());
			
		}
		
		
		i+=3;
		
		submissionHeaderRow=blockReportSheet.createRow(i);
		
		colId=0;
		subMissCell=submissionHeaderRow.createCell(colId++);
		subMissCell.setCellValue("Submission Details ");
		
		
		blockReportSheet.addMergedRegion(new CellRangeAddress(i,i,0,3));
		subMissCell.setCellStyle(headerCellStyle);
		blockReportSheet.autoSizeColumn(subMissCell.getColumnIndex());
		
		
		i++;
		submissionHeaderRow=blockReportSheet.createRow(i++);
		
		colId=0;
		districtCell=submissionHeaderRow.createCell(colId++);
		districtCell.setCellValue("District");
		districtCell.setCellStyle(headCellStyle);
		blockReportSheet.autoSizeColumn(districtCell.getColumnIndex());
		
		Cell blockCell=submissionHeaderRow.createCell(colId++);
		blockCell.setCellValue("Block");
		blockCell.setCellStyle(headCellStyle);
		blockReportSheet.autoSizeColumn(blockCell.getColumnIndex());
		
		
		cboCell=submissionHeaderRow.createCell(colId++);
		cboCell.setCellValue("CBO Type");
		cboCell.setCellStyle(headCellStyle);
		blockReportSheet.autoSizeColumn(cboCell.getColumnIndex());
		
		submissionCell=submissionHeaderRow.createCell(colId++);
		submissionCell.setCellValue("Number Of Submission");
		submissionCell.setCellStyle(headCellStyle);
		blockReportSheet.autoSizeColumn(submissionCell.getColumnIndex());
		
		
		for(String key:blockDataMap.keySet())
		{
			
			for(String type:blockDataMap.get(key).keySet())
			{
				colId=0;
				Row submissionDataRow=blockReportSheet.createRow(i++);
				districtCell=submissionDataRow.createCell(colId++);
				districtCell.setCellValue(key.split("_")[0]);
				districtCell.setCellStyle(leftCellStyle);
				
				
				blockCell=submissionDataRow.createCell(colId++);
				blockCell.setCellValue(key.split("_")[1]);
				blockCell.setCellStyle(leftCellStyle);
				
				cboCell=submissionDataRow.createCell(colId++);
				cboCell.setCellValue(type.replace("_", " - "));
				cboCell.setCellStyle(leftCellStyle);
				
				 submissionCell=submissionDataRow.createCell(colId++);
				submissionCell.setCellValue(blockDataMap.get(key).get(type));
				submissionCell.setCellStyle(rightCellStyle);
				
				blockReportSheet.autoSizeColumn(districtCell.getColumnIndex());
				blockReportSheet.autoSizeColumn(blockCell.getColumnIndex());
				blockReportSheet.autoSizeColumn(cboCell.getColumnIndex());
				blockReportSheet.autoSizeColumn(submissionCell.getColumnIndex());
			}
			
		}
		
		
		
		
		workbook.close();
		String outputPath =context.getRealPath("resources\\"
				+applicationMessageSource.getMessage("submission.details", null,
						null)+sdf2.format(new java.util.Date())+".xlsx");
		FileOutputStream fileOut = null;
		fileOut = new FileOutputStream(outputPath);
		reportWorbook.write(fileOut);
		fileOut.close();
		reportWorbook.close();
		
		
		return outputPath;
	}
}
