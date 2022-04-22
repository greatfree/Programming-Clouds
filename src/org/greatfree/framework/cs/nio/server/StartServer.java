package org.greatfree.framework.cs.nio.server;

import java.io.IOException;

import org.greatfree.framework.cs.nio.Config;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class StartServer
{

	public static void main(String[] args) throws IOException
	{
		System.out.println("My server is starting up ...");
		MyServer.CS().start(Config.PORT);
		System.out.println("My server is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("My server is stopped ...");
	}

}
