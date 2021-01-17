package com.omarathon.riotapicrawler.src.lib;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.summoner.Summoner;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SummonerHistory {
    private Cache<Summoner, MatchHistory> cache;

    public SummonerHistory() {
        this(1, TimeUnit.DAYS);
    }

    public SummonerHistory(long rememberDuration, TimeUnit rememberDurationUnit) {
        this(CacheBuilder.newBuilder()
                .expireAfterWrite(rememberDuration, rememberDurationUnit)
                .maximumSize(100000));
    }

    public SummonerHistory(CacheBuilder<Object, Object> builder) {
        cache = builder.build();
    }

    public void addVisitedSummoner(Summoner summoner, MatchHistory matchHistory) {
        cache.put(summoner, matchHistory);
    }

    public void removeVisitedSummoner(Summoner summoner) {
        cache.invalidate(summoner);
    }

    public boolean wasVisited(Summoner summoner) {
        return getMatchHistory(summoner) != null;
    }

    public MatchHistory getMatchHistory(Summoner summoner) {
        return cache.getIfPresent(summoner);
    }

    public MatchHistory getRandomMatchHistory() {
        Object[] values = cache.asMap().values().toArray();
        if (values.length == 0) return null;
        return (MatchHistory) values[new Random().nextInt(values.length)];
    }

    public Cache<Summoner, MatchHistory> getCache() {
        return cache;
    }

    public void setCache(Cache<Summoner, MatchHistory> cache) {
        this.cache = cache;
    }
}
