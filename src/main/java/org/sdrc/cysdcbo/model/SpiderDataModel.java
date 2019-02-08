/**
 * Return the spider data for dashboard
 * 
 * @author Harsh(harsh@sdrc.co.in)
 * 
 */

package org.sdrc.cysdcbo.model;

/**
 * 
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
public class SpiderDataModel {
	
	private String axis ;
	private String value;
	private String timePeriod;
	private int fxmId;
	/**
	 * @return the axis
	 */
	public String getAxis() {
		return axis;
	}
	/**
	 * @param axis the axis to set
	 */
	public void setAxis(String axis) {
		this.axis = axis;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	public int getFxmId() {
		return fxmId;
	}
	public void setFxmId(int fxmId) {
		this.fxmId = fxmId;
	}
	
	

}
