package com.erywim.generic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GenericArray {
    //泛型 + 数组
    //可以   声明   带泛型数组的引用，但是不能  直接创建   带泛型的数组对象
    //尽量不使用泛型数组，而是优先用集合。因为数组和泛型在设计上就存在冲突

    public static void main(String[] args) {

//        ArrayList<String>[] arr1 = new ArrayList<>[10];//这种方式不允许执行
        ArrayList<String>[] arr2 = new ArrayList[10]; // 如果创建的时候不指定对象的类型，那么允许创建

    }


    public <T> void show(Class<T> clazz,int len){
        T[] arr = (T[]) Array.newInstance(clazz, len); // 也可以通过Array.newInstance()方法创建

    }
}
