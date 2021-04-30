package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 07/09/2020, Bing Li
public class SliceUploadRequest extends Request
{
	private static final long serialVersionUID = -701920528233776374L;
	
	// All of uploaded slices have the same session key. 07/12/2020, Bing Li
	private String sessionKey;
	private String fileName;
	private String sliceKey;
	private int partitionIndex;
	// The slice index. 07/11/2020, Bing Li
	private int index;
	// The encoded block ID. 07/11/2020, Bing Li
	private int edID;
	// The position in the encoded block. 07/11/2020, Bing Li
	private int position;
	private byte[] data;
	private String description;
	private boolean isDone;

	public SliceUploadRequest(String sessionKey, String fileName, String sliceKey, int partitionIndex, int index, int edID, int position, byte[] data, String description, boolean isDone)
	{
		/*
		 * Using the constructor, only one replica of the slice is saved in the cluster. 09/07/2020, Bing Li
		 */
		super(MulticastMessageType.UNICAST_REQUEST, S3AppID.SLICE_UPLOAD_REQUEST);
		
		/*
		 * The data in bytes is sent to the EDSA server to encode. So it is not correct to replicate. 09/08/2020, Bing Li
		 * 
		 * Using the constructor, multiple replicas of the slice are saved in the cluster. The value of replicas is equal to that of S3Config.REPICAS. Then, it is effective to avoid possible loss of data. 09/07/2020, Bing Li
		 */
//		super(MulticastMessageType.BROADCAST_REQUEST, S3AppID.SLICE_UPLOAD_REQUEST, Rand.getRandom(S3Config.REPICAS));
		this.sessionKey = sessionKey;
		this.fileName = fileName;
		this.sliceKey = sliceKey;
		this.partitionIndex = partitionIndex;
		this.index = index;
		this.edID = edID;
		this.position = position;
		this.data = data;
		this.description = description;
		this.isDone = isDone;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	public String getSliceKey()
	{
		return this.sliceKey;
	}
	
	public int getPartitionIndex()
	{
		return this.partitionIndex;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	public int getEDID()
	{
		return this.edID;
	}
	
	public int getPosition()
	{
		return this.position;
	}
	
	public byte[] getData()
	{
		return this.data;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public boolean isDone()
	{
		return this.isDone;
	}
}
