package com.greatfree.testing.coordinator.searching;

import com.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of IsPublisherExistedAnycastReader for the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastReaderCreator implements HashCreatable<IsPublisherExistedAnycastReaderSource, IsPublisherExistedAnycastReader>
{
	@Override
	public IsPublisherExistedAnycastReader createResourceInstance(IsPublisherExistedAnycastReaderSource source)
	{
		return new IsPublisherExistedAnycastReader(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
