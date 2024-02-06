package org.hacsick.chat.mq;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingMessageQueue implements MessageQueue{

    private final BlockingQueue<Runnable> messageQueue;

    public BlockingMessageQueue() {
        this.messageQueue = new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors());
    }

    public BlockingMessageQueue(int queueSize) {
        this.messageQueue = new ArrayBlockingQueue<>(queueSize);
    }

    public void offer(final Runnable task) {
        this.messageQueue.offer(task);
    }

    public Runnable poll() {
        return Optional.ofNullable(this.messageQueue.poll())
                .orElse(() -> {});
    }

    public BlockingQueue<Runnable> getMessageQueue() {
        return messageQueue;
    }
}
