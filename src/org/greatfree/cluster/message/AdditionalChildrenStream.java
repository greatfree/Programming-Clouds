package org.greatfree.cluster.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 09/12/2020, Bing Li
public class AdditionalChildrenStream extends OutMessageStream<AdditionalChildrenRequest>
{

	public AdditionalChildrenStream(ObjectOutputStream out, Lock lock, AdditionalChildrenRequest message)
	{
		super(out, lock, message);
	}

}
