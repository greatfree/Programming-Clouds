package edu.chainnet.s3.edsa.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;

/*
 * 
 * The EDSA server focuses on data encoding/decoding. 07/11/2020, Bing Li
 * 
 * The program plays the role of EDSA cluster's root. 07/10/2020, Bing Li
 */

// Created: 07/10/2020, Bing Li
class EDSARoot
{
	private ClusterServerContainer root;

	private EDSARoot()
	{
	}
	
	private static EDSARoot instance = new EDSARoot();
	
	public static EDSARoot EDSA()
	{
		if (instance == null)
		{
			instance = new EDSARoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stopChildren() throws IOException, DistributedNodeFailedException
	{
		this.root.stopCluster();
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.root.stop(timeout);
	}
	
	public void start(int port, RootTask task, String s3Path) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);

		// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
//		int registryPort = new Integer(reader.read(S3Config.SELECT_REGISTRY_SERVER_PORT));
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
		reader.close();

		this.root = new ClusterServerContainer(port, S3Config.EDSA_SERVER_NAME, registryIP, registryPort, task);
		this.root.start();
	}
}
