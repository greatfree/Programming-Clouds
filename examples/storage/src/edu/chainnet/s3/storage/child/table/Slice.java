package edu.chainnet.s3.storage.child.table;

import org.greatfree.util.UniqueKey;

/*
 * The class is updated. It contains not only the encoded data but also the decoded data. 07/19/2020, Bing Li
 * 
 * The class contains the encoded slice and the key of the slice. 07/15/2020, Bing Li
 */

// Created: 07/15/2020, Bing Li
public class Slice extends UniqueKey
{
	private static final long serialVersionUID = 5049692987037156590L;
	
	private byte[] slice;
	
	public Slice(String sliceKey, byte[] slice)
	{
		super(sliceKey);
		this.slice = slice;
	}

	public byte[] getSlice()
	{
		return this.slice;
	}
}
