package ca.streaming.news.subscriber;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.subscriber.SubscriberDB;
import org.greatfree.util.FileManager;

import ca.streaming.news.message.VideoSearchRequest;
import ca.streaming.news.message.VideoSearchResponse;
import ca.streaming.news.message.VideoSearchStream;
import ca.streaming.news.publisher.NewsConfig;

// Created: 04/03/2020, Bing Li
class VideoSearchRequestThread extends RequestQueue<VideoSearchRequest, VideoSearchStream, VideoSearchResponse>
{

	public VideoSearchRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		VideoSearchStream request;
		VideoSearchResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.getRequest();
					if (SubscriberDB.DB().search(request.getMessage().getQuery()).size() > 0)
					{
						response = new VideoSearchResponse(FileManager.loadFile(NewsConfig.FILE_DES_PATH, request.getMessage().getIndex() * NewsConfig.PIECE_SIZE, (request.getMessage().getIndex() + 1) * NewsConfig.PIECE_SIZE));
					}
					else
					{
						response = new VideoSearchResponse(null);
					}
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
