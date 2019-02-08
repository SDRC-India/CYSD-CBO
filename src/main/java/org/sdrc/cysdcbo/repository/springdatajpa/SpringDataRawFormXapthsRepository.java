/**
 * 
 */
package org.sdrc.cysdcbo.repository.springdatajpa;

import org.sdrc.cysdcbo.domain.RawFormXapths;
import org.sdrc.cysdcbo.repository.RawFormXapthsRepository;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * @author Harsh Pratyush
 *
 */
@RepositoryDefinition(domainClass=RawFormXapths.class,idClass=Integer.class)
public interface SpringDataRawFormXapthsRepository extends
		RawFormXapthsRepository {

}
