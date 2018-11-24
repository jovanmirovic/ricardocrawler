package ch.ricardo.crawler;

import java.io.FileInputStream;
import java.util.Properties;

import ch.ricardo.crawler.exception.ApplicationException;

/**
 * <P>
 * Class used for reading crawler configuration settings.
 * <P/>
 * 
 * @author Jovan Mirovic
 * 
 *         Created 24 Nov 2018
 *
 */
public class CrawlerProperties
{
	private static Properties props = null;

	/**
	 * <P>
	 * Get start url for crawler from config file.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 */
	public static String getSeedUrl() throws ApplicationException
	{
		if (props == null)
		{
			readProperties();
		}
		return props.getProperty("seedUrl");
	}

	/**
	 * <P>
	 * Get a path where to store crawled data from config file.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 */
	public static String getCrawledDataPath() throws ApplicationException
	{
		if (props == null)
		{
			readProperties();
		}
		return props.getProperty("crawledDataPath");
	}

	/**
	 * <P>
	 * Read properties from config file.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 */
	private static void readProperties() throws ApplicationException
	{
		props = new Properties();
		FileInputStream in = null;
		try
		{
			in = new FileInputStream("config/crawler.properties");
			props.load(in);
		} catch (Exception e)
		{
			throw new ApplicationException("Error reading crawler properties.", e);
		} finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				} catch (Exception e)
				{
					throw new ApplicationException("Error closing crawler.properties file input stream.", e);
				}
			}
		}
	}
}
