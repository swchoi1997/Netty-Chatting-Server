package utils;

import org.hacsick.server.message.Command;
import org.hacsick.server.message.CommonMessage;
import org.hacsick.server.message.Payload;
import org.junit.jupiter.api.Test;

class GsonUtilsTest {


    @Test
    public void convertMsgToClass() {
        String jsonExample = """
                {
                  "uniqueValue": "uniqueValue",
                  "command": "CHAT_ROOM_ERROR",
                  "body": {
                    "from": "fromUser",
                    "to": "toUser",
                    "message": "Hello!"
                  }
                }
                """;

//        Payload payload = GsonUtils.convertJsonToClass(jsonExample, Payload.class);
//
//        System.out.println(payload);
    }

    @Test
    public void convertClassToMsg() {
        Payload payload = Payload.of("Test", Command.CHAT_ROOM_STATUS_CHANGE,
                CommonMessage.of("sangwon", "yuha", "hello"));

        String s = GsonUtils.convertClassToJson(payload);
        System.out.println(s);


    }


}