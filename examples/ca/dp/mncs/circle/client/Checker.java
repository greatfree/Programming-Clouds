package ca.dp.mncs.circle.client;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;

import ca.dp.mncs.circle.message.PollLikeResponse;
import ca.dp.mncs.circle.message.PollPostResponse;

// Created: 02/28/2020, Bing Li
class Checker implements Runnable
{

	@Override
	public void run()
	{
		try
		{
			PollPostResponse postRes = CircleReader.RR().checkNewPosts();
			for (String entry : postRes.getPosts())
			{
				System.out.println(entry);
			}
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("------------------------------");
		
		try
		{
			PollLikeResponse likeRes = CircleReader.RR().checkNewLikes("006");
			for (String entry : likeRes.getFriends())
			{
				System.out.println(entry);
			}
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
