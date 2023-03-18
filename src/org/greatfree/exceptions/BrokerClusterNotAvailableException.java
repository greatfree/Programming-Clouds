package org.greatfree.exceptions;

/**
 * 
 * @author libing
 * 
 * 03/07/2023
 *
 */
public class BrokerClusterNotAvailableException extends Exception
{
	private static final long serialVersionUID = -3092275130483105996L;
	
	private String disabledPeerName;
	
	public BrokerClusterNotAvailableException(String dpn)
	{
		this.disabledPeerName = dpn;
	}

	public BrokerClusterNotAvailableException()
	{
		this.disabledPeerName = null;
	}

	public String getDisabledPeerName()
	{
		return this.disabledPeerName;
	}
	
	public String toString()
	{
		if (this.disabledPeerName != null)
		{
			return this.disabledPeerName + "'s server-side is disabled and no broker clusters are available to support the interaction!";
		}
		else
		{
			return "No broker clusters are available to support the interaction!";
		}
	}
}
