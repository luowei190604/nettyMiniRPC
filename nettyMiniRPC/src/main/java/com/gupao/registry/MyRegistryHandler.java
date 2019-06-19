package com.gupao.registry;

import java.lang.reflect.Method;

import com.gupao.protocol.InvokeProtocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyRegistryHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof InvokeProtocol){
			InvokeProtocol reciveMsg=(InvokeProtocol)msg;
			String className = reciveMsg.getClassName();
			String methodName = reciveMsg.getMethodName();
			Class<?>[] params = reciveMsg.getParams();
			Object[] values = reciveMsg.getValues();
			
			if("com.gupao.api.ISayHello".equals(className)){
//				Class<?> forName = Class.forName(className);
//				Object instance = forName.newInstance();
				Class<?> forName = Class.forName("com.gupao.provider.ISayHelloImpl");
				Object instance = forName.newInstance();
				Method implMethod = forName.getDeclaredMethod
						(methodName, params);
				Object resutl = implMethod.invoke(instance, values);
				ctx.write(resutl);
				ctx.flush();
				ctx.close();
			}
			
		}else{
			System.out.println("出错了");
		}
	
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("连接异常");
	}

	
}
