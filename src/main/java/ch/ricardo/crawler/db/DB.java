package ch.ricardo.crawler.db;

import java.io.FileInputStream;
import java.util.Properties;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import ch.ricardo.crawler.exception.ApplicationException;

/**
 * <P>
 * Class used for acquiring database connection.
 * <P/>
 * 
 * @author Jovan Mirovic
 * 
 *         Created 24 Nov 2018
 *
 */
public class DB
{
	private static MongoClient mongoClient = null;

	/**
	 * <P>
	 * Acquire database.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 */
	public static MongoDatabase getDatabase() throws ApplicationException
	{
		Properties props = new Properties();
		FileInputStream in = null;
		try
		{
			in = new FileInputStream("config/db.properties");
			props.load(in);
		} catch (Exception e)
		{
			throw new ApplicationException("Error reading database connection parameters.", e);
		} finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				} catch (Exception e)
				{
					throw new ApplicationException("Error closing db.properties file input stream.", e);
				}
			}
		}

		ServerAddress serverAddress = new ServerAddress(props.getProperty("server"),
				Integer.valueOf(props.getProperty("port")));
		mongoClient = new MongoClient(serverAddress);
		return mongoClient.getDatabase("ricardocrawler");
	}

	/**
	 * <P>
	 * Close mongo client to void resource leaking.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 */
	public static void closeMongoClient() throws ApplicationException
	{
		mongoClient.close();
	}
}
