
# # riot-api-crawler

A highly customisable and extensible League of Legends match crawler, utilising [Orianna](https://github.com/meraki-analytics/orianna) as a Java framework for the Riot Games League of Legends API, and [Guava](https://github.com/google/guava) as a caching provider.

This tool may be used for gathering large datasets of ``Matches`` in an intelligent and efficient manner.

Current version: **2.0**.

## Functionality

The crawler begins at a ``Summoner``, obtains their ``MatchHistory``, sends their ``MatchHistory`` to its ``OutputHandler`` which will process each ``Match``, then seeks the next ``Summoner`` to crawl from the ``Participants`` in each ``Match`` of the ``MatchHistory``.

Intelligent behaviour may be implemented through the use of ``Filters``, which make the decisions regarding the ``Matches`` and the ``Summoners`` to crawl, as well as the ``Matches`` that are handled by the ``OutputHandler``.

If no crawlable next ``Summoner`` is found from a current ``Summoner``, the crawler shall backtrack, moving to a previously visited Summoner, and resume the search from them.

With the backtracking behaviour, the crawler traverses through ``Matches`` and ``Summoners`` in a depth-first fashion, where as soon as a crawlable ``Summoner`` is found, we move to them. However, when backtracking, we pick random previous points (``Summoners``).

Below is a flowchart detailing the operation of the ``Crawler`` when it's called on an input Summoner:

![Crawler Flowchart](https://i.imgur.com/BvKHI9B.png)

## Quick Start

Below is an example to begin crawling with a very basic configuration, where it:
 - Prints the output Matches to System.out (using the [PrintOutputHandler](riotapicrawler/presets/outputhandlers/PrintOutputHandler.java)) with no formatting (using a [DoNothingMatchFormatter](riotapicrawler/presets/matchformatters/DoNothingMatchFormatter.java)),
 - Accepts all Matches and Summoners as crawlable, and obtains 5 matches per Summoner (by initialising a [BasicCrawlerConfig](riotapicrawler/presets/crawlerconfigs/BasicCrawlerConfig.java) with 5 as the input maxMatches parameter)
 
 ```java
// Construct the PrintOutputHandler with a StringFormatter
// that uses a DoNothingMatchFilter, so we format the Matches
// into unmodified Strings and print them to System.out.
OutputHandler outputHandler  = new PrintOutputHandler(new StringMatchFormatter(new DoNothingMatchFormatter()));

// Construct a BasicCrawlerConfig, with 5 as the input maxMatches parameter
// (number of recent Matches to obtain from each match history)
CrawlerConfig crawlerConfig = new BasicCrawlerConfig(5);

// Construct a Crawler, with the above CrawlerConfig and OutputHandler
Crawler crawler = new Crawler(crawlerConfig, outputHandler);

// Set Orianna's API key to your Riot API key
Orianna.setRiotAPIKey("YOUR RIOT API KEY GOES HERE");

// Run the Crawler on a new thread, starting from an input Summoner of choice
// (with their Platform). Chosen here is https://euw.op.gg/summoner/userName=pff.
crawler.run("pff", Platform.EUROPE_WEST);
 ```
 
 

 ## Main Classes
These files may be located within *riotapicrawler/src*, and include:
 - [**Crawler.java**](riotapicrawler/src/Crawler.java) - The central object to be constructed, initiates crawling on a new thread after calling its run method with an input Summoner.
 
 - [**OutputHandler**](riotapicrawler/src/lib/handler/OutputHandler.java) - Recieves and processes the output from the Crawler.
 
- [**CrawlerConfig**](riotapicrawler/src/lib/CrawlerConfig.java) - Manages the tools used by the Crawler that determine its movement through Matches and Summoners.

- [**SummonerHistory**](riotapicrawler/src/lib/SummonerHistory.java) - Manages the previously visited Summoners.

- [**CrawlerListener**](riotapicrawler/src/lib/CrawlerListener.java) - Implementing this class allows handling of the events generated from the Crawler.

- [**MatchFilter**](riotapicrawler/src/lib/filter/MatchFilter.java) - Controls the movement of the crawler through Matches, and restricts the data sent an OutputHandler.

- [**SummonerFilter**](riotapicrawler/src/lib/filter/SummonerFilter.java) - Controls the movement of the crawler through Summoners.

## Customisablity and Extensibility

Customisability and extensibility are offered in most classes, notably:
- [Filters](../../wiki/Filters)
- [OutputHandlers](../../wiki/OutputHandlers)
- [Listeners](../../wiki/Listeners)

Please visit the [**Wiki**](../../wiki/Home) for further information regarding constructing and implementing your own classes.

Included in the [presets](riotapicrawler/presets) folder, one may find presets for customisable classes. They are also contained within the builds.

## Example

One may find an examplar use of the Crawler within [**Example.java**](examples/Example.java), wherein it:
   - Only crawls Matches that are *at least 20 minutes long*,
   - Only crawls Summoners that are *at least level 30*,
   - Only processes *5 recent Matches* for each crawled Summoner,
   - Formats each Match into a *set of statistics for both teams*, and prints the results to System.out.

## Dependencies
This project was developed via Maven, and used the following dependencies as libraries:
 - [**riot-api-java**](https://github.com/taycaldwell/riot-api-java) - com.github.taycaldwell, riot-api-java
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
 - [**Google Gson**](https://github.com/google/gson) - com.google.code.gson, gson
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
 - [**Google Firebase**](https://firebase.google.com/) - com.google.firebase, firebase-admin
```xml
<dependencies>  
	 <dependency> 
		 <groupId>com.google.firebase</groupId>  
		 <artifactId>firebase-admin</artifactId>  
		 <version>6.8.1</version>  
	 </dependency>
</dependencies>
```
 - [**firebase4j**](https://github.com/bane73/firebase4j) - com.github.bane73, firebase4j
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

One may *use this crawler* by **adding [one of the jars](builds)** within the *builds* directory of this repo to their project. It is their choice whether to choose the jar *containing or not containing the dependencies*, however if the latter option is chosen they must **install the appropriate dependencies as above**.

It is recommended for one to install the first-half of the above dependencies and to use the jar that does not contain the dependencies in their project, if they do not intend to use the PostFirebaseOutputHandler.

**One may either clone this repository to obtain the JAR files, or download them from the Dropbox links below**:

 - [with dependencies](https://www.dropbox.com/s/s4ll1tlsen1bysh/riotapicrawler-1.2-dep.jar?dl=0)
 - [without dependencies](https://www.dropbox.com/s/vsew4i75c1zui5y/riotapicrawler-1.2.jar?dl=0)
 
(Current version: **2.0**)

## Known Issues

 - If a Summoner's MatchList is not obtainable, i.e a 404 error, the crawler stops haphazardly (perhaps they were banned). Currently one may resolve this by implementing a SummonerFilter which tests whether their MatchList data is obtainable / tests whether they are banned.

## Future Developments

Here I list some interesting future developments to the project:

  - Caching API request results to reduce number of API calls.

## Changelog (dd/mm/yyyy)

  - **27/07/2019**: Added Elo MatchFilter and SummonerFilter, filter parameter adjustments. (**v1.2**)
  - **16/07/2019**: Added a PrintOutputHandler. (**v1.1**)
  - **15/07/2019**: First version uploaded. (**v1.0**)
 

## Remark

One is recommended only to use this code within prototype systems - *it may not be safe for production*.
