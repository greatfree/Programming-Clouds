package edu.chainnet.sc.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.tdchain.Trans;
import cn.tdchain.TransHead;
import cn.tdchain.jbcc.BatchTrans;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/14/2020, Bing Li
class AddBatchTrans extends Base
{

	public static void main(String[] args)
	{
		try
		{
			for (int i = 0; i < 3; i++)
			{
				BatchTrans<Trans> batchTrans = transList();
				// # add batch trade
				Result<BatchTrans<TransHead>> result = connection.addBatchTrans(batchTrans);

				if (result.isSuccess())
				{
					log.info("\n===> batch trans success.");
				}
				else
				{
					log.info("\n===> batch trans fail.");
				}
				Tools.printResult(result);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static BatchTrans<Trans> transList()
	{
		int transCount = 3;
		List<Trans> transList = new ArrayList<>(transCount);
		Trans trans = null;
		for (int i = 0; i < transCount; i++)
		{
			trans = new Trans();
			trans.setKey("ConnectionTest-key" + i);
			Map<String, Object> data = new HashMap<>();
			data.put("name", "warne" + i);
			data.put("age", 20 + i);
			data.put("index", i);
			trans.setData(JSON.toJSONString(data));
			trans.setType("BatchTest");

			transList.add(trans);
		}

		BatchTrans<Trans> batchTrans = new BatchTrans<>();
		batchTrans.addTransToBatch(transList);

		return batchTrans;
	}
}
