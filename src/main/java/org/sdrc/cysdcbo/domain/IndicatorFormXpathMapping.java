/**
 * 
 */
package org.sdrc.cysdcbo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
@Entity
public class IndicatorFormXpathMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int indicatorFormXpathMappingId;

	@ManyToOne
	private RawFormXapths dhXpath;

	@ManyToOne
	private RawFormXapths chcXpath;

	@ManyToOne
	private RawFormXapths phcXpath;

	@Column(nullable = false)
	private String label;
	
	@Column(nullable = false)
	private String sector;
	
	private String subSector;
	
	@Column(nullable = false)
	private String subGroup;
	
	@Column(nullable = false)
	private String type;

	public int getIndicatorFormXpathMappingId() {
		return indicatorFormXpathMappingId;
	}

	public void setIndicatorFormXpathMappingId(int indicatorFormXpathMappingId) {
		this.indicatorFormXpathMappingId = indicatorFormXpathMappingId;
	}

	public RawFormXapths getDhXpath() {
		return dhXpath;
	}

	public void setDhXpath(RawFormXapths dhXpath) {
		this.dhXpath = dhXpath;
	}

	public RawFormXapths getChcXpath() {
		return chcXpath;
	}

	public void setChcXpath(RawFormXapths chcXpath) {
		this.chcXpath = chcXpath;
	}

	public RawFormXapths getPhcXpath() {
		return phcXpath;
	}

	public void setPhcXpath(RawFormXapths phcXpath) {
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
