package edu.chainnet.s3.storage;

/*
 * The cluster is used to persist encoded blocks. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
public class StorageConfig
{
	public final static String FILE_STORE_KEY = "FilePathCache";
	public final static long FILE_STORE_CACHE_SIZE = 2000;
	public final static int FILE_STORE_SIZE = 10000;
	public final static int FILE_STORE_OFFHEAP_SIZE = 50;
	public final static int FILE_STORE_DISK_SIZE = 200;

	public final static String SLICE_STATE_STORE_KEY = "SliceStateCache";
	public final static long SLICE_STATE_STORE_CACHE_SIZE = 2000;
	public final static int SLICE_STATE_STORE_SIZE = 10000;
	public final static int SLICE_STATE_STORE_OFFHEAP_SIZE = 50;
	public final static int SLICE_STATE_STORE_DISK_SIZE = 200;
	
	public final static String PATH_CACHE = "PathCache";
	public final static int PATH_CACHE_SIZE = 100;
	public final static int PATH_OFFHEAP_SIZE_IN_MB = 20;
	public final static int PATH_DISK_SIZE_IN_MB = 80;
	
	public final static String S3_ENCODED_SUFFIX = ".s3e";
	public final static String S3_DECODED_SUFFIX = ".s3d";
	
	public final static long TOTAL_STORAGE_SPACE = 100000000;
	public final static long FREE_STORAGE_SPACE = 100000000;

	// Since it is possible that multiple storage children reside on the same machine, the object is used as an identification to notify all of the children about the issue. 08/21/2020, Bing Li
	public final static String CHILD_CONFIG_OBJECT = "ChildConfigObject";
	public final static String CHILD = "Child";
	
	public final static String CHILD_PATHS = "ChildPaths";
	public final static int CHILD_PATH_CACHE_SIZE = 100;
	public final static int CHILD_PATH_OFFHEAP_SIZE_IN_MB = 20;
	public final static int CHILD_PATH_DISK_SIZE_IN_MB = 80;
}
