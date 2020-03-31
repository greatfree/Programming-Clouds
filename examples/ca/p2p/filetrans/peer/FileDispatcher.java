package ca.p2p.filetrans.peer;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import ca.p2p.filetrans.message.FileTransConfig;
import ca.p2p.filetrans.message.FileTransRequest;
import ca.p2p.filetrans.message.FileTransResponse;
import ca.p2p.filetrans.message.FileTransStream;

// Created: 03/07/2020, Bing Li
class FileDispatcher extends ServerDispatcher<ServerMessage>
{
	private RequestDispatcher<FileTransRequest, FileTransStream, FileTransResponse, FileTransRequestThread, FileTransRequestThreadCreator> requestDispatcher;

	public FileDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<FileTransRequest, FileTransStream, FileTransResponse, FileTransRequestThread, FileTransRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new FileTransRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.requestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case FileTransConfig.FILE_DATA_REQUEST:
				System.out.println("FILE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.requestDispatcher.isReady())
				{
					super.execute(this.requestDispatcher);
				}
				this.requestDispatcher.enqueue(new FileTransStream(message.getOutStream(), message.getLock(), (FileTransRequest)message.getMessage()));
				break;
		}
		
	}

}
