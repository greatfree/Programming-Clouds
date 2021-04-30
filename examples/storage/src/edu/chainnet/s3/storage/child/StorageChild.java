package edu.chainnet.s3.storage.child;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.message.PathsRequest;
import edu.chainnet.s3.message.PathsResponse;
import edu.chainnet.s3.storage.Env;
import edu.chainnet.s3.storage.StorageEnv;
import edu.chainnet.s3.storage.child.table.SSStore;
import edu.chainnet.s3.storage.root.table.ChildPath;

/*
 * The program also works as the child of the pool for the storage system. 09/13/2020, Bing Li
 * 
 * This is the child of the cluster for storage. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
public class StorageChild
{
	private ClusterChildContainer child;

	/*
	private String childID;
	private String sssPath;
	private String filePath;
	private String ccoPath;
	*/
	
	private Env env = null;
	private AtomicBoolean isInit;

	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage.child");
	
	private StorageChild()
	{
		this.isInit = new AtomicBoolean(false);
	}
	
	private static StorageChild instance = new StorageChild();
	
	public static StorageChild STORE()
	{
		if (instance == null)
		{
			instance = new StorageChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		if (this.isInit.get())
		{
			SSFile.STORE().dispose();
			SSStore.STORE().dispose();
		}
		this.child.stop(timeout);
	}
	
	/*
	 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. So the method is abandoned. 09/14/2020, Bing Li
	 */
	/*
	public void dispose() throws IOException
	{
		log.info("StorageChild-stop(): ccoPath = " + this.env.getCCOPath());
		Object obj = FileManager.readObjectSync(this.env.getCCOPath());
		ChildConfigObject cco = (ChildConfigObject)obj;
		cco.decrementCurrentCount();
		cco.setS3Path(this.sssPath);
		cco.setFilePath(this.filePath);
		log.info("StorageChild-dispose(): childID = " + this.env.getChildID());
		cco.setUnAssigned(env.getChildID());
		FileManager.writeObjectSync(this.env.getCCOPath(), cco);
	}
	*/

	/*
	 * The child of the storage cluster invokes the method to start. During the procedure, the initialization is performed as well. 09/13/2020, Bing Li
	 */
	public void start(String rootKey, ChildTask task, String s3Path, String storePath) throws IOException, RemoteReadException, InterruptedException, ClassNotFoundException
	{
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);

		// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
//		int registryPort = new Integer(reader.read(S3Config.SELECT_REGISTRY_SERVER_PORT));
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
		reader.close();

		reader = new XPathOnDiskReader(storePath, true);

		/*
		 * When deploying the storage child on a single machine, the below lines are used. 07/21/2020, Bing Li
		 */
//		String sssPath = reader.read(S3Config.SELECT_SSSTATE_PATH);
//		String filesPath = reader.read(S3Config.SELECT_STORAGE_FILES_PATH);
		
		/*
		 * It is feasible to detect whether all of the storage children are located on the same machine. If so, they can determine the below paths by themselves. 08/13/2020, Bing Li
		 */
		
		/*
		 * Each child needs to have different paths to persist data since the current testing, all of the children reside on the same machine. Otherwise, it must get conflicted. The below solution aims to deal with the issue. But each time one node gets a new path. After real deployment, the below lines must be updated. 07/21/2020, Bing Li
		 */
//		String sssPath = reader.read(S3Config.SELECT_SSSTATE_PATH) + Tools.generateUniqueKey() + UtilConfig.FORWARD_SLASH;
//		String filesPath = reader.read(S3Config.SELECT_STORAGE_FILES_PATH) + Tools.generateUniqueKey() + UtilConfig.FORWARD_SLASH;
		
		/*
		 * Now I need to test the system on different machines. So the paths are created as follows. 08/13/2020, Bing Li
		 */
		String sssPath = reader.read(S3Config.SELECT_SSSTATE_PATH);
		String filePath = reader.read(S3Config.SELECT_STORAGE_FILE_PATH);
		reader.close();

		/*
		 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. 09/14/2020, Bing Li
		 * 
		 * It is possible that multiple processes reside on the same machine. The below code determines the paths for each child on the same machine. If each child occupies one machine entirely, the below code is not important. 09/03/2020, Bing Li
		 */
		/*
		this.env = StorageEnv.generateEnv(storePath, registryIP, registryPort, sssPath, filePath);
//		cco.setS3Path(this.sssPath);
//		cco.setFilePath(this.filePath);
		FileManager.writeObjectSync(this.env.getCCOPath(), this.env.getCCO());
		log.info("StorageChild-start(): CCO updated ...");
		
		SSStore.STORE().init(this.env.getSSSPath());
		log.info("StorageChild-start(): SSStore initiated ...");
		SSFile.STORE().init(this.env.getFilePath(), registryIP, registryPort);
		log.info("StorageChild-start(): SSFile initiated ...");
		*/

		this.child = new ClusterChildContainer(registryIP, registryPort, task);
//		this.child.start(S3Config.STORAGE_SERVER_KEY);
		this.child.start(rootKey);
		log.info("StorageChild-start(): child started ...");

		ChildPath path = ((PathsResponse)this.child.readRoot(new PathsRequest(sssPath, filePath))).getPath();
		SSStore.STORE().init(path.getS3Path());
		log.info("StorageChild-start(): SSStore initiated ...");
		SSFile.STORE().init(path.getFilePath(), registryIP, registryPort);
		log.info("StorageChild-start(): SSFile initiated ...");
		this.isInit.set(true);
	}

	/*
	 * The selected child to join the storage cluster invokes the method to initialize. 09/13/2020, Bing Li
	 */
	public void init(IPAddress ip) throws IOException, ClassNotFoundException, RemoteReadException
	{
		XPathOnDiskReader reader = new XPathOnDiskReader(this.env.getStorePath(), true);
		String sssPath = reader.read(S3Config.SELECT_SSSTATE_PATH);
		String filePath = reader.read(S3Config.SELECT_STORAGE_FILE_PATH);
		reader.close();
		
		/*
		 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. 09/14/2020, Bing Li
		 */
		/*
		this.env = StorageEnv.generateEnv(this.env, sssPath, filePath);
		FileManager.writeObjectSync(this.env.getCCOPath(), this.env.getCCO());
		log.info("PoolChild-init(): CCO updated ...");
		
		SSStore.STORE().init(this.env.getSSSPath());
		log.info("PoolChild-init(): SSStore initiated ...");
		SSFile.STORE().init(this.env.getFilePath(), this.env.getRegistryIP(), this.env.getRegistryPort());
		log.info("PoolChild-init(): SSFile initiated ...");
		*/
		
//		ChildPath path = ((PathsResponse)this.child.readRoot(new PathsRequest(sssPath, filePath))).getPath();
		ChildPath path = ((PathsResponse)this.child.readCollaborator(ip, new PathsRequest(sssPath, filePath))).getPath();
		SSStore.STORE().init(path.getS3Path());
		log.info("StorageChild-start(): SSStore initiated ...");
		SSFile.STORE().init(path.getFilePath(), this.env.getRegistryIP(), this.env.getRegistryPort());
		log.info("StorageChild-start(): SSFile initiated ...");
		this.isInit.set(true);
	}

	/*
	 * The child of the pool cluster invokes the method to start. 09/13/2020, Bing Li
	 */
	public void startWithoutInit(String rootKey, ChildTask task, String s3Path, String storePath) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		XPathOnDiskReader reader = new XPathOnDiskReader(s3Path, true);
		String registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);
		int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
		reader.close();
		this.env = StorageEnv.generateEnv(storePath, registryIP, registryPort);
		
		this.child = new ClusterChildContainer(registryIP, registryPort, task);
//		this.child.start(S3Config.POOL_SERVER_KEY);
		this.child.start(rootKey);
	}
}
