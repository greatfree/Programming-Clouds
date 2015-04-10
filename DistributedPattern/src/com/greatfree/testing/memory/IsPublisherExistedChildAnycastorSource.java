package com.greatfree.testing.memory;

import com.greatfree.multicast.ChildMessageCreatorGettable;
import com.greatfree.multicast.ChildMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.IsPublisherExistedAnycastRequest;

/*
 * The source to create the instance of IsPublisherExistedChildAnycastor. It is used by the resource pool to use the anycastors efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedChildAnycastorSource extends ChildMulticastorSource<IsPublisherExistedAnycastRequest, IsPublisherExistedRequestCreator> implements ChildMessageCreatorGettable<IsPublisherExistedAnycastRequest>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedChildAnycastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, IsPublisherExistedRequestCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}

	/*
	 * Expose the request creator. 11/29/2014, Bing Li
	 */
	@Override
	public IsPublisherExistedRequestCreator getMessageCreator()
	{
		return super.getMessageCreator();
	}
}
