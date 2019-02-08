package org.sdrc.cysdcbo.model;

import java.util.List;


public class SectorModel {
	private Integer iC_NId;
	private Integer iC_Parent_NId;
	private String iC_Name;
	private List<Integer> utTimeperiods;
	
	
	public List<Integer> getUtTimeperiods() {
		return utTimeperiods;
	}
	public void setUtTimeperiods(List<Integer> utTimeperiods) {
		this.utTimeperiods = utTimeperiods;
	}
	public Integer getiC_NId() {
		return iC_NId;
	}
	public void setiC_NId(Integer iC_NId) {
		this.iC_NId = iC_NId;
	}
	public Integer getiC_Parent_NId() {
		return iC_Parent_NId;
	}
	public void setiC_Parent_NId(Integer iC_Parent_NId) {
		this.iC_Parent_NId = iC_Parent_NId;
	}
	public String getiC_Name() {
		return iC_Name;
	}
	public void setiC_Name(String iC_Name) {
		this.iC_Name = iC_Name;
	}
	

}
