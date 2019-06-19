package com.gupao.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.gupao.protocol.InvokeProtocol;
import com.gupao.registry.MyRegistryHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ProxyFactory {

	@SuppressWarnings("unchecked")
	public static<T> T getInstance(Class<T> cl){
		
		JDKProxy jdkProxy = new JDKProxy(cl);
		return (T) Proxy.newProxyInstance
				(cl.getClassLoader(), new Class[]{cl},jdkProxy);
		
	}
	public static class JDKProxy implements InvocationHandler{
		
		private Class<?> cl;
		
		public JDKProxy(Class<?> cl){
			this.cl=cl;
		}
		
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(proxy.getClass().equals(method.getDeclaringClass())){
				System.out.println("baichi 你用实现调用了");
			}else{
				return doRpcInvoke(proxy,method,args);
			}
			
			return null;
		}


		private Object doRpcInvoke(Object proxy,Method method, Object[] args) throws Exception {
			InvokeProtocol pt=new InvokeProtocol();
			pt.setClassName(this.cl.getName());
			pt.setMethodName(method.getName());
			pt.setParams(method.getParameterTypes());
			pt.setValues(args);
			
			final ConsumerHandler consumerHandler = new ConsumerHandler();
			EventLoopGroup workGroup=new NioEventLoopGroup();
			try{
				Bootstrap client = new Bootstrap();
				client.group(workGroup).
					channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {

							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								ChannelPipeline pipeline = ch.pipeline();
								pipeline.addLast(new LengthFieldBasedFrameDecoder
										(Integer.MAX_VALUE, 0, 4,0,4));
								pipeline.addLast(new LengthFieldPrepender(4));
								pipeline.addLast("encoder",new ObjectEncoder());
								pipeline.addLast("decoder",new ObjectDecoder
										(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
								pipeline.addLast(consumerHandler);
							}
						});
				ChannelFuture future = client.connect("localhost", 8080);
				future.channel().writeAndFlush(pt).sync();
				future.channel().closeFuture().sync();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				workGroup.shutdownGracefully();
			}
			
			return consumerHandler.getResponse();
			
		}
		
	}

}
