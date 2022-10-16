package org.greatfree.reuse;

import java.io.IOException;

import org.greatfree.util.FreeObject;

/*
 * This is an interface to define a resource creator that initiates an instance of the resource in the resource pool, such as RetrievablePool. The resource must derive from FreeObject. 08/26/2014, Bing Li
 */

// Created: 08/26/2014, Bing Li
public interface Creatable<Source, Resource extends FreeObject>
{
	/*
	 * The interface to initiate an instance of the resource. The argument, source, encloses the initiation values for the instance. 08/26/2014, Bing Li
	 */
	public Resource createClientInstance(Source source) throws IOException;
}
