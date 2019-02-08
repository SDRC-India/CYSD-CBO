package org.sdrc.cysdcbo.repository;

import java.util.List;

import org.sdrc.cysdcbo.domain.IndicatorFormXpathMapping;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public interface IndicatorFormXpathMappingRepository {
	
	@Transactional
	<S extends IndicatorFormXpathMapping> List<S> save (Iterable<S> indicatorFormXpathMappings);

	List<IndicatorFormXpathMapping> findDistinctByLabelNotIn(List<String> names );

	List<Object[]> findDistinctLabels();

}
