/**
 * 
 */
package org.sdrc.cysdcbo.repository.springdatajpa;

import java.util.List;
import java.util.Set;

import org.sdrc.cysdcbo.domain.RawDataScore;
import org.sdrc.cysdcbo.repository.RawDataScoreRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

/**
 * @author Harsh Pratyush(harsh@sdrc.co.in)
 *
 */

@RepositoryDefinition(domainClass = RawDataScore.class, idClass = Integer.class)
public interface SpringDataRawDataScoreRepository extends
		RawDataScoreRepository {

	
	@Query(" SELECT SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId "
			+ " THEN 1 ELSE 0 END) as SUMed,"
			+ " rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
			+ " FROM RawDataScore rowRds ,"
			+ " RawDataScore colRds,Area area "
			+ " ,ChoicesDetails rowChoice , ChoicesDetails colChoice"
			+ " WHERE colRds.rawFormXapths.xPathId IN :xPathsIdCol "
			+ " AND 'select_one '+rowChoice.choicName = rowRds.rawFormXapths.type "
			+ " AND 'select_one '+colChoice.choicName = colRds.rawFormXapths.type "
			+ " AND rowChoice.label = rowRds.score "
			+ " AND colChoice.label = colRds.score "
			+ " AND rowChoice.form.formId = rowRds.rawFormXapths.form.formId "
			+ " AND colChoice.form.formId = colRds.rawFormXapths.form.formId "
			+ " AND (area.parentAreaId = :districtId OR area.areaId=:districtId)"
			+ " AND rowRds.lastVisitData.area.parentAreaId = area.areaId"
			+ " AND colRds.lastVisitData.area.parentAreaId = area.areaId"
			+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
			+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
			+ " AND rowRds.score!=null AND rowRds.score!='' "
			+ " AND colRds.score!=null AND colRds.score!='' "
			+ " AND rowRds.lastVisitData.isLive = TRUE "
			+ " AND colRds.lastVisitData.isLive = TRUE "
			+ " GROUP BY rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
			+ " ORDER BY rowChoice.choiceValue DESC ,colChoice.choiceValue DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
	@Override
	public List<Object[]> findCrossTabReportForADistrictAndAllTimePeriod(
			@Param("xPathsRow")List<Integer> xPathsRow, @Param("xPathsIdCol") List<Integer> xPathsIdCol, @Param("districtId")int districtId);
	
	
	@Override
	@Query(" SELECT SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId "
			+ " THEN 1 ELSE 0 END) as SUMed,"
			+ " rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
			+ " FROM RawDataScore rowRds ,"
			+ " RawDataScore colRds ,Area area"
			+ " ,ChoicesDetails rowChoice , ChoicesDetails colChoice"
			+ " WHERE colRds.rawFormXapths.xPathId IN :xPathsIdCol "
			+ " AND 'select_one '+rowChoice.choicName = rowRds.rawFormXapths.type "
			+ " AND 'select_one '+colChoice.choicName = colRds.rawFormXapths.type "
			+ " AND rowChoice.form.formId = rowRds.rawFormXapths.form.formId "
			+ " AND colChoice.form.formId = colRds.rawFormXapths.form.formId "
			+ " AND rowChoice.label = rowRds.score "
			+ " AND colChoice.label = colRds.score "
			+ " AND (area.parentAreaId = :districtId OR area.areaId=:districtId)"
			+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
			+ " AND rowRds.lastVisitData.timPeriod.timePeriodId= :timePeriodId "
			+ " AND rowRds.lastVisitData.area.parentAreaId = area.areaId"
			+ " AND colRds.lastVisitData.area.parentAreaId = area.areaId"
			+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
			+ " AND rowRds.score!=null AND rowRds.score!='' "
			+ " AND colRds.score!=null AND colRds.score!='' "
			+ " AND rowRds.lastVisitData.isLive = TRUE "
			+ " AND colRds.lastVisitData.isLive = TRUE "
			+ " GROUP BY rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod"
			+ " ORDER BY rowChoice.choiceValue DESC ,colChoice.choiceValue DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabReportForADistrictAndATimePeriod(
				@Param("xPathsRow")List<Integer> xPathsRow, @Param("xPathsIdCol") List<Integer> xPathsIdCol,
				@Param("timePeriodId")int timePeriodId, @Param("districtId")int districtId);
		
		
		@Override
		@Query(" SELECT SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId "
				+ " THEN 1 ELSE 0 END) as SUMed,"
				+ " rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " RawDataScore colRds "
				+ " ,ChoicesDetails rowChoice , ChoicesDetails colChoice "
				+ " WHERE colRds.rawFormXapths.xPathId IN :xPathsIdCol "
				+ " AND 'select_one '+rowChoice.choicName = rowRds.rawFormXapths.type "
				+ " AND 'select_one '+colChoice.choicName = colRds.rawFormXapths.type "
				+ " AND rowChoice.form.formId = rowRds.rawFormXapths.form.formId "
				+ " AND colChoice.form.formId = colRds.rawFormXapths.form.formId "
				+ " AND rowChoice.label = rowRds.score "
				+ " AND colChoice.label = colRds.score "
				+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND colRds.score!=null AND colRds.score!='' "
				+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND colRds.lastVisitData.isLive = TRUE "
				+ " GROUP BY rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowChoice.choiceValue DESC ,colChoice.choiceValue DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC")
		public List<Object[]> findCrossTabReportForAllTimePeriod(
				@Param("xPathsRow")List<Integer> xPathsRow, @Param("xPathsIdCol") List<Integer> xPathsIdCol);
		
		
		@Override
		@Query(" SELECT SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId "
				+ " THEN 1 ELSE 0 END) as SUMed,"
				+ " rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " RawDataScore colRds "
				+ " ,ChoicesDetails rowChoice , ChoicesDetails colChoice "
				+ " WHERE colRds.rawFormXapths.xPathId IN :xPathsIdCol "
				+ " AND 'select_one '+rowChoice.choicName = rowRds.rawFormXapths.type "
				+ " AND 'select_one '+colChoice.choicName = colRds.rawFormXapths.type "
				+ " AND rowChoice.form.formId = rowRds.rawFormXapths.form.formId "
				+ " AND colChoice.form.formId = colRds.rawFormXapths.form.formId "
				+ " AND rowChoice.label = rowRds.score "
				+ " AND colChoice.label = colRds.score "
				+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId= :timePeriodId "
				+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND colRds.score!=null AND colRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND colRds.lastVisitData.isLive = TRUE "				
				+ " GROUP BY rowChoice.choiceValue,colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowChoice.choiceValue DESC ,colChoice.choiceValue DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabReportForATimePeriod(
				@Param("xPathsRow")List<Integer> xPathsRow, @Param("xPathsIdCol") List<Integer> xPathsIdCol,
				@Param("timePeriodId")int timePeriodId);
		
		
		
		@Override
		@Query("SELECT SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 "
				+ " THEN 1 ELSE 0 END) AS AtleastOne , "
				+" SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwo , "
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwo ,"
				+ " colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " RawDataScore colRds "
				+ " , ChoicesDetails colChoice "
				+ " WHERE colRds.rawFormXapths.xPathId IN :choiceType "
				+ " AND 'select_one '+colChoice.choicName = colRds.rawFormXapths.type "
				+ " AND colChoice.form.formId = colRds.rawFormXapths.form.formId "
				+ " AND colChoice.label = colRds.score "
				+ " AND rowRds.rawFormXapths.xPathId IN :intType "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND colRds.score!=null AND colRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND colRds.lastVisitData.isLive = TRUE "				
				+ " GROUP BY colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY colChoice.choiceValue DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForOnlyOneIntegerType(
				@Param("choiceType")List<Integer> choiceType,@Param("intType")List<Integer> intType,
				@Param("timePeriodIds")Set<Integer> timePeriodIds);
		
		
		
		@Override
		@Query("SELECT SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 "
				+ " THEN 1 ELSE 0 END) AS AtleastOne , "
				+" SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwo , "
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwo ,"
				+ " colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " RawDataScore colRds "
				+ " , ChoicesDetails colChoice ,Area area "
				+ " WHERE colRds.rawFormXapths.xPathId IN :choiceType "
				+ " AND 'select_one '+colChoice.choicName = colRds.rawFormXapths.type "
				+ " AND colChoice.form.formId = colRds.rawFormXapths.form.formId "
				+ " AND colChoice.label = colRds.score "
				+ " AND rowRds.rawFormXapths.xPathId IN :intType "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND colRds.score!=null AND colRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND colRds.lastVisitData.isLive = TRUE "
				+ " AND (area.parentAreaId = :districtId OR area.areaId=:districtId)"
				+ " AND rowRds.lastVisitData.area.parentAreaId = area.areaId "
				+ " AND colRds.lastVisitData.area.parentAreaId = area.areaId "
				+ " GROUP BY colChoice.choiceValue,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY colChoice.choiceValue DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForOnlyOneIntegerTypeADistrict(
				@Param("choiceType")List<Integer> choiceType,@Param("intType")List<Integer> intType,
				@Param("timePeriodIds")Set<Integer> timePeriodIds,@Param("districtId")int districtId);
	
		
		@Override
		@Query("SELECT SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 AND colRds.score >=1 "
				+ " THEN 1 ELSE 0 END) AS AtleastOneOne , "
				+ " SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 AND colRds.score >=2 "
				+ " THEN 1 ELSE 0 END) AS AtleastOneTwo , "
				+ " SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 AND colRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS AtleastOneMoreTwo , "
				+"  SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 AND colRds.score >= 1 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwoOne , "
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 AND colRds.score >= 2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwoTwo ,"
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 AND colRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwoMoreTwo ,"
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 AND colRds.score >= 1 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwoOne ,"
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 AND colRds.score >= 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwoTwo , "
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 AND colRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwoMoreTwo ,"
				+ " rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " RawDataScore colRds "
				+ " WHERE colRds.rawFormXapths.xPathId IN :xPathsIdCol "
				+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND colRds.score!=null AND colRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND colRds.lastVisitData.isLive = TRUE "				
				+ " GROUP BY rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForOnlyIntegerType(
				@Param("xPathsRow")List<Integer> xPathsRow, @Param("xPathsIdCol") List<Integer> xPathsIdCol,
				@Param("timePeriodIds")Set<Integer> timePeriodIds);

		
		@Override
		@Query("SELECT SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 AND colRds.score >=1 "
				+ " THEN 1 ELSE 0 END) AS AtleastOneOne , "
				+ " SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 AND colRds.score >=2 "
				+ " THEN 1 ELSE 0 END) AS AtleastOneTwo , "
				+ " SUM(CASE WHEN  rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=1 AND colRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS AtleastOneMoreTwo , "
				+"  SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 AND colRds.score >= 1 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwoOne , "
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 AND colRds.score >= 2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwoTwo ,"
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score >=2 AND colRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwoMoreTwo ,"
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 AND colRds.score >= 1 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwoOne ,"
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 AND colRds.score >= 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwoTwo , "
				+ " SUM(CASE WHEN rowRds.lastVisitData.lastVisitDataId = colRds.lastVisitData.lastVisitDataId AND rowRds.score > 2 AND colRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwoMoreTwo ,"
				+ " rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " RawDataScore colRds ,Area area "
				+ " WHERE colRds.rawFormXapths.xPathId IN :xPathsIdCol "
				+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+" 	AND rowRds.lastVisitData.lastVisitDataId=colRds.lastVisitData.lastVisitDataId"
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND colRds.score!=null AND colRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND colRds.lastVisitData.isLive = TRUE "	
				+ " AND (area.parentAreaId = :districtId OR area.areaId=:districtId)"
				+ " AND rowRds.lastVisitData.area.parentAreaId = area.areaId "
				+ " AND colRds.lastVisitData.area.parentAreaId = area.areaId "
				+ " GROUP BY rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForOnlyIntegerTypeForADistrict(
				@Param("xPathsRow")List<Integer> xPathsRow, @Param("xPathsIdCol") List<Integer> xPathsIdCol,
				@Param("timePeriodIds")Set<Integer> timePeriodIds, @Param("districtId")int districtId);
		
		
		
		@Override
		@Query("SELECT SUM(CASE WHEN  rowRds.score >=1 "
				+ " THEN 1 ELSE 0 END) AS AtleastOne , "
				+" SUM(CASE WHEN  rowRds.score >=2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwo , "
				+ " SUM(CASE WHEN  rowRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwo ,"
				+ " rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " Area area "
				+ " WHERE rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " AND (area.parentAreaId = :districtId OR area.areaId=:districtId)"
				+ " AND rowRds.lastVisitData.area.parentAreaId = area.areaId "
				+ " GROUP BY rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowRds.lastVisitData.xForm.formId DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForOnlyOneIntegerTypeADistrictByFacilityWise(
				@Param("xPathsRow")List<Integer> xPathsRow,
				@Param("timePeriodIds")Set<Integer> timePeriodIds, @Param("districtId")int districtId);
		
		
		@Override
		@Query("SELECT SUM(CASE WHEN  rowRds.score >=1 "
				+ " THEN 1 ELSE 0 END) AS AtleastOne , "
				+" SUM(CASE WHEN  rowRds.score >=2 "
				+ " THEN 1 ELSE 0 END) AS AtleastTwo , "
				+ " SUM(CASE WHEN  rowRds.score > 2 "
				+ " THEN 1 ELSE 0 END) AS MoreThanTwo ,"
				+ " rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds "
				+ " WHERE rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " GROUP BY rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowRds.lastVisitData.xForm.formId DESC ,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForOnlyOneIntegerTypeByFacilityWise(
				@Param("xPathsRow")List<Integer> xPathsRow,
				@Param("timePeriodIds")Set<Integer> timePeriodIds);
		
		
		
		@Override
		@Query(" SELECT COUNT(*) as count,"
				+ " rowChoice.choiceValue,rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " Area area , "
				+ " ChoicesDetails rowChoice"
				+ " WHERE 'select_one '+rowChoice.choicName = rowRds.rawFormXapths.type "
				+ " AND rowChoice.form.formId = rowRds.rawFormXapths.form.formId "
				+ " AND rowChoice.label = rowRds.score "
				+ " AND (area.parentAreaId = :districtId OR area.areaId=:districtId)"
				+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+ " AND rowRds.lastVisitData.area.parentAreaId = area.areaId"
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " GROUP BY rowChoice.choiceValue,rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowChoice.choiceValue DESC ,rowRds.lastVisitData.xForm.formId DESC,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForChoiceTypeADistrictByFacilityWise(
				@Param("xPathsRow")List<Integer> xPathsRow,
				@Param("timePeriodIds")Set<Integer> timePeriodIds, @Param("districtId")int districtId);
		
		
		@Override
		@Query(" SELECT COUNT(*) as count,"
				+ " rowChoice.choiceValue,rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod "
				+ " FROM RawDataScore rowRds ,"
				+ " ChoicesDetails rowChoice"
				+ " WHERE 'select_one '+rowChoice.choicName = rowRds.rawFormXapths.type "
				+ " AND rowChoice.form.formId = rowRds.rawFormXapths.form.formId "
				+ " AND rowChoice.label = rowRds.score "
				+ " AND rowRds.rawFormXapths.xPathId IN :xPathsRow "
				+ " AND rowRds.lastVisitData.timPeriod.timePeriodId IN :timePeriodIds "
				+ " AND rowRds.score!=null AND rowRds.score!='' "
				+ " AND rowRds.lastVisitData.isLive = TRUE "
				+ " GROUP BY rowChoice.choiceValue,rowRds.lastVisitData.xForm.formId,rowRds.lastVisitData.timPeriod.timeperiod"
				+ " ORDER BY rowChoice.choiceValue DESC ,rowRds.lastVisitData.xForm.formId DESC,rowRds.lastVisitData.timPeriod.timeperiod ASC ")
		public List<Object[]> findCrossTabForChoiceTypetByFacilityWise(
				@Param("xPathsRow")List<Integer> xPathsRow,
				@Param("timePeriodIds")Set<Integer> timePeriodIds);
}
