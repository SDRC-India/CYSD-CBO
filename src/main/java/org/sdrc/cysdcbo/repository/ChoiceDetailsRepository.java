/**
 * 
 */
package org.sdrc.cysdcbo.repository;

import java.util.List;
import java.util.Set;

import org.sdrc.cysdcbo.domain.ChoicesDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public interface ChoiceDetailsRepository {
	
	@Transactional
	void save(ChoicesDetails choicesDetails);

	List<ChoicesDetails> findAll();

	List<ChoicesDetails> findByFormFormId(Integer formId);

	List<ChoicesDetails> findByFormFormIdAndLabel(Integer formId, String blockName);

	/**
	 * This method will return the unique number of choices for some set of choice xpaths
	 * @param xPathsRow
	 * @return
	 */
	Set<String> findByXpathIdAndType(List<Integer> xPathsRow);

	List<ChoicesDetails> findByChoicName(String choiceType);

	List<ChoicesDetails> findByChoicNameAndLabelContaining(
			String choiceType, String choiceValueType);

}
