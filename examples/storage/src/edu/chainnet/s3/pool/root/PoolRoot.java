package edu.chainnet.s3.pool.root;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;

/*
 * Except the name, the code is almost identical to that of StorageRoot. 09/10/2020, Bing Li
 */

// Created: 09/10/2020, Bing Li
class PoolRoot
{
	private ClusterServerContainer root;

	private final static Logger log = Logger.getLogger("edu.chainnet.s3.pool.root");

	private PoolRoot()
	{
	}
	
	private static PoolRoot instance = new PoolRoot();
	
	public static PoolRoot STORE()
	{
		if (instance == null)
		{
			instance = new PoolRoot();
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
		log.info("PoolRoot-start(): s3Path = " + s3Path);
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);
		reader.close();

		// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;

		// As a pool, no partition is required. So the replicas is NOT set. 09/13/2020, Bing Li
		this.root = new ClusterServerContainer(port, S3Config.POOL_SERVER_NAME, registryIP, registryPort, task);
		this.root.start();
	}

}
