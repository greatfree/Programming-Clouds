package org.greatfree.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

/*
 * The class encloses the output stream to send the response of PortResponse to the client. 05/01/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
public class PortStream extends OutMessageStream<PortRequest>
{

	public PortStream(ObjectOutputStream out, Lock lock, PortRequest message)
	{
		super(out, lock, message);
	}

}
