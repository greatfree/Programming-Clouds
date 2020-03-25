package org.greatfree.message.multicast;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

/*
 * The class encloses the output stream to send the response of ClusterIPResponse to the client. 04/30/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class ClusterIPStream extends OutMessageStream<ClusterIPRequest>
{

	public ClusterIPStream(ObjectOutputStream out, Lock lock, ClusterIPRequest message)
	{
		super(out, lock, message);
	}

}
