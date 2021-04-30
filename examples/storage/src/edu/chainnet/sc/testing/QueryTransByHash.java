package edu.chainnet.sc.testing;

import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

/*
 * Not found. 10/15/2020, Bing Li
 */

// Created: 10/15/2020, Bing Li
class QueryTransByHash extends Base
{

	public static void main(String[] args)
	{
		try
		{
			String hash = "3ee5f300e77beddec41e2ff66d247f0a77db93117231765881e2e2d820c7bff72";
			Result<Trans> result = connection.getTransByHash(hash);

			if (result.isSuccess())
			{
				if (result.getEntity().getHash().equals(hash))
				{
					log.info("\n===> query trans success by hash.");
				}
				else
				{
					log.info("\n===> query trans fail by hash.");
				}
			}
			else
			{
				log.info("\nnot fount trans .");
			}
			Tools.printResult(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
