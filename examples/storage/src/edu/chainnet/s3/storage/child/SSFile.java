package edu.chainnet.s3.storage.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.KeyCreator;
import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.S3File;
import edu.chainnet.s3.message.SubmitEncodedSliceRequest;
import edu.chainnet.s3.message.SubmitEncodedSliceResponse;
import edu.chainnet.s3.meta.table.SSState;
import edu.chainnet.s3.storage.StorageConfig;
import edu.chainnet.s3.storage.child.table.FileDescription;
import edu.chainnet.s3.storage.child.table.SSStore;
import edu.chainnet.s3.storage.child.table.Slice;

/*
 * The program is responsible for persisting slices. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
public class SSFile
{
	private String id;
	private String filesPath;
	private String registryIP;
	private int registryPort;
	private IPAddress edsaAddress;
	
	private SSFile()
	{
	}

	private static SSFile instance = new SSFile();
	
	public static SSFile STORE()
	{
		if (instance == null)
		{
			instance = new SSFile();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		StandaloneClient.CS().dispose();
	}

	public void init(String filesPath, String registryIP, int registryPort) throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.id = KeyCreator.getServerID();
		this.filesPath = filesPath;
		StandaloneClient.CS().init();
		this.registryIP = registryIP;
		this.registryPort = registryPort;
//		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP,  registryPort, new PeerAddressRequest(S3Config.EDSA_SERVER_KEY));
//		this.edsaAddress = response.getPeerAddress();
		this.edsaAddress = UtilConfig.NO_IP_ADDRESS;
	}
	
	public String getFilesPath()
	{
		return this.filesPath;
	}
	
	public boolean submit(FileDescription file, SubmitEncodedSliceRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (this.edsaAddress == UtilConfig.NO_IP_ADDRESS)
		{
			this.edsaAddress = StandaloneClient.CS().getIPAddress(this.registryIP, this.registryPort, S3Config.EDSA_SERVER_KEY);
		}
		Response response;
		try
		{
			response = (Response)StandaloneClient.CS().read(this.edsaAddress.getIP(), this.edsaAddress.getPort(), request);
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			return false;
		}
		List<SubmitEncodedSliceResponse> sesrs = Tools.filter(response.getResponses(), SubmitEncodedSliceResponse.class);
		String filePath = SSStore.STORE().getPath(file.getSessionKey());
		String slicePath;
		for (SubmitEncodedSliceResponse entry : sesrs)
		{
			/*
			if (!entry.isSucceeded())
			{
				return false;
			}
			*/
			for (Map.Entry<String, Slice> sliceEntry : entry.getSlices().entrySet())
			{
				slicePath = S3File.getSlicePath(filePath, sliceEntry.getKey(), StorageConfig.S3_DECODED_SUFFIX);
				try
				{
					S3File.persistSlice(slicePath, sliceEntry.getValue().getSlice());
				}
				catch (IOException e)
				{
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * This is a temporary solution. Will be updated later on. 07/13/2020, Bing Li
	 */
	public SSState getSSState()
	{
		return new SSState(this.id, StorageConfig.TOTAL_STORAGE_SPACE, StorageConfig.FREE_STORAGE_SPACE, true, Calendar.getInstance().getTime());
	}
}
