package com.erywim.reflect;


import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射获取的四个步骤：
 * 1. 加载类，获取类的字节码：class对象
 * 2. 获取类的构造器：Constructor对象
 * 3. 获取类的成员变量：Field对象
 * 4. 获取类的成员方法：Method对象
 */
public class TestReflect {


    @Test
    //1. 加载类，获取类的字节码：class对象
    public void testGetClass() throws ClassNotFoundException {
        //方式一：类名.class 方式
        Class<Student> c1 = Student.class;
        System.out.println("c1.getName() = " + c1.getName());//全类名
        System.out.println("c1.getSimpleName() = " + c1.getSimpleName());//简名：Student
        //方式二：CLass.forName
        Class<?> c2 = Class.forName("com.erywim.reflect.Student");
        System.out.println("c2.getName() = " + c2.getName());
        System.out.println("c2.getSimpleName() = " + c2.getSimpleName());
        //方式三：对象.getClass()
        Student s = new Student();
        Class<? extends Student> c3 = s.getClass();
        System.out.println("c3.getName() = " + c3.getName());
        System.out.println("c3.getSimpleName() = " + c3.getSimpleName());
    }

    @Test
    //2. 获取类的构造器：Constructor对象
    public void testGetConstructors() throws ClassNotFoundException, NoSuchMethodException {
        Class<?> c = Class.forName("com.erywim.reflect.Student");
        //方式一：getConstructors() ：获取全部构造器（只能获取public修饰的）
        System.out.println("=====================方式一：getConstructors()==============================");
        Constructor<?>[] constructors = c.getConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println("constructor.getName() = " + constructor.getName() + "\t\t" + "constructor.paramCount() = " + constructor.getParameterCount());
        }
        //方式二：getDeclaredConstructors() ：获取全部构造器（只要存在就能拿到）
        System.out.println("=====================方式二：getDeclaredConstructors()==============================");
        Constructor<?>[] declaredConstructors = c.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            System.out.println("declaredConstructor.getName() = " + declaredConstructor.getName() + "\t\t" + "declaredConstructor.paramCount() = " + declaredConstructor.getParameterCount());
        }
        //方式三：getConstructor(CLass<?>... parameterType) ：获取指定参数的构造器（只能拿到public修饰的）
        System.out.println("=====================方式三：getConstructor(CLass<?>... parameterType)==============================");
        Constructor<?> constructor = c.getConstructor();
        System.out.println("constructor.getName() = " + constructor.getName() + "\t\t" + "constructor.paramCount() = " + constructor.getParameterCount());
        //方式四：getDeclaredConstructor(CLass<?>... parameterType) ：获取指定参数的构造器（只要存在就能拿到）
        System.out.println("=====================方式四：getDeclaredConstructor(CLass<?>... parameterType)==============================");
        Constructor<?> declaredConstructor = c.getDeclaredConstructor(int.class, String.class);
        System.out.println("declaredConstructor.getName() = " + declaredConstructor.getName() + "\t\t" + "declaredConstructor.paramCount() = " + declaredConstructor.getParameterCount());
    }

    @Test
    public void testInstanceConstructor() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //反射获取的构造器的使用样例
        Class<Student> clazz = Student.class;
        Constructor<Student> noParamConstructor = clazz.getDeclaredConstructor();

        //用法一：创建对象。通过newInstance(Object... initargs) 实现
        Student s1 = noParamConstructor.newInstance();
        s1.setAge(18);
        s1.setName("小王");
        System.out.println("s1 = " + s1);

        //用法二：创建对象。通过setAccessible(boolean flag) 改变访问权限，再通过newInstance(Object... initargs) 实现
        Constructor<Student> oneParamConstructor = clazz.getDeclaredConstructor(int.class);
        oneParamConstructor.setAccessible(true); //反射破坏封装性的体现
        Student s2 = oneParamConstructor.newInstance(22);
        System.out.println("s2 = " + s2);
    }

    @Test
    //3. 获取类的成员变量：Field对象
    public void testGetFields() throws ClassNotFoundException, NoSuchFieldException {
        Class<Student> clazz = Student.class;
        //方式一：getFields() ：获取全部成员变量（只能获取public修饰的）
        System.out.println("=====================方式一：getFields()==============================");
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            System.out.println("field.getName() = " + field.getName() + "\t\t" + "field.getType() = " + field.getType());
        }
        //方式二：getDeclaredFields() ：获取全部成员变量（只要存在就能拿到）
        System.out.println("=====================方式二：getDeclaredFields()==============================");
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println("declaredField.getName() = " + declaredField.getName() + "\t\t" + "declaredField.getType() = " + declaredField.getType());
        }
        //方式三：getField(String name) ：获取指定名称的成员变量（只能拿到public修饰的）
        System.out.println("=====================方式三：getField(String name)==============================");
        Field field = clazz.getField("address");
        System.out.println("field.getName() = " + field.getName() + "\t\t" + "field.getType() = " + field.getType());
        //方式四：getDeclaredField(String name) ：获取指定名称的成员变量（只要存在就能拿到）
        System.out.println("=====================方式四：getDeclaredField(String name)==============================");
        Field declaredField = clazz.getDeclaredField("age");
        System.out.println("declaredField.getName() = " + declaredField.getName() + "\t\t" + "declaredField.getType() = " + declaredField.getType());
    }

    @Test
    //使用反射获取到的成员变量
    public void testUseField() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        //基本用法是 取值 get(Object obj)  赋值 set(Object obj,Object value)  对于私有属性需要改变访问权限 setAccessible(boolean flag)
        Class<Student> clazz = Student.class;
        Constructor<Student> constructor = clazz.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        Student student = constructor.newInstance(29);

        Field name = clazz.getDeclaredField("name");
        name.setAccessible(true);
        name.set(student, "小刘");
        System.out.println("student = " + student);

        Field age = clazz.getDeclaredField("age");
        age.setAccessible(true);
        System.out.println("age.get(student) = " + age.get(student));
    }

    @Test
    //4. 获取类的成员方法：Method对象
    public void testGetMethods() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<Student> clazz = Student.class;
        //方式一：getMethods() ：获取全部成员方法（只能获取public修饰的）
        System.out.println("=====================方式一：getMethods()==============================");
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            System.out.println("method.getName() = " + method.getName() + "\t\t" + "method.getReturnType() = " + method.getReturnType());
        }
        //方式二：getDeclaredMethods() ：获取全部成员方法（只要存在就能拿到）
        System.out.println("=====================方式二：getDeclaredMethods()==============================");
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println("declaredMethod.getName() = " + declaredMethod.getName() + "\t\t" + "declaredMethod.getReturnType() = " + declaredMethod.getReturnType());
        }
        //方式三：getMethod(String name, Class<?>... parameterTypes) ：获取指定名称的成员方法（只能拿到public修饰的）
        System.out.println("=====================方式三：getMethod(String name, Class<?>... parameterTypes)==============================");
        Method method = clazz.getMethod("getAge");
        System.out.println("method.getName() = " + method.getName() + "\t\t" + "method.getReturnType() = " + method.getReturnType());
        //方式四：getDeclaredMethod(String name, Class<?>... parameterTypes) ：获取指定名称的成员方法（只要存在就能拿到）
        System.out.println("=====================方式四：getDeclaredMethod(String name, Class<?>... parameterTypes)==============================");
        Method declaredMethod = clazz.getDeclaredMethod("getAge");
        System.out.println("declaredMethod.getName() = " + declaredMethod.getName() + "\t\t" + "declaredMethod.getReturnType() = " + declaredMethod.getReturnType());
    }

    @Test
    //反射获取到的成员方法的使用
    public void testUseMethod() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<Student> clazz = Student.class;
        //通常用来直接调用通过 invoke(Object obj, Object... args) 方法进行触发
        Student student = clazz.getConstructor().newInstance();

        Method toString = clazz.getMethod("toString");
        System.out.println("toString.invoke(student) = " + toString.invoke(student));
    }

}
