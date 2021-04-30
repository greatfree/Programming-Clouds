package edu.chainnet.sc.testing;

import cn.tdchain.Block;
import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

/*
 * It gets failed!. 10/15/2020, Bing Li
 */

// Created: 10/15/2020, Bing Li
class QueryMaxBlock extends Base
{

	public static void main(String[] args)
	{
		for (int i = 1; i < 3; i++)
		{
			maxBlock();
		}
	}

	public static void maxBlock()
	{
		try
		{
			Result<Block<Trans>> result = connection.getMaxBlock();

			if (result.isSuccess())
			{
				log.info("\n===> query max block success.");
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
