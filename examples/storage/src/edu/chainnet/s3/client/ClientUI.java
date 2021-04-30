package edu.chainnet.s3.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.FileManager;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

import edu.chainnet.s3.KeyCreator;
import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.message.DownloadDecodedFileRequest;
import edu.chainnet.s3.message.DownloadDecodedFileResponse;
import edu.chainnet.s3.message.DownloadDecodedSliceRequest;
import edu.chainnet.s3.message.DownloadDecodedSliceResponse;
import edu.chainnet.s3.message.DownloadEncodedFileRequest;
import edu.chainnet.s3.message.DownloadEncodedFileResponse;
import edu.chainnet.s3.message.ReadRequest;
import edu.chainnet.s3.message.ReadResponse;
import edu.chainnet.s3.message.SlicePartitionRequest;
import edu.chainnet.s3.message.SlicePartitionResponse;
import edu.chainnet.s3.message.CreateSlicePartitionRequest;
import edu.chainnet.s3.message.CreateSlicePartitionResponse;
import edu.chainnet.s3.message.SliceUploadRequest;
import edu.chainnet.s3.message.SliceUploadResponse;
import edu.chainnet.s3.message.WriteRequest;
import edu.chainnet.s3.message.WriteResponse;
import edu.chainnet.s3.storage.child.table.FileDescription;
import edu.chainnet.s3.storage.child.table.SliceState;

