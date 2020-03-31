package edu.greatfree.container.p2p.peer;

import java.util.Calendar;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

import edu.greatfree.container.p2p.message.AddPartnerNotification;
import edu.greatfree.container.p2p.message.ChatNotification;
import edu.greatfree.container.p2p.message.P2PChatApplicationID;

// Created: 01/12/2019, Bing Li
class ChatTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case P2PChatApplicationID.ADD_PARTNER_NOTIFICATION:
				System.out.println("ADD_PARTNER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AddPartnerNotification apn = (AddPartnerNotification)notification;
				System.out.println(apn.getLocalUserName() + " says: " + apn.getInvitation());
				break;
				
			case P2PChatApplicationID.CHAT_NOTIFICATION:
				System.out.println("CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ChatNotification cn = (ChatNotification)notification;
				System.out.println(cn.getSenderName() + " says, " + cn.getMessage());
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
