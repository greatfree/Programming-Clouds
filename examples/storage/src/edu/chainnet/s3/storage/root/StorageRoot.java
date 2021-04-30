package edu.chainnet.s3.storage.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.storage.root.table.Paths;

/*
 * The program is the root of the storage cluster. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
class StorageRoot
{
	private ClusterServerContainer root;

	private StorageRoot()
	{
	}
	
	private static StorageRoot instance = new StorageRoot();
	
	public static StorageRoot STORE()
	{
		if (instance == null)
		{
			instance = new StorageRoot();
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
		Paths.STORE().dispose();
		this.root.stop(timeout);
	}

	public void start(int port, RootTask task, String s3Path, String storagePath) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);
		reader.close();
		
		reader = new XPathOnDiskReader(storagePath, true);
		int replicas = Integer.valueOf(reader.read(S3Config.SELECT_REPLCAS));
		String partitionPath = reader.read(S3Config.SELECT_PARTITIONS_PATH);
		reader.close();

		// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
//		int registryPort = new Integer(reader.read(S3Config.SELECT_REGISTRY_SERVER_PORT));
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;

		Paths.STORE().init(partitionPath);

		// The storage cluster needs to replicate for the fault-tolerance issue. 09/10/2020, Bing Li
		this.root = new ClusterServerContainer(port, S3Config.STORAGE_SERVER_NAME, registryIP, registryPort, task, replicas);
		this.root.start();
	}
}
