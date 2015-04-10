package com.greatfree.testing.coordinator.searching;

import com.greatfree.multicast.RootAnycastReaderSource;
import com.greatfree.multicast.RootAnycastRequestCreatorGettable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.IsPublisherExistedAnycastRequest;

/*
 * The source contains all of the arguments to create the instance of anycast reader, IsPublisherExistedAnycastReader. It is used by the resource pool that manages the instances of IsPublisherExistedAnycastReader. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastReaderSource extends RootAnycastReaderSource<String, IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastRequestCreator> implements RootAnycastRequestCreatorGettable<IsPublisherExistedAnycastRequest, String>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedAnycastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, IsPublisherExistedAnycastRequestCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}

	/*
	 * Expose the anycast request creator. 11/29/2014, Bing Li
	 */
	@Override
	public IsPublisherExistedAnycastRequestCreator getRequestCreator()
	{
		return super.getCreator();
	}
}
