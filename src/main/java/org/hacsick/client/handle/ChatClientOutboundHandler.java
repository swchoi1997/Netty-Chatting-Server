package org.hacsick.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.util.Arrays;
import java.util.List;
import org.hacsick.client.ClientInfo;
import org.hacsick.server.message.Command;
import org.hacsick.server.message.CommonMessage;
import org.hacsick.server.message.Payload;

public class ChatClientOutboundHandler extends ChannelOutboundHandlerAdapter {

    private final ClientInfo clientInfo;

    public ChatClientOutboundHandler(final ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise)
            throws Exception {
        final String userMessage = (String) msg;
        final List<String> userCommandAndMessage = Arrays.stream(userMessage.split(" ", 2)).toList();

        //Check Empty Msg
        if (userCommandAndMessage.isEmpty()) return;

        //Check Msg Type
        if (!this.isNumeric(userCommandAndMessage.getFirst())) {
            try {
                if ("help".equals(userCommandAndMessage.getFirst())) {
                    //TODO Print HELP MSG
                    return;
                }
                throw new IllegalArgumentException("Invalid Command Please Check");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        final Command command = Command.find(Integer.valueOf(userCommandAndMessage.getFirst()));
        if (command == Command.UNKNOWN) {
            try {
                throw new IllegalArgumentException("Invalid Command Please Check");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        String name = this.clientInfo.getName();
        String clientMessage = "";
        if (userCommandAndMessage.size() == 2) {
            clientMessage = userCommandAndMessage.getLast();
        }

        Payload<CommonMessage> payload = Payload.of(
                        command,
                        CommonMessage.toAdmin(name, clientMessage));

        super.write(ctx, payload, promise);
    }

    private boolean isNumeric(final String value) {
        for(char v: value.toCharArray()){
            if (!Character.isDigit(v)) {
                return false;
            }
        }
        return true;
    }
}
