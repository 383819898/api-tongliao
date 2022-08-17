package com.shiku.commons.thread;

@FunctionalInterface
public interface Callback {

    void execute(Object obj);

}