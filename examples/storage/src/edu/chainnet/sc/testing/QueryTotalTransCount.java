package edu.chainnet.sc.testing;

import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/15/2020, Bing Li
class QueryTotalTransCount extends Base
{

	public static void main(String[] args)
	{
		try
		{
			Result<Long> result = connection.getBlockChainTransCount();
			Tools.printResult(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
