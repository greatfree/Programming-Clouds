package org.greatfree.message.multicast;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/*
 * The class encloses the output stream to send the response of ClusterIPResponse to the client. 04/30/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class PrimitiveClusterIPStream extends MessageStream<PrimitiveClusterIPRequest>
{

	public PrimitiveClusterIPStream(ObjectOutputStream out, Lock lock, PrimitiveClusterIPRequest message)
	{
		super(out, lock, message);
	}

}
