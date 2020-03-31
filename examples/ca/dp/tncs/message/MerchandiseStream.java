package ca.dp.tncs.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/22/2020, Bing Li
public class MerchandiseStream extends OutMessageStream<MerchandiseRequest>
{

	public MerchandiseStream(ObjectOutputStream out, Lock lock, MerchandiseRequest message)
	{
		super(out, lock, message);
	}

}
