package edu.chainnet.s3.message;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * All the files which have the same name are returned to the client for further indication. 07/13/2020, Bing Li
 */

// Created: 07/13/2020, Bing Li
public class ReadResponse extends ServerMessage
{
	private static final long serialVersionUID = -4130308769066809973L;

	// All the files which have the same name are returned to the client for further indication. 07/13/2020, Bing Li
	private Map<String, FileDescription> files;
	private IPAddress ssIP;

	public ReadResponse(Map<String, FileDescription> files, IPAddress ssIP)
	{
		super(S3AppID.READ_RESPONSE);
		this.files = files;
		this.ssIP = ssIP;
	}

	public Map<String, FileDescription> getFiles()
	{
		return this.files;
	}
	
	public IPAddress getSSIP()
	{
		return this.ssIP;
	}
}
