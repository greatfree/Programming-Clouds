package edu.chainnet.s3.storage.child.table;

import java.util.Map;
import java.util.logging.Logger;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.CacheMap;
import org.greatfree.cache.local.store.MapStore;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.KeyCreator;
import edu.chainnet.s3.storage.StorageConfig;

/*
 * This is the cache to keep all of the meta information about the slices persisted in the child. 07/12/2020, Bing Li 
 */

// Created: 07/12/2020, Bing Li
public class SSStore
{
	private MapStore<FileDescription, FileDescriptionFactory, StoreKeyCreator> files;
	private MapStore<SliceState, SliceStateFactory, StoreKeyCreator> sliceStates;
	private CacheMap<FilePath, FilePathFactory> paths;

	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage.child.table");

	private SSStore()
	{
	}

	private static SSStore instance = new SSStore();
	
	public static SSStore STORE()
	{
		if (instance == null)
		{
			instance = new SSStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.files.shutdown();
		this.sliceStates.shutdown();
		this.paths.close();
	}
	
	public void init(String path)
	{
		log.info("SSStore-init(): initiating files ...");
		this.files = new MapStore.MapStoreBuilder<FileDescription, FileDescriptionFactory, StoreKeyCreator>()
				.rootPath(path)
				.storeKey(StorageConfig.FILE_STORE_KEY)
				.factory(new FileDescriptionFactory())
				.cacheSize(StorageConfig.FILE_STORE_CACHE_SIZE)
				.keyCreator(new StoreKeyCreator())
				.totalStoreSize(StorageConfig.FILE_STORE_SIZE)
				.offheapSizeInMB(StorageConfig.FILE_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(StorageConfig.FILE_STORE_DISK_SIZE)
				.build();

		log.info("SSStore-init(): files initiated ...");

		log.info("SSStore-init(): initiating sliceStates ...");

		this.sliceStates = new MapStore.MapStoreBuilder<SliceState, SliceStateFactory, StoreKeyCreator>()
				.rootPath(path)
				.storeKey(StorageConfig.SLICE_STATE_STORE_KEY)
				.factory(new SliceStateFactory())
				.cacheSize(StorageConfig.SLICE_STATE_STORE_CACHE_SIZE)
				.keyCreator(new StoreKeyCreator())
				.totalStoreSize(StorageConfig.SLICE_STATE_STORE_SIZE)
				.offheapSizeInMB(StorageConfig.SLICE_STATE_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(StorageConfig.SLICE_STATE_STORE_DISK_SIZE)
				.build();

		log.info("SSStore-init(): sliceStates initiated ...");

		log.info("SSStore-init(): initiating paths ...");

		this.paths = new CacheMap.CacheMapBuilder<FilePath, FilePathFactory>()
				.factory(new FilePathFactory())
				.rootPath(path)
				.cacheKey(StorageConfig.PATH_CACHE)
				.cacheSize(StorageConfig.PATH_CACHE_SIZE)
				.offheapSizeInMB(StorageConfig.PATH_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(StorageConfig.PATH_DISK_SIZE_IN_MB)
				.build();
		log.info("SSStore-init(): paths initiated ...");

	}

	/*
	 * The methods are designed for the files cache. 07/12/2020, Bing Li
	 */
	public void put(String sessionKey, String fileName, String description)
	{
		log.info("SSStore-put(): fileName = " + fileName);
		String fileNameKey = KeyCreator.getFileNameKey(fileName);
		log.info("SSStore-put(): fileNameKey = " + fileNameKey);
		this.files.put(fileNameKey, new FileDescription(fileNameKey, sessionKey, fileName, description));
	}
	
	public Map<String, FileDescription> get(String fileName)
	{
		log.info("SSStore-get(): fileName = " + fileName);
		log.info("SSStore-get(): fileNameKey = " + KeyCreator.getFileNameKey(fileName));
		return this.files.getValues(KeyCreator.getFileNameKey(fileName));
	}

	/*
	 * 
	 * Since the path can be generated upon the file path and the slice key quickly. It is not necessary to keep it. 07/19/2020, Bing Li
	 * 
	 * The methods are designed for the slices state cache. 07/12/2020, Bing Li
	 */
//	public void put(String sessionKey, String sliceKey, String fileName, String filePath, int ebID, int position, String slicePath)
//	public void put(String sessionKey, String sliceKey, String fileName, String filePath, int edID, int position)
	public void put(String sessionKey, String sliceKey, String fileName, int edID, int position)
	{
//		this.sliceStates.put(sessionKey, new SliceState(sliceKey, fileName, ebID, position, slicePath));
		
		log.info("SSStore-put(): edID = " + edID);
		log.info("SSStore-put(): position = " + position);
		
		SliceState s = new SliceState(sliceKey, fileName, edID, position);
		log.info("SSStore-put(): state = " + s);
		this.sliceStates.put(sessionKey, s);
	}

	public Map<String, SliceState> getSlices(String sessionKey)
	{
		return this.sliceStates.getValues(sessionKey);
	}
	
	/*
	 * The methods are designed for the path cache. 07/12/2020, Bing Li
	 */
	public void addPath(String sessionKey, String fileName, String filePath)
	{
		log.info("SSStore-addPath(): sessionKey = " + sessionKey);
		this.paths.put(sessionKey, new FilePath(sessionKey, fileName, filePath));
	}
	
	public boolean isSessionExisted(String sessionKey)
	{
		return this.paths.containsKey(sessionKey);
	}
	
	public String getPath(String sessionKey)
	{
		log.info("SSStore-getPath(): sessionKey = " + sessionKey);
		if (this.paths.containsKey(sessionKey))
		{
			return this.paths.get(sessionKey).getPath();
		}
		return UtilConfig.EMPTY_STRING;
	}
}
