package edu.chainnet.s3.edsa.child;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.greatfree.client.StandaloneClient;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.edsa.CodingTool;
import edu.chainnet.s3.edsa.EDSAConfig;
import edu.chainnet.s3.message.AssemblePersistNotification;
import edu.chainnet.s3.message.SliceUploadRequest;
import edu.chainnet.s3.storage.child.table.Slice;

/*
 * The program performs the services of encoding/decoding for data blocks concurrently. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
class Coder
{
	private NotificationDispatcher<SliceUploadRequest, EncodeThread, EncodeThreadCreator> encoderDispatcher;
	private ThreadPool pool;
	private String registryIP;
	private int registryPort;
	private IPAddress storageAddress;

	private final static Logger log = Logger.getLogger("edu.chainnet.s3.edsa.child");

	/*
	 * No slices are persisted on the EDSA cluster. So the path is not needed. 07/19/2020, Bing Li
	 */
//	private String filesPath;

	private Coder()
	{
	}
	
	private static Coder instance = new Coder();
	
	public static Coder EDSA()
	{
		if (instance == null)
		{
			instance = new Coder();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.pool.shutdown(EDSAConfig.ENCODING_THREAD_POOL_SHUTDOWN_TIMEOUT);
		Scheduler.GREATFREE().shutdown(EDSAConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
		this.encoderDispatcher.dispose();
		StandaloneClient.CS().dispose();
	}

//	public void init(String filesPath, String registryIP, int registryPort) throws ClassNotFoundException, RemoteReadException, IOException
	public void init(String registryIP, int registryPort) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		this.filesPath = filesPath;
		
		Scheduler.GREATFREE().init(EDSAConfig.SCHEDULER_THREAD_POOL_SIZE, EDSAConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);
		
		this.encoderDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SliceUploadRequest, EncodeThread, EncodeThreadCreator>()
				.poolSize(EDSAConfig.ENCODING_DISPATCHER_POOL_SIZE)
				.threadCreator(new EncodeThreadCreator())
				.notificationQueueSize(EDSAConfig.ENCODING_QUEUE_SIZE)
				.dispatcherWaitTime(EDSAConfig.ENCODING_DISPATCHER_WAIT_TIME)
				.waitRound(EDSAConfig.ENCODING_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(EDSAConfig.ENCODING_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(EDSAConfig.ENCODING_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.build();
		
		this.pool = new ThreadPool(EDSAConfig.ENCODING_DISPATCHER_POOL_SIZE, EDSAConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		StandaloneClient.CS().init();
		
		this.registryIP = registryIP;
		this.registryPort = registryPort;
		
//		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP,  registryPort, new PeerAddressRequest(S3Config.STORAGE_SERVER_KEY));
//		this.storageAddress = response.getPeerAddress();
		this.storageAddress = UtilConfig.NO_IP_ADDRESS;
	}
	
	public void enqueue(SliceUploadRequest notification)
	{
		if (!this.encoderDispatcher.isReady())
		{
			this.pool.execute(this.encoderDispatcher);
		}
		this.encoderDispatcher.enqueue(notification);
	}
	
	public void encode(String sessionKey, String fileName, String sliceKey, int partitionIndex, byte[] sliceData, int edID, int position, String description) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		byte[] encodedData = CodingTool.encode(sliceData, EDSAConfig.PUBLIC_KEY);
		
		if (this.storageAddress == UtilConfig.NO_IP_ADDRESS)
		{
			this.storageAddress = StandaloneClient.CS().getIPAddress(this.registryIP, this.registryPort, S3Config.STORAGE_SERVER_KEY);
		}
		
		log.info("Coder-encode(): edID = " + edID + ", position = " + position);
		
		// A simple solution to the problem is to add a client to the child. 07/12/2020, Bing Li
		// How to send the data to the storage server? The child has no such interface. 07/11/2020, Bing Li
		StandaloneClient.CS().syncNotify(this.storageAddress.getIP(), this.storageAddress.getPort(), new AssemblePersistNotification(sessionKey, fileName, sliceKey, partitionIndex, encodedData, edID, position, description));
	}
	
	public Map<String, Slice> decode(Map<String, Slice> encodedSlices)
	{
		Map<String, Slice> decodedSlices = new HashMap<String, Slice>();
		for (Map.Entry<String, Slice> entry : encodedSlices.entrySet())
		{
			decodedSlices.put(entry.getKey(), new Slice(entry.getKey(), CodingTool.decode(entry.getValue().getSlice(), EDSAConfig.PRIVATE_KEY)));
		}
		return decodedSlices;
	}

	/*
	 * Since the decoded slices are returned to the storage cluster, the below method is needed. 07/19/2020, Bing Li
	 */
	/*
	public boolean decode(String sessionKey, String sliceKey, byte[] encodedSlice)
	{
		byte[] decodedSlice = CodingTool.decode(encodedSlice, EDSAConfig.PRIVATE_KEY);
		String filePath = S3File.getFilePath(this.filesPath, sessionKey);
		String slicePath = S3File.getSlicePath(filePath, sliceKey, StorageConfig.S3_DECODED_SUFFIX);
		try
		{
			S3File.persistSlice(slicePath, decodedSlice);
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	*/
}
