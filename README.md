# ricardocrawler
Java crawler for https://www.ricardo.ch

## Implementation steps and decisions...
- I've been reading about crawling and crawling best practices.
- First I was about to use some Java web crawler framework like Apache Nutch or Storm Crawler. It was really hard to get them up and running on Windows development environment in couple of steps. I've decided to create crawler without using framework, because, in my opinion, it demonstrates development in a better way since the implementation details are not hidden behind framework and I had much more control.
- Decided to use Maven as a build and dependency management tool for my project. Using maven-assembly-plugin to build complete project with all dependencies as a executable jar file. Here I wanted to provide ability (beside configuration in conf directory) to send configuration information as command line arguments to main method in Crawler class, but didn't have time to implement it. 
- Decided to use MongoDB NoSQl database for storing any data important for crawler since I had to keep the links crawler already visited at some place, which could be used and shared by multiple instances of crawler. Decided to use MongoDB database because it scales up easily if needed.
- Decided to use Jsoup Java library for connecting to the web page and selecting it's content.
- Created conf directory to store configuration settings outside of the source code so they can be easily changed and used by crawler.
- Created crawled_data directory (path can be set in crawler.properties file) to keep crawled data in a files with data in JSON format.
- Used Google Chrome development tool for inspecting ricardo.sh API and web pages content.
- Using Chrome development tool found out that categories have links with .ric-category-nav__parent-link CSS class and that this link points to the pages with articles from different categories. Found out that article has CSS class .ric-article.
- Implemented logic that uses previous information for identifying article and extracting title, link and article text to keep it in JSON format in the file.
- Didn't have time to inspect the article page in more details and to extract more useful information about article itself.
- Added MAX_DEPTH to define how deep redirections to different pages should go.
- Didn't have time to inspect robots.txt file and to implement better politeness (by delaying crawler requests to the web pages for example).
- Some criticism of ricardo.ch - As far as I could see site is not implemented as a single page web application, but I didn't have much to to investigate deeper. At every click it loads complete HTML pages with a lot of javascript libraries and there are some failed request for some of resources. I would rather implement it as a single page application, since it's mostly data driven, and load only the parts of the page that really need to be changed.

## Requirements
- MongoDB - Crawler uses MongoDB database for storing the links that are already visted in order to avoid visiting them again. 
- Maven - Used for retrieving project dependencies and building the project.

## Configuration
- Configration should be done using crawler.properties and db.properties files from conf directory. 
- In crawler.properties you should set the path where the crawled data (files containing article details in JSON format) will be stored - by default data will go to the target directory. You can also set seedUrl, but this should stay https://www.ricardo.ch for now.
- In db.properties files you should set connection to MongoDB database (only server and port for now, without password, default values are localhost for server and 27017 for port)

## Build and run executable jar
- You should run "mvn install" on project root containing pom.xml file. This will create a target directory at project root.
- You should then run the crawler by running "java -jar crawler-0.0.1-SNAPSHOT-jar-with-dependencies.jar" from target directory.
- If you haven't changed crawledDataPath from crawler.properties, you should be able to see crawled data in target/crawled_data directory.
- It's also possible to provide configuration setting when running "java -jar crawler-0.0.1-SNAPSHOT-jar-with-dependencies.jar", but this is not implemented yet.

## Docker build and run
- Run "docker build -t ricardocrawler_image ." at project root containing Dockerfile to create an image.
- Run "docker run -t -d ricardocrawler_image" to run the image in container.
- Run "docker ps -a" to get container id.
- Set IP of the server in crawler.properties to your host IP address where you have MongoDB installed that should be used by different instances of crawler.
- Run "docker exec -it <container_id> bash -c "cd /srv/ricardocrawler; mvn install;" to build the project.
- Run "docker exec -it <container_id> bash -c "cd /srv/ricardocrawler/target; java -jar crawler-0.0.1-SNAPSHOT-jar-with-dependencies.jar""
- Crawled data will be created in target folder if not defined differently in crawler.properties.