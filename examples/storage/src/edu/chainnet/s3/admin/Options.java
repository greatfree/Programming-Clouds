package edu.chainnet.s3.admin;

// Created: 07/20/2020, Bing Li
final class Options
{
	public final static int NO_OPTION = -1;
	public final static int QUIT = 0;

	public final static int ENLARGE_SCALE_OF_STORAGE_CLUSTER = 1;
//	public final static int REDUCE_SCALE_OF_STORAGE_CLUSTER = 2;
	public final static int STOP_ONE_STORAGE_CHILD = 2;
	public final static int STOP_META_SERVER = 3;
	public final static int STOP_EDSA_CHILDREN = 4;
	public final static int STOP_EDSA_ROOT = 5;
	public final static int STOP_POOL_CHILDREN = 6;
	public final static int STOP_POOL_ROOT = 7;
	public final static int STOP_STORAGE_CHILDREN = 8;
	public final static int STOP_STORAGE_ROOT = 9;
	public final static int STOP_REGISTRY_SERVER = 10;
}
