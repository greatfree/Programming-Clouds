package edu.chainnet.sc.testing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.tdchain.Trans;
import cn.tdchain.TransHead;
import cn.tdchain.jbcc.Result;
import edu.chainnet.sc.Base;
import edu.chainnet.sc.Tools;

// Created: 10/13/2020, Bing Li
class AddTransaction extends Base
{

	public static void sendTrans()
	{
		Trans trans = trans();
		Result<TransHead> result = connection.addTrans(trans);

		if (result.isSuccess())
		{
			log.info("\n===> add trans success.");
		}
		else
		{
			log.info("\n===> add trans fail.");
		}
		Tools.printResult(result);
	}

	public static Trans trans()
	{
        Trans trans = new Trans();
        trans.setKey("warne");
        Map<String, Object> data = new HashMap<>();
        data.put("name", "warne");
        data.put("age", 20);
        data.put("where", "I am tian de technology.");
        trans.setData(JSON.toJSONString(data));
        trans.setType("Test");
        trans.setTimestamp(new Date().getTime());
        return trans;
	}

	public static void main(String[] args)
	{
		for (int i = 0; i < 5; i++)
		{
			sendTrans();
		}
	}
}
