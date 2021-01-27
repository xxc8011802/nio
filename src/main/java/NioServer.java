import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class NioServer
{

    private Selector selector;

    public void init(int port) throws IOException
    {
        //获得一个通道管理器
        this.selector=Selector.open();

        //服务端通道
        ServerSocketChannel serverSocketChannel1 = ServerSocketChannel.open();

        //设置服务端通道非阻塞
        serverSocketChannel1.configureBlocking(false);

        ServerSocketChannel bind = serverSocketChannel1.bind(new InetSocketAddress(port));

        /**
         * SelectionKey.OP_CONNECT : 一个channel成功连接到另一个服务器称为”连接就绪“。
         * SelectionKey.OP_ACCEPT  : 一个server socket channel准备号接收新进入的连接称为”接收就绪“。
         * SelectionKey.OP_READ  : 一个有数据可读的通道可以说是”读就绪“。
         * SelectionKey.OP_WRITE : 一个等待写数据的通道可以说是”写就绪“。  一般不注册写事件
         */
        //通道和selector绑定，并为通道注册SelectionKey
        SelectionKey selectionKey1 = serverSocketChannel1.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen()
        throws IOException
    {
        while(true){
            selector.select();
            Iterator iterator = this.selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = (SelectionKey)iterator.next();
                iterator.remove();
                //服务端准备好接受客户端
                if(key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);
                    channel.register(this.selector, SelectionKey.OP_READ);
                    //-------------------------------------------------
                    String time = new Date().toString();
                    channel.write(ByteBuffer.wrap(new String("send Message to client || "+time).getBytes()));
                }else if(key.isReadable()){
                    SocketChannel readChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(100);
                    readChannel.read(buffer);
                    byte[] data = buffer.array();
                    String msg = new String(data).trim();
                    System.out.println("Server收到信息："+msg);
                    ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
                    //channel.write(outBuffer);// 将消息回送给客户端
                }else if(key.isWritable()){

                }
            }
        }
    }

    public static void main(String[] args)throws IOException
    {
        NioServer server = new NioServer();
        server.init(8000);
        server.listen();
    }
}
