import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel listChannel;
    private static final int PORT = 6666;

    public GroupChatServer(){
        try {
            // 构建选择器
            selector = Selector.open();
            // 构建SeverSocketChannel
            listChannel = ServerSocketChannel.open();
            // 绑定端口
            listChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            listChannel.configureBlocking(false);
            // 将该listChannel注册到selector中
            listChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        // 循环处理
        while(true){
            try {
                int count = selector.select(2000);
                if(count > 0){
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        // 监听到accept
                        if(key.isAcceptable()){
                            SocketChannel accept = listChannel.accept();
                            accept.register(selector, SelectionKey.OP_READ);
                            // 给出提示
                            System.out.println(accept.getRemoteAddress() + "上线了");
                        }
                        // 通道发生read事件，通道是可读的状态
                        if(key.isReadable()){
                            readData(key);
                        }
                        // 当前的key删除,防止重复处理
                        iterator.remove();
                    }

                }else{
                    System.out.println("服务端等待中....");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // 读取客户端消息
    private void readData(SelectionKey key){
        // 定义一个socketChannel
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            // 根据count的值进行处理
            if(count > 0){
                // 把缓冲区的数据转成字符串
                String msg = new String(buffer.array());
                System.out.println("从客户端接收到消息： " + msg);
                sendInfoToOtherClient(msg, channel);
            }
        } catch (Exception e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                // 取消注册
                key.cancel();
                // 关闭通道
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    // 转发消息给其他的客户
    private void sendInfoToOtherClient(String msg, SocketChannel self){
        System.out.println("服务器转发消息中");
        for (SelectionKey key : selector.keys()){
            // 通过key取出对应的socketChannel
            Channel targetChannel = key.channel();
            // 排除自己
            if(targetChannel instanceof SocketChannel && targetChannel != self){
                SocketChannel dest = (SocketChannel) targetChannel;
                // 将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                try {
                    dest.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
