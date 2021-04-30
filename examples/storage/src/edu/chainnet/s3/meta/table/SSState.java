package edu.chainnet.s3.meta.table;

import java.util.Date;

import org.greatfree.util.UniqueKey;

/*
 * The class keeps the states of storage servers. Those information can be used to evaluate the N of data blocks. 07/10/2020, Bing Li
 */

// Created: 07/10/2020, Bing Li
public class SSState extends UniqueKey
{
	private static final long serialVersionUID = 6778125388887546021L;
	
	private long totalStorageSpace;
	private long freeStorageSpace;
	private boolean isAlive;
	private Date updatedTime;
	
	public SSState(String key, long tss, long fss, boolean isAlive, Date ut)
	{
		super(key);
		this.totalStorageSpace = tss;
		this.freeStorageSpace = fss;
		this.isAlive = isAlive;
		this.updatedTime = ut;
	}
	
	public long getTotalStorageSpace()
	{
		return this.totalStorageSpace;
	}
	
	public long getFreeStorageSpace()
	{
		return this.freeStorageSpace;
	}
	
	public boolean isAlive()
	{
		return this.isAlive;
	}
	
	public Date getUpdatedTime()
	{
		return this.updatedTime;
	}
}
