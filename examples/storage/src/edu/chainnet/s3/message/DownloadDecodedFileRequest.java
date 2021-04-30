package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * After the slices are decoded, they are merged to the original file at the EDSA cluster. The request is then sent to the EDSA cluster to download the file. 07/14/2020, Bing Li
 */

// Created: 07/14/2020, Bing Li
public class DownloadDecodedFileRequest extends Request
{
	private static final long serialVersionUID = -1406680661732771520L;
	
	private FileDescription file;

	public DownloadDecodedFileRequest(FileDescription file)
	{
		/*
		 * Now the strategy is changed. The slices are randomly persisted in the storage cluster. So broadcasting request is required. 07/19/2020, Bing Li
		 */
		super(MulticastMessageType.BROADCAST_REQUEST, S3AppID.DOWNLOAD_DECODED_FILE_REQUEST);

		/*
		 * Since decoded slices are retained on the EDSA cluster upon the session key through unicasting, the client is able to retrieve the information from the cluster through unicasting upon the session key. So the constructor is updated. 07/19/2020, Bing Li
		 */
//		super(file.getSessionKey(), S3AppID.DOWNLOAD_DECODED_FILE_REQUEST);
		this.file = file;
	}

	public FileDescription getFile()
	{
		return this.file;
	}
}
