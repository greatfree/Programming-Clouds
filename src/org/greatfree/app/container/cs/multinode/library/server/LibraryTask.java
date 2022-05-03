package org.greatfree.app.container.cs.multinode.library.server;

import java.util.Calendar;

import org.greatfree.app.container.cs.multinode.library.message.BorrowBookRequest;
import org.greatfree.app.container.cs.multinode.library.message.BorrowBookResponse;
import org.greatfree.app.container.cs.multinode.library.message.LibraryID;
import org.greatfree.app.container.cs.multinode.library.message.ReturnBookNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 12/19/2018, Bing Li
public class LibraryTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case LibraryID.RETURN_BOOK_NOTIFICATION:
				System.out.println("RETURN_BOOK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ReturnBookNotification rbNotifiication = (ReturnBookNotification)notification;
				LibraryDB.CS().save(rbNotifiication.getBookTitle(), rbNotifiication.getAuthor());
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case LibraryID.BORROW_BOOK_REQUEST:
				System.out.println("BORROW_BOOK_REQUEST received @" + Calendar.getInstance().getTime());
				BorrowBookRequest req = (BorrowBookRequest)request;
				String author = LibraryDB.CS().get(req.getBookTitle());

				return new BorrowBookResponse(req.getBookTitle(), author);
		}
		return null;
	}
}
