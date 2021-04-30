package edu.chainnet.sc.testing;

import cn.tdchain.jbcc.bql.Condition;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;
import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import cn.tdchain.jbcc.bql.BQL;
import cn.tdchain.jbcc.bql.BQLResult;

// Created: 10/14/2020, Bing Li
class BQLQueryTrans extends Base
{

	public static void main(String[] args)
	{
		Condition c1 = new Condition("name", BQL.Relationship.equal, "xiaoming");
		Condition c2 = new Condition("age", BQL.Relationship.greater, 18);

		c1.setAnd(c2);

		BQL bql = new BQL();
		bql.setPage(1);
		bql.setCondition(c1);

		try
		{
			Result<BQLResult> result = connection.getNewTransByBQL(bql);

			if (result.isSuccess())
			{
				BQLResult bqlResult = result.getEntity();
				log.info("bqlResult page=" + bqlResult.getPage());
				log.info("bqlResult size=" + bqlResult.getSize());
				log.info("bqlResult count=" + bqlResult.getCount());
				log.info("bqlResult getList().size()=" + bqlResult.getList().size());
				for (Trans t : bqlResult.getList())
				{
					log.info(t.toJsonString());
				}
				log.info("\n===> query new trans success.");

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
