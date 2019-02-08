/**
 * 
 */
package org.sdrc.cysdcbo.repository;

import java.sql.Timestamp;

import org.sdrc.cysdcbo.domain.UserLoginMeta;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public interface UserLoginMetaRepository {

	@Transactional
	UserLoginMeta save(UserLoginMeta userLoginMeta);

	void updateStatusForAll(Timestamp loggedOutDateTime);

	void updateStatus(Timestamp loggedOutDateTime, long userLoginMetaId);
}
