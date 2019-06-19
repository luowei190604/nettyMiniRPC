package com.gupao.consumer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ConsumerHandler extends ChannelInboundHandlerAdapter {
	private Object result;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		result=msg;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	}

	public Object getResponse() {
		return result;
	}
	

}
