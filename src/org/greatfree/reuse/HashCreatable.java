package org.greatfree.reuse;

import org.greatfree.util.HashFreeObject;

/*
 * The interface defines the method to create instances which extend HashFreeObject. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public interface HashCreatable<Source, Resource extends HashFreeObject>
{
	public Resource createResourceInstance(Source source);
}
