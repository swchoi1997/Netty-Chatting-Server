package org.hacsick.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.hacsick.server.handle.ChatServerHandlerInitializer;

public class ChatServer {

    private final int serverPort;

    public ChatServer(final int serverPort) {
        this.serverPort = serverPort;
    }

    public void launch() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // Netty Available Core Count

        try {
            final ServerBootstrap server = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerHandlerInitializer());

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
