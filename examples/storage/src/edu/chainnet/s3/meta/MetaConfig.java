package edu.chainnet.s3.meta;

// Created: 07/10/2020, Bing Li
public class MetaConfig
{
	public final static String SSSTATE_CACHE = "DSStateCache";
	public final static int SSSTATE_CACHE_SIZE = 100;
	public final static int SSSTATE_OFFHEAP_SIZE_IN_MB = 5;
	public final static int SSSTATE_DISK_SIZE_IN_MB = 10;

	public final static String FILEMETA_CACHE = "FileMetaCache";
	public final static int FILEMETA_CACHE_SIZE = 10000;
	public final static int FILEMETA_OFFHEAP_SIZE_IN_MB = 50;
	public final static int FILEMETA_DISK_SIZE_IN_MB = 200;

	public final static String SLICE_PARTITION_STORE_KEY = "SlicePartitionCache";
	public final static int SLICE_PARTITION_CACHE_SIZE = 10000;
	public final static int SLICE_PARTITION_STORE_SIZE = 30000;
	public final static int SLICE_PARTITION_CACHE_OFFHEAP_SIZE = 50;
	public final static int SLICE_PARTITION_CACHE_DISK_SIZE = 200;

	public final static int INVALID_N = 0;
	
	public final static long SS_STATE_UPDATE_PERIOD = 5000;
}
