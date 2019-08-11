
# # riot-api-crawler

A highly customisable and extensible League of Legends match crawler, utilising [Orianna](https://github.com/meraki-analytics/orianna) as a Java framework for the Riot Games League of Legends API, and [Guava](https://github.com/google/guava) as a caching mechanism.

This tool may be used for gathering large datasets of ``Matches`` in an intelligent and efficient manner.

Current version: **2.0**.

## Functionality

The crawler begins at a ``Summoner``, obtains their ``MatchHistory``, sends their ``MatchHistory`` to its ``OutputHandler`` which will process each ``Match``, then seeks the next ``Summoner`` to crawl from the ``Participants`` in each ``Match`` of the ``MatchHistory``.

Intelligent behaviour may be implemented through the use of ``Filters``, which make the decisions regarding the ``Matches`` and the ``Summoners`` to crawl, as well as the ``Matches`` that are handled by the ``OutputHandler``.

The crawler traverses through ``Matches`` and ``Summoners`` in a depth-first fashion, where as soon as a crawlable ``Summoner`` is found, we move to them.

Below is a flowchart detailing the operation of the ``Crawler`` when it's called on an input Summoner:

![Crawler Flowchart](https://i.imgur.com/BvKHI9B.png)

## Notable Behaviour

In its current implementation, the crawler has the following behaviour for error-handling:

  - After a call to the Riot API, if a **RateLimitException** is obtained, then there is a built-in **throttling system** which shall **sleep the thread** until further calls to the API can be made again. When possible, the request shall be **repeated**.
- When obtaining the MatchList for the Summoner being crawled, all errors except a RateLimitException cause the crawler to stop crawling.
- When obtaining the Match object for each MatchReference in the list of MatchReferences, any RiotApiException shall mean such Match is not sent to the OutputHandler, nor is it stored to be potentially crawled.
- When obtaining the Summoner object for each Player in a Match, any RiotApiException shall mean that such Summoner will not be crawled, and is skipped.

## Quick Start

Below is an example to begin crawling with a very basic configuration, where it:
 - Simply prints the output Matches to System.out (using the [PrintOutputHandler](riotapicrawler/presets/outputhandlers/PrintOutputHandler.java)) with no formatting (using a [DoNothingMatchFormatter](riotapicrawler/presets/matchformatters/DoNothingMatchFormatter.java)),
 - Accepts all Matches and Summoners as crawlable, and obtains 5 matches per Summoner (by initialising a [BasicCrawlerConfig](riotapicrawler/presets/crawlerconfigs/BasicCrawlerConfig.java) with 5 as the input maxMatches parameter)
 
 ```java
 // Construct the PrintOutputHandler with a DoNothingMatchFilter
 // (we will simply print the raw Match data)
 OutputHandler outputHandler  = new PrintOutputHandler(new DoNothingMatchFormatter());
 
 // Construct a BasicCrawlerConfig, with 5 as the input maxMatches parameter
 // (number of recent Matches to obtain from each match history)
 CrawlerConfig crawlerConfig = new BasicCrawlerConfig(5);
 
 // Construct a Path where we would like the logs from the Crawler to be written to.
 // In this example, I choose "C:/data/crawler"
 Path logsDirectory = Paths.get("C:/data/crawler");
 
 // Construct a Crawler, with your input Riot API key and the above
 // OutputHandler, CrawlerConfig and log Path
 Crawler crawler = new Crawler("YOUR API KEY GOES HERE", outputHandler, crawlerConfig, logsDirectory);
 
 // Run the Crawler on a new thread, starting from an input Summoner of choice
 // (with their Platform). Chosen here is https://euw.op.gg/summoner/userName=pff.
 crawler.run("pff", Platform.EUW);
 ```
 
 

 ## Main Classes
These files may be located within *riotapicrawler/src*, and include:
 - [**Crawler.java**](riotapicrawler/src/Crawler.java) - The central object to be initialised, initiates crawling on a new thread after calling its run method with an input Summoner. One must construct a Crawler with:
    - your Riot API key,
    - an OutputHandler, which does something desired with the obtained Match objects,
    - a CrawlerConfig, which filters the crawled upon Matches and Summoners, and provides additional parameters to configure the operation of the crawler,
    -  a [Path](https://docs.oracle.com/javase/7/docs/api/java/nio/file/Path.html), where the logs from the crawler shall be generated.



## Interfaces and Customisability Classes
These files may be located within *riotapicrawler/src/lib*, and include:
  - [**OutputHandler.java**](riotapicrawler/src/lib/OutputHandler.java) - An interface which takes a Match object and does something with it, for example formatting and saving it to a JSON file, or uploading it to a database.
  -  [**MatchFilter.java**](riotapicrawler/src/lib/MatchFilter.java) and [**SummonerFilter.java**](riotapicrawler/src/lib/SummonerFilter.java) - Interfaces which predicate Match and Summoner objects, with their filter method returning true if the Object is "accepted" by the filter, and false otherwise.
  - [**MatchFormatter.java**](riotapicrawler/src/lib/MatchFormatter.java) - An interface which transforms an input Match into an output Object.

The above interfaces, although seemingly abstract, provide a high level of customisability. They are combined in the following class:
  - [**CrawlerConfig.java**](riotapicrawler/src/lib/CrawlerConfig.java) - A *configuration class for the crawler*.  Stores a **MatchFilter** and **SummonerFilter** used by the crawler to  *predicate the crawled Matches and Summoners*,  and an **integer maxMatches property** which determines the *number of recent matches to obtain and send to the output handler for each crawled player*.

Included in the presets folder of this repo, one may find presets for all of these interfaces and classes. They are also contained within the builds.

The **provided presets** are mostly basic and show a *simple* and *more complex* example, however there is included **two OutputHandlers**:
  - [**FileOutputHandler.java**](riotapicrawler/presets/outputhandlers/FileOutputHandler.java) - An OutputHandler that writes a JSON file with name of the game ID for each Match object at a specified directory, with contents the output of the Match from its MatchFormatter. 
  
Utilises [Google Gson](https://github.com/google/gson).

  - [***PostFirebaseOutputHandler.java***](riotapicrawler/extras/postfirebaseoutputhandler/PostFirebaseOutputHandler.java) - An **extension output handler**, located within the *extras/postfirebaseoutputhandler* directory of this repo, which HTTP POSTs the formatted output from a MatchFormatter to a [Google Firebase](https://firebase.google.com/).
  
Utilises [Google Gson](https://github.com/google/gson) and [firebase4j](https://github.com/bane73/firebase4j).

## Examples

One may find examplar uses of the Crawler within the *examples* directory of this repo:

 - [**Example.java**](examples/Example.java) - an examplar use of the Crawler, which:
 
   - Only crawls Matches that are *at least 20 minutes long*,
   - Only crawls Summoners that are *at least level 30*,
   - Only processes *5 recent Matches* for each crawled Summoner,
   - Formats each Match into a *set of statistics for both teams*,
      and stores the results in *JSON files within a given directory*.

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
 
(Current version: **1.2**)

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
