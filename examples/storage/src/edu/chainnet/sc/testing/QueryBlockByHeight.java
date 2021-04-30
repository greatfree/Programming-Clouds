package edu.chainnet.sc.testing;

import cn.tdchain.Block;
import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/15/2020, Bing Li
class QueryBlockByHeight extends Base
{
	public static void main(String[] args)
	{
		for (int i = 1; i < 3; i++)
		{
			blockByHeight(Long.valueOf(i));
		}
	}

	public static void blockByHeight(long height)
	{
		try
		{
			Result<Block<Trans>> result = connection.getBlock(height);

			if (result.isSuccess())
			{
				if (result.getEntity().getHeight().equals(height))
				{
					log.info("\n===> query max block success.");
				}
			}
			else
			{
				log.info("\n===> query max block fail.");
			}
			Tools.printResult(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
