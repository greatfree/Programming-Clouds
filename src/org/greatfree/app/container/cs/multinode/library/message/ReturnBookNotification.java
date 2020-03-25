package org.greatfree.app.container.cs.multinode.library.message;

import org.greatfree.message.container.Notification;

// Created: 12/19/2018, Bing Li
public class ReturnBookNotification extends Notification
{
	private static final long serialVersionUID = -9136763829495151078L;
	
	private String bookTitle;
	private String author;

	public ReturnBookNotification(String bookTitle, String author)
	{
//		super(CSMessageType.NOTIFICATION, LibraryID.RETURN_BOOK_NOTIFICATION);
		super(LibraryID.RETURN_BOOK_NOTIFICATION);
		this.bookTitle = bookTitle;
		this.author = author;
	}

	public String getBookTitle()
	{
		return this.bookTitle;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
}
