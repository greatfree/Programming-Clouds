package edu.chainnet.s3.message;

// Created: 07/09/2020, Bing Li
public class S3AppID
{
	public final static int WRITE_REQUEST = 10001;
	public final static int WRITE_RESPONSE = 10002;

	public final static int SLICE_UPLOAD_REQUEST = 10003;
	public final static int SLICE_UPLOAD_RESPONSE = 10004;
	
	public final static int ASSEMBLE_PERSIST_NOTIFICATION = 10005;
	
	public final static int READ_REQUEST = 10006;
	public final static int READ_RESPONSE = 10007;
	
	public final static int SSSTATE_REQUEST = 10008;
	public final static int SSSTATE_RESPONSE = 10009;
	
	public final static int RETRIEVE_FILE_REQUEST = 10010;
	public final static int RETRIEVE_FILE_RESPONSE = 10011;
	
	public final static int DOWNLOAD_ENCODED_FILE_REQUEST = 10012;
	public final static int DOWNLOAD_ENCODED_FILE_RESPONSE = 10013;
	
	public final static int DECODE_SLICES_REQUEST = 10014;
	public final static int DECODE_SLICES_RESPONSE = 10015;
	
	public final static int DOWNLOAD_DECODED_FILE_REQUEST = 10016;
	public final static int DOWNLOAD_DECODED_FILE_RESPONSE = 10017;
	
	public final static int SUBMIT_ENCODED_SLICE_REQUEST = 10018;
	public final static int SUBMIT_ENCODED_SLICE_RESPONSE = 10019;
	
	public final static int DOWNLOAD_DECODED_SLICE_REQUEST = 10020;
	public final static int DOWNLOAD_DECODED_SLICE_RESPONSE = 10021;
	
	public final static int SHUTDOWN_META_NOTIFICATION = 10022;
	public final static int SHUTDOWN_EDSA_CHILDREN_NOTIFICATION = 10023;
	public final static int SHUTDOWN_EDSA_ROOT_NOTIFICATION = 10024;
	public final static int SHUTDOWN_STORAGE_CHILDREN_NOTIFICATION = 10025;
	public final static int SHUTDOWN_STORAGE_ROOT_NOTIFICATION = 10026;
	
	public final static int CREATE_SLICE_PARTITION_REQUEST = 10027;
	public final static int CREATE_SLICE_PARTITION_RESPONSE = 10028;

	public final static int SLICE_PARTITION_REQUEST = 10029;
	public final static int SLICE_PARTITION_RESPONSE = 10030;
	
	public final static int STOP_ONE_STORAGE_CHILD_NOTIFICATION = 10031;

	public final static int SHUTDOWN_POOL_ROOT_NOTIFICATION = 10032;
	public final static int SHUTDOWN_POOL_CHILDREN_NOTIFICATION = 10033;
	
	public final static int SCALE_CHANGING_NOTIFICATION = 10034;
	public final static int SCALE_CHANGED_NOTIFICATION = 10035;
	
	public final static int INITIALIZE_CHILDREN_NOTIFICATION = 10036;
	
	public final static int PATHS_REQUEST = 10037;
	public final static int PATHS_RESPONSE = 10038;
}
