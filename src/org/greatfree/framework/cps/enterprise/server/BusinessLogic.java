package org.greatfree.framework.cps.enterprise.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.framework.cps.enterprise.EnterpriseConfig;
import org.greatfree.framework.cps.enterprise.EnterpriseTools;
import org.greatfree.util.FileManager;

// Created: 04/21/2020, Bing Li
class BusinessLogic
{
	private Map<String, Object> sessionBeans;
	
	private BusinessLogic()
	{
		this.sessionBeans = new ConcurrentHashMap<String, Object>();
	}
	
	private static BusinessLogic instance = new BusinessLogic();
	
	public static BusinessLogic ENTERPRISE()
	{
		if (instance == null)
		{
			instance = new BusinessLogic();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void initSessionBean(String clientKey, String className, List<String> parameterTypes, Object[] parameterValues, byte[] classBytes) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
		FileManager.saveFile(EnterpriseConfig.CLASSPATH + className, classBytes);
		this.sessionBeans.put(EnterpriseTools.getObjectKey(clientKey, className), EnterpriseTools.getObject(className, parameterTypes, parameterValues));
	}

	public Object invoke(String clientKey, String className, String methodName, List<String> parameterTypes, Object[] parameterValues) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method method = EnterpriseTools.getMethod(className, methodName, parameterTypes);
		return method.invoke(this.sessionBeans.get(EnterpriseTools.getObjectKey(clientKey, className)), parameterValues);
	}
}
