package ca.dp.mncs.circle.client;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

import ca.dp.mncs.circle.message.CircleConfig;
import ca.dp.mncs.circle.message.PollLikeRequest;
import ca.dp.mncs.circle.message.PollLikeResponse;
import ca.dp.mncs.circle.message.PollPostRequest;
import ca.dp.mncs.circle.message.PollPostResponse;
import ca.dp.mncs.circle.message.PostRequest;
import ca.dp.mncs.circle.message.PostResponse;

// Created: 02/26/2020, Bing Li
class CircleReader
{
	private CircleReader()
	{
	}
	
	private static CircleReader instance = new CircleReader();
	
	public static CircleReader RR()
	{
		if (instance == null)
		{
			instance = new CircleReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init()
	{
		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
	}

	public void shutdown() throws IOException
	{
		RemoteReader.REMOTE().shutdown();
	}

	public PostResponse post(String post) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), CircleConfig.CIRCLE_SERVER_IP, CircleConfig.CIRCLE_SERVER_PORT, new PostRequest(post)));
	}

	public PollPostResponse checkNewPosts() throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PollPostResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), CircleConfig.CIRCLE_SERVER_IP, CircleConfig.CIRCLE_SERVER_PORT, new PollPostRequest()));
	}

	public PollLikeResponse checkNewLikes(String postID) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PollLikeResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), CircleConfig.CIRCLE_SERVER_IP, CircleConfig.CIRCLE_SERVER_PORT, new PollLikeRequest(postID)));
	}
}
