package org.sdrc.cysdcbo.repository;

import java.util.List;

import org.sdrc.cysdcbo.domain.Area;
import org.springframework.transaction.annotation.Transactional;

public interface AreaRepository {
	
	List<Area> findByAreaLevelAreaLevelId(Integer areaLevelId);
   
	List<Integer> findAreaIdByParentAreaId(Integer paremtAreaID);
	
	Area findByAreaId(Integer areaId);

	List<Area> findAll();

	Area findByAreaCode(String areaCode);

	List<Area> findTopOneByParentAreaIdOrderByAreaCodeDesc(Integer parentAreaId);

	@Transactional
	Area save(Area area);

	Area findByAreaCodeAndParentAreaId(String blockName, Integer parentAreaId);

	Area findByAreaNameAndParentAreaId(String otherBlock, Integer areaId);
}
