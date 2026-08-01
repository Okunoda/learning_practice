package com.erywim.generic;

import java.util.List;
import java.util.Random;

public class GenericWildcard {

    /*
    类型通配符一般是用?代替具体的类型参数，例如：
    List<?>
    List<? extends Number>
    List<? super Number>
     */
    public static void show(List<? extends  Number> list, List<? super Number> list2){ //可以看到，这个成员方法直接用通配符定义了泛型参数。类上并没有泛型标识，这个方法也并非泛型方法
        //list1 的元素只能是Number或者Number的子类
        //list2 的元素只能是Number或者Number的父类
        Number number = list.get(new Random().nextInt(list.size()));
        System.out.println("number = " + number+"  number.getClass().getName() = "+number.getClass().getName());

        Object object = list2.get(new Random().nextInt(list2.size()));
        System.out.println("object = " + object+"  object.getClass().getName() = "+object.getClass().getName());
    }

    public static void main(String[] args) {
        show(List.of(1,2,3), List.of(1.1,2.2,3.3));
    }

    public static class GenericWildcard2<E>{

        public E show(List<? extends E> list){
            return list.get(new Random().nextInt(list.size()));
        }

        public static void main(String[] args) {
            GenericWildcard2<Number> gw = new GenericWildcard2<>();
            System.out.println(gw.show(List.of(1,2,3)));
        }
    }
}
