package edu.chainnet.s3.edsa.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;

/*
 * The EDSA server focuses on data encoding/decoding. 07/11/2020, Bing Li
 * 
 * Data is NOT persisted on the child. Another cluster is constructed for the persistence. 07/11/2020, Bing Li
 * 
 * The program encodes/decodes data. Additionally, it also persists encoded data. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
class EDSAChild
{
	private ClusterChildContainer child;

	private EDSAChild()
	{
	}
	
	private static EDSAChild instance = new EDSAChild();
	
	public static EDSAChild EDSA()
	{
		if (instance == null)
		{
			instance = new EDSAChild();
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
		Coder.EDSA().dispose();
		this.child.stop(timeout);
	}
	
//	public void start(ChildTask task, String s3Path, String edsaPath) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	public void start(ChildTask task, String s3Path) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);

		// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
//		int registryPort = new Integer(reader.read(S3Config.SELECT_REGISTRY_SERVER_PORT));
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
		reader.close();

		/*
		reader = new XPathOnDiskReader(edsaPath, true);
		String edsaFilePath = reader.read(S3Config.SELECT_EDSA_FILES_PATH);
		reader.close();
		*/

//		Coder.EDSA().init(edsaFilePath, registryIP, registryPort);
		Coder.EDSA().init(registryIP, registryPort);

		this.child = new ClusterChildContainer(registryIP, registryPort, task);
		this.child.start(S3Config.EDSA_SERVER_KEY);
	}
}
