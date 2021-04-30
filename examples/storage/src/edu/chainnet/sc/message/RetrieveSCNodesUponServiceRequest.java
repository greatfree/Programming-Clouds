package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 10/19/2020, Bing Li
public class RetrieveSCNodesUponServiceRequest extends Request
{
	private static final long serialVersionUID = -1531654209902552049L;
	
	private String serviceName;
	private boolean isHistorical;

	public RetrieveSCNodesUponServiceRequest(String serviceName, boolean isHistorical)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_SC_NODES_UPON_SERVICE_REQUEST);
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
