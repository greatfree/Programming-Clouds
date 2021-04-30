package edu.chainnet.s3.message;

import java.util.Map;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

import edu.chainnet.s3.storage.child.table.Slice;

/*
 * The encoded slice is sent to the EDSA cluster for decoding. 07/15/2020, Bing Li
 */

// Created: 07/15/2020, Bing Li
public class SubmitEncodedSliceRequest extends Request
{
	private static final long serialVersionUID = -6952407269113805423L;

//	private FileDescription file;
//	private Map<String, SliceState> states;
	private Map<String, Slice> encodedSlices;

//	public SubmitEncodedSliceRequest(FileDescription file, Map<String, SliceState> states, Map<String, Slice> slices)
	public SubmitEncodedSliceRequest(Map<String, Slice> slices)
	{
		/*
		 * Using the constructor, the request is sent to a random child of the EDSA cluster. 07/18/2020, Bing Li
		 */
		super(MulticastMessageType.UNICAST_REQUEST, S3AppID.SUBMIT_ENCODED_SLICE_REQUEST);
		
		/*
		 * Since I made a new design that the decoded slices are sent to the storage cluster for downloading, the below constructor is not proper. 07/19/2020, Bing Li
		 * 
		 * Using the constructor, the request is sent to the child whose key is nearest to the session key. It raises the performance of searching from the client since the client is allowed to search using unicasting. 07/18/2020, Bing Li
		 */
//		super(file.getSessionKey(), S3AppID.SUBMIT_ENCODED_SLICE_REQUEST);
//		this.file = file;
//		this.states = states;
		this.encodedSlices = slices;
	}

	/*
	public FileDescription getFile()
	{
		return this.file;
	}

	public Map<String, SliceState> getStates()
	{
		return this.states;
	}
	*/
	
	public Map<String, Slice> getSlices()
	{
		return this.encodedSlices;
	}
}
