package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 10/17/2020, Bing Li
public class RetrieveBCNodesUponServiceRequest extends Request
{
	private static final long serialVersionUID = -8600169131096416527L;
	
	private String serviceName;
	private boolean isHistorical;

	public RetrieveBCNodesUponServiceRequest(String serviceName, boolean isHistorical)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_BC_NODES_UPON_SERVICE_REQUEST);
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
