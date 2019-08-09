package com.omarathon.riotapicrawler.src.lib.filter;

public interface Filter<F> {
    boolean filter(F o);
}
