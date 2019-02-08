/**
 * 
 */
package org.sdrc.cysdcbo.service;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public interface MasterRawDataService {
	/**
	 * This method will be used to read the xfrom excel and persist the
	 * corresponding xpath in Database
	 * 
	 * @return
	 * @throws Exception 
	 */
	public boolean generateXpath() throws Exception;
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean updateAreaTable() throws Exception;
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean getRawDataFromOdk() throws Exception;
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean uptoQuestionLevel() throws Exception;

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean updateXformMapping() throws Exception;
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean formatGeoCordinates() throws Exception;
	
	
	
	/**
	 * This will generate raw data of all submission when user will request for generation of excel
	 * @return
	 * @throws Exception
	 */
	public String generateExcel() throws Exception;
	
	
	
	/**
	 * it will perisit raw data for each xpath for each submission
	 * 
	 * @return
	 * @throws Exception 
	 */
	public boolean persistRawData() throws Exception;
	
	/**
	 * This method will get invoked at 12:00 AM every night
	 * @throws Exception
	 */
	void updateDatasFacilityScoreScheduler() throws Exception;


	void updateDatasRawDatasScheduler() throws Exception;


	void updateDatasExcelScheduler() throws Exception;


	/**
	 * This method will generate the report of number of submission district wise
	 * @return
	 * @throws Exception
	 */
	String getSubmissionReport() throws Exception;

}
