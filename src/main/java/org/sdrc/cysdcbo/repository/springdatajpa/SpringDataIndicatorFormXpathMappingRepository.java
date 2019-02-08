/**
 * 
 */
package org.sdrc.cysdcbo.repository.springdatajpa;

import java.util.List;

import org.sdrc.cysdcbo.domain.IndicatorFormXpathMapping;
import org.sdrc.cysdcbo.repository.IndicatorFormXpathMappingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */

@RepositoryDefinition(domainClass=IndicatorFormXpathMapping.class,idClass=Integer.class)
public interface SpringDataIndicatorFormXpathMappingRepository extends
		IndicatorFormXpathMappingRepository {

	
	@Override
	@Query("SELECT indi.indicatorFormXpathMappingId,indi.label,indi.type,indi.sector,indi.dhXpath.xPathId"
			+ ",indi.chcXpath.xPathId,indi.phcXpath.xPathId FROM IndicatorFormXpathMapping indi WHERE indi.indicatorFormXpathMappingId IN "
			+ " (SELECT MAX(indi1.indicatorFormXpathMappingId) FROM IndicatorFormXpathMapping indi1 GROUP BY indi1.label )"
			+ " ORDER BY indi.label ASC ")
	public List<Object []> findDistinctLabels();
	
}
