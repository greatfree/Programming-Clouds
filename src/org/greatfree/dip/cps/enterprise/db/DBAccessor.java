package org.greatfree.dip.cps.enterprise.db;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.dip.cps.enterprise.EnterpriseConfig;
import org.greatfree.dip.cps.enterprise.EnterpriseTools;
import org.greatfree.util.FileManager;

// Created: 04/23/2020, Bing Li
class DBAccessor
{
	private Map<String, Object> entityBeans;

	private DBAccessor()
	{
		this.entityBeans = new ConcurrentHashMap<String, Object>();
	}
	
	private static DBAccessor instance = new DBAccessor();
	
	public static DBAccessor ENTERPRISE()
	{
		if (instance == null)
		{
			instance = new DBAccessor();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void initEntityBean(String clientKey, String className, List<String> parameterTypes, Object[] parameterValues, byte[] classBytes) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
		FileManager.saveFile(EnterpriseConfig.CLASSPATH + className, classBytes);
		this.entityBeans.put(EnterpriseTools.getObjectKey(clientKey, className), EnterpriseTools.getObject(className, parameterTypes, parameterValues));
	}

	public Object invoke(String clientKey, String className, String methodName, List<String> parameterTypes, Object[] parameterValues) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method method = EnterpriseTools.getMethod(className, methodName, parameterTypes);
		return method.invoke(this.entityBeans.get(EnterpriseTools.getObjectKey(clientKey, className)), parameterValues);
	}
}
