package org.hacsick.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.hacsick.server.handle.ChatServerHandlerInitializer;
import org.hacsick.server.room.ChatRoomManager;

public class ChatServer {

    private final int serverPort;

    public ChatServer(final int serverPort) {
        this.serverPort = serverPort;
    }

    public void launch() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // Netty Available Core Count

        try {
            ChatServerHandlerInitializer childHandler = new ChatServerHandlerInitializer();
            final ServerBootstrap server = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(childHandler);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                ChatRoomManager chatRoomManager = childHandler.getChatRoomManager();
                chatRoomManager.getChatRoomContainer().entrySet().forEach(System.out::println);

            }, 2, 5, TimeUnit.SECONDS);

            ChannelFuture future = server.bind(this.serverPort).sync();
            future.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }


    }
}
