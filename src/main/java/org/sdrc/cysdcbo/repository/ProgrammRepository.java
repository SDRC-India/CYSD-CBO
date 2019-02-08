/**
 * 
 */
package org.sdrc.cysdcbo.repository;

import org.sdrc.cysdcbo.domain.Program;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public interface ProgrammRepository {

	Program findByProgramId(int i);

}
