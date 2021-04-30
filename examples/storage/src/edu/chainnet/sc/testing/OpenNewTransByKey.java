package edu.chainnet.sc.testing;

import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/15/2020, Bing Li
class OpenNewTransByKey extends Base
{

	public static void main(String[] args)
	{
		String key = "warne";
		try
		{
			Result<Trans> result = connection.getNewTransByKey(key);

			if (result.isSuccess())
			{
				if (result.getEntity().getKey().equals(key))
				{
					log.info("\n===> query new trans success.");
				}
			}
			else
			{
				log.info("\n===> query new trans fail.");
			}
			Tools.printResult(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
