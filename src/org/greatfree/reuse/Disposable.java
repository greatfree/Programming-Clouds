package org.greatfree.reuse;

import java.io.IOException;

import org.greatfree.util.FreeObject;

/*
 * The interface defines a method for the disposer that collects the resource in the resource pool, such as RetrievablePool. The resource must derive from FreeObject. 08/26/2014, Bing Li
 */

// Created: 08/26/2014, Bing Li
public interface Disposable<Resource extends FreeObject>
{
	/*
	 * The interface to dispose a resource. 08/26/2014, Bing Li
	 */
	public void dispose(Resource rsc) throws IOException;
}
