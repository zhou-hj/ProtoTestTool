package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet packet = (Packet)msg;
        
        ProtoManager.getInstance().handleProto(packet);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (ProtoManager.getInstance().getChannel() != null) {
			ProtoManager.getInstance().getChannel().disconnect();
		}
		ProtoManager.getInstance().setChannel(ctx.channel());
		System.out.println("登录  " + ctx.channel().remoteAddress().toString().replace("/", "") + "  成功");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("\n断开  " + ctx.channel().remoteAddress().toString().replace("/", "") + "  连接");
	}

}
