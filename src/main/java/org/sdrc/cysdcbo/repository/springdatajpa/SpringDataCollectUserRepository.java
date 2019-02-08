/**
 * 
 */
package org.sdrc.cysdcbo.repository.springdatajpa;

import org.sdrc.cysdcbo.domain.CollectUser;
import org.sdrc.cysdcbo.repository.CollectUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Harsh(harsh@sdrc.co.in)
 *
 */
public interface SpringDataCollectUserRepository extends
		JpaRepository<CollectUser, Integer>, CollectUserRepository {

}
