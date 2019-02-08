package org.sdrc.cysdcbo.repository.springdatajpa;

import java.util.List;

import org.sdrc.cysdcbo.domain.FacilityScore;
import org.sdrc.cysdcbo.repository.FacilityScoreRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

@RepositoryDefinition(domainClass =FacilityScore.class, idClass = Integer.class)
public interface SpringDataFacilityScoreRepository extends
		FacilityScoreRepository {

	@Override
	@Query(value = "SELECT fc.formXpathScoreId, fxm.label, fxm.parentXpathId, AVG((fc.score/fxm.maxScore)*100), fxm.maxScore FROM "
			+ "FacilityScore fc JOIN FormXpathScoreMapping fxm ON "
			+ "fc.formXpathScoreId = fxm.formXpathScoreId, "
			+ "LastVisitData ld WHERE ld.LastVisitDataId = fc.lastVisitDataId "
			+ "AND ld.IsLive = 1"
			+ "GROUP BY fc.formXpathScoreId, fxm.label, fxm.parentXpathId, fxm.maxScore, fxm.formId HAVING fxm.formId = :formId", nativeQuery = true)
	List<Object[]> findAvgByFormId(@Param("formId") Integer formId);

	@Override
	@Query(value = "SELECT fxm.label, fxm.parentXpathId, fxm.maxScore, fc.score, "
			+ "fc.lastVisitDataId,(fc.score / fxm.maxScore)*100 FROM "
			+ "FacilityScore fc JOIN FormXpathScoreMapping fxm "
			+ "ON fc.formXpathScoreId = fxm.formXpathScoreId WHERE, "
			+ "LastVisitData ld where ld.LastVisitDataId = fc.lastVisitDataId "
			+ " AND ld.IsLive = 1 AND fxm.formId = :formId", nativeQuery = true)
	List<Object[]> findAllPercentValueByFormId(@Param("formId") Integer formId);

	@Override
	@Query("SELECT fxm.label,(fc.score / fxm.maxScore)*100,fc.lastVisitData.timPeriod.timeperiod,fxm.formXpathScoreId FROM "
			+ "FacilityScore fc JOIN fc.formXpathScoreMapping fxm "
			+ "WHERE fxm.parentXpathId=:parenXpathId " 
			+ "AND fc.lastVisitData.lastVisitDataId=:lastVisitDataId "
			+ " AND fc.lastVisitData.isLive = true  AND fxm.form.formId = :formId "
			+ " AND fxm.maxScore IS NOT NULL "
			+ "ORDER BY fxm.label ASC ")
	public List<Object[]> findSpiderDataChartByLasatVisitDatAndFormId(
			@Param("formId") Integer formId,
			@Param("lastVisitDataId") Integer lastVisitDataId,@Param("parenXpathId")Integer parenXpathId);

	@Override
	@Query("SELECT fxm.label,(AVG(fc.score) / fxm.maxScore)*100,fc.lastVisitData.timPeriod.timeperiod ,fxm.formXpathScoreId FROM "
			+ "FacilityScore fc JOIN fc.formXpathScoreMapping fxm "
			+ " WHERE  fxm.parentXpathId=:parenXpathId "
			+ " AND fxm.cboType =:cboType"
			+ " AND fc.formXpathScoreMapping.formXpathScoreId = fxm.formXpathScoreId "
			+ " AND fc.lastVisitData.isLive = true  AND fxm.form.formId = :formId "
			+ " AND fc.lastVisitData.timPeriod.timePeriodId = :timeperiodId"
			+ " AND fxm.maxScore IS NOT NULL "
			+ " GROUP BY fxm.label ,fxm.maxScore,fc.lastVisitData.timPeriod.timeperiod,fxm.formXpathScoreId "
			+ "ORDER BY fxm.label ASC ")
	public List<Object[]> findSpiderDataChartByFormId(
			@Param("formId") Integer formId,@Param("parenXpathId")Integer parenXpathId,
			@Param("timeperiodId")int timeperiodId,@Param("cboType")String cboType);
	
	@Override
	@Query("SELECT fxm.label,(AVG(fc.score) / fxm.maxScore)*100,fc.lastVisitData.timPeriod.timeperiod ,fxm.formXpathScoreId FROM "
			+ "FacilityScore fc JOIN fc.formXpathScoreMapping fxm,Area area "
			+ " WHERE  fxm.parentXpathId=:parenXpathId "
			+ " AND fc.formXpathScoreMapping.formXpathScoreId = fxm.formXpathScoreId "
			+ " AND area.parentAreaId=:areaId "
			+ " AND fxm.cboType =:cboType"
			+ " AND fc.lastVisitData.area.parentAreaId=area.areaId "
			+ " AND fc.lastVisitData.isLive = true  AND fxm.form.formId = :formId "
			+ " AND fxm.maxScore IS NOT NULL "
			+ " AND fc.lastVisitData.timPeriod.timePeriodId = :timeperiodId"
			+ " GROUP BY fxm.label ,fxm.maxScore,fc.lastVisitData.timPeriod.timeperiod,fxm.formXpathScoreId "
			+ "ORDER BY fxm.label ASC ")
	public List<Object[]> findSpiderDataChartByFormIdForDistrict(
			@Param("formId") Integer formId,@Param("parenXpathId")Integer parenXpathId,@Param("areaId")Integer areaId,
			@Param("timeperiodId")int timeperiodId,@Param("cboType")String cboType);
	
	@Override
	@Query("SELECT fxm.label,(AVG(fc.score) / fxm.maxScore)*100,fc.lastVisitData.timPeriod.timeperiod,fxm.formXpathScoreId FROM "
			+ "FacilityScore fc JOIN fc.formXpathScoreMapping fxm "
			+ "WHERE  fxm.parentXpathId=:parenXpathId "
			+ "AND fc.formXpathScoreMapping.formXpathScoreId = fxm.formXpathScoreId "
			+ "AND fc.lastVisitData.area.parentAreaId=:areaId "
			+ "AND fc.lastVisitData.isLive = true  AND fxm.form.formId = :formId "
			+ " AND fxm.maxScore IS NOT NULL "
			+ " AND fc.lastVisitData.timPeriod.timePeriodId = :timeperiodId"
			+ " GROUP BY fxm.label ,fxm.maxScore,fc.lastVisitData.timPeriod.timeperiod ,fxm.formXpathScoreId "
			+ "ORDER BY fxm.label ASC ")
	public List<Object[]> findSpiderDataChartByFormIdForDistrictForDGA(
			@Param("formId") Integer formId,@Param("parenXpathId")Integer parenXpathId,@Param("areaId")Integer areaId,@Param("timeperiodId")int timeperiodId);


	
	@Override
	@Query("SELECT fxm.label,(fc.score / fxm.maxScore)*100,fc.lastVisitData.timPeriod.timeperiod,fxm.formXpathScoreId FROM "
			+ " FacilityScore fc JOIN fc.formXpathScoreMapping fxm , LastVisitData lvd "
			+ " WHERE fxm.parentXpathId=:parenXpathId "
			+ " AND lvd.lastVisitDataId = :lastVisitDataId " 
			+ " AND fc.lastVisitData.area.areaId = lvd.area.areaId "
			+ " AND fxm.maxScore IS NOT NULL "
			+ " AND fc.lastVisitData.isLive = true  AND fxm.form.formId = :formId "
			+ " AND fc.lastVisitData.timPeriod.timePeriodId = :timeperiodId"
			+ " ORDER BY fxm.label ASC ")
	public List<Object[]> findSpiderDataChartByLasatVisitDatAndFormIdAndTimePeriodId(
			@Param("formId") Integer formId,@Param("lastVisitDataId")Integer areaId,@Param("parenXpathId")Integer parenXpathId,@Param("timeperiodId")int timeperiodId);

		@Override
		@Query("SELECT COUNT(fc.lastVisitData),fc.formXpathScoreMapping.cboType "
				+ " FROM FacilityScore fc WHERE "
				+ " fc.lastVisitData.isLive IS TRUE "
				+ " AND fc.formXpathScoreMapping.parentXpathId = -1"
				+ " GROUP BY fc.formXpathScoreMapping.cboType,fc.formXpathScoreMapping.formXpathScoreId"
				+ " ORDER BY fc.formXpathScoreMapping.formXpathScoreId ASC ")
		public List<Object[]> getCBOAssessed();
}
