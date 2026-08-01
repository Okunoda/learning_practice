package com.erywim.generic;

/**
 * 子类也是泛型类，继承父类时，子类和父类的泛型需要保持一致。
 * 同时，子类可以拓展泛型。也就是至少要包含父类的泛型类型
 */
public class SubGeneric<E,T,V> extends Generic<E>{


    /**
     * 子类不是泛型类，继承父类时，父类要明确泛型的数据结构
     */
    public static class SubGeneric2 extends Generic<String>{

    }
}
