package edu.chainnet.center.message;

import org.greatfree.message.multicast.container.ChildRootRequest;

// Created: 04/27/2021, Bing Li
public class DataPathsRequest extends ChildRootRequest
{
	private static final long serialVersionUID = 5408550069080583644L;
	
	private String docPath;
	private String indexPath;
	private String cachePath;

	public DataPathsRequest(String docPath, String indexPath, String cachePath)
	{
		super(CenterApplicationID.DATA_PATHS_REQUEST);
		this.docPath = docPath;
		this.indexPath = indexPath;
		this.cachePath = cachePath;
	}

	public String getDocPath()
	{
		return this.docPath;
	}
	
	public String getIndexPath()
	{
		return this.indexPath;
	}
	
	public String getCachePath()
	{
		return this.cachePath;
	}
}

