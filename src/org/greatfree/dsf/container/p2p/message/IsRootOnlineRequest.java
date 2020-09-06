package org.greatfree.dsf.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class IsRootOnlineRequest extends Request
{
	private static final long serialVersionUID = -593463368819973793L;

	private String rootID;
	private String childKey;

	public IsRootOnlineRequest(String rootID, String childKey)
	{
		super(P2PChatApplicationID.IS_ROOT_ONLINE_REQUEST);
		this.rootID = rootID;
		this.childKey = childKey;
	}

	public String getRootID()
	{
		return this.rootID;
	}
	
	public String getChildKey()
	{
		return this.childKey;
	}
}
