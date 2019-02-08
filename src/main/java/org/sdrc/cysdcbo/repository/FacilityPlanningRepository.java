package org.sdrc.cysdcbo.repository;

import java.util.List;

import org.sdrc.cysdcbo.domain.FacilityPlanning;

/**
 * 
 * @author Harsh(harsh@sdrc.co.in)
 *
 */
public interface FacilityPlanningRepository {
	
	public FacilityPlanning findByAreaAreaIdAndXFormFormIdAndTimPeriodTimePeriodId(Integer areaId,Integer formId, int timePeriodId);
 
	public List<Integer> findAllTimePeriodNids();

}
