package org.sdrc.cysdcbo.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This domain class or entity class will keep all the scores obtained for respective xpaths.
 @author Harsh Pratyush
 * @since version 1.0.0.0
 *
 */

@Entity
@Table
public class FacilityScore implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int facilityScoreId;

	@ManyToOne
	@JoinColumn(name="formXpathScoreId")
	private FormXpathScoreMapping formXpathScoreMapping;
	
	private Double score;
	
	@ManyToOne
	@JoinColumn(name="lastVisitDataId")
	private LastVisitData lastVisitData;

	public int getFacilityScoreId() {
		return facilityScoreId;
	}

	public void setFacilityScoreId(int facilityScoreId) {
		this.facilityScoreId = facilityScoreId;
	}

	public FormXpathScoreMapping getFormXpathScoreMapping() {
		return formXpathScoreMapping;
	}

	public void setFormXpathScoreMapping(FormXpathScoreMapping formXpathScoreMapping) {
		this.formXpathScoreMapping = formXpathScoreMapping;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public LastVisitData getLastVisitData() {
		return lastVisitData;
	}

	public void setLastVisitData(LastVisitData lastVisitData) {
		this.lastVisitData = lastVisitData;
	}
	
}
