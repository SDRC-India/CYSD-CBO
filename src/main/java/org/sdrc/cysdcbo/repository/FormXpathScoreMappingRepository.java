package org.sdrc.cysdcbo.repository;
/**
 * @author Harsh(harsh@sdrc.co.in)
 */
import java.util.List;

import org.sdrc.cysdcbo.domain.FormXpathScoreMapping;
import org.springframework.transaction.annotation.Transactional;

public interface FormXpathScoreMappingRepository {
	
	public List<FormXpathScoreMapping> findByParentXpathId(Integer parentId);
	
	public FormXpathScoreMapping findByFormXpathScoreId(Integer formXpathScoreId);
	
	public List<FormXpathScoreMapping> findAll();

	public FormXpathScoreMapping findByFormXpathScoreIdAndDistrictId(Integer formXpathScoreId,Integer DistrictId);
	
	public FormXpathScoreMapping findByFormXpathScoreIdAndDistrictIdForDga(Integer formXpathScoreId,Integer DistrictId);

	public List<FormXpathScoreMapping> findByFormIdAndWithNoChildren(
			Integer formId);

	@Transactional
	public FormXpathScoreMapping save(FormXpathScoreMapping formXpathScoreMapping);

	public int findLastId();

	public List<FormXpathScoreMapping> findByFormIdAndWithNoChildren(
			Integer formId, String type);
}
