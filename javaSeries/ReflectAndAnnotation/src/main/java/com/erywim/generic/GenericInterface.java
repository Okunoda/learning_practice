package com.erywim.generic;

/**
 * 泛型接口定义语法：
 *  interface 接口名称 <泛型标识, 泛型标识....>{
 *      泛型标识  方法名();
 *  }
 * @param <T>
 */
public interface GenericInterface<T>{
    T getValue(T value);
}
