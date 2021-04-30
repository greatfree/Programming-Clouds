package edu.chainnet.s3.meta;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.message.DecodeSlicesRequest;
import edu.chainnet.s3.message.DecodeSlicesResponse;
import edu.chainnet.s3.message.RetrieveFileRequest;
import edu.chainnet.s3.message.RetrieveFileResponse;
import edu.chainnet.s3.message.SSStateRequest;
import edu.chainnet.s3.message.SSStateResponse;
import edu.chainnet.s3.meta.table.MetaCache;
import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * This is the implementation of the meta server. The API, PeerContainer, is the primary technique to program the server. Now only a single distributed node to provide the resources for the server. If workload is heavy, a cluster is considered for change. Additionally, the server is the middle tier between the client and the EDSA cluster. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
class MetaServer
{
	private PeerContainer peer;
	private IPAddress edsaIPAddress;
	private IPAddress ssIPAddress;
	private int partitionSize;

	private final static Logger log = Logger.getLogger("edu.chainnet.s3.meta");

	public MetaServer()
	{
	}
	
	private static MetaServer instance = new MetaServer();
	
	public static MetaServer META()
	{
		if (instance == null)
		{
			instance = new MetaServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		MetaCache.META().dispose();
		this.peer.stop(timeout);
	}

	public void start(String peerName, int port, ServerTask task, String s3Path, String metaPath) throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Get the IP address of the registry server from the configuration file. 07/09/2020, Bing Li
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);
//		int registryPort = new Integer(reader.read(S3Config.SELECT_REGISTRY_SERVER_PORT));
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
		reader.close();
		
		reader = new XPathOnDiskReader(metaPath, true);
		String metaStatePath = reader.read(S3Config.SELECT_METASTATES_PATH);
		reader.close();

		MetaCache.META().init(metaStatePath);

		this.peer = new PeerContainer(peerName, port, registryIP, registryPort, task, true);
		this.peer.start();
		
		// Get the IP address of the EDSA server from the registry server. 07/09/2020, Bing Li
//		PeerAddressResponse response = (PeerAddressResponse)this.readRegistry(registryIP,  registryPort, new PeerAddressRequest(S3Config.EDSA_SERVER_KEY));
//		this.edsaIPAddress = response.getPeerAddress();
		this.edsaIPAddress = this.peer.getIPAddress(registryIP, registryPort, S3Config.EDSA_SERVER_KEY);

//		response = (PeerAddressResponse)this.readRegistry(registryIP,  registryPort, new PeerAddressRequest(S3Config.STORAGE_SERVER_KEY));
//		this.ssIPAddress = response.getPeerAddress();
		this.ssIPAddress = this.peer.getIPAddress(registryIP, registryPort, S3Config.STORAGE_SERVER_KEY);
		
		 this.partitionSize = this.peer.getPartitionSize(this.ssIPAddress.getIP(), this.ssIPAddress.getPort());
	}
	
	public IPAddress getEDSAIP()
	{
		return this.edsaIPAddress;
	}
	
	public IPAddress getSSIP()
	{
		return this.ssIPAddress;
	}
	
	public void resetPartitionSize() throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.partitionSize = this.peer.getPartitionSize(this.ssIPAddress.getIP(), this.ssIPAddress.getPort());
	}
	
	public int createPartitionIndex(String sessionKey, String sliceKey)
	{
		int partitionIndex = Rand.getRandom(this.partitionSize);
		MetaCache.META().put(sessionKey, sliceKey, partitionIndex);
		return partitionIndex;
	}
	
	public int getPartitionIndex(String sessionKey, String sliceKey)
	{
		return MetaCache.META().get(sessionKey, sliceKey);
	}

	/*
	public PeerAddressResponse readRegistry(String ip, int port, PeerAddressRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeerAddressResponse)this.peer.read(ip, port, request);
	}
	*/

	public void notifyEDSA(Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.edsaIPAddress.getIP(), this.edsaIPAddress.getPort(), notification);
	}
	
	public MulticastResponse readEDSA(Request request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (MulticastResponse)this.peer.read(this.edsaIPAddress.getIP(), this.edsaIPAddress.getPort(), request);
	}

	public void notifySS(Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.ssIPAddress.getIP(), this.ssIPAddress.getPort(), notification);
	}
	
	public void updateSSStates(SSStateRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		log.info("MetaServer-updateSSStates(): ssIPAddress = " + this.ssIPAddress);
		Response response = (Response)this.peer.read(this.ssIPAddress.getIP(), this.ssIPAddress.getPort(), request);
		List<SSStateResponse> sssrs = Tools.filter(response.getResponses(), SSStateResponse.class);
		for (SSStateResponse entry : sssrs)
		{
			MetaCache.META().put(entry.getState());
		}
	}
	
	public Map<String, FileDescription> retrieveFiles(RetrieveFileRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response response = (Response)this.peer.read(this.ssIPAddress.getIP(), this.ssIPAddress.getPort(), request);
		List<RetrieveFileResponse> rfrs = Tools.filter(response.getResponses(), RetrieveFileResponse.class);
		Map<String, FileDescription> files = new HashMap<String, FileDescription>();
		for (RetrieveFileResponse entry : rfrs)
		{
			if (entry.getFiles() != S3Config.NO_FILES)
			{
				files.putAll(entry.getFiles());
			}
		}
		return files;
	}
	
	public boolean isDecodingSucceeded(DecodeSlicesRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response response = (Response)this.peer.read(this.ssIPAddress.getIP(), this.ssIPAddress.getPort(), request);
		List<DecodeSlicesResponse> dsrs = Tools.filter(response.getResponses(), DecodeSlicesResponse.class);
		for (DecodeSlicesResponse entry : dsrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
}
