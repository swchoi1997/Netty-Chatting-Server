package org.hacsick.chat.mq;

import java.util.concurrent.BlockingQueue;

public interface MessageQueue {

    void offer(final Runnable task);

    Runnable poll();

    BlockingQueue<Runnable> getMessageQueue();
}
