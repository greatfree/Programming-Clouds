package org.greatfree.reuse;

import org.greatfree.util.HashFreeObject;

/*
 * The interface defines the method to dispose the objects that are derived from HashFreeObject. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public interface HashDisposable<Resource extends HashFreeObject>
{
	public void dispose(Resource t) throws InterruptedException;
}
