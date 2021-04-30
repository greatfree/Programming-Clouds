package edu.chainnet.s3;

import java.util.Map;

import org.greatfree.server.container.PeerContainer;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.storage.child.table.FileDescription;

/*
 * The project is a new storage system for block-chain security. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
public class S3Config
{
	public final static String USER_HOME = "user.home";

//	public final static String S3_HOME = "/S3/";
//	public final static String S3_HOME = "/Wind/Collaboration/ChainNet/S3/Config/";
	public final static String S3_HOME = "/Temp/Config/";
	public final static String S3_CONFIG_PATH = "/S3Config/";
	public final static String S3_CLIENT_CONFIG_PATH = "/ClientConfig/";
	public final static String S3_META_CONFIG_PATH = "/Meta/";
	public final static String S3_STORAGE_CONFIG_PATH = "/Storge/";
	
	public final static String S3_CONFIG_FILE = "S3Config.xml";
	public final static String CLIENT_CONFIG_FILE = "ClientConfig.xml";
	public final static String META_CONFIG_FILE = "MetaConfig.xml";
//	public final static String EDSA_CONFIG_FILE = "/home/libing/Temp/EDSAConfig.xml";
	public final static String STORAGE_CONFIG_FILE = "StorageConfig.xml";
	
	public final static String META_SERVER_NAME = "meta";
	public final static String EDSA_SERVER_NAME = "edsa";
	public final static String STORAGE_SERVER_NAME = "storage";
	public final static String POOL_SERVER_NAME = "pool";
	
	public final static String META_SERVER_KEY = PeerContainer.getPeerKey(META_SERVER_NAME);
	public final static String EDSA_SERVER_KEY = PeerContainer.getPeerKey(EDSA_SERVER_NAME);
	public final static String STORAGE_SERVER_KEY = PeerContainer.getPeerKey(STORAGE_SERVER_NAME);
	public final static String POOL_SERVER_KEY = PeerContainer.getPeerKey(POOL_SERVER_NAME);
	
	public final static String SELECT_REGISTRY_SERVER_IP = "/S3Config/RegistryIP/text()";
	
	// Now the port of the registry sever is fixed to be the one, 8941, for simplicity. If needed, the port can be updated. Then, the port can be defined in the configuration file. 07/20/2020, Bing Li
//	public final static String SELECT_REGISTRY_SERVER_PORT = "/S3Config/RegistryPort/text()";
	
	public final static String SELECT_CLIENT_FILE = "/S3Client/ClientFile/text()";
	public final static String SELECT_K = "/S3Client/K/text()";
	public final static String SELECT_SLICE_SIZE = "/S3Client/SliceSize/text()";
//	public final static String SELECT_PARTITION_INDEXES = "/S3Client/PartitionIndex/text()";
	
	public final static String SELECT_METASTATES_PATH = "/MetaConfig/MetaPath/text()";
	
//	public final static String SELECT_EDSA_FILES_PATH = "/EDSA/FilePath/text()";
	public final static String SELECT_SSSTATE_PATH = "/Storage/SSSPath/text()";
	public final static String SELECT_STORAGE_FILE_PATH = "/Storage/FilePath/text()";
	public final static String SELECT_REPLCAS = "/Storage/Replicas/text()";
	public final static String SELECT_PARTITIONS_PATH = "/Storage/PartitionsPath/text()";
	
	public final static int META_SERVER_PORT = 8801;
	public final static int EDSA_ROOT_PORT = 8802;
	public final static int STORAGE_ROOT_PORT = 8803;
	public final static int POOL_ROOT_PORT = 8804;
	
	public final static String FILE_DESCRIPTION = "Hello-";
	
	public final static Map<String, FileDescription> NO_FILES = null;
	public final static FileDescription NO_FILE = null;
	
	public final static String NO_CONFIG = UtilConfig.EMPTY_STRING;
	
	public final static int ADDITIONAL_K = 6;
	
	public final static int REPICAS = 2;
	
	public final static int UPLOAD_SLEEP = 3000;
	public final static int DOWNLOAD_SLEEP = 3000;
}
