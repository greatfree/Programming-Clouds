package edu.chainnet.s3.edsa.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

import edu.chainnet.s3.message.SliceUploadRequest;
import edu.chainnet.s3.message.SliceUploadResponse;
import edu.chainnet.s3.message.SubmitEncodedSliceRequest;
import edu.chainnet.s3.message.SubmitEncodedSliceResponse;
import edu.chainnet.s3.message.S3AppID;

/*
 * The EDSA server focuses on data encoding/decoding. 07/11/2020, Bing Li
 * 
 * The tasks of an EDSA child node are accomplished through the program. 07/11/2020, Bing Li 
 */

// Created: 07/11/020, Bing Li
class EDSAChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.edsa.child");

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					EDSAChild.EDSA().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public MulticastResponse processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case S3AppID.SLICE_UPLOAD_REQUEST:
				log.info("SLICE_UPLOAD_REQUEST received @" + Calendar.getInstance().getTime());
				SliceUploadRequest fsur = (SliceUploadRequest)request;
				Coder.EDSA().enqueue(fsur);
				return new SliceUploadResponse(true, fsur.getCollaboratorKey());
				
			case S3AppID.SUBMIT_ENCODED_SLICE_REQUEST:
				log.info("SUBMIT_ENCODED_SLICE_REQUEST received @" + Calendar.getInstance().getTime());
				SubmitEncodedSliceRequest sesr = (SubmitEncodedSliceRequest)request;
				/*
				Slice slice;
				boolean isSucceeded = true;
				for (Map.Entry<String, SliceState> entry : sesr.getStates().entrySet())
				{
					slice = sesr.getSlices().get(entry.getKey());
					isSucceeded = Coder.EDSA().decode(entry.getKey(), slice.getKey(), slice.getSlice());
					if (!isSucceeded)
					{
						break;
					}
				}
				return new SubmitEncodedSliceResponse(isSucceeded, sesr.getCollaboratorKey());
				*/
				return new SubmitEncodedSliceResponse(Coder.EDSA().decode(sesr.getSlices()), sesr.getCollaboratorKey());

				/*
				 * Now the decoded slices are persisted at the storage cluster rather than the EDSA cluster. So the below message is sent to the storage cluster. 07/19/2020, Bing Li
				 */
				/*
			case S3AppID.DOWNLOAD_DECODED_FILE_REQUEST:
				System.out.println("DOWNLOAD_DECODED_FILE_REQUEST received @" + Calendar.getInstance().getTime());
				DownloadDecodedFileRequest ddfr = (DownloadDecodedFileRequest)request;
				break;
				
			case S3AppID.DOWNLOAD_DECODED_SLICE_REQUEST:
				System.out.println("DOWNLOAD_DECODED_SLICE_REQUEST received @" + Calendar.getInstance().getTime());
				DownloadDecodedSliceRequest ddsr = (DownloadDecodedSliceRequest)request;
				break;
				*/
		}
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(Response response)
	{
		// TODO Auto-generated method stub
		
	}

}
