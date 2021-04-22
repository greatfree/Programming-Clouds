package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.Request;

// Created: 07/14/2019, Bing Li
public class PollVendorRequest extends Request
{
	private static final long serialVersionUID = -2478918538714079156L;

	private String customerKey;
	private String vendorKey;
	private int vendorPostCount;
	private long timespan;
	
	public PollVendorRequest(String customerKey, String vendorKey, int vendorPostCount, long timespan)
	{
		super(customerKey, CommerceApplicationID.POLL_VENDOR_REQUEST);
		this.customerKey = customerKey;
		this.vendorKey = vendorKey;
		this.vendorPostCount = vendorPostCount;
		this.timespan = timespan;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public int getVendorPostCount()
	{
		return this.vendorPostCount;
	}
	
	public long getTimespan()
	{
		return this.timespan;
	}
}
