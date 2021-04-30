package edu.chainnet.sc.testing;

import cn.tdchain.Trans;
import cn.tdchain.jbcc.BatchTrans;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/15/2020, Bing Li
class OpenTransaction extends Base
{
	public static void main(String[] args)
	{
		try
		{

			BatchTrans<Trans> batchTrans = AddBatchTrans.transList();
			String[] keys = batchTrans.keyToArray();

			// # open transaction
			boolean success = connection.startTransaction(keys);
			if (success)
			{
				log.info("\n===> open tansaction success. ");
				connection.addBatchTrans(batchTrans);
			}
			else
			{
				log.info("===>\nopen tansaction fail. ");
			}
			// # release lock-transaction
			connection.stopTransaction(keys);

			Tools.printResult(batchTrans);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
