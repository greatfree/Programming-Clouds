package edu.chainnet.s3.message;

import java.util.Map;

import org.greatfree.message.multicast.MulticastResponse;

import edu.chainnet.s3.storage.child.table.SliceState;

/*
 * To lower the workload to the EDSA cluster, the response contains the slices keys of the file only. Then, the client downloads the slice according to its key one by one. This is an approach to lower the burden of the EDSA cluster. 07/18/2020, Bing Li
 */

// Created: 07/14/2020, Bing Li
public class DownloadDecodedFileResponse extends MulticastResponse
{
	private static final long serialVersionUID = 800016797698210424L;

	private Map<String, SliceState> states;
//	private boolean isSucceeded;

//	public DownloadDecodedFileResponse(Map<String, SliceState> states, boolean isSucceeded, String collaboratorKey)
	public DownloadDecodedFileResponse(Map<String, SliceState> states, String collaboratorKey)
	{
		super(S3AppID.DOWNLOAD_DECODED_FILE_RESPONSE, collaboratorKey);
		this.states = states;
//		this.isSucceeded = isSucceeded;
	}
	
	public Map<String, SliceState> getStates()
	{
		return this.states;
	}

	/*
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
	*/
}
