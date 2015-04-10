package com.greatfree.testing.memory;

import com.greatfree.reuse.HashCreatable;

/*
 * The creator to initialize the instance of IsPublisherExistedChildAnycastor. It is used by the resource pool to manage instances of IsPublisherExistedChildAnycastor efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedChildAnycastorCreator implements HashCreatable<IsPublisherExistedChildAnycastorSource, IsPublisherExistedChildAnycastor>
{
	@Override
	public IsPublisherExistedChildAnycastor createResourceInstance(IsPublisherExistedChildAnycastorSource source)
	{
		return new IsPublisherExistedChildAnycastor(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}
}
