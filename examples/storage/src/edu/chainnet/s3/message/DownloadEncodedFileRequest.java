package edu.chainnet.s3.message;

import org.greatfree.message.container.Request;

import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * After a client confirms the file to be downloaded, the message is sent to the meta server. 07/14/2020, Bing Li
 */

// Created: 07/14/2020, Bing Li
public class DownloadEncodedFileRequest extends Request
{
	private static final long serialVersionUID = 7490396613186942711L;
	
	private FileDescription file;
	
	public DownloadEncodedFileRequest(FileDescription file)
	{
		super(S3AppID.DOWNLOAD_ENCODED_FILE_REQUEST);
		this.file = file;
	}
	
	public FileDescription getFile()
	{
		return this.file;
	}
}
