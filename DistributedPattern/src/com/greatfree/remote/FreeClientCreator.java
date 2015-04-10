package com.greatfree.remote;

import java.io.IOException;

import com.greatfree.reuse.Creatable;

/*
 * The class contains the method to create an instance of FreeClient by its IP address and port number. It extends the interface of ResourceCreated and it is used as the resource creator in the RetrievablePool. 09/17/20214
 */

// Created: 09/17/2014, Bing Li
public class FreeClientCreator implements Creatable<IPPort, FreeClient>
{
	// The method to create instance of FreeClient by its IP address and the port number. 09/17/2024,
	@Override
	public FreeClient createResourceInstance(IPPort source) throws IOException
	{
		// Invoke the constructor of FreeClient. 09/17/2014, Bing Li
		return new FreeClient(source.getIP(), source.getPort());
	}
}
