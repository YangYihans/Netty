import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        /**
         * 1. 客户端创建一个事件循环组
         */
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try{
            /**
             * 2。 创建客户端启动对象
             * 客户端使用的不是ServerBootstrap 而是Bootstrap
             */
            Bootstrap bootstrap = new Bootstrap();

            /**
             * 3. 设置相关参数
             */
            bootstrap.group(eventExecutors)  // 设置线程组
                    .channel(NioSocketChannel.class)  // 设置客户端通道的实现类（反射）
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            System.out.println("-------client ready----------");

            /**
             * 4. 启动客户端连接服务器端
             */
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();

            /**
             * 关闭通道进行监听
             */
            channelFuture.channel().closeFuture().sync();
        }finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