// Created: 07/09/2020, Bing Li
class ClientUI
{
	private Scanner in = new Scanner(System.in);
	private IPAddress edsaAddress = UtilConfig.NO_IP_ADDRESS;
	private IPAddress ssAddress = UtilConfig.NO_IP_ADDRESS;
	
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.client");

	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CLIENT()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.UPLOADING);
		System.out.println(ClientMenu.DOWNLOADING);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	public void execute(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		switch (option)
		{
			case MenuOptions.UPLOAD_FILE:
				// Each file uploading has a unique session key. 07/12/2020, Bing Li
				String sessionKey = KeyCreator.createSessionKey();
				WriteResponse response = (WriteResponse)S3Client.CLIENT().readMeta(new WriteRequest(sessionKey, S3Client.CLIENT().getFileName(), S3Client.CLIENT().getFileSize(), S3Client.CLIENT().getK(), S3Client.CLIENT().getMaxSliceSize()));
				if (response.isSucceeded())
				{
					this.edsaAddress = response.getEDSAIP();
					this.ssAddress = response.getSSRootIP();
					S3Client.CLIENT().setN(response.getN());
					
					log.info("N = " + response.getN());
					log.info("K = " + S3Client.CLIENT().getK());
					log.info("Slice size = " + S3Client.CLIENT().getSliceSize());
					
					this.uploadFile(sessionKey, S3Config.FILE_DESCRIPTION + sessionKey);
				}
				break;
				
			case MenuOptions.DOWNLOAD_FILE:
				log.info("Tell me the file name: ");
				String fileName = in.nextLine();
				ReadResponse rr = (ReadResponse)S3Client.CLIENT().readMeta(new ReadRequest(fileName));
				this.ssAddress = rr.getSSIP();
				int index = 0;
				String optStr;
				int fileIndex;
				FileDescription file = S3Config.NO_FILE;
				if (rr.getFiles().size() >= UtilConfig.ONE)
				{
					List<String> sessions = new ArrayList<String>();
//					for (FileDescription entry : rr.getFiles().values())
					for (Map.Entry<String, FileDescription> entry : rr.getFiles().entrySet())
					{
						log.info(++index + ") " + entry.getValue());
						sessions.add(entry.getKey());
						log.info("fileKey = " + entry.getKey());
					}
					log.info("Which one do you need? [1 ~ " + index + "]?");

					do
					{
						optStr = in.nextLine();
						try
						{
							fileIndex = Integer.parseInt(optStr);
						}
						catch (NumberFormatException e)
						{
							fileIndex = UtilConfig.NO_INDEX;
							log.info("Your option is NOT correct!");
						}
					}
					while (fileIndex < 0 || fileIndex > index);
					String selectedFileKey = sessions.get(fileIndex - 1);
					
					log.info("selectedFileKey = " + selectedFileKey);
					
					file = rr.getFiles().get(selectedFileKey);
					log.info(file + " is being downloaded ...");
					this.downloadFile(file);
					log.info(file + " is downloaded ...");
				}
				else if (rr.getFiles().size() <= 0)
				{
					log.info("No such a file to download ...");
				}
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
	
	private void downloadFile(FileDescription file) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		DownloadEncodedFileResponse defr = (DownloadEncodedFileResponse)S3Client.CLIENT().readMeta(new DownloadEncodedFileRequest(file));
		if (defr.isSucceeded())
		{
			Response response = (Response)S3Client.CLIENT().read(this.ssAddress, new DownloadDecodedFileRequest(file));
			List<DownloadDecodedFileResponse> ddfrs = Tools.filter(response.getResponses(), DownloadDecodedFileResponse.class);
//			List<DownloadDecodedSliceResponse> ddsrs;
			long totalDownloadedSize = 0;
			Set<String> sliceKeys = Sets.newHashSet();
			int ddIndex = 0;
			int ssIndex = 0;
			List<String> stateKeys;
			DownloadDecodedFileResponse entry;
			SlicePartitionResponse spr = new SlicePartitionResponse(-1, true);
			String stateKey;
			SliceState state;
			
			do
			{
				if (spr.isSucceeded())
				{
					entry = ddfrs.get(ddIndex);
					if (entry.getStates() != null)
					{
						stateKeys = new ArrayList<String>(entry.getStates().keySet());
						do
						{
							stateKey = stateKeys.get(ssIndex);
							state = entry.getStates().get(stateKey);
							if (!sliceKeys.contains(state.getKey()))
							{
								sliceKeys.add(state.getKey());
								spr = (SlicePartitionResponse)S3Client.CLIENT().readMeta(new SlicePartitionRequest(file.getSessionKey(), state.getKey()));
								// When increasing the scale of the storage cluster, the downloading process should be paused temporarily. If so, the instance of isSucceeded of SlicePartitionResponse is false. 09/11/2020, Bing Li
								if (spr.isSucceeded())
								{
									log.info("ClientUI-downloadFile(): spr partitionIndex = " + spr.getPartitionIndex());
									
									response = (Response)S3Client.CLIENT().read(this.ssAddress, new DownloadDecodedSliceRequest(file.getSessionKey(), state.getKey(), spr.getPartitionIndex()));
									/*
									ddsrs = Tools.filter(response.getResponses(), DownloadDecodedSliceResponse.class);
									for (DownloadDecodedSliceResponse res : ddsrs)
									{
										if (res.getSlice() != null)
										{
											log.info("Downloading slice: " + state);
											totalDownloadedSize += res.getSlice().length;
											log.info("Slice size: " + res.getSlice().length + ", totalDownloadedSize = " + totalDownloadedSize);
											S3File.merge(state, res.getSlice());
										}
									}
									*/
									DownloadDecodedSliceResponse ddsr = (DownloadDecodedSliceResponse)response.getResponse();
									log.info("Downloading slice: " + state);
									totalDownloadedSize += ddsr.getSlice().length;
									log.info("Slice size: " + ddsr.getSlice().length + ", totalDownloadedSize = " + totalDownloadedSize);
									ssIndex++;
								}
								else
								{
									Thread.sleep(S3Config.DOWNLOAD_SLEEP);
								}
							}
							else
							{
								ssIndex++;
							}
						}
						while (ssIndex < stateKeys.size());
					}
					ddIndex++;
					ssIndex = 0;
				}
				else
				{
					Thread.sleep(S3Config.DOWNLOAD_SLEEP);
				}
				log.info("current slice / total slices = " + ddIndex + " / " + ddfrs.size());
			}
			while (ddIndex < ddfrs.size());
			
			log.info("Downloading is succeeded!");
			
			/*
			 * Original slices are merged here in order. 07/15/2020, Bing Li
			 */
		}
		else
		{
			log.info(file + " is failed to download when the downloading encoding file!");
		}
	}
	
	private void uploadFile(String sessionKey, String description) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	{
		long size = FileManager.getFileSize(S3Client.CLIENT().getFile());
		byte[] bytes;
		Response response;
		List<SliceUploadResponse> uploadResponses = null;
		int sliceIndex = 0;
		boolean isSucceeded = true;
		int numberOfGroups = 0;
		
		if (S3Client.CLIENT().getN() % S3Client.CLIENT().getK() != 0)
		{
			numberOfGroups = S3Client.CLIENT().getK() + 1;
		}
		else
		{
			numberOfGroups = S3Client.CLIENT().getK();
		}
		int edID;
		int position;
		long uploadedSize = 0;
		String sliceKey;
		
		// When increasing the scale of the storage cluster, the uploading process should be paused temporarily. If so, the instance of isSucceeded of CreateSlicePartitionResponse is false. 09/11/2020, Bing Li
		int startIndex = 0;
		int endIndex = startIndex + S3Client.CLIENT().getSliceSize() - 1;
		bytes = FileManager.loadFile(S3Client.CLIENT().getFile(), startIndex, endIndex);
		edID = sliceIndex % numberOfGroups;
		position = sliceIndex / numberOfGroups;
		sliceKey = KeyCreator.getSliceStateKey(sessionKey, edID, position);

//		CreateSlicePartitionResponse cspr = new CreateSlicePartitionResponse(-1, true);
		CreateSlicePartitionResponse cspr;
		while (endIndex < size)
		{
			// Create the partition index for the slice to be uploaded. 09/11/2020, Bing Li
			cspr = (CreateSlicePartitionResponse)S3Client.CLIENT().readMeta(new CreateSlicePartitionRequest(sessionKey, sliceKey));
			if (cspr.isSucceeded())
			{
				response = (Response)S3Client.CLIENT().read(this.edsaAddress, new SliceUploadRequest(sessionKey, S3Client.CLIENT().getFileName(), sliceKey, cspr.getPartitionIndex(), sliceIndex, edID, position, bytes, description, false));
				uploadResponses = Tools.filter(response.getResponses(), SliceUploadResponse.class);
				for (SliceUploadResponse entry : uploadResponses)
				{
					if (!entry.isSucceeded())
					{
						isSucceeded = false;
						break;
					}
				}
				
				uploadedSize += bytes.length;
				log.info(bytes.length + " bytes sent: EDID = " + edID + ", position = " + position + "; uploaded size = " + uploadedSize);

				startIndex = endIndex + 1;
				endIndex = startIndex + S3Client.CLIENT().getSliceSize() - 1;
				sliceIndex++;

				// When increasing the scale of the storage cluster, the uploading process should be paused temporarily. If so, the instance of isSucceeded of CreateSlicePartitionResponse is false. 09/11/2020, Bing Li
				bytes = FileManager.loadFile(S3Client.CLIENT().getFile(), startIndex, endIndex);
				edID = sliceIndex % numberOfGroups;
				position = sliceIndex / numberOfGroups;
				
				sliceKey = KeyCreator.getSliceStateKey(sessionKey, edID, position);
			}
			else
			{
				log.info("Uploading is paused temporarily ...");
				Thread.sleep(S3Config.UPLOAD_SLEEP);
			}
		}

		cspr = new CreateSlicePartitionResponse(-1, true);
		if (isSucceeded)
		{
			if (cspr.isSucceeded())
			{
				edID = sliceIndex % numberOfGroups;
				position = sliceIndex / numberOfGroups;
				bytes = FileManager.loadFile(S3Client.CLIENT().getFile(), startIndex, (int)(size - 1));

				uploadedSize += bytes.length;
				log.info("Last: " + bytes.length + " bytes sent: EDID = " + edID + ", position = " + position + "; uploaded size = " + uploadedSize);
				
				sliceKey = KeyCreator.getSliceStateKey(sessionKey, edID, position);
				cspr = (CreateSlicePartitionResponse)S3Client.CLIENT().readMeta(new CreateSlicePartitionRequest(sessionKey, sliceKey));
				if (cspr.isSucceeded())
				{
					response = (Response)S3Client.CLIENT().read(this.edsaAddress, new SliceUploadRequest(sessionKey, S3Client.CLIENT().getFile(), sliceKey, cspr.getPartitionIndex(), sliceIndex, edID, position, bytes, description, true));
					uploadResponses = Tools.filter(response.getResponses(), SliceUploadResponse.class);
					for (SliceUploadResponse entry : uploadResponses)
					{
						if (entry.isSucceeded())
						{
							log.info("The file is succeeded to upload to the meta server");
							return;
						}
					}
				}
				else
				{
					log.info("Uploading is paused temporarily ...");
					Thread.sleep(S3Config.UPLOAD_SLEEP);
				}
			}
			else
			{
				log.info("Uploading is paused temporarily ...");
				Thread.sleep(S3Config.UPLOAD_SLEEP);
			}
		}
		log.info("The file is failed to upload to the meta server");
	}
}
