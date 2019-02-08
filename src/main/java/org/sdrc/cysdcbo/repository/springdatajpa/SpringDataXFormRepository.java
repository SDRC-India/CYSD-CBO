package org.sdrc.cysdcbo.repository.springdatajpa;

import java.util.List;

import org.sdrc.cysdcbo.domain.XForm;
import org.sdrc.cysdcbo.repository.XFormRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Harsh Pratyush(harsh@sdrc.co.in)
 * @author Sarita
 * @since version 1.0.0.0
 *
 */
public interface SpringDataXFormRepository extends XFormRepository, Repository<XForm, String>{
	
	XForm findByXFormIdAndIsLiveTrue(String getxFormId);
	
	@Override
	@Transactional
	@Modifying
	@Query("UPDATE XForm form SET form.isLive = FALSE WHERE form.formId= :xFormId")
	void updateIsLiveById(@Param("xFormId") Integer xFormId);
	
	@Override
	@Query("SELECT xform FROM XForm xform WHERE xform.isLive = True AND xform.xFormId=:xFormId")
	XForm findByxFormIdAndIsLiveTrue(@Param("xFormId") String xFormId);
	
	
	
	@Override
	@Query("Select xform.xFormId FROM XForm xform WHERE xform.isLive = True")
	public List<String> findAllXformNameByIsLiveTrue();
}
