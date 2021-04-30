package edu.chainnet.s3.message;

import java.util.Map;

import org.greatfree.message.multicast.MulticastResponse;

import edu.chainnet.s3.storage.child.table.Slice;

// Created: 07/15/2020, Bing Li
public class SubmitEncodedSliceResponse extends MulticastResponse
{
	private static final long serialVersionUID = 8780426700556869214L;

	private Map<String, Slice> decodedSlices;
//	private boolean isSucceeded;

//	public SubmitEncodedSliceResponse(Map<String, Slice> decodedSlices, boolean isSucceeded, String collaboratorKey)
	public SubmitEncodedSliceResponse(Map<String, Slice> decodedSlices, String collaboratorKey)
	{
		super(S3AppID.SUBMIT_ENCODED_SLICE_RESPONSE, collaboratorKey);
		this.decodedSlices = decodedSlices;
//		this.isSucceeded = isSucceeded;
	}
	
	public Map<String, Slice> getSlices()
	{
		return this.decodedSlices;
	}

	/*
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
	*/
}
