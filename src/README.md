server 初始化 开启OP_ACCEPT监听 bind 通道端口
client 初始化 开启OP_CONNECT监听 连接通道端口
client---主动连接并向通道写入msg,注册读事件---->server
sever----接受连接并向通道写入msg,注册读事件---->client
