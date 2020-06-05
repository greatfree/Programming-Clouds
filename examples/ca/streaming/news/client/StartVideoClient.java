package ca.streaming.news.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.FileManager;

import ca.streaming.news.publisher.NewsConfig;

// Created: 04/05/2020, Bing Li
class StartVideoClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ServiceAccessor.UNI().init();

		Scanner in = new Scanner(System.in);
		String option = "n";
		String publisher;
		String category;
		String query;
	
		do
		{
			if (option.equals("n"))
			{
				System.out.println("Tell me your favorite publisher:");
				publisher = in.nextLine();
				System.out.println("Tell me your favorite category:");
				category = in.nextLine();
				ServiceAccessor.UNI().setSubscriberAddress(publisher, category);
			}
			
			System.out.println("Search query:");
			query = in.nextLine();
			
			if (ServiceAccessor.UNI().isVideoExisted(query))
			{
				System.out.println("Search Results");
				System.out.println("--------------------------------------------");
				System.out.println("The video is existed for downloading ...");
				byte[] videoPiece;
				for (int i = 0; i < NewsConfig.MAX_PIECES_SIZE; i++)
				{
					videoPiece = ServiceAccessor.UNI().searchVideo(query, i);
					if (videoPiece != null)
					{
						FileManager.saveFile(NewsConfig.FILE_USER_DES_PATH, videoPiece, true);
						Thread.sleep(StreamConfig.TIMEOUT + NewsConfig.USER_TIMEOUT);
					}
					else
					{
						break;
					}
				}
				System.out.println("The video is downloaded ...");
			}
			else
			{
				System.out.println("The video is NOT existed for downloading ...");
			}
			System.out.println("--------------------------------------------");
			System.out.println("Continue the same subscription? (y/n/q)?");
			option = in.nextLine();
		}
		while (!option.equals("q"));

		ServiceAccessor.UNI().dispose();
		in.close();
	}

}
