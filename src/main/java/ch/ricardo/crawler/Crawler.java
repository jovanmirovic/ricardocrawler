package ch.ricardo.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.ricardo.crawler.db.DB;
import ch.ricardo.crawler.exception.ApplicationException;

/**
 * <P>
 * Class containing crawler specific logic.
 * <P/>
 * 
 * @author Jovan Mirovic
 * 
 *         Created 24 Nov 2018
 *
 */
public class Crawler
{

	// Use a fake USER_AGENT so the web server thinks the robot is a normal web
	// browser.
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	// the depth of the crawler - how deep crawler should go during redirection to
	// different links
	private static final int MAX_DEPTH = 4;
	// mongo database storing the links crawler already visited to avoid duplication
	private static MongoDatabase database = null;
	// curr article being processed in order to use some useful data from this
	// object
	private static Element currArticle = null;

	public static void main(String[] args) throws SQLException, IOException
	{
		try
		{
			// acquire database connection
			database = DB.getDatabase();
			// start crawling
			getLinks(CrawlerProperties.getSeedUrl(), false, 0);
			// close mongo client to avoid resource leaking
			DB.closeMongoClient();
			// catching application exception and logging the message(better logging
			// framework should be implemented)
		} catch (ApplicationException e)
		{
			// here localization should be implemented if needed
			System.err.println(e.getMessage());
			System.err.println(e.getCause().getMessage());
		}
	}

	/**
	 * <P>
	 * Method used for start crawling. This method is also being called recursively
	 * for crawling until certain depth.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 * @param String URL Represents URL which should be crawled.
	 * @param        boolean isArticle Represents indicator whether the provided URL
	 *               is article URL or not.
	 * @param        int depth Represents current depth of recursive method call.
	 * 
	 * @exception ApplicationException Represents application specific exception.
	 */
	private static void getLinks(String URL, boolean isArticle, int depth) throws ApplicationException
	{
		// if crawler already exceeded max depth just stop the crawling process
		if (depth == MAX_DEPTH)
		{
			return;
		}
		// if article log the article url
		if (isArticle)
		{
			System.out.println("ARTICLE URL: " + URL);
		} else
		{
			System.out.println("URL: " + URL);
		}
		// check if the given URL is already in database
		MongoCollection<org.bson.Document> linksCollection = database.getCollection("links");
		long foundLinksCount = linksCollection.countDocuments(new BasicDBObject("url", URL));
		// if given URL not in database already
		if (foundLinksCount == 0)
		{
			// get useful information
			Document doc = null;
			try
			{
				Connection connection = Jsoup.connect(URL).userAgent(USER_AGENT);
				// get content
				doc = connection.get();
				// insert visited URL to db to avoid visiting it again
				linksCollection.insertOne(new org.bson.Document("url", URL));
				// catch exception if there were problems connecting to the url
			} catch (IOException e)
			{
				throw new ApplicationException(String.format("Error connecting to the url %s.", URL), e);
			}

			// examined ricardo.ch and found out there is a menu with submenu containing
			// links with css class "ric-category-nav__parent-link" which lead to the page
			// with articles
			Elements categories = doc.select("a.ric-category-nav__parent-link");
			// examined articles page and found out that there are link to article details
			// with css class "ric-article"
			Elements articles = doc.select("a.ric-article");

			// increasing depth
			depth++;
			// visit all categories from menu and submenu
			for (Element category : categories)
			{
				getLinks(category.attr("abs:href"), false, depth);
			}

			// visit all articles
			for (Element article : articles)
			{
				// set current article being processed
				currArticle = article;
				getLinks(article.attr("abs:href"), true, depth);
			}

			// if current element is article write the details in a file as JSON object
			if (isArticle)
			{
				writeToFile(currArticle, doc.text());
			}
		}
	}

	/**
	 * <P>
	 * Method used for writing article as JSON to file.
	 * <P/>
	 * 
	 * @author Jovan Mirovic Comtrade
	 * 
	 *         Created 24 Nov 2018
	 *
	 */
	private static void writeToFile(Element article, String details)
	{
		Writer writer = null;
		try
		{
			// read path where crawled data should be stored from config file
			String dirPath = (CrawlerProperties.getCrawledDataPath().endsWith("/")
					? CrawlerProperties.getCrawledDataPath()
					: CrawlerProperties.getCrawledDataPath() + "/");
			// create directory where crawled data should be stored if doesn't exist already
			File dir = new File(dirPath);
			if (!dir.exists())
			{
				dir.mkdir();
			}
			// prepare writer and set encoding to UTF-8
			writer = new OutputStreamWriter(
					new FileOutputStream((CrawlerProperties.getCrawledDataPath().endsWith("/")
							? CrawlerProperties.getCrawledDataPath()
							: CrawlerProperties.getCrawledDataPath() + "/") + article.attr("title")),
					StandardCharsets.UTF_8);
			try
			{
				// prepare article JSON object
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.createObjectNode();
				((ObjectNode) node).put("title", article.attr("title"));
				((ObjectNode) node).put("link", article.attr("abs:href"));
				((ObjectNode) node).put("details", details);
				String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
				// save to file
				writer.write(jsonString);
			} catch (IOException e)
			{
				System.err.println(e.getMessage());
			}
			writer.close();
		} catch (IOException e)
		{
			System.err.println(e.getMessage());
		} catch (ApplicationException e)
		{
			System.err.println(e.getMessage());
		}
	}
}
