package edu.chainnet.s3.storage.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.S3File;
import edu.chainnet.s3.message.AssemblePersistNotification;
import edu.chainnet.s3.message.DecodeSlicesRequest;
import edu.chainnet.s3.message.DecodeSlicesResponse;
import edu.chainnet.s3.message.DownloadDecodedFileRequest;
import edu.chainnet.s3.message.DownloadDecodedFileResponse;
import edu.chainnet.s3.message.DownloadDecodedSliceRequest;
import edu.chainnet.s3.message.DownloadDecodedSliceResponse;
import edu.chainnet.s3.message.InitializeChildrenNotification;
import edu.chainnet.s3.message.RetrieveFileRequest;
import edu.chainnet.s3.message.RetrieveFileResponse;
import edu.chainnet.s3.message.S3AppID;
import edu.chainnet.s3.message.SSStateRequest;
import edu.chainnet.s3.message.SSStateResponse;
import edu.chainnet.s3.message.SubmitEncodedSliceRequest;
import edu.chainnet.s3.storage.child.table.SSStore;
import edu.chainnet.s3.storage.StorageConfig;
import edu.chainnet.s3.storage.child.table.Slice;
import edu.chainnet.s3.storage.child.table.SliceState;

/*
 * The program defines the tasks accomplished by the child of the storage cluster. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
public class StorageChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage.child");

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
		String filePath;
		String sliceKey;
		String slicePath;
		switch (notification.getApplicationID())
		{
			case S3AppID.ASSEMBLE_PERSIST_NOTIFICATION:
				log.info("ASSEMBLE_PERSIST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AssemblePersistNotification apn = (AssemblePersistNotification)notification;
				if (!SSStore.STORE().isSessionExisted(apn.getSessionKey()))
				{
					filePath = S3File.getFilePath(SSFile.STORE().getFilesPath(), apn.getSessionKey());
					
					log.info("StorageChildTask: filePath = " + filePath);
					
					SSStore.STORE().put(apn.getSessionKey(), apn.getFileName(), apn.getDescription());
					SSStore.STORE().addPath(apn.getSessionKey(), apn.getFileName(), filePath);
				}
				else
				{
					filePath = SSStore.STORE().getPath(apn.getSessionKey());
				}
//				sliceKey = KeyCreator.getSliceStateKey(apn.getSessionKey(), apn.getEDID(), apn.getPosition());
				sliceKey = apn.getSliceKey();
				slicePath = S3File.getSlicePath(filePath, sliceKey, StorageConfig.S3_ENCODED_SUFFIX);
				try
				{
					log.info(apn.getEncodedSlice().length + " bytes are persisted at " + slicePath);
					S3File.persistSlice(slicePath, apn.getEncodedSlice());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
//				SSStore.STORE().put(apn.getSessionKey(), sliceKey, apn.getFileName(), filePath, apn.getEDID(), apn.getPosition(), slicePath);
//				SSStore.STORE().put(apn.getSessionKey(), sliceKey, apn.getFileName(), filePath, apn.getEDID(), apn.getPosition());
				SSStore.STORE().put(apn.getSessionKey(), sliceKey, apn.getFileName(), apn.getEDID(), apn.getPosition());
				break;
				
			case S3AppID.INITIALIZE_CHILDREN_NOTIFICATION:
				log.info("INITIALIZE_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				InitializeChildrenNotification icn = (InitializeChildrenNotification)notification;
				try
				{
					StorageChild.STORE().init(icn.getCollaboratorAddress());
				}
				catch (ClassNotFoundException | IOException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StorageChild.STORE().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case S3AppID.STOP_ONE_STORAGE_CHILD_NOTIFICATION:
				log.info("STOP_ONE_STORAGE_CHILD_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StorageChild.STORE().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
		String filePath;
		switch (request.getApplicationID())
		{
			case S3AppID.SSSTATE_REQUEST:
				log.info("SSSTATE_REQUEST received @" + Calendar.getInstance().getTime());
				SSStateRequest sssr = (SSStateRequest)request;
				return new SSStateResponse(SSFile.STORE().getSSState(), sssr.getCollaboratorKey());
				
			case S3AppID.RETRIEVE_FILE_REQUEST:
				log.info("RETRIEVE_FILE_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveFileRequest rfr = (RetrieveFileRequest)request;
				return new RetrieveFileResponse(SSStore.STORE().get(rfr.getFileName()), rfr.getCollaboratorKey());
				
			case S3AppID.DECODE_SLICES_REQUEST:
				log.info("DECODE_SLICES_REQUEST received @" + Calendar.getInstance().getTime());
				DecodeSlicesRequest dsr = (DecodeSlicesRequest)request;
				filePath = SSStore.STORE().getPath(dsr.getFile().getSessionKey());
				if (!filePath.equals(UtilConfig.EMPTY_STRING))
				{
					Map<String, SliceState> states = SSStore.STORE().getSlices(dsr.getFile().getSessionKey());
					Map<String, Slice> slices = new HashMap<String, Slice>();
					for (SliceState entry : states.values())
					{
						try
						{
							/*
							 * Since the slices are distributed evenly within the cluster. So the size of the loaded slices should NOT be large for each child. 07/19/2020, Bing Li
							 */
