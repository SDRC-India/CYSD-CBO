package org.sdrc.cysdcbo.repository;

import java.util.List;
import java.util.Set;

import org.sdrc.cysdcbo.domain.LastVisitData;
import org.springframework.dao.DataAccessException;

public interface LastVisitDataRepository {

	List<Object[]> getDataByFormId(Integer formId, Integer sectorId, int timeperiodId) throws DataAccessException;
	
	List<Object[]> getDataByFormIdAndAreaId(Integer formId, Integer sectorId,Integer areaId, int timeperiodId) throws DataAccessException;

	List<Object[]> getByLastVisitData(Integer lastVisitDataId)
			throws DataAccessException;

	List<Object[]> getDataByFormIdAndDistrictAreaId(Integer formId,
			Integer sectorId, Integer areaId, int timeperiodId);

	List <Object[]> getDataBySectorIdIdAndDistrictAreaId(Integer sectorId,
			Integer areaId, int timeperiodId);

	List<Object[]> getDataByparentSectorIdIdAndDistrictAreaId(Integer sectorId,
			Integer areaId, int timeperiodId);
	
	LastVisitData findByLastVisitDataIdAndIsLiveTrue(Integer lastVisitDataId);

	LastVisitData save(LastVisitData lastVisitDataLocal);

	LastVisitData findByxFormFormIdAndInstanceId(Integer formId,
			String instanceId);

	List<LastVisitData> findAll();

	List<Object[]> findMaxMinTimePeriodIdForADistrict(Integer areaId, Integer formId);

	List<Object[]> findMaxMinTimePeriodIdForState();

	List<Object[]> findMaxMinTimePeriodIdForAFacility(Integer lastVisitDataId);

	List<Object[]> findMaxMinTimePeriodIdForADistrictPHCCHC(Integer areaId,
			Integer formId);

	LastVisitData findByxFormFormIdAndAreaAreaIdAndTimPeriodTimePeriodId(
			Integer formId, Integer areaId, int i);

	List<LastVisitData> findByTimPeriodTimePeriodId(int i);


//	List<LastVisitData> findByAreaAreaCodeAndIsLiveTrueAndXFormFormIdNotEqualsOrderByTimPeriodTimePeriodIdAsc(
//			String areaCode, Integer i);

	List<LastVisitData> findByAreaAreaCodeAndIsLiveTrueAndXFormFormIdLessThanOrderByTimPeriodTimePeriodIdAsc(
			String areaCode, int i);

//	List<LastVisitData> findByIsLiveTrue();

	List<LastVisitData> findByIsLiveTrueAndxFormFormIdIn(Set<Integer> ids);

	List<LastVisitData> findByLatitudeIsNullAndIsLiveTrue();

	LastVisitData findByAreaAreaIdAndTimPeriodTimePeriodIdAndIsLiveTrueAndLatitudeIsNotNull(
			Integer areaId, int timePeriodId);

	List<LastVisitData> findAllByIsLiveTrueOrderByMarkedAsCompleteDateDesc();

}
