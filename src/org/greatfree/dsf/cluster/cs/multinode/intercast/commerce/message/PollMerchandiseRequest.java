package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.Request;

// Created: 07/14/2019, Bing Li
public class PollMerchandiseRequest extends Request
{
	private static final long serialVersionUID = -9177376895223023863L;
	
	private String customerKey;
	private String merchandiseKey;
	private int merchandisePostCount;
	private long timespan;

	public PollMerchandiseRequest(String customerKey, String merchandiseKey, int merchandisePostCount, long timespan)
	{
		super(customerKey, CommerceApplicationID.POLL_MERCHANDISE_REQUEST);
		this.customerKey = customerKey;
		this.merchandiseKey = merchandiseKey;
		this.merchandisePostCount = merchandisePostCount;
		this.timespan = timespan;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
	
	public int getMerchandisePostCount()
	{
		return this.merchandisePostCount;
	}
	
	public long getTimespan()
	{
		return this.timespan;
	}
}
