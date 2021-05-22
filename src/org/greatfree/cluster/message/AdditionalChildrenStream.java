package org.greatfree.cluster.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 09/12/2020, Bing Li
public class AdditionalChildrenStream extends MessageStream<AdditionalChildrenRequest>
{

	public AdditionalChildrenStream(ObjectOutputStream out, Lock lock, AdditionalChildrenRequest message)
	{
		super(out, lock, message);
	}

}
