/**
 * 
 */
package org.sdrc.cysdcbo.model;


/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public class IndicatorFormXpathMappingModel {
	
	private int indicatorFormXpathMappingId;

	private int dhXpath;

	private int chcXpath;

	private int phcXpath;

	private String label;
	
	private String sector;
	
	private String subSector;
	
	private String subGroup;
	
	private String type;

	public int getIndicatorFormXpathMappingId() {
		return indicatorFormXpathMappingId;
	}

	public void setIndicatorFormXpathMappingId(int indicatorFormXpathMappingId) {
		this.indicatorFormXpathMappingId = indicatorFormXpathMappingId;
	}

	public int getDhXpath() {
		return dhXpath;
	}

	public void setDhXpath(int dhXpath) {
		this.dhXpath = dhXpath;
	}

	public int getChcXpath() {
		return chcXpath;
	}

	public void setChcXpath(int chcXpath) {
		this.chcXpath = chcXpath;
	}

	public int getPhcXpath() {
		return phcXpath;
	}

	public void setPhcXpath(int phcXpath) {
		this.phcXpath = phcXpath;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getSubSector() {
		return subSector;
	}

	public void setSubSector(String subSector) {
		this.subSector = subSector;
	}

	public String getSubGroup() {
		return subGroup;
	}

	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
