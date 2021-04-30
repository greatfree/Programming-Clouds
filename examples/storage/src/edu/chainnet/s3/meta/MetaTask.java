package edu.chainnet.s3.meta;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.message.DecodeSlicesRequest;
import edu.chainnet.s3.message.DownloadEncodedFileRequest;
import edu.chainnet.s3.message.DownloadEncodedFileResponse;
import edu.chainnet.s3.message.ReadRequest;
import edu.chainnet.s3.message.ReadResponse;
import edu.chainnet.s3.message.RetrieveFileRequest;
import edu.chainnet.s3.message.S3AppID;
import edu.chainnet.s3.message.SSStateRequest;
import edu.chainnet.s3.message.SlicePartitionRequest;
import edu.chainnet.s3.message.SlicePartitionResponse;
import edu.chainnet.s3.message.CreateSlicePartitionRequest;
import edu.chainnet.s3.message.CreateSlicePartitionResponse;
import edu.chainnet.s3.message.WriteRequest;
import edu.chainnet.s3.message.WriteResponse;
import edu.chainnet.s3.meta.table.FileMeta;
import edu.chainnet.s3.meta.table.MetaCache;
import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * This is the interaction between the client and the meta server. 07/13/2020, Bing Li
 * 
 * The program defines the tasks to be completed by the meta server. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
class MetaTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.meta");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case S3AppID.SCALE_CHANGING_NOTIFICATION:
				log.info("SCALE_CHANGING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				Controller.MAN().setPaused(true);
				break;
				
			case S3AppID.SCALE_CHANGED_NOTIFICATION:
				log.info("SCALE_CHANGED_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					MetaServer.META().resetPartitionSize();
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					e.printStackTrace();
				}
				Controller.MAN().setPaused(false);
				
				break;

			case S3AppID.SHUTDOWN_META_NOTIFICATION:
				log.info("SHUTDOWN_META_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					MetaServer.META().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case S3AppID.WRITE_REQUEST:
				log.info("WRITE_REQUEST received @" + Calendar.getInstance().getTime());
				WriteRequest ur = (WriteRequest)request;
				Date lastUpdated = MetaCache.META().getLastUpdated();
				// When the last updated time exceeds the predefine period, it is required to broadcast the cluster to the storage server to get their latest states. 07/13/2020, Bing Li
				try
				{
					if (lastUpdated != UtilConfig.NO_TIME)
					{
						if (Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), lastUpdated) > MetaConfig.SS_STATE_UPDATE_PERIOD)
						{
							// Update the latest states of the storage cluster. 07/13/2020, Bing Li
							MetaServer.META().updateSSStates(new SSStateRequest());
						}
					}
					else
					{
						// Update the latest states of the storage cluster. 07/13/2020, Bing Li
						MetaServer.META().updateSSStates(new SSStateRequest());
					}
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					e.printStackTrace();
				}

				// Evaluate the N and keeps the meta data of the new uploaded file. 07/13/2020, Bing Li
//				FileMeta fm = new FileMeta(ur.getSessionKey(), ur.getFileName(), ur.getSize(), ur.getK(), MetaManager.evaluateBlocksN(ur.getSize()));
				
				// 	This is a simplified solution to evaluate the value of N for testing to speed up the overall distributed architecture. 07/21/2020, Bing Li 
//				FileMeta fm = new FileMeta(ur.getSessionKey(), ur.getFileName(), ur.getSize(), ur.getK(), MetaManager.evaluateBlocksN(ur.getK()));
				FileMeta fm = new FileMeta(ur.getSessionKey(), ur.getFileName(), ur.getSize(), ur.getK(), MetaManager.evaluateBlocksN(ur.getSize(), ur.getK(), ur.getMaxSliceSize()));
				MetaCache.META().put(fm);
				if (fm.getN() != MetaConfig.INVALID_N)
				{
					return new WriteResponse(MetaServer.META().getEDSAIP(), MetaServer.META().getSSIP(), fm.getN());
				}
				else
				{
					return new WriteResponse();
				}
				
			case S3AppID.CREATE_SLICE_PARTITION_REQUEST:
				log.info("CREATE_SLICE_PARTITION_REQUEST received @" + Calendar.getInstance().getTime());
				CreateSlicePartitionRequest cspr = (CreateSlicePartitionRequest)request;
				// It is suggested to keep partition index concurrently to raise the performance. Now it is done sequentially. 09/09/2020, Bing Li
				return new CreateSlicePartitionResponse(MetaServer.META().createPartitionIndex(cspr.getSessionKey(), cspr.getSliceKey()), !Controller.MAN().isPaused());
				
			case S3AppID.SLICE_PARTITION_REQUEST:
				log.info("SLICE_PARTITION_REQUEST received @" + Calendar.getInstance().getTime());
				SlicePartitionRequest spr = (SlicePartitionRequest)request;
				return new SlicePartitionResponse(MetaServer.META().getPartitionIndex(spr.getSessionKey(), spr.getSliceKey()), !Controller.MAN().isPaused());
				
			case S3AppID.READ_REQUEST:
				log.info("READ_REQUEST received @" + Calendar.getInstance().getTime());
				ReadRequest rr = (ReadRequest)request;
				try
				{
					Map<String, FileDescription> files = MetaServer.META().retrieveFiles(new RetrieveFileRequest(rr.getFileName()));
					return new ReadResponse(files, MetaServer.META().getSSIP());
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					return new ReadResponse(S3Config.NO_FILES, MetaServer.META().getSSIP());
				}
				
			case S3AppID.DOWNLOAD_ENCODED_FILE_REQUEST:
				log.info("DOWNLOAD_ENCODED_FILE_REQUEST received @" + Calendar.getInstance().getTime());
				DownloadEncodedFileRequest dfr = (DownloadEncodedFileRequest)request;
				try
				{
					log.info("MetaTask-processRequest(): " + dfr.getFile());
					return new DownloadEncodedFileResponse(MetaServer.META().isDecodingSucceeded(new DecodeSlicesRequest(dfr.getFile())));
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					return new DownloadEncodedFileResponse(false);
				}
		}
		return null;
	}

}
