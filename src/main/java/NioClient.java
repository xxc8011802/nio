import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class NioClient
{
    private Selector selector;

    public void initClient(String ip,int port) throws IOException
    {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = Selector.open();
        // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调用channel.finishConnect();才能完成连接
        boolean flag = channel.connect(new InetSocketAddress(ip,port));
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void listen() throws IOException{
        while(true){
            selector.select();
            Iterator ite = this.selector.selectedKeys().iterator();
            while(ite.hasNext()){
                SelectionKey key = (SelectionKey) ite.next();
                ite.remove();
                //监听连接服务端事件是否就绪，初始化时候已经注册了OP_CONNECT
                if(key.isConnectable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    //主动去连接服务端，只有服务端开启OP_ACCEPT
                    if(channel.isConnectionPending()){
                        channel.finishConnect();
                    }
                    channel.configureBlocking(false);
                    channel.register(this.selector, SelectionKey.OP_READ);
                    //-------------------------------------------------------
                    try{
                        Thread.sleep(5000);
                    }catch (InterruptedException e){

                    }
                    String time = new Date().toString();
                    channel.write(ByteBuffer.wrap(new String("send Message to Server || "+time).getBytes()));
                }else if(key.isReadable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(100);
                    channel.read(buffer);
                    byte[] data = buffer.array();
                    String msg = new String(data).trim();
                    System.out.println("Client收到信息："+msg);
                    ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
                    //channel.write(outBuffer);// 将消息回送给服务端
                }else if(key.isWritable()){
                }
            }
        }
    }

    public static void main(String[] args)  throws IOException
    {
        NioClient client = new NioClient();
        client.initClient("localhost",8000);
        client.listen();
    }
}
