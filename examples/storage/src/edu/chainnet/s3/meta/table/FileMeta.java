package edu.chainnet.s3.meta.table;

import java.util.ArrayList;
import java.util.List;

import org.greatfree.util.UniqueKey;

/*
 * The class keeps the meta data of the files in the S3. 07/10/2020, Bing Li
 */

// Created: 07/10/2020, Bing Li
public class FileMeta extends UniqueKey
{
	private static final long serialVersionUID = 9194996401117191695L;

	private String fileName;
	private long fileSize;
	private int k;
	private int n;
	private List<String> addresses;
	
	public FileMeta(String sessionKey, String fn, long fs, int k, int n)
	{
		super(sessionKey);
		this.fileName = fn;
		this.fileSize = fs;
		this.k = k;
		this.n = n;
		this.addresses = new ArrayList<String>();
	}
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public long getFileSize()
	{
		return this.fileSize;
	}
	
	public int getK()
	{
		return this.k;
	}
	
	public int getN()
	{
		return this.n;
	}
	
	public List<String> getAddresses()
	{
		return this.addresses;
	}
}
