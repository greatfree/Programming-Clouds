package ca.p2p.filetrans.message;

import org.greatfree.message.ServerMessage;

// Created: 03/07/2020, Bing Li
public class FileTransRequest extends ServerMessage
{
	private static final long serialVersionUID = 6273656297072639854L;
	
	private String fileName;
	private byte[] data;

	public FileTransRequest(String fn, byte[] data)
	{
		super(FileTransConfig.FILE_DATA_REQUEST);
		this.fileName = fn;
		this.data = data;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	public byte[] getData()
	{
		return this.data;
	}
}
