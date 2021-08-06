package org.greatfree.demo.cluster.mncs.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 02/17/2019, Bing Li
public class MerchandiseRequest extends ClusterRequest
{
	private static final long serialVersionUID = 1542695247585948242L;

	private String vendorKey;
	private String merchandiseName;

	public MerchandiseRequest(String vendorKey, String merchandiseName)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, BusinessApplicationID.MERCHANDISE_REQUEST);
		this.vendorKey = vendorKey;
		this.merchandiseName = merchandiseName;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public String getMerchandiseName()
	{
		return this.merchandiseName;
	}
}
