package com.erywim.generic;

/**
 * 泛型定义语法：
 *   class 类名称 <泛型标识, 泛型标识, .....> {
 *       泛型标识 变量名;
 *   }
 *
 *   常用的泛型标识：T E K V
 *
 *
 * 泛型使用的时候不允许传入基本数据类型，因为泛型在底层的时候首先会被替换成Object，然后再将标识符在合适的时候替换成传入的对象类型。而基本数据类型不继承自Object，所以不能用
 * @param <T>
 */
public class Generic<T>{
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
