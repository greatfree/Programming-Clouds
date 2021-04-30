package edu.chainnet.s3.meta;

import java.util.Set;
import java.util.logging.Logger;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.meta.table.MetaCache;

/*
 * The program is responsible for managing the meta data for the S3 system. 07/10/2020, Bing Li
 */

// Created: 07/10/2020, Bing Li
class MetaManager
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.meta");

	/*
	 * The method is a temporary solution. It needs to be modified upon investigating specific requirements. 07/21/2020, Bing Li
	 */
	public static int evaluateBlocksN(long fileSize)
	{
		Set<String> keys = MetaCache.META().getSSStateKeys();
		int aliveDSSize = 0;
		for (String entry : keys)
		{
			if (MetaCache.META().isAlive(entry))
			{
				aliveDSSize++;
			}
		}

		int n = 0;
		if (aliveDSSize > 0)
		{
			long sliceSize = fileSize / aliveDSSize;
			for (String entry : keys)
			{
				if (MetaCache.META().isAlive(entry) && MetaCache.META().getFreeSpace(entry) > sliceSize)
				{
					n++;
				}
			}
			return n;
		}
		return MetaConfig.INVALID_N;
	}

	/*
	 * The solution is too rough. If the size of the file is large, the slice size becomes large as well. 09/04/2020, Bing Li
	 * 
	 * This is a simplified solution for testing to speed up the overall distributed architecture. 07/21/2020, Bing Li 
	 */
	public static int evaluateBlocksN(int k)
	{
		return k + S3Config.ADDITIONAL_K;
	}

	/*
	 * The solution to evaluate the N is also a temporary one. It aims to reduce the size of the slice. 09/04/2020, Bing Li
	 */
	public static int evaluateBlocksN(long fileSize, int k, int maxSliceSize)
	{
		int n = evaluateBlocksN(k);
		if (fileSize < maxSliceSize)
		{
			return n;
		}
		else
		{
			log.info("MetaManager-evaluateBlocksN(): fileSize = " + fileSize);
			log.info("MetaManager-evaluateBlocksN(): maxSliceSize = " + maxSliceSize);
			log.info("MetaManager-evaluateBlocksN(): k = " + k);
			log.info("MetaManager-evaluateBlocksN(): n = " + n);
			
			int largeN = (int)(fileSize / maxSliceSize + n * k);
			if (fileSize / maxSliceSize < n)
			{
				return n;
			}
			else
			{
				return largeN;
			}
		}
	}
}
