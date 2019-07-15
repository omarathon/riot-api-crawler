
# # riot-api-crawler

A customisable League of Legends match crawler, utilising [riot-api-java](https://github.com/taycaldwell/riot-api-java) as a Java wrapper for the [Riot API](https://developer.riotgames.com/).

Features built-in request throttling and interactive logging.

Current version: 1.0.

## Functionality
The crawler operates as follows:
 1. Take a base summoner as an input, and set them as the Summoner being crawled.
 2. Fetch x recent games from the crawled Summoner's match history, where x is determined by the CrawlerConfig.
 3. Handle each obtained Match object via the OutputHandler, which shall do something desired with each Match.
 4. Proceed to search for the next Summoner to crawl. Iterate through each Match, passing it through a filter in the CrawlerConfig to determine if it's crawlable, until a  crawlable Match is found.
 5. Obtain all of the Summoners in the Match.
 6. Randomly pick a new Summoner from the Match, passing each through a filter in the CrawlerConfig to determine if they're crawlable, until a crawlable Summoner is found.
 7. If (6) ends with no crawlable Summoners found, return to (4), moving to the next Match.
 8. If (6) ends with no more Matches left at (4), the crawler has reached a dead end, and its operation shall stop. This event is extremely unlikely unless the filters in the CrawlerConfig are extremely restrictive.
 9. If a crawlable Summoner has been found, set them as the new Summoner being crawled, and return to (2).

## Notable Behaviour

In its current implementation, the crawler has the following  behaviour for error-handling:

  - After a call to the Riot API, if a RateLimitException is obtained, then there is a built-in throttling system which shall sleep the thread until further calls to the API can be made again. When possible, the request shall be repeated.
- When obtaining the MatchList for the Summoner being crawled, all errors except a RateLimitException cause the crawler to stop crawling.
- When obtaining the Match object for each MatchReference in the list of MatchReferences, any RiotApiException shall mean such Match is not sent to the OutputHandler, nor is it stored to be potentially crawled.
- When obtaining the Summoner object for each Player in a Match, any RiotApiException shall mean that such Summoner will not be crawled, and is skipped.

 ## Main Classes
These files may be located within riotapicrawler/src, and include:
 - Crawler.java: The central object to be initialised, initiates crawling on a new thread after calling its run method with an input Summoner. One must construct a Crawler with:
    - your Riot API key,
    - an OutputHandler, which does something desired with the obtained Match objects,
    - a CrawlerConfig, which filters the crawled upon Matches and Summoners, and provides additional parameters to configure the operation of the crawler,
    -  a Path, where the logs from the crawler shall be generated.



## Interfaces and Customisability Classes
These files may be located within riotapicrawler/src/lib, and include:
  - OutputHandler.java: An interface which takes a Match object and does something with it, for example formatting and saving it to a JSON file, or uploading it to a database.
  -  MatchFilter.java and SummonerFilter.java: Interfaces which predicate Match and Summoner objects, with their filter method returning true if the Object is "accepted" by the filter, and false otherwise.
  - MatchFormatter.java: An interface which transforms an input Match into an output Object.

The above interfaces, although seemingly abstract, provide a high level of customisability. They are combined in the following class:
  - CrawlerConfig.java: A configuration class for the crawler.  Stores a MatchFilter and SummonerFilter used by the crawler to  predicate the crawled Matches and Summoners,  and an integer maxMatches property which determines the number of recent matches to obtain and  send to the output handler for each crawled player.

Included in the presets folder of this repo, one may find presets for all of these interfaces and classes. They are also contained within the builds. 

The provided presets are mostly basic and show a simple and more complex example, however there is included two OutputHandlers:
  - FileOutputHandler.java - An OutputHandler that writes a JSON file with name of the game ID for each Match object at a specified directory, with contents the output of the Match from its MatchFormatter. 
Utilises [Google Gson](https://github.com/google/gson).
  - PostFirebaseOutputHandler.java - An extension output handler, located within the extras/postfirebaseoutputhandler directory of this repo, which HTTP POSTs the formatted output from a MatchFormatter to a [Google Firebase](https://firebase.google.com/).
Utilises [Google Gson](https://github.com/google/gson) and [firebase4j](https://github.com/bane73/firebase4j).

## Examples

One may find examplar uses of the Crawler within the examples directory of this repo:

 - Example.java: an examplar use of the Crawler, which:
 
   - Only crawls Matches that are at least 20 minutes long,
   - Only crawls Summoners that are at least level 30,
   - Only processes 5 recent Matches for each crawled Summoner,
   - Formats each Match into a set of statistics for both teams,
      and stores the results in JSON files within a given directory.

## Dependencies
This project was developed via Maven, and used the following dependencies as libraries:
 - **[riot-api-java](https://github.com/taycaldwell/riot-api-java)** - com.github.taycaldwell, riot-api-java
```xml
<repositories>  
	 <repository> 
		 <id>jitpack.io</id>  
		 <url>https://jitpack.io</url>  
	 </repository>
</repositories>
<dependencies>
	 <dependency>  
		 <groupId>com.github.taycaldwell</groupId>  
		 <artifactId>riot-api-java</artifactId>  
		 <version>4.2.0</version>  
	</dependency>
</dependencies>
```
 - **[Google Gson](https://github.com/google/gson)** - com.google.code.gson, gson
```xml
<dependencies>
	<dependency>  
		 <groupId>com.google.code.gson</groupId>  
		 <artifactId>gson</artifactId>  
		 <version>2.8.5</version>  
	</dependency>
</dependencies>
```

There are some additional dependencies required for the extras, specifically for the PostFirebaseOutputHandler:
 - **[Google Firebase](https://firebase.google.com/)** - com.google.firebase, firebase-admin
```xml
<dependencies>  
	 <dependency> 
		 <groupId>com.google.firebase</groupId>  
		 <artifactId>firebase-admin</artifactId>  
		 <version>6.8.1</version>  
	 </dependency>
</dependencies>
```
 - **[firebase4j](https://github.com/bane73/firebase4j)** - com.github.bane73, firebase4j
```xml
<dependencies>  
	<dependency>  
		 <groupId>com.github.bane73</groupId>  
		 <artifactId>firebase4j</artifactId>  
		 <version>Tmaster-b6f90e9764-1</version>  
	</dependency>
</dependencies>
```

## Usage

One may use this crawler by adding one of the jars within the builds directory of this repo to their project. It is their choice whether to choose the jar containing or not containing the dependencies, however if the latter option is chosen they must install the appropriate dependencies as above.

It is recommended for one to install the first-half of the above dependencies and to use the jar that does not contain the dependencies in their project, if they do not intend to use the PostFirebaseOutputHandler.

## Future Developments

Here I list some interesting future developments to the project:

  - Multithreaded Crawler instances.

## Changelog

  - 15/07/2019: First version uploaded.
 

## Remark

One is recommended only to use this code within prototype systems - *it may not be safe for production*.
