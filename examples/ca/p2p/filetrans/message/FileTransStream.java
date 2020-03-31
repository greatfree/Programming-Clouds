package ca.p2p.filetrans.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 03/07/2020, Bing Li
public class FileTransStream extends OutMessageStream<FileTransRequest>
{

	public FileTransStream(ObjectOutputStream out, Lock lock, FileTransRequest message)
	{
		super(out, lock, message);
	}

}
