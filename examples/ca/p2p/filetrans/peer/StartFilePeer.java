package ca.p2p.filetrans.peer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.message.ChatPartnerResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Tools;

import ca.p2p.filetrans.message.FileTransResponse;

// Created: 03/07/2020, Bing Li
class StartFilePeer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		String localName;
		String partnerName;
		System.out.println("Please input your name: ");
		Scanner in = new Scanner(System.in);
		localName = in.nextLine();
		System.out.println("Please input your partner name: ");
		partnerName = in.nextLine();
		
		FilePeer.PEER().start(localName);

		System.out.println("Are you ready? Press Enter to continue ... ");
		in.nextLine();

		ChatPartnerResponse response = FilePeer.PEER().searchUser(Tools.getHash(partnerName));
		
		System.out.println("Partner's IP = " + response.getIP() + ":" + response.getPort());

		if (localName.equals("greatfree"))
		{
			System.out.println("Press enter to load your picture ...");
			in.nextLine();
			FileTransResponse fr = FilePeer.PEER().sendFile(response.getIP(), response.getPort(), "me.jpg", FileManager.loadFile("/home/libing/Wind/Data/me.jpg"));
			if (fr.isSucceeded())
			{
				System.out.println("The file is transmitted successfully!");
			}
			else
			{
				System.out.println("The file is failed to be transmitted");
			}
		}
		
		System.out.println("Press Enter to exit ...");
		in.nextLine();
		
		FilePeer.PEER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}

}
