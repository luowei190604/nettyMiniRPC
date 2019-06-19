package com.gupao.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyRegistry {
	
	private int port=8080;
	
	private void start() throws Exception{
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workGroup=new NioEventLoopGroup();
		ServerBootstrap server = new ServerBootstrap();
		server.group(bossGroup,workGroup)
			.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new LengthFieldBasedFrameDecoder
								(Integer.MAX_VALUE, 0, 4,0,4));
						pipeline.addLast(new LengthFieldPrepender(4));
						pipeline.addLast("encoder",new ObjectEncoder());
						pipeline.addLast("decoder",new ObjectDecoder
								(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
						pipeline.addLast(new MyRegistryHandler());
					}
				}).
				option(ChannelOption.SO_BACKLOG, 10)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		//这里相当于用个循环	
		ChannelFuture future = server.bind(8080).sync();
		System.out.println("start netty listen 8080");
		future.channel().closeFuture().sync();
		
	}
	public static void main(String[] args) throws Exception {
		new NettyRegistry().start();
	}
	
}
