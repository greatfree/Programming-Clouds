package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

// Created: 10/09/2018, Bing Li
public class IsRootOnlineRequest extends ServerMessage
{
	private static final long serialVersionUID = -787638633075833431L;
	
	private String rootID;

	public IsRootOnlineRequest(String rootID)
	{
		super(ClusterMessageType.IS_ROOT_ONLINE_REQUEST);
		this.rootID = rootID;
	}

	public String getRootID()
	{
		return this.rootID;
	}
}
