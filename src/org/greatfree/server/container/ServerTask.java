package org.greatfree.server.container;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

/*
 * I think it is better to move the revision to the cryptography implementation rather than here. Then, it code looks more clean. 04/28/2022, Bing Li
 * 
 * The methods are not used often. It is only useful for internal multicasting when implementing new APIs. In the current cases, it is designed for cryptography-based multicasting and clustering. 04/28/2022, Bing Li
 * 
 * 	public void processNotification(ServerMessage notification);
 * 
 * public ServerMessage processRequest(ServerMessage request);
 * 
 */

// Created: 12/18/2018, Bing Li
public interface ServerTask
{
	public void processNotification(Notification notification);
	
	/*
	 * The method is not used often. It is only useful for internal multicasting when implementing new APIs. In the current cases, it is designed for cryptography-based multicasting and clustering. 04/28/2022, Bing Li
	 */
//	public void processNotification(ServerMessage notification);

	public ServerMessage processRequest(Request request);
	
	/*
	 * The method is not used often. It is only useful for internal multicasting when implementing new APIs. In the current cases, it is designed for cryptography-based multicasting and clustering. 04/28/2022, Bing Li
	 */
//	public ServerMessage processRequest(ServerMessage request);
}


