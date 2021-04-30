package edu.chainnet.s3.client;

import java.io.IOException;

import org.greatfree.client.StandaloneClient;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.util.FileManager;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;

/*
 * The program is the client to interact with the meta server. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing L
class S3Client
{
	private IPAddress metaAddress;
	private String uploadFile;
	private String uploadFileName;
	private int k;
	private int n;
	private int maxSliceSize;
	private int sliceSize;

	private S3Client()
	{
	}

	/*
	 * Initialize a singleton. 07/09/2020, Bing Li
	 */
	private static S3Client instance = new S3Client();
	
	public static S3Client CLIENT()
	{
		if (instance == null)
		{
			instance = new S3Client();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException, InterruptedException
	{
		StandaloneClient.CS().dispose();
	}
	
	public void init(String s3Path, String clientPath) throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		
		// Get the IP address of the registry server from the configuration file. 07/09/2020, Bing Li
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);
		
		// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
//		int registryPort = new Integer(reader.read(S3Config.SELECT_REGISTRY_SERVER_PORT));
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
		reader.close();

		// Get the IP address of the meta server from the registry server. 07/09/2020, Bing Li
		reader = new XPathOnDiskReader(clientPath, true);
		this.uploadFile = reader.read(S3Config.SELECT_CLIENT_FILE);
		this.uploadFileName = this.uploadFile.substring(this.uploadFile.lastIndexOf(UtilConfig.FORWARD_SLASH) + 1);
		this.k = new Integer(reader.read(S3Config.SELECT_K));
		this.maxSliceSize = new Integer(reader.read(S3Config.SELECT_SLICE_SIZE));
		reader.close();
		
//		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP, registryPort, new PeerAddressRequest(S3Config.META_SERVER_KEY));
		this.metaAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, S3Config.META_SERVER_KEY);
	}
	
	public String getFile()
	{
		return this.uploadFile;
	}
	
	public String getFileName()
	{
		return this.uploadFileName;
	}
	
	public long getFileSize()
	{
		return FileManager.getFileSize(this.uploadFile);
	}
	
	public int getK()
	{
		return this.k;
	}
	
	public void setN(int n)
	{
		this.sliceSize = (int)this.getFileSize() / n;
		this.n = n;
	}
	
	public int getN()
	{
		return this.n;
	}
	
	public int getSliceSize()
	{
		return this.sliceSize;
	}
	
	public int getMaxSliceSize()
	{
		return this.maxSliceSize;
	}
	
	public ServerMessage readMeta(org.greatfree.message.container.Request request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ServerMessage)StandaloneClient.CS().read(this.metaAddress.getIP(), this.metaAddress.getPort(), request);
	}
	
	public void writeMeta(Notification notification) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.metaAddress.getIP(), this.metaAddress.getPort(), notification);
	}
	
	public void writeMetaAsync(Notification notification)
	{
		StandaloneClient.CS().asyncNotify(this.metaAddress.getIP(), this.metaAddress.getPort(), notification);
	}
	
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return StandaloneClient.CS().read(ip.getIP(), ip.getPort(), request);
	}
}
