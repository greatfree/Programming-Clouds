package edu.chainnet.crawler.child.crawl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;

import ca.mama.util.FreeUtilConfig;

// Created: 04/24/2021, Bing Li
class JsoupTools
{
	public static Document getDoc(String hubURL)
	{
		Document doc = null;
		HttpURLConnection hConn = null;
		InputStream is = null;
		URL url;
		try
		{
			url = new URL(hubURL);
			hConn = (HttpURLConnection)url.openConnection();
			is = new BufferedInputStream(hConn.getInputStream());
			doc = Jsoup.parse(is, FreeUtilConfig.UTF_8, hubURL);
		}
		catch (IOException e)
		{
//			return null;
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
			if (hConn != null)
			{
				hConn.disconnect();
			}
		}
		return doc;
	}
	
	public static Document getDocIgnoreContentType(String hubURL, int timeout) throws IOException
	{
		Document doc = null;
		Connection conn = Jsoup.connect(hubURL);
		conn.ignoreContentType(true);
		conn.timeout(timeout);
		doc = conn.get();
		doc.charset(Charset.forName(FreeUtilConfig.UTF_8));
		return doc;
	}

	// For testing. 01/02/2018, Bing Li
	public static boolean getDocIgnoreContentTypeForTesting(String hubURL, int timeout)
	{
		Document doc = null;
		Connection conn = Jsoup.connect(hubURL);
		conn.ignoreContentType(true);
		conn.timeout(timeout);
		try
		{
			doc = conn.get();
			doc.charset(Charset.forName(FreeUtilConfig.UTF_8));
			return true;
		}
		catch (IOException | UncheckedIOException e)
		{
			return false;
		}
	}

}
