package edu.chainnet.s3.storage.child.table;

import org.greatfree.util.UniqueKey;

/*
 * The slice storage state is described by the class. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
public class SliceState extends UniqueKey
{
	private static final long serialVersionUID = 5592566998721768213L;
	
	private String fileName;
	private int edID;
	private int position;
	// Since the path can be generated upon the file path and the slice key quickly. It is not necessary to keep it. 07/19/2020, Bing Li
//	private String path;

//	public SliceState(String key, String fileName, int edID, int position, String path)
	public SliceState(String key, String fileName, int edID, int position)
	{
		super(key);
		this.fileName = fileName;
		this.edID = edID;
		this.position = position;
//		this.path = path;
	}
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public int getEDID()
	{
		return this.edID;
	}
	
	public int getPosition()
	{
		return this.position;
	}

	/*
	public String getPath()
	{
		return this.path;
	}
	*/
	
	public String toString()
	{
		return this.fileName + ": edID = " + this.edID + ", position = " + this.position;
	}
}
