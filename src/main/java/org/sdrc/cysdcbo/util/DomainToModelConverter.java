package org.sdrc.cysdcbo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.cysdcbo.domain.Area;
import org.sdrc.cysdcbo.domain.AreaLevel;
import org.sdrc.cysdcbo.domain.ChoicesDetails;
import org.sdrc.cysdcbo.domain.CollectUser;
import org.sdrc.cysdcbo.domain.LastVisitData;
import org.sdrc.cysdcbo.domain.Program;
import org.sdrc.cysdcbo.domain.XForm;
import org.sdrc.cysdcbo.model.AreaLevelModel;
import org.sdrc.cysdcbo.model.AreaModel;
import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.model.LastVisitDataModel;
import org.sdrc.cysdcbo.model.ProgramModel;
import org.sdrc.cysdcbo.model.XFormModel;
import org.sdrc.cysdcbo.repository.ChoiceDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class DomainToModelConverter {
	
	/**
	 * @author Harsh Pratyush(harsh@sdrc.co.in)
	 * @param areaLevel The Entity
	 * @return AreaLevelModel The Model
	 */
	
	
	@Autowired
	ChoiceDetailsRepository choiceDetailsRepository;
	
	public AreaLevelModel toAreaLevelModel(AreaLevel areaLevel) {
		
		AreaLevelModel areaLevelModel = new AreaLevelModel();
		
		areaLevelModel.setAreaLevelId(areaLevel.getAreaLevelId());
		areaLevelModel.setAreaLevelName(areaLevel.getAreaLevelName());
		
		return areaLevelModel;
	}

	/**
	 * 
	 * @param xForm
	 * @return
	 * @author Harsh Pratyush(harsh@sdrc.co.in)
	 */
	public XFormModel toXFormModel(XForm xForm) {
		
		XFormModel xFormModel = new XFormModel();
		
		xFormModel.setAreaLevelModel(toAreaLevelModel(xForm.getAreaLevel()));
		xFormModel.setAreaXPath(xForm.getAreaXPath());
		xFormModel.setDateOfVisitXPath(xForm.getDateOfVisitXPath());
		xFormModel.setFormId(xForm.getFormId());
		xFormModel.setLive(xForm.isLive());
		xFormModel.setOdkServerURL(xForm.getOdkServerURL());
		xFormModel.setPassword(xForm.getPassword());
		xFormModel.setSecondaryAreaXPath(xForm.getSecondaryAreaXPath());
		xFormModel.setLocationXPath(xForm.getLocationXPath());
		xFormModel.setUsername(xForm.getUsername());
		xFormModel.setxFormId(xForm.getxFormId());
		xFormModel.setToEmailIdsXPath(xForm.getToEmailIdsXPath());
		xFormModel.setCcEmailIds(xForm.getCcEmailIds());
		xFormModel.setxFormTitle(xForm.getxFormIdTitle());
		xFormModel.setSendRawData(xForm.isSendRawData());
		xFormModel.setRootElement(xForm.getxFormId());
		
		return xFormModel;
	}

	/**
	 * 
	 * @param collectUser
	 * @return
	 * @author Harsh Pratyush(harsh@sdrc.co.in)
	 */
	public CollectUserModel toCollectUserModel(CollectUser collectUser) {
		
		
		CollectUserModel collectUserModel = new CollectUserModel();
		
		collectUserModel.setEmailId(collectUser.getEmailId());
		collectUserModel.setLive(collectUser.isLive());
		collectUserModel.setName(collectUser.getName());
		collectUserModel.setPassword(collectUser.getPassword());
		collectUserModel.setUserId(collectUser.getUserId());
		collectUserModel.setUsername(collectUser.getUsername());
		
		
		return collectUserModel;
	}


	
	/**
	 * 
	 * @param area
	 * @return
	 * @author Harsh Pratyush(harsh@sdrc.co.in)
	 */
	public AreaModel toAreaModel(Area area){
		
		AreaModel areaModel = new AreaModel();
		if(area != null){
			areaModel.setAreaId(area.getAreaId());
			areaModel.setAreaLevelId(area.getAreaLevel().getAreaLevelId());
			areaModel.setAreaName(area.getAreaName());
			areaModel.setParentAreaId(area.getParentAreaId());
			return areaModel;
		}
		return null;
		
	}

	public ProgramModel toProgramModel(Program program) {
		
		
		ProgramModel programModel = new ProgramModel();
		
		programModel.setLive(program.getIsLive());
		programModel.setProgramId(program.getProgramId());
		programModel.setProgramName(program.getProgramName());
		
		
		return programModel;
	}
	
	public LastVisitDataModel toLastVisitDataModel(LastVisitData lastVisitData) {
		
		LastVisitDataModel lastVisitDataModel = new LastVisitDataModel();
		
		if(lastVisitData != null){
			lastVisitDataModel.setLastVisitDataId(lastVisitData.getLastVisitDataId());
			lastVisitDataModel.setAreaModel(toAreaModel(lastVisitData.getArea()));
			return lastVisitDataModel;
		}
		return null;
	}
	
	
	@Bean
	@Scope(value="singleton")
	public Map<String,String> putChoiceDetails()
	{
		final Map<String,String> choiceMap=new HashMap<String, String>();
		List<ChoicesDetails> choicesDetails = choiceDetailsRepository.findByFormFormId(17);
		for(ChoicesDetails choicesDetail :choicesDetails)
		{
			choiceMap.put(choicesDetail.getChoicName()+"_"+choicesDetail.getLabel(), choicesDetail.getChoiceValue());
		}
		
		return choiceMap;
	}
	
			
}
