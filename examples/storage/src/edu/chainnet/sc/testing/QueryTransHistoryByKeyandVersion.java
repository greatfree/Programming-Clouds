package edu.chainnet.sc.testing;

import java.util.List;

import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/15/2020, Bing Li
class QueryTransHistoryByKeyandVersion extends Base
{

	public static void main(String[] args)
	{
		String key = "xiaoming_5";
		int startIndex = 0;
		int endIndex = 10;
		try
		{
			Result<List<Trans>> result = connection.getTransHistoryByKey(key, startIndex, endIndex);
			log.info("\n===> query result: ");

			Tools.printResult(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
