package org.sdrc.cysdcbo.service;

import java.sql.Timestamp;
import java.util.List;

import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.model.FormsToDownloadMediafiles;
import org.sdrc.cysdcbo.model.MediaFilesToUpdate;
import org.sdrc.cysdcbo.model.ModelToCollectApplication;
import org.sdrc.cysdcbo.model.ProgramXFormsModel;
/**
 * @author Harsh(harsh@sdrc.co.in)
 */

public interface UserService {
	CollectUserModel findByUserName(String userName);
	
	

	List<ProgramXFormsModel> getProgramWithXFormsList(String username, String password);	
	
	boolean insertUserTable();
	
	ModelToCollectApplication getModelToCollectApplication(List<FormsToDownloadMediafiles> list,String username,String password);
	
	List<MediaFilesToUpdate> getMediaFilesToUpdate(List<FormsToDownloadMediafiles> list);

	long saveUserLoginMeta(String ipAddress, Integer userId, String userAgent, String sessionID);

	void updateLoggedOutStatus(long userLoginMeta, Timestamp loggedOutDateTime);



	/**
	 * This method will update the user password
	 * @param collectUserModel
	 * @return
	 */
	boolean updatePassword(CollectUserModel collectUserModel);
}
