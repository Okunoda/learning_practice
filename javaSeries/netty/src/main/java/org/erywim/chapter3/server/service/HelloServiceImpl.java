package org.erywim.chapter3.server.service;

/**
 * @author Erywim 2024/7/30
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        int i = 1/0;
        return "你好" + name;
    }
}
