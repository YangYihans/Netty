import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public static void main(String[] args) throws Exception {
        // 1. 创建ServerSocketChannel 在服务端监听新的客户端的socket连接
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2. 得到一个Selector对象 selector是windowsSelectorImpl的一个实例
        Selector selector = Selector.open();

        // 3. 绑定一个端口6666, 在服务器端进行监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        // 4. 设置为非zuse
        serverSocketChannel.configureBlocking(false);

        // 5. 把serverSocketChannel注册到 selector,  关心的事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6. 循环等待客户端连接
        while(true){
            // 等待1s，如果没有事件发生，返回
            if(selector.select(1000) == 0){
                System.out.println("服务器等待了1s，无连接");
                continue;
            }
            /**
             * 如果返回>0. 就获取到相关的selectionKey集合，
             * 返回>0。说明已经获取到关注的事件
             */
            // 7. 获取关注的事件的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 8. 遍历Set
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while(keyIterator.hasNext()){

                // 9. 获取到对应的SelectionKey
                SelectionKey key = keyIterator.next();

                // 10. 根据key对应的通道发生的事件做相应的处理
                if(key.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功....生成了一个socketChannel" + socketChannel.hashCode());

                    // 将SocketChannel注册为非阻塞
                    socketChannel.configureBlocking(false);

                    // 将socketChannel注册到selector中，关注的事件为OP_READ， 同时给socketChannel关联一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if(key.isReadable()){
                    // 11. 通过key反向获取到对应的channel
                    SocketChannel channel = (SocketChannel) key.channel();

                    // 12. 获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();

                    channel.read(buffer);

                    System.out.println("从客户端读取到buffer： " + new String(buffer.array()));
                }

                // 13. 手动从集合中移动档期那的selectionKey，防止重复操作
                keyIterator.remove();
            }

        }
    }
}
