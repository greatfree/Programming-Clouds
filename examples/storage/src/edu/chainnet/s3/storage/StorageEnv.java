package edu.chainnet.s3.storage;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.storage.child.table.ChildConfigObject;
import edu.chainnet.s3.storage.child.table.StoragePaths;

/*
 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. So most methods of the program is abandoned. 09/14/2020, Bing Li
 * 
 * The program generates the ID of each child. It is useful if multiple children are located on the same machine for testing. 09/10/2020, Bing Li
 */

// Created: 09/10/2020, Bing Li
public class StorageEnv
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage");
	
	public static Env generateEnv(String storePath, String registryIP, int registryPort)
	{
		Env env = new Env.EnvBuilder()
				.storePath(storePath)
				.registryIP(registryIP)
				.registryPort(registryPort)
				.build();
		return env;
	}

	/*
	 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. So the method is abandoned. 09/14/2020, Bing Li
	 */
	public static Env generateEnv(Env env, String sssPath, String filePath) throws IOException
	{
		String ccoPath = sssPath + StorageConfig.CHILD_CONFIG_OBJECT;
		Object obj = FileManager.readObjectSync(ccoPath);
		
		ChildConfigObject cco;
		String childID;
		if (obj == null)
		{
			cco = new ChildConfigObject();
			childID = StorageConfig.CHILD + cco.getCurrentCount();

			sssPath = sssPath + childID + UtilConfig.FORWARD_SLASH;
			log.info("0) StorageChild-start(): sssPath = " + sssPath);
			filePath = filePath + childID + UtilConfig.FORWARD_SLASH;
			log.info("0) StorageChild-start(): filePath = " + filePath);
			log.info("StorageChild-start(): ccoPath = " + ccoPath);
			cco.addPaths(childID, sssPath, filePath);

			FileManager.makeDir(sssPath);
		}
		else
		{
			log.info("StorageChild-start(): ...");
			cco = (ChildConfigObject)obj;
			cco.incrementCurrentCount();
			log.info("StorageChild-start(): current count = " + cco.getCurrentCount());
			log.info("StorageChild-start(): count = " + cco.getCount());
			if (cco.getCount() >= cco.getCurrentCount())
			{
				Map<String, StoragePaths> paths = cco.getPaths();
				// For the demo on the same machine, the below code keeps the children in order. Then, they are partitioned in the previous state when starting again. 09/09/2020, Bing Li
				// The below code is not necessary if each node runs on a unique machine. For fault-tolerance consideration, each node is partitioned. It must join the same partition and access the same directory when starting again. Otherwise, it is possible that the replicated data cannot be found. 09/09/2020, Bing Li
				childID = StorageConfig.CHILD + cco.getCurrentCount();
				sssPath = paths.get(childID).getS3Path();
				log.info("1) StorageChild-start(): sssPath = " + sssPath);
				filePath = paths.get(childID).getFilePath();
				log.info("1) StorageChild-start(): filePath = " + filePath);
			}
			else
			{
				cco.incrementCount();
				childID = StorageConfig.CHILD + cco.getCurrentCount();
				sssPath = sssPath + childID + UtilConfig.FORWARD_SLASH;
				log.info("2) StorageChild-start(): sssPath = " + sssPath);
				filePath = filePath + childID + UtilConfig.FORWARD_SLASH;
				log.info("2) StorageChild-start(): filePath = " + filePath);
				cco.addPaths(childID, sssPath, filePath);
			}
			cco.setAssigned(childID);
			log.info("StorageChild-start(): updating CCO ...");
		}
		env.setChildID(childID);
		env.setSSSPath(sssPath);
		env.setFilePath(filePath);
//		env.setCCOPath(ccoPath);
//		env.setCCO(cco);
		return env;
	}

	/*
	 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. So the methods is abandoned. 09/14/2020, Bing Li
	 */
	public static Env generateEnv(String storePath, String registryIP, int registryPort, String sssPath, String filePath) throws IOException
	{
		String ccoPath = sssPath + StorageConfig.CHILD_CONFIG_OBJECT;
		Object obj = FileManager.readObjectSync(ccoPath);
		
		ChildConfigObject cco;
		String childID;
		if (obj == null)
		{
			cco = new ChildConfigObject();
			childID = StorageConfig.CHILD + cco.getCurrentCount();
//			cco.addS3Path(this.sssPath);
//			cco.addFilePath(this.filePath);

			sssPath = sssPath + childID + UtilConfig.FORWARD_SLASH;
			log.info("0) StorageChild-start(): sssPath = " + sssPath);
			filePath = filePath + childID + UtilConfig.FORWARD_SLASH;
			log.info("0) StorageChild-start(): filePath = " + filePath);
			log.info("StorageChild-start(): ccoPath = " + ccoPath);
			cco.addPaths(childID, sssPath, filePath);

			FileManager.makeDir(sssPath);
		}
		else
		{
			log.info("StorageChild-start(): ...");
			cco = (ChildConfigObject)obj;
			cco.incrementCurrentCount();
			log.info("StorageChild-start(): current count = " + cco.getCurrentCount());
			log.info("StorageChild-start(): count = " + cco.getCount());
			if (cco.getCount() >= cco.getCurrentCount())
			{
				/*
				Map<String, Boolean> s3Paths = cco.getS3Paths();
				for (Map.Entry<String, Boolean> entry : s3Paths.entrySet())
				{
					if (!entry.getValue())
					{
						this.sssPath = entry.getKey();
						break;
					}
				}
				log.info("1) StorageChild-start(): sssPath = " + this.sssPath);
				
				Map<String, Boolean> filePaths = cco.getFilePaths();
				for (Map.Entry<String, Boolean> entry : filePaths.entrySet())
				{
					if (!entry.getValue())
					{
						this.filePath = entry.getKey();
						break;
					}
				}
				log.info("1) StorageChild-start(): filePath = " + this.filePath);
				*/
				
				
				/*
				 * The below cannot keep the order of the child to be partitioned in the previous result. 09/09/2020, Bing Li
				 */
				/*
				Map<String, StoragePaths> paths = cco.getPaths();
				for (Map.Entry<String, StoragePaths> entry : paths.entrySet())
				{
					if (!entry.getValue().isAssigned())
					{
						this.childID = entry.getKey();
						this.sssPath = entry.getValue().getS3Path();
						log.info("1) StorageChild-start(): sssPath = " + this.sssPath);
						this.filePath = entry.getValue().getFilePath();
						log.info("1) StorageChild-start(): filePath = " + this.filePath);
						break;
					}
				}
				*/
				
				Map<String, StoragePaths> paths = cco.getPaths();
				// For the demo on the same machine, the below code keeps the children in order. Then, they are partitioned in the previous state when starting again. 09/09/2020, Bing Li
				// The below code is not necessary if each node runs on a unique machine. For fault-tolerance consideration, each node is partitioned. It must join the same partition and access the same directory when starting again. Otherwise, it is possible that the replicated data cannot be found. 09/09/2020, Bing Li
				childID = StorageConfig.CHILD + cco.getCurrentCount();
				sssPath = paths.get(childID).getS3Path();
				log.info("1) StorageChild-start(): sssPath = " + sssPath);
				filePath = paths.get(childID).getFilePath();
				log.info("1) StorageChild-start(): filePath = " + filePath);
			}
			else
			{
				cco.incrementCount();
				childID = StorageConfig.CHILD + cco.getCurrentCount();
				sssPath = sssPath + childID + UtilConfig.FORWARD_SLASH;
				log.info("2) StorageChild-start(): sssPath = " + sssPath);
				filePath = filePath + childID + UtilConfig.FORWARD_SLASH;
				log.info("2) StorageChild-start(): filePath = " + filePath);
				cco.addPaths(childID, sssPath, filePath);
			}
//			cco.addS3Path(this.sssPath);
//			cco.addFilePath(this.filePath);
			cco.setAssigned(childID);
			log.info("StorageChild-start(): updating CCO ...");
		}
//		return new Env(childID, sssPath, filePath, ccoPath, cco);
		Env env = new Env.EnvBuilder()
				.childID(childID)
				.storePath(storePath)
				.registryIP(registryIP)
				.registryPort(registryPort)
				.sssPath(sssPath)
				.filePath(filePath)
//				.ccoPath(ccoPath)
//				.cco(cco)
				.build();
		return env;
	}

}
