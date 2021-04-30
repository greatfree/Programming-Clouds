package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * After the meta server receives the downloading file request, it sends the broadcast request to the cluster of storage server for decoding. Then, the storage cluster loads and sends the encoded slices to the EDSA server for decoding. 07/14/2020, Bing Li
 */

// Created: 07/14/2020, Bing Li
public class DecodeSlicesRequest extends Request
{
	private static final long serialVersionUID = -50499103512826450L;
	
	private FileDescription file;

	public DecodeSlicesRequest(FileDescription file)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, S3AppID.DECODE_SLICES_REQUEST);
		this.file = file;
	}

	public FileDescription getFile()
	{
		return this.file;
	}
}
