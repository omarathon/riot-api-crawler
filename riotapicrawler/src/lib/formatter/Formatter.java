package com.omarathon.riotapicrawler.src.lib.formatter;

// formats Source objects of type S into Destination objects of type D
public interface Formatter<S, D> {
    D format(S source);
}
