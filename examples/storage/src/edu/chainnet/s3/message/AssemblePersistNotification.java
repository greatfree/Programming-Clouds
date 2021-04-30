package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

/*
 * The encoded slice is sent to the storage cluster through the message. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
public class AssemblePersistNotification extends Notification
{
	private static final long serialVersionUID = -7794005269924418266L;

	private String sessionKey;
	private String fileName;
	private String sliceKey;
	private byte[] encodedSlice;
	private int edID;
	private int position;
	private String description;

	public AssemblePersistNotification(String sessionKey, String fileName, String sliceKey, int partitionIndex, byte[] encodedSlice, int edID, int position, String description)
	{
		// The edID should be String. 07/11/2020, Bing Li
		
		/*
		 * If using the constructor, all of the encoded slices are sent to the same child of the storage cluster. But it is not a secure solution. 07/15/2020, Bing Li
		 */
//		super(EDSAConfig.EDID_PREFIX + edID, MulticastMessageType.UNICAST_NOTIFICATION, S3AppID.ASSEMBLE_PERSIST_NOTIFICATION);
		
		/*
		 * Using the constructor, the encoded slices are distributed evenly among the children of the storage cluster. 07/15/2020, Bing Li
		 */
//		super(MulticastMessageType.UNICAST_NOTIFICATION, S3AppID.ASSEMBLE_PERSIST_NOTIFICATION, partitionIndex);
		
		/*
		 * To replicate data within a partition, the below constructor is required. 09/09/2020, Bing Li
		 */
		super(MulticastMessageType.BROADCAST_NOTIFICATION, S3AppID.ASSEMBLE_PERSIST_NOTIFICATION, partitionIndex);
		this.sessionKey = sessionKey;
		this.fileName = fileName;
		this.sliceKey = sliceKey;
		this.encodedSlice = encodedSlice;
		this.edID = edID;
		this.position = position;
		this.description = description;
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
	
	public byte[] getEncodedSlice()
	{
		return this.encodedSlice;
	}
	
	public int getEDID()
	{
		return this.edID;
	}
	
	public int getPosition()
	{
		return this.position;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
