package edu.chainnet.sc.testing;

import java.util.List;

import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/15/2020, Bing Li
class QueryTransByType extends Base
{

	public static void main(String[] args)
	{
		String type = "Test";
		try
		{
			Result<List<Trans>> result = connection.getTransListByType(type);

			log.info("\n===> query result: ");

			Tools.printResult(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
