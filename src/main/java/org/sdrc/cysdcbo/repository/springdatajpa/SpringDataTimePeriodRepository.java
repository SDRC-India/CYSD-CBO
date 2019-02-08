package org.sdrc.cysdcbo.repository.springdatajpa;

import java.util.Set;

import org.sdrc.cysdcbo.domain.TimePeriod;
import org.sdrc.cysdcbo.repository.TimePeriodRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
/**
 * 
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
@RepositoryDefinition(domainClass=TimePeriod.class,idClass=Integer.class)
public interface SpringDataTimePeriodRepository extends TimePeriodRepository{

	
	@Override
	@Query ("SELECT tp.timeperiod FROM TimePeriod tp ORDER BY tp.timePeriodId DESC")
	public Set<String> findTimePeriods();
}
