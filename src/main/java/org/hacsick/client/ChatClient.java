package org.hacsick.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Scanner;
import org.hacsick.client.handle.ChatClientHandlerInitializer;

public class ChatClient {

    private final String hostIp;
    private final String hostPort;

    public ChatClient(final String hostIp, final String hostPort) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap clientBootStrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientHandlerInitializer());

            ChannelFuture f = clientBootStrap
                    .connect(this.hostIp, Integer.parseInt(this.hostPort))
                    .sync();


            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();

                if (input == null) {
                    continue;
                }

                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                f.channel().writeAndFlush(input + "\r\n");
            }

            scanner.close();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }

    }


    public static void main(String[] args) {
        new ChatClient("127.0.0.1", "18888").start();
    }
}
