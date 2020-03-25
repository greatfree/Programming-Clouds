package org.greatfree.util;

import java.io.IOException;

/*
 * The interface is inherited when it is used to construct a method-based constructor, i.e., the builder pattern. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public interface Builder<T>
{
	public T build() throws IOException;
}
