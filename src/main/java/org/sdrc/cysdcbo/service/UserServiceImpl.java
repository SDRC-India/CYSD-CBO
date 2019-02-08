package org.sdrc.cysdcbo.service;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.cysdcbo.domain.CollectUser;
import org.sdrc.cysdcbo.domain.Program;
import org.sdrc.cysdcbo.domain.Program_XForm_Mapping;
import org.sdrc.cysdcbo.domain.UserLoginMeta;
import org.sdrc.cysdcbo.domain.UserRoleFeaturePermissionMapping;
import org.sdrc.cysdcbo.domain.User_Program_XForm_Mapping;
import org.sdrc.cysdcbo.domain.XForm;
import org.sdrc.cysdcbo.model.AreaModel;
import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.model.FeatureModel;
import org.sdrc.cysdcbo.model.FeaturePermissionMappingModel;
import org.sdrc.cysdcbo.model.FormsToDownloadMediafiles;
import org.sdrc.cysdcbo.model.MediaFilesToUpdate;
import org.sdrc.cysdcbo.model.ModelToCollectApplication;
import org.sdrc.cysdcbo.model.PermissionModel;
import org.sdrc.cysdcbo.model.ProgramModel;
import org.sdrc.cysdcbo.model.ProgramXFormsModel;
import org.sdrc.cysdcbo.model.RoleFeaturePermissionSchemeModel;
import org.sdrc.cysdcbo.model.RoleModel;
import org.sdrc.cysdcbo.model.UserRoleFeaturePermissionMappingModel;
import org.sdrc.cysdcbo.model.XFormModel;
import org.sdrc.cysdcbo.repository.CollectUserRepository;
import org.sdrc.cysdcbo.repository.ProgrammRepository;
import org.sdrc.cysdcbo.repository.UserLoginMetaRepository;
import org.sdrc.cysdcbo.repository.User_Program_XForm_MappingRepository;
import org.sdrc.cysdcbo.repository.XFormRepository;
import org.sdrc.cysdcbo.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.pdf.codec.Base64;
/**
 * 
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	CollectUserRepository collectUserRespository;
	
	@Autowired
	private User_Program_XForm_MappingRepository user_Program_XForm_MappingRepository;
	
	@Autowired
	private ProgrammRepository programmRepository;
	
	@Autowired
	UserLoginMetaRepository userLoginMetaRepository;
	
	@Autowired
	private MessageDigestPasswordEncoder  passwordEncoder;
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(UserServiceImpl.class);
	@Autowired
	private XFormRepository xFormRepository;
	@Autowired
	private ResourceBundleMessageSource messages;
	
	@Override
	@Transactional
	public CollectUserModel findByUserName(String userName) 
	{
		CollectUser collectUser=collectUserRespository.findByUsernameAndIsLiveTrue(userName);
		
		
		CollectUserModel collectUserModel=null;
		if(collectUser!=null)
		{	
			collectUserModel=new CollectUserModel();
		collectUserModel.setUserId(collectUser.getUserId());
		collectUserModel.setUsername(collectUser.getUsername());
		collectUserModel.setName(collectUser.getName());
		collectUserModel.setPassword(collectUser.getPassword());
		collectUserModel.setLive(collectUser.isLive());
		collectUserModel.setEmailId(collectUser.getEmailId());
		
		List<UserRoleFeaturePermissionMappingModel> userRoleFeaturePermissionMappingModels = new ArrayList<UserRoleFeaturePermissionMappingModel>();
		
		for(UserRoleFeaturePermissionMapping featurePermissionMapping:collectUser.getUserRoleFeaturePermissionMappings())
		{
			UserRoleFeaturePermissionMappingModel userRoleFeaturePermissionMappingModel=new UserRoleFeaturePermissionMappingModel();
			
			userRoleFeaturePermissionMappingModel.setUserRoleFeaturePermissionId(featurePermissionMapping.getUserRoleFeaturePermissionId());
		
			
			RoleFeaturePermissionSchemeModel roleSchemeModel=new RoleFeaturePermissionSchemeModel();
			AreaModel areaModel=new AreaModel();
			RoleModel roleModel=new RoleModel();
			FeaturePermissionMappingModel featurePermissionMappingModel=new FeaturePermissionMappingModel();
			FeatureModel featureModel=new FeatureModel();
			PermissionModel permissionModel=new PermissionModel();
			
			
			roleSchemeModel.setAreaCode(featurePermissionMapping.getRoleFeaturePermissionScheme().getAreaCode());
			areaModel.setAreaId(featurePermissionMapping.getRoleFeaturePermissionScheme().getArea().getAreaId());
			areaModel.setAreaLevelId(featurePermissionMapping.getRoleFeaturePermissionScheme().getArea().getAreaLevel().getAreaLevelId());
			areaModel.setAreaName(featurePermissionMapping.getRoleFeaturePermissionScheme().getArea().getAreaName());
			areaModel.setParentAreaId(featurePermissionMapping.getRoleFeaturePermissionScheme().getArea().getParentAreaId());
			areaModel.setAreaCode(featurePermissionMapping.getRoleFeaturePermissionScheme().getArea().getAreaCode());
			roleModel.setRoleId(featurePermissionMapping.getRoleFeaturePermissionScheme().getRole().getRoleId());
			roleModel.setRoleName(featurePermissionMapping.getRoleFeaturePermissionScheme().getRole().getRoleName());
			
			roleSchemeModel.setRole(roleModel);
			roleSchemeModel.setAreaModel(areaModel);
			roleSchemeModel.setSchemeName(featurePermissionMapping.getRoleFeaturePermissionScheme().getSchemeName());
			featureModel.setFeatureName(featurePermissionMapping.getRoleFeaturePermissionScheme().getFeaturePermissionMapping().getFeature().getFeatureName());
			featureModel.setDescription(featurePermissionMapping.getRoleFeaturePermissionScheme().getFeaturePermissionMapping().getFeature().getDescription());
			permissionModel.setPermissionName(featurePermissionMapping.getRoleFeaturePermissionScheme().getFeaturePermissionMapping().getPermission().getPermissionName());
			featurePermissionMappingModel.setFeature(featureModel);
			featurePermissionMappingModel.setPermission(permissionModel);
			roleSchemeModel.setFeaturePermissionMapping(featurePermissionMappingModel);
			userRoleFeaturePermissionMappingModel.setRoleFeaturePermissionSchemeModel(roleSchemeModel);
			userRoleFeaturePermissionMappingModels.add(userRoleFeaturePermissionMappingModel);
		}
		collectUserModel.setUserRoleFeaturePermissionMappings(userRoleFeaturePermissionMappingModels);
		// TODO Auto-generated method stub
		}
		
		return collectUserModel;
	}

	@Override
	public List<ProgramXFormsModel> getProgramWithXFormsList(String username,
			String password) {
		// TODO Auto-generated method stub

		// the user is present in the database or not. if present, live or not
		CollectUser user = collectUserRespository.findByUsernameAndPasswordAndIsLiveTrue(
				username, password);
		if (user != null) {

			// this variable will return
			List<ProgramXFormsModel> programWithXFormsList = new ArrayList<ProgramXFormsModel>();

			// fetching user-program-xForm mappings
			for (User_Program_XForm_Mapping user_Program_XForm_Mapping : user_Program_XForm_MappingRepository
					.findByUser(username)) {

				List<XFormModel> xFormModels = new ArrayList<XFormModel>();

				/*
				 * Checking whether the program and it's xForms are present or
				 * not in the programWithXFormsList. If present, get the XForm
				 * list, add one, remove the list and restore the latest list If
				 * not present add new program and xForm list
				 */
				boolean programPresent = false;
				for (ProgramXFormsModel programXFormsModel : programWithXFormsList) {
					if (programXFormsModel.getProgramModel().getProgramId() == user_Program_XForm_Mapping
							.getProgram_XForm_Mapping().getProgram()
							.getProgramId()) {
						programPresent = true;
						// get the XForm list
						xFormModels = programXFormsModel.getxFormsModel();
					}
				}

				if (programPresent) {
					// program and it's xForms are present in the
					// programWithXFormsList
					XForm xForm = user_Program_XForm_Mapping
							.getProgram_XForm_Mapping().getxForm();

					XFormModel xFormModel = new XFormModel();

					xFormModel.setxFormId(xForm.getxFormId().trim());
					xFormModel.setOdkServerURL(xForm.getOdkServerURL().trim());
					xFormModel.setUsername(xForm.getUsername().trim());
					xFormModel.setPassword(Base64.encodeBytes(xForm.getPassword().trim().getBytes()));

					// add one
					xFormModels.add(xFormModel);

					// remove the list
					ProgramModel programModel = null;
					for (int i = 0; i < programWithXFormsList.size(); i++) {
						if (programWithXFormsList.get(i).getProgramModel()
								.getProgramId() == user_Program_XForm_Mapping
								.getProgram_XForm_Mapping().getProgram()
								.getProgramId()) {
							programModel = programWithXFormsList.get(i)
									.getProgramModel();
							programWithXFormsList.remove(i);
						}
					}

					// restore the latest list
					ProgramXFormsModel programWithXFormsModelChild = new ProgramXFormsModel();

					programWithXFormsModelChild.setProgramModel(programModel);
					programWithXFormsModelChild.setxFormsModel(xFormModels);

					programWithXFormsList.add(programWithXFormsModelChild);

				} else {
					ProgramXFormsModel programWithXFormsModelChild = new ProgramXFormsModel();

					Program program = user_Program_XForm_Mapping
							.getProgram_XForm_Mapping().getProgram();

					ProgramModel programModel = new ProgramModel();
					programModel.setProgramId(program.getProgramId());
					programModel.setProgramName(program.getProgramName());
					programWithXFormsModelChild.setProgramModel(programModel);

					XForm xForm = user_Program_XForm_Mapping
							.getProgram_XForm_Mapping().getxForm();

					XFormModel xFormModel = new XFormModel();

					xFormModel.setxFormId(xForm.getxFormId().trim());
					xFormModel.setOdkServerURL(xForm.getOdkServerURL().trim());
					xFormModel.setUsername(xForm.getUsername().trim());
					xFormModel.setPassword(Base64.encodeBytes(xForm.getPassword().trim().getBytes()));
					xFormModels.add(xFormModel);

					programWithXFormsModelChild.setxFormsModel(xFormModels);

					programWithXFormsList.add(programWithXFormsModelChild);
				}
			}
			logger.info("Data sent for username : " + username);
			return programWithXFormsList;
		} else {
			logger.warn("Username : " + username + " authentication failed!");
			return null;
		}
	}

	@Override
	public boolean insertUserTable() {
		Program program=programmRepository.findByProgramId(5);
		List<Program_XForm_Mapping> program_XForm_Mappings=program.getProgram_XForm_Mappings();
		List<CollectUser> collectUsers=collectUserRespository.findByIsLiveTrue();
		Map<String,CollectUser> collectUserMap=new HashMap<String,CollectUser>();
		for(CollectUser collectUser:collectUsers)
		{
			collectUserMap.put(collectUser.getUsername(), collectUser);
		}
		try
		{
		FileInputStream file=new FileInputStream("D://DGA V2//USER LIST DGA 2 CG (2).xlsx");
		XSSFWorkbook wb=new XSSFWorkbook(file);
		XSSFSheet sheet=wb.getSheetAt(0);
		
		for(int i=1;i<=sheet.getLastRowNum();i++)
		{
			Row row=sheet.getRow(i);
			if(i>=45)
			{
				System.out.println("Done");
			}
			Cell nameCell=row.getCell(0);
			Cell emailCell=row.getCell(1);
//			Cell passWord=row.getCell(3);
			
			CollectUser collectUser=new CollectUser();
			collectUser.setCreatedDate(new Timestamp(new java.util.Date().getTime()));
			collectUser.setCreatedBy("Harsh");
			collectUser.setUsername(nameCell.getStringCellValue().split(" ")[0].toLowerCase().trim());
			collectUser.setName(nameCell.getStringCellValue());
			collectUser.setEmailId(emailCell.getStringCellValue());
			collectUser.setPassword(collectUser.getUsername()+"@"+RandomStringUtils.randomNumeric(3)+"#!");
			collectUser.setLive(true);
			if(collectUserMap.containsKey(collectUser.getUsername()))
			{
				CollectUser collectUserOld=	collectUserMap.get(collectUser.getUsername());
				collectUserOld.setUpdatedDate(new Timestamp(new java.util.Date().getTime()));
				collectUserOld.setUpdatedBy("Harsh");
				collectUserOld.setLive(false);
				collectUserRespository.save(collectUserOld);
			}
			CollectUser collectUsernew=null;
			try
			{
			 collectUsernew = collectUserRespository.save(collectUser);
			}
			catch (DataIntegrityViolationException data)
			{
				collectUser.setUsername(nameCell.getStringCellValue().split(" ")[0].toLowerCase().trim()+nameCell.getStringCellValue().split(" ")[1].toLowerCase().trim());
				collectUser.setPassword(collectUser.getUsername()+"@"+RandomStringUtils.randomNumeric(3)+"#!");
				collectUsernew = collectUserRespository.save(collectUser);
			}
			for(Program_XForm_Mapping program_XForm_Mapping:program_XForm_Mappings)
			{
				User_Program_XForm_Mapping user_Program_XForm_Mapping = new User_Program_XForm_Mapping();
				
				user_Program_XForm_Mapping.setCollectUser(collectUsernew);
				user_Program_XForm_Mapping.setIsLive(true);
				user_Program_XForm_Mapping.setProgram_XForm_Mapping(program_XForm_Mapping);
				user_Program_XForm_Mapping.setCreatedDate(new Timestamp(new java.util.Date().getTime()));
				user_Program_XForm_Mapping.setCreatedBy("Harsh");
				
				user_Program_XForm_MappingRepository.save(user_Program_XForm_Mapping);
			}
		}
		wb.close();
		return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ModelToCollectApplication getModelToCollectApplication(
			List<FormsToDownloadMediafiles> list, String username,
			String password) {
		// TODO Auto-generated method stub
		
		ModelToCollectApplication modelToCollectApplication = new ModelToCollectApplication();
		List<ProgramXFormsModel> programWithXFormsList = getProgramWithXFormsList(username,password);
		if(programWithXFormsList!=null){
			modelToCollectApplication.setProgramXFormModelList(programWithXFormsList);
		}else{
			return null;
		}
	//	modelToCollectApplication.setListOfMediaFilesToUpdate(null);
		if(getMediaFilesToUpdate(list)!=null){
			modelToCollectApplication.setListOfMediaFilesToUpdate(getMediaFilesToUpdate(list));
		}else{
			modelToCollectApplication.setListOfMediaFilesToUpdate(null);
		}
		
	
		return modelToCollectApplication;
	}

	@Override
	public List<MediaFilesToUpdate> getMediaFilesToUpdate(
			List<FormsToDownloadMediafiles> formToDownloadMediaList) {
		// TODO Auto-generated method stub
				List<MediaFilesToUpdate> mediaFilesToUpdatesList = new ArrayList<MediaFilesToUpdate>();
				try{
					List<XForm> xForms = xFormRepository.findAllByIsLiveTrue();
					formToDownloadMediaList.forEach(formFromMobile->{
						MediaFilesToUpdate mediaFilesToUpdate = new MediaFilesToUpdate();
						XForm xForm = getValidatedXForm(xForms, formFromMobile);
						if(xForm!=null){
							mediaFilesToUpdate.setxFormId(xForm.getxFormId());
							Path path = Paths.get(xForm.getMediaPath());
							byte[] data;
							try {
								data = Files.readAllBytes(path);
								String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(data);
								mediaFilesToUpdate.setMediaFile(encodedString);
							} catch (Exception e) {
								logger.error("Media file not present in the specified path");					
							}
							
						}else{				
							mediaFilesToUpdate.setxFormId(formFromMobile.getFormId());
							mediaFilesToUpdate.setMediaFile(null);
						}
						mediaFilesToUpdatesList.add(mediaFilesToUpdate);
					});
				}catch(Exception e){
					logger.error(""+e);		
				}
			
				
			
				
			
				
				return mediaFilesToUpdatesList;
	}
	private XForm getValidatedXForm(List<XForm> xForms, FormsToDownloadMediafiles formFromMobile) {
		
		SimpleDateFormat sdf = new SimpleDateFormat(messages.getMessage(Constants.Odk.MEDIA_FILE_UPDATED_DATE, null, null));
		
		String xFormUpdatedDate = null;
		
		for(XForm form : xForms){
			Date date = new Date();
			if(form.getUpdatedDate()!=null){
				date.setTime(form.getUpdatedDate().getTime());
				 xFormUpdatedDate = new SimpleDateFormat(messages.getMessage(Constants.Odk.MEDIA_FILE_UPDATED_DATE, null, null)).format(date);
				 try {
						if(formFromMobile.getFormId().equals(form.getxFormId())
								&& sdf.parse(formFromMobile.getDownloadOrUpdateDate()).before(sdf.parse(xFormUpdatedDate))
								&& form.getMediaPath() != null){
							return form;
							
						}
					} catch (ParseException e) {
						return null;
					}
			}		
			
			
		}
		
		return null;
	}
	
	@Override
	@Transactional
	public long saveUserLoginMeta(String ipAddress, Integer userId, String userAgent,String sessionID) {
		CollectUser mstUser=collectUserRespository.findByUserId(userId);
		UserLoginMeta userLoginMeta = new UserLoginMeta();
		userLoginMeta.setUserIpAddress(ipAddress);
		userLoginMeta.setCollectUser(mstUser);
		userLoginMeta.setLoggedInDateTime(new Timestamp(new Date().getTime()));
		userLoginMeta.setUserAgent(userAgent);
		userLoginMeta.setLoggedIn(true);
		userLoginMeta.setSeesionID(sessionID.split("=")[1].trim());
		return userLoginMetaRepository.save(userLoginMeta).getUserLogInMetaId();
	}

	// update login meta while signing out
	@Override
	@Transactional
	public void updateLoggedOutStatus(long userLoginMetaId, Timestamp loggedOutDateTime) {

		// while server start up
		if (userLoginMetaId == -1) {
			userLoginMetaRepository.updateStatusForAll(loggedOutDateTime);
		} else
			userLoginMetaRepository.updateStatus(loggedOutDateTime, userLoginMetaId);
	}

	@Override
	@Lazy
	@Transactional
	public boolean updatePassword(CollectUserModel collectUserModel) {
		
		CollectUser collectUser=collectUserRespository.findByUsernameAndIsLiveTrue(collectUserModel.getUsername());
		if(collectUser==null) return false;
		collectUser.setPassword(passwordEncoder.encodePassword(collectUserModel.getPassword(), collectUserModel.getUsername()));
		return true;
	}

}
