package com.gupao.consumer;

import com.gupao.api.ISayHello;

public class ConsumerInvoke {

	public static void main(String[] args) {
		ISayHello hello = ProxyFactory.getInstance(ISayHello.class);
		String sayHello = hello.sayHello("zhangsan");
		System.out.println(sayHello);
	}
}
