package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 10/19/2020, Bing Li
public class RetrieveONNodesUponServiceRequest extends Request
{
	private static final long serialVersionUID = -3522572124694937778L;
	
	private String serviceName;
	private boolean isHistorical;

	public RetrieveONNodesUponServiceRequest(String serviceName, boolean isHistorical)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_ON_NODES_UPON_SERVICE_REQUEST);
		this.serviceName = serviceName;
		this.isHistorical = isHistorical;
	}

	public String getServiceName()
	{
		return this.serviceName;
	}
	
	public boolean isHistorical()
	{
		return this.isHistorical;
	}
}
