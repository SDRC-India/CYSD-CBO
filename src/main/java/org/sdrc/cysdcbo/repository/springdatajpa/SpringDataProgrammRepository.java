/**
 * 
 */
package org.sdrc.cysdcbo.repository.springdatajpa;

import org.sdrc.cysdcbo.domain.Program;
import org.sdrc.cysdcbo.repository.ProgrammRepository;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */

@RepositoryDefinition(domainClass=Program.class,idClass=Integer.class)
public interface SpringDataProgrammRepository extends ProgrammRepository {

}
