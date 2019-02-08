/**
 * 
 */
package org.sdrc.cysdcbo.repository.springdatajpa;

import java.util.List;
import java.util.Set;

import org.sdrc.cysdcbo.domain.ChoicesDetails;
import org.sdrc.cysdcbo.repository.ChoiceDetailsRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
@RepositoryDefinition(domainClass=ChoicesDetails.class,idClass=Integer.class)
public interface SpringDataChoiceDetailsRepository extends
		ChoiceDetailsRepository {
	
	
	@Override
	@Query(" SELECT DISTINCT(choices.choiceValue) FROM ChoicesDetails choices , RawFormXapths rfxm "
			+ " WHERE rfxm.xPathId IN :xPathsRow "
			+ " AND 'select_one '+choices.choicName = rfxm.type "
			+ " ORDER BY choices.choiceValue DESC ")
	public Set<String> findByXpathIdAndType(@Param("xPathsRow")List<Integer> xPathsRow);

}
