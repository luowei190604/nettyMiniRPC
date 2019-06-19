package com.gupao.provider;

import com.gupao.api.ISayHello;

public class ISayHelloImpl implements ISayHello{

	@Override
	public String sayHello(String msg) {
		return "hello world"+msg;
	}

}
