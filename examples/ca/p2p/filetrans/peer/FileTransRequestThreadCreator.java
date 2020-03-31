package ca.p2p.filetrans.peer;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.p2p.filetrans.message.FileTransRequest;
import ca.p2p.filetrans.message.FileTransResponse;
import ca.p2p.filetrans.message.FileTransStream;

// Created: 03/07/2020, Bing Li
class FileTransRequestThreadCreator implements RequestThreadCreatable<FileTransRequest, FileTransStream, FileTransResponse, FileTransRequestThread>
{

	@Override
	public FileTransRequestThread createRequestThreadInstance(int taskSize)
	{
		return new FileTransRequestThread(taskSize);
	}

}
