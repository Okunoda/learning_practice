package com.erywim.generic;

import java.util.ArrayList;
import java.util.Random;

public class GenericMethod {

    /**
     * 泛型方法:
     *  泛型方法定义语法：
     *      方法修饰符  <泛型标识, 泛型标识....>  返回值类型  方法名称(参数列表){}
     *  只有修饰符和返回值之间声明了泛型标识，该方法才能算作泛型方法。形参中才能使用该泛型标识
     *      如果没有声明泛型标识，而是仅仅在参数列表中用到了某个泛型标识，那么该方法就不是泛型方法
     *
     *  tips: 能使用泛型方法就尽量不用泛型类
     *
     * @param t
     * @param <T>
     */
    public <T> void show(T t){
        System.out.println(t);
    }

    //泛型方法：返回值类型声明了泛型标识
    public <E> E show2(ArrayList<E> param){
        return param.get(new Random().nextInt(param.size()));
    }


    public static class GenericMethod2<T>{

        //泛型方法的调用，类型是通过调用方法的时候来指定的。也就是说方法里面即便是和泛型类的泛型标识用同一个，实际使用的时候也不会受到类的影响
        public <T> T show(ArrayList<T> list){
            return list.get(new Random().nextInt(list.size()));
        }

        //但是泛型类的成员方法必须要和泛型类使用相同的泛型标识，并且实际使用的时候受泛型类的声明限制
        //并且普通的成员方法，用到了类的泛型标识，那么这个方法将 不能  声明为static 。泛型方法不受此限制
        public T show2(ArrayList<T> t){
            return t.get(new Random().nextInt(t.size()));
        }
        public static <T,E,K> void show3(T t, E e, K k){  //可以声明为静态方法
            System.out.println("t.getClass().getName() = " + t.getClass().getName());
            System.out.println("e.getClass().getName() = " + e.getClass().getName());
            System.out.println("k.getClass().getName() = " + k.getClass().getName());
        }

        //泛型方法同样支持可变参数
        public static <E> void show4(E... e){
            for (int i = 0; i < e.length; i++) {
                System.out.println("e[i] = " + e[i]);
            }
        }

        public static void main(String[] args) {
            GenericMethod2<String> gm = new GenericMethod2<>();
            ArrayList<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(2);
            Integer show = gm.show(list);
            System.out.println("参数值：\t"+ show + "\t\t参数类型：  " + show.getClass().getName());
            System.out.println("=====================================");

            ArrayList<String> list2 = new ArrayList<>();
            list2.add("hello");
            list2.add("world");
            String show2 = gm.show(list2);
            System.out.println("参数值：\t"+ show2 + "\t\t参数类型：  " + show2.getClass().getName());

            System.out.println("=====================================");

            ArrayList<Integer> list3 = new ArrayList<>();
            list3.add(1);
            list3.add(2);
//            Integer show3 = gm.show2(list3); // 这里会编译报错，因为泛型类声明为String类型，但是这个成员方法传入了Integer类型的泛型
//            System.out.println("参数值：\t"+ show3 + "\t\t参数类型：  " + show3.getClass().getName());
            System.out.println("=====================================");
            GenericMethod2.show3(1,"hello", true);
            System.out.println("=====================================");
            GenericMethod2.show4(1,2,3);
        }
    }

}
