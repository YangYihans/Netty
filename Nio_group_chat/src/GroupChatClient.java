import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class GroupChatClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupChatClient() throws IOException {

    }

    // 向服务器发送消息
    public void sendInfo(String info){
        info = username + "say " + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取从服务端回复的消息
    public void readInfo(){
        try {
            int readChannels = selector.select();
            if(readChannels > 0){
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        // 得到相关的通道
                        SocketChannel sc = (SocketChannel) key.channel();
                        // 得到buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // 把读到的缓冲区的数据转换成字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }else{
                        System.out.println("没有可用的通道....");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
