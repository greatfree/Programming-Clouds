package org.greatfree.dip.multicast;

import java.io.Serializable;

/*
 * The testing data to be sent to distributed nodes in the multicasting manner. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class HelloWorld implements Serializable
{
	private static final long serialVersionUID = -3366357589324485114L;
	
	private String helloWorld;
	
	public HelloWorld(String helloWorld)
	{
		this.helloWorld = helloWorld;
	}

	public String getHelloWorld()
	{
		return this.helloWorld;
	}
}
