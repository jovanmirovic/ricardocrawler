# ricardocrawler
Java crawler for https://www.ricardo.ch

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