package ca.p2p.filetrans.peer;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

import ca.p2p.filetrans.message.FileTransRequest;
import ca.p2p.filetrans.message.FileTransResponse;
import ca.p2p.filetrans.message.FileTransStream;

// Created: 03/07/2020, Bing Li
class FileTransRequestThread extends RequestQueue<FileTransRequest, FileTransStream, FileTransResponse>
{

	public FileTransRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FileTransStream request;
		FileTransResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					FileManager.saveFile("/home/libing/Temp/" + request.getMessage().getFileName(), request.getMessage().getData());
					System.out.println(request.getMessage().getFileName() + ": size = " + request.getMessage().getData().length + " bytes, received and persisted!");
					response = new FileTransResponse(true);
				}
				catch (IOException e)
				{
					response = new FileTransResponse(false);
				}
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}				
				this.disposeMessage(request, response);
			}
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
