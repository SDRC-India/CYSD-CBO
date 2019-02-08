/**
 * 
 */
package org.sdrc.cysdcbo.model;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 * This model will contain all the selected data from crosstab page
 * 
 *
 */
public class CrossTabDataModel {
	
	private int colIndicatorFormXpathMappingId;

	private int colDhXpath;// will be zero in case of indicator is not applicable for DH

	private int colChcXpath;// will be zero in case of indicator is not applicable for CHC

	private int colPhcXpath;// will be zero in case of indicator is not applicable for PHC

	private String coLabel;
	
	private int rowIndicatorFormXpathMappingId;

	private int rowDhXpath;// will be zero in case of indicator is not applicable for DH

	private int rowChcXpath;// will be zero in case of indicator is not applicable for CHC

	private int rowPhcXpath;// will be zero in case of indicator is not applicable for PHC

	private String rowLabel;
	
	private int facilityTypeId;// will be zero in case of Facility Type-All
	
	private int districtId;// will be zero in case of state selection
	
	private int timePeriodId;// will be zero in case of Timeperiod-All
	
	private String rowIndicatorFormXpathMappingType;
	
	private String colIndicatorFormXpathMappingType;

	public int getColIndicatorFormXpathMappingId() {
		return colIndicatorFormXpathMappingId;
	}

	public void setColIndicatorFormXpathMappingId(int colIndicatorFormXpathMappingId) {
		this.colIndicatorFormXpathMappingId = colIndicatorFormXpathMappingId;
	}

	public int getColDhXpath() {
		return colDhXpath;
	}

	public void setColDhXpath(int colDhXpath) {
		this.colDhXpath = colDhXpath;
	}

	public int getColChcXpath() {
		return colChcXpath;
	}

	public void setColChcXpath(int colChcXpath) {
		this.colChcXpath = colChcXpath;
	}

	public int getColPhcXpath() {
		return colPhcXpath;
	}

	public void setColPhcXpath(int colPhcXpath) {
		this.colPhcXpath = colPhcXpath;
	}

	public String getCoLabel() {
		return coLabel;
	}

	public void setCoLabel(String coLabel) {
		this.coLabel = coLabel;
	}

	public int getRowIndicatorFormXpathMappingId() {
		return rowIndicatorFormXpathMappingId;
	}

	public void setRowIndicatorFormXpathMappingId(int rowIndicatorFormXpathMappingId) {
		this.rowIndicatorFormXpathMappingId = rowIndicatorFormXpathMappingId;
	}

	public int getRowDhXpath() {
		return rowDhXpath;
	}

	public void setRowDhXpath(int rowDhXpath) {
		this.rowDhXpath = rowDhXpath;
	}

	public int getRowChcXpath() {
		return rowChcXpath;
	}

	public void setRowChcXpath(int rowChcXpath) {
		this.rowChcXpath = rowChcXpath;
	}

	public int getRowPhcXpath() {
		return rowPhcXpath;
	}

	public void setRowPhcXpath(int rowPhcXpath) {
		this.rowPhcXpath = rowPhcXpath;
	}

	public String getRowLabel() {
		return rowLabel;
	}

	public void setRowLabel(String rowLabel) {
		this.rowLabel = rowLabel;
	}

	public int getFacilityTypeId() {
		return facilityTypeId;
	}

	public void setFacilityTypeId(int facilityTypeId) {
		this.facilityTypeId = facilityTypeId;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public int getTimePeriodId() {
		return timePeriodId;
	}

	public void setTimePeriodId(int timePeriodId) {
		this.timePeriodId = timePeriodId;
	}

	public String getRowIndicatorFormXpathMappingType() {
		return rowIndicatorFormXpathMappingType;
	}

	public void setRowIndicatorFormXpathMappingType(
			String rowIndicatorFormXpathMappingType) {
		this.rowIndicatorFormXpathMappingType = rowIndicatorFormXpathMappingType;
	}

	public String getColIndicatorFormXpathMappingType() {
		return colIndicatorFormXpathMappingType;
	}

	public void setColIndicatorFormXpathMappingType(
			String colIndicatorFormXpathMappingType) {
		this.colIndicatorFormXpathMappingType = colIndicatorFormXpathMappingType;
	}
	
	

}
