package ca.p2p.filetrans.message;

import org.greatfree.message.ServerMessage;

// Created: 03/07/2020, Bing Li
public class FileTransResponse extends ServerMessage
{
	private static final long serialVersionUID = 7003737696079965154L;
	
	private boolean isSucceeded;

	public FileTransResponse(boolean isSuc)
	{
		super(FileTransConfig.FILE_DATA_RESPONSE);
		this.isSucceeded = isSuc;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
