package com.omarathon.riotapicrawler.src.lib.handler;

// handles inputs of type T
public interface Handler<T> {
    void handle(T input);
}
