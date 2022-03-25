# Mirai QQRobot HTTPAPI 插件 JAVA 封装库

目前已实现的功能：
+ 接收QQ消息
+ 发送QQ/群消息
+ 接收好友申请并同意/拒绝/拉黑
+ 好友列表
+ 接收好友后自动发送一句问候语

## 例子

```java
String qq = "1234567"; //机器人的QQ号
String verifyKey = "myVerifyKey"; //mirai http 插件 verifyKey
QQBot qqbot = new QQBot(qq, verifyKey);
        qqbot.onMessageReceived(new QQEventListener() {
            //接收QQ消息
            @Override
            public void onReceivedFriendMessage(FriendMessage friendMessage) {
                System.out.println("收到消息：" + JSON.toJSONString(friendMessage));
                if(friendMessage.getMessage().equals("你好")){
                    List<QQMessage> qqMessages = new ArrayList<>();
                    qqMessages.add(QQMessage.textMessage("你好鸭！"));
                    qqbot.sendFriendMessage(friendMessage.getSender().getId(), qqMessages);
                }
            }

            //接收好友请求
            @Override
            public NewFriendRequestHandleResult onReceivedNewFriendRequest(NewFriendRequest request) {
                System.out.println("收到好友请求：" + JSON.toJSONString(request));
                NewFriendRequestHandleResult newFriendRequestHandleResult = new NewFriendRequestHandleResult();
                newFriendRequestHandleResult.setAccept(true);
                newFriendRequestHandleResult.setMessage("哈罗好");
                return newFriendRequestHandleResult;
            }

            //接收好友请求后的自动回复
            @Override
            public List<QQMessage> postReceivedNewFriendRequest(NewFriendRequest request) {
                return Arrays.asList(QQMessage.textMessage("你好鸭！"));
            }
        });
        new Scanner(System.in).next();
        qqbot.stop();
```
