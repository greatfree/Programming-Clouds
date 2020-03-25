package org.greatfree.multicast.root;

/*
 * The class is not used at all. 01/15/2019, Bing Li
 */

// Created: 11/04/2018, Bing Li
class SignalNotification
{
	private RootRendezvousPoint rp;
	private String collaboratorKey;

	public SignalNotification(RootRendezvousPoint rp, String collaboratorKey)
	{
		this.rp = rp;
		this.collaboratorKey = collaboratorKey;
	}

	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}

	public RootRendezvousPoint getRP()
	{
		return this.rp;
	}
}
