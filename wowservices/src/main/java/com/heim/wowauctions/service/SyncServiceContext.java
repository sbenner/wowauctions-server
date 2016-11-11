package com.heim.wowauctions.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 11/11/16
 * Time: 9:11 PM
 */
@Component
@Scope("singleton")
public class SyncServiceContext {

    private  BlockingQueue<Long> queue;

    public SyncServiceContext(){
           setQueue(new LinkedBlockingQueue<Long>());
    }

    public BlockingQueue<Long> getQueue() {
        return this.queue;
    }

    public void setQueue(BlockingQueue<Long> queue) {
        this.queue = queue;
    }
}
