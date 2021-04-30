package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.ChildRootRequest;

/*
 * Each child of the collaborator needs to set up a DB on the disk. Since I need to test the system on a single computer, it is necessary for those children to exploit different paths. The progrmessageam keeps such a consistency. 10/20/2020, Bing Li
 */

// Created: 10/20/2020, Bing Li
public class PathsRequest extends ChildRootRequest
{
	private static final long serialVersionUID = 5832225248343672888L;

	public PathsRequest()
	{
		super(SCAppID.PATHS_REQUEST);
	}

}
