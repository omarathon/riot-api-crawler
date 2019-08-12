[![Latest Release](https://img.shields.io/github/release/omarathon/riot-api-crawler.svg)](https://github.com/omarathon/riot-api-crawler/releases/latest) [![Jitpack Release](https://jitpack.io/v/omarathon/riotapicrawler.svg)](https://jitpack.io/#omarathon/riotapicrawler)

# Riot API Crawler

A highly customisable and extensible League of Legends match crawler, utilising [Orianna](https://github.com/meraki-analytics/orianna) as a Java framework for the Riot Games League of Legends API, and [Guava](https://github.com/google/guava) as a caching provider.

This tool may be used for gathering large datasets of ``Matches`` in an intelligent and efficient manner.

Please read the [**Wiki**](../../wiki/Home) if you require further information.

## Functionality

### Orianna

This tool uses [Orianna] for Riot API calls. It uses the following [GhostObjects](https://github.com/meraki-analytics/orianna/blob/master/orianna/src/main/java/com/merakianalytics/orianna/types/core/GhostObject.java): [Summoner](https://github.com/meraki-analytics/orianna/blob/master/orianna/src/main/java/com/merakianalytics/orianna/types/core/summoner/Summoner.java), [Match](https://github.com/meraki-analytics/orianna/blob/master/orianna/src/main/java/com/merakianalytics/orianna/types/core/match/Match.java), [MatchHistory](https://github.com/meraki-analytics/orianna/blob/master/orianna/src/main/java/com/merakianalytics/orianna/types/core/match/MatchHistory.java).

It uses many other types within Orianna, and one is recommended to view either the [Orianna documentation](https://github.com/meraki-analytics/orianna/tree/master/docs), or its [source code](https://github.com/meraki-analytics/orianna/tree/master/orianna/src/main/java/com/merakianalytics/orianna).

### What it does

The crawler begins at a ``Summoner``, obtains their ``MatchHistory``, sends their ``MatchHistory`` to its ``OutputHandler`` which will process each ``Match``, then seeks the next ``Summoner`` to crawl from the ``Participants`` in each ``Match`` of the ``MatchHistory``.

Intelligent behaviour may be implemented through the use of ``Filters``, which make the decisions regarding the ``Matches`` and the ``Summoners`` to crawl, as well as the ``Matches`` that are handled by the ``OutputHandler``.

If no crawlable next ``Summoner`` is found from a current ``Summoner``, the crawler shall backtrack, moving to a previously visited Summoner, and resume the search from them.

With the backtracking behaviour, the crawler traverses through ``Matches`` and ``Summoners`` in a depth-first fashion, where as soon as a crawlable ``Summoner`` is found, we move to them. However, when backtracking, we pick random previous points (``Summoners``).

Below is a flowchart detailing the operation of the ``Crawler`` when it's called on an input Summoner:

![Crawler Flowchart](https://i.imgur.com/BvKHI9B.png)

## Quick Start

**Before running the Crawler, one must set Orianna's Riot API key**. This can be done like so:
```java
Orianna.setRiotAPIKey("YOUR API KEY HERE");
```

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

One may find an examplar use of the Crawler within [**Example.java**](riotapicrawler/Example.java), wherein it:
   - Only crawls Matches that are *at least 20 minutes long*,
   - Only crawls Summoners that are *at least level 30*,
   - Only processes *5 recent Matches* for each crawled Summoner,
   - Formats each Match into a *set of statistics for both teams*, and prints the results to System.out.
  
## Usage

### Automatic

One may add this tool to their Maven project by visiting the [Jitpack](https://jitpack.io/#omarathon/riotapicrawler/). One must add the Jitpack repository, and then the riotapicrawler depdendency from the Jitpack repo:
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.omarathon</groupId>
  <artifactId>riotapicrawler</artifactId>
  <version>v2.0.1</version>
</dependency>
```

Please ensure you're using the **latest release**. The tag of this release may be found via the Jitpack badge at the top of this README.

## Dependencies
This project was developed via Maven, and used the following dependencies as libraries:
 - [**Orianna**](https://github.com/meraki-analytics/orianna) - **Essential**.
```xml
<dependency>
  <groupId>com.merakianalytics.orianna</groupId>
  <artifactId>orianna</artifactId>
  <version>4.0.0-rc4</version>
</dependency>
```

- [**Guava**](https://github.com/google/guava) - **Essential**.
```xml
<dependency>
  <groupId>com.google.guava</groupId>
  <artifactId>guava</artifactId>
  <version>28.0-jre</version>
</dependency>
```

 - [**Google Gson**](https://github.com/google/gson) - Utilised within [StringMatchFormatter](riotapicrawler/presets/matchformatters/StringMatchFormatter.java). If you use this class, or any object uses this class, you must install this dependency.
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
 - [**Google Firebase**](https://firebase.google.com/) - Utilised within [PostFirebaseOutputHandler](riotapicrawler/presets/outputhandlers/PostFirebaseOutputHandler.java). If you use this class, or any object uses this class, you must install this dependency.
```xml
<dependency>
  <groupId>com.google.firebase</groupId>
  <artifactId>firebase-admin</artifactId>
  <version>6.9.0</version>
</dependency>
```
 - [**firebase4j**](https://github.com/bane73/firebase4j) - Utilised within [PostFirebaseOutputHandler](riotapicrawler/presets/outputhandlers/PostFirebaseOutputHandler.java). If you use this class, or any object uses this class, you must install this dependency.
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.bane73</groupId>
  <artifactId>firebase4j</artifactId>
  <version>-SNAPSHOT</version>
</dependency>
```

## Changelog

Below lists a changelog, in dd/mm/yyyy format:

  - **11/08/2019**: Complete rewrite. **new functionality**: caching in filter results, visited summoners and estimators, and backtracking to previously visited summoners when reach dead ends. (**v2.0**)
  - **27/07/2019**: Added Elo MatchFilter and SummonerFilter, filter parameter adjustments. (**v1.2**)
  - **16/07/2019**: Added a PrintOutputHandler. (**v1.1**)
  - **15/07/2019**: First version uploaded. (**v1.0**)
  
## Remark
  
If you happen to use this tool in your project, citing this repository would be very much appreciated!
