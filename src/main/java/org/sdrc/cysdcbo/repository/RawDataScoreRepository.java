/**
 * 
 */
package org.sdrc.cysdcbo.repository;

import java.util.List;
import java.util.Set;

import org.sdrc.cysdcbo.domain.RawDataScore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Harsh Pratyush(harsh@sdrc.co.in)
 *
 */
public interface RawDataScoreRepository {

	@Transactional
	void save(RawDataScore rawDataScore);

	List<RawDataScore> findByLastVisitDataLastVisitDataId(
			Integer lastVisitDataId);

	/**
	 * 
	 * @param xPathsRow
	 * @param xPathsIdCol
	 * @param timePeriodId
	 * @param districtId
	 * @return
	 */
	List<Object[]> findCrossTabReportForADistrictAndATimePeriod(List<Integer> xPathsRow,
			List<Integer> xPathsIdCol, int timePeriodId, int districtId);

	List<Object[]> findCrossTabReportForADistrictAndAllTimePeriod(
			List<Integer> xPathsRow, List<Integer> xPathsIdCol, int districtId);

	List<Object[]> findCrossTabReportForATimePeriod(List<Integer> xPathsRow,
			List<Integer> xPathsIdCol, int timePeriodId);

	List<Object[]> findCrossTabReportForAllTimePeriod(List<Integer> xPathsRow,
			List<Integer> xPathsIdCol);

	List<Object[]> findCrossTabForOnlyOneIntegerType(List<Integer> xPathsIdCol,
			List<Integer> xPathsRow, Set<Integer> timePeriodIds);

	List<Object[]> findCrossTabForOnlyOneIntegerTypeADistrict(
			List<Integer> xPathsRow, List<Integer> xPathsIdCol,
			Set<Integer> timePeriodIds, int districtId);

	List<Object[]> findCrossTabForOnlyIntegerType(List<Integer> xPathsRow,
			List<Integer> xPathsIdCol, Set<Integer> timePeriodIds);

	List<Object[]> findCrossTabForOnlyIntegerTypeForADistrict(
			List<Integer> xPathsRow, List<Integer> xPathsIdCol,
			Set<Integer> timePeriodIds, int districtId);

	List<Object[]> findCrossTabForOnlyOneIntegerTypeADistrictByFacilityWise(
			 List<Integer> xPathsRow,
			Set<Integer> timePeriodIds, int districtId);

	List<Object[]> findCrossTabForOnlyOneIntegerTypeByFacilityWise(
			List<Integer> xPathsRow, Set<Integer> timePeriodIds);

	List<Object[]> findCrossTabForChoiceTypeADistrictByFacilityWise(
			List<Integer> xPathsRow, Set<Integer> timePeriodIds, int districtId);

	List<Object[]> findCrossTabForChoiceTypetByFacilityWise(
			List<Integer> xPathsRow, Set<Integer> timePeriodIds);

	RawDataScore findByLastVisitDataLastVisitDataIdAndRawFormXapthsXPathId(
			Integer lastVisitDataId, int signatureId);
	

}
