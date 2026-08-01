package com.erywim.reflect;


public class Student {
    private int age;
    private String name;
    public String address;


    public Student(int age, String name) {
        this.age = age;
        this.name = name;
    }
    public Student() {
    }

    private Student(int age){ //创建一个私有的构造器，可以通过setAccessible(true)方法进行破解访问
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
