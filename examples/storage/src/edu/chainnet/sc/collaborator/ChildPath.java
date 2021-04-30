package edu.chainnet.sc.collaborator;

import java.io.Serializable;

import org.greatfree.util.UtilConfig;

// Created: 10/20/2020, Bing Li
public class ChildPath implements Serializable
{
	private static final long serialVersionUID = -5757063621187771727L;
	
	private String childID;
	private String bcPath;
	private String dsPath;
	private String historyBCPath;
	private String historyDSPath;
	
	public ChildPath(String childID, String bcPath, String dsPath, String historyBCPath, String historyDSPath)
	{
		this.childID = childID;
		this.bcPath = bcPath;
		this.dsPath = dsPath;
		this.historyBCPath = historyBCPath;
		this.historyDSPath = historyDSPath;
	}

	public String getChildID()
	{
		return this.childID;
	}
	
	public String getBCPath()
	{
		return this.bcPath;
	}
	
	public String getDSPath()
	{
		return this.dsPath;
	}
	
	public String getHistoryBCPath()
	{
		return this.historyBCPath;
	}
	
	public String getHistoryDSPath()
	{
		return this.historyDSPath;
	}
	
	public String toString()
	{
		return this.childID + UtilConfig.NEW_LINE + this.bcPath + UtilConfig.NEW_LINE + this.dsPath + UtilConfig.NEW_LINE + this.historyBCPath + UtilConfig.NEW_LINE + this.historyDSPath;
	}
}
