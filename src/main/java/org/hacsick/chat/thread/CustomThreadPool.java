package org.hacsick.chat.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.hacsick.chat.mq.MessageQueue;

public class CustomThreadPool {

    public static ExecutorService of(final int coreSize, final int maxCoreSize, final MessageQueue messageQueue) {
        return new ThreadPoolExecutor(
                coreSize,
                maxCoreSize,
                60L,
                TimeUnit.SECONDS,
                messageQueue.getMessageQueue(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
