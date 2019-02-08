package org.sdrc.cysdcbo.repository;

import java.util.List;

import org.sdrc.cysdcbo.domain.CollectUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Harsh(harsh@sdrc.co.in)
 *
 */
public interface CollectUserRepository {

	CollectUser findByUsernameAndIsLiveTrue(String userName);

	CollectUser findByUsernameAndPasswordAndIsLiveTrue(String username,
			String password);

	CollectUser findByUserId(Integer userId);

	@Transactional
	CollectUser save(CollectUser collectUser);

	List<CollectUser> findByIsLiveTrue();
}
