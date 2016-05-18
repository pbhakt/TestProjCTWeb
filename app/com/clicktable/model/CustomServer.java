package com.clicktable.model;




public class CustomServer extends Server
{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7732774388100845341L;
	private Integer totalSeatedTables;
	private Integer totalFinishedTables;
	private Integer totalSeatedCovers;
	private Integer totalFinishedCovers;
	
	
	
	public CustomServer()
	{
	    super();
	}
	
	
	public CustomServer(Server server)
	{
	  this.setAssignedTables(server.getAssignedTables());
	  this.setColorCode(server.getColorCode());
	  this.setCreatedBy(server.getCreatedBy());
	  this.setCreatedDate(server.getCreatedDate());
	  this.setGuid(server.getGuid());
	  this.setId(server.getId());
	  this.setLanguageCode(server.getLanguageCode());
	  this.setName(server.getName());
	  this.setRestaurantGuid(server.getRestaurantGuid());
	  this.setServerId(server.getServerId());
	  this.setStatus(server.getStatus());
	  this.setUpdatedBy(server.getUpdatedBy());
	  this.setUpdatedDate(server.getUpdatedDate());
	  
	    
	}


	public Integer getTotalSeatedTables() {
	    return totalSeatedTables;
	}


	public void setTotalSeatedTables(Integer totalSeatedTables) {
	    this.totalSeatedTables = totalSeatedTables;
	}


	public Integer getTotalFinishedTables() {
	    return totalFinishedTables;
	}


	public void setTotalFinishedTables(Integer totalFinishedTables) {
	    this.totalFinishedTables = totalFinishedTables;
	}


	public Integer getTotalSeatedCovers() {
	    return totalSeatedCovers;
	}


	public void setTotalSeatedCovers(Integer totalSeatedCovers) {
	    this.totalSeatedCovers = totalSeatedCovers;
	}


	public Integer getTotalFinishedCovers() {
	    return totalFinishedCovers;
	}


	public void setTotalFinishedCovers(Integer totalFinishedCovers) {
	    this.totalFinishedCovers = totalFinishedCovers;
	}
	
	
	
	

	

}