//							slices.put(entry.getKey(), new Slice(entry.getKey(), S3File.loadSlice(entry.getPath())));
							slices.put(entry.getKey(), new Slice(entry.getKey(), S3File.loadSlice(S3File.getSlicePath(filePath, entry.getKey(), StorageConfig.S3_ENCODED_SUFFIX))));
						}
						catch (IOException e)
						{
							return new DecodeSlicesResponse(false, dsr.getCollaboratorKey());
						}
					}
//						return new DecodeSlicesResponse(SSFile.STORE().submit(new SubmitEncodedSliceRequest(dsr.getFile(), states, slices)), dsr.getCollaboratorKey());
					try
					{
						return new DecodeSlicesResponse(SSFile.STORE().submit(dsr.getFile(), new SubmitEncodedSliceRequest(slices)), dsr.getCollaboratorKey());
					}
					catch (ClassNotFoundException | RemoteReadException | IOException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					/*
					 * It is possible a node does not have the slices for a file. If so, it should not cause the downloading process failed. 08/14/2020, Bing Li
					 */
//					return new DecodeSlicesResponse(false, dsr.getCollaboratorKey());
					return new DecodeSlicesResponse(true, dsr.getCollaboratorKey());
				}
				
			case S3AppID.DOWNLOAD_DECODED_FILE_REQUEST:
				log.info("DOWNLOAD_DECODED_FILE_REQUEST received @" + Calendar.getInstance().getTime());
				DownloadDecodedFileRequest ddfr = (DownloadDecodedFileRequest)request;
				return new DownloadDecodedFileResponse(SSStore.STORE().getSlices(ddfr.getFile().getSessionKey()), ddfr.getCollaboratorKey());
				
			case S3AppID.DOWNLOAD_DECODED_SLICE_REQUEST:
				log.info("DOWNLOAD_DECODED_SLICE_REQUEST received @" + Calendar.getInstance().getTime());
				DownloadDecodedSliceRequest ddsr = (DownloadDecodedSliceRequest)request;
				filePath = SSStore.STORE().getPath(ddsr.getSessionKey());
				log.info("StorageChildTask-processRequest(): filePath = " + filePath);
				byte[] slice;
				try
				{
//					String slicePath = S3File.getSlicePath(filePath, ddsr.getSliceKey(), StorageConfig.S3_DECODED_SUFFIX);
//					log.info("StorageChildTask-processRequest(): slicePath = " + slicePath);
					slice = S3File.loadSlice(S3File.getSlicePath(filePath, ddsr.getSliceKey(), StorageConfig.S3_DECODED_SUFFIX));
					return new DownloadDecodedSliceResponse(slice, ddsr.getCollaboratorKey());
				}
				catch (IOException e)
				{
					return new DownloadDecodedSliceResponse(ddsr.getCollaboratorKey());
				}
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
