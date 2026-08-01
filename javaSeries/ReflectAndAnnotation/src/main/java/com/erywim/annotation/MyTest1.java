package com.erywim.annotation;


import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/*
    元注解：常见有 @Target@Retention@Inherited@Repeatable
    @Target：指定注解使用的位置
        传入枚举类ElementType，枚举类中定义了几种位置，例如：
            TYPE--类，接口等
            FIELD--成员变量/字段
            METHOD--方法
            PARAMETER--方法参数
            CONSTRUCTOR--构造方法
            LOCAL_VARIABLE--局部变量
            ANNOTATION_TYPE--注解

    @Retention：指定注解的保留周期
        传入枚举类RetentionPolicy，枚举类中定义了几种保留周期，例如：
            SOURCE--源代码，编译时丢弃。默认是 CLASS
            CLASS--类，接口（包含类）注解被保留在class文件中，但jvm加载class文件时丢弃
            RUNTIME--运行时，注解被保留在class文件中，jvm加载class文件时仍然存在

    @Inherited：指定注解是否被继承
    @Repeatable：指定注解是否可重复
 */
public @interface MyTest1 {
    //注解的格式是   类型 属性名称()  default  默认值;
    String value() default "hello";//注意名称后面的“()”
    String[] cccc();
}
