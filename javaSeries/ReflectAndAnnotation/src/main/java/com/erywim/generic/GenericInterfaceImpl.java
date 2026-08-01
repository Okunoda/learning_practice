package com.erywim.generic;

/**
 * 实现类也是泛型类，实现类和接口的泛型类型要一致
 */
public class GenericInterfaceImpl<T,K,E> implements GenericInterface<T>{
    @Override
    public T getValue(T value) {
        return null;
    }

    public E getValue(E value , K value2){
        return value;
    }


    //实现类不是泛型类，接口要明确数据类型
    public static class GenericInterfaceImpl2 implements GenericInterface<String>{
        @Override
        public String getValue(String value) {
            return null;
        }
    }
}
