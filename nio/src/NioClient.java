import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {
    public static void main(String[] args) throws Exception {
        // 1. 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();

        // 2. 设置非阻塞模式
        socketChannel.configureBlocking(false);

        // 3. 提供服务器端的IP和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        // 4. 连接服务器
        if(!socketChannel.connect(inetSocketAddress)){
            while(socketChannel.finishConnect()){
                System.out.println("因为连接需要事件，客户端不会阻塞， 可以做其他的工作");
            }
        }

        // 5. 连接成功发送数据
        String str = "hello, nick_yang";

        // 6. 包裹一个字节数组到buffer中
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        // 7. 发送数据 将buffer数据写入到channel
        socketChannel.write(buffer);

        System.in.read();
    }
}
