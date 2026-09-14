package main

import (
	"errors"
	"fmt"
	// othertwopackage "hello/otherTwoPackage"
	"strconv"
	"time"
	// "hello/othertwopackage"
)


func arr(){
	var arr [3]int = [3]int{1,2,3,}
	fmt.Println(arr)

	var arr1 = [3]int{1,2,3}
	fmt.Println(arr1)

	var arr2 = [...]int{1,3}
	// arr2[3] = 123 out of range
	fmt.Println(arr2)
	fmt.Println(len(arr2))
}

func slice(){
	//可变长度数组——切片（集合的概念）
	var list []string 
	list = append(list, "item1")
	list = append(list, "item2")

	fmt.Println(list)
}

func makeTest(){
	//除了基本数据类型，其他数据类型如果只定义不赋值，那么实际的值就是nil
	//可以通过make函数提前创建数组长度
	//make([]type, length, capacity)

	var list = make([]int,0)
	fmt.Println(list,len(list),cap(list))
	list = append(list, 1)
	fmt.Println(list,len(list),cap(list))
}

func sliceTruncate(){
	/*
	 切片入参含义（索引下标）：从哪开始(闭) : 到哪为止(开) : 到哪为止算容量
	     					 low         high          max
	len = 看得见的范围：high - low
	cap = 连隐藏空间算上的范围：max - low

	如果切片的cap超过后，就会创建一个新的切片，此时就脱离和原本数组的关系了
	*/
	var list = [...]int{1,2,3,4,5,6,7,8}

	slice1 := list[:]// [1 2 3 4 5 6 7 8]，取全部元素
	fmt.Println(slice1)
	fmt.Println(list[1:2])// [2]，索引 1 到 2（左闭右开），只取下标 1 的元素

	//浅拷贝形式，共享底层数据
	slice1[0] = 999
	fmt.Println(slice1)
	fmt.Println(list)

	//切出来的切片进行append操作也会影响到原数组，如果在cap内，就会将原本的数据给替换掉
	slice2 := list[6:7]
	slice2 = append(slice2, 888)
	fmt.Println(slice2)
	fmt.Println(list)
}

func mapTest(){
	/*
	map的key必须是基本数据类型，value可以是任意类型
	map使用之前必须初始化
	*/
	//声明
	var m1 map[string]int
	//初始化（这两步可以合并）
	m1 = make(map[string]int)
	//初始化2
	m1 = map[string]int{}
	//设置值
	m1["k1"] = 123
	fmt.Println(m1)

	//取值
	fmt.Println(m1["k1"])

	//删除值
	delete(m1,"k1")
	fmt.Println(m1)


	//声明并赋值
	var m2 = map[string]int{
		"name":123,
	}
	age1 := m2["age1"] // 取一个不存在的
	fmt.Println(age1)
	age2, ok := m2["age1"]
	fmt.Println(age2, ok)
}


func arrFor(){
	list := make([]string,10)

	for i:=0;i < 10;i++{
		list[i] = strconv.Itoa(i) + "-test"
		// list = append(list, strconv.Itoa(i) + "-test")
	}

	for index , value := range list{
		fmt.Println(index,value)
	}
}

func mapFor(){
	m1 := map[string]int{
		"name":11,
		"age":123,
	}

	for key,value := range m1{
		fmt.Println(key,value)
	}
}

//入参1：多参数
func multiParam(n1 int,n2 int,args3 string){
	fmt.Print("测试" + args3 + " 相加：")
	fmt.Println(n1+n2)
}
//入参2：同类型参数合并
func sameParamUnion(n1,n2 int){
	fmt.Print("相加：")
	fmt.Println(n1+n2)
}
//入参3：多个参数
func addParam(numList ...int){
	fmt.Println(numList)
}

//返回值1：多返回值
func multiReturn() (int,string,error){
	return 123,"返回str",errors.New("测试错误")
}

//返回值2：命名返回值（裸返回）
func namedReturn() (res string) {
	/*
	 命名返回值：定义返回值时直接给它起名字
	 1. 相当于函数开头就声明好了变量 res，初始值是对应类型的零值（string 的零值是 ""）
	 2. 函数体内可以直接给它赋值、修改
	 3. return 后面可以什么都不写，叫"裸返回"，返回的就是 res 当前的值
	 注意：裸返回只建议在短函数里用，函数一长，读代码的人就不知道 return 出去的是什么了
	*/
	fmt.Println("res 还没赋值，当前是零值:", res == "") //true

	res = "我是命名返回值"
	return //裸返回，等价于 return res
}

//返回值3：多个命名返回值
func namedMultiReturn() (num int, msg string, err error) {
	/*
	 对比上面的 multiReturn() (int,string,error)：匿名时调用方只能猜每个返回值的含义
	 命名之后 (num, msg, err) 一眼就能看懂，最后一个习惯命名为 err
	 num、msg、err 在函数开头就初始化成零值了（0、""、nil）
	*/
	num = 100
	msg = "操作成功"
	//err 不用动，本来就是 nil
	return //等价于 return num, msg, err
}

//返回值4：命名返回值最经典的场景——配合 defer 修改返回值
func namedReturnDefer() (res int) {
	/*
	 defer 的执行时机：return 之后、函数真正退出之前
	 匿名返回值：return 时值已经拷贝走了，defer 里再改也影响不到返回值
	 命名返回值：defer 还来得及在函数退出前修改返回值
	 典型用途：recover 兜底 panic、出错时给 err 补充信息
	*/
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("defer 里 recover 捕获到 panic:", r)
			res = -1 //把返回值改成 -1，调用方不用崩溃也能知道出错了
		}
	}()

	panic("程序炸了")
	//永远执行不到这里，但函数照样能"正常"返回 -1
}

func funcCalled(i int) int{
	fmt.Println(i,"  func called.....")
	return i
}

func switchTest(i int){
	switch i {
	case 0 :
		fmt.Println("case 0被输出")
	case funcCalled(3):
		fmt.Println("case 3被输出")
	case funcCalled(1):
		fmt.Println("case 1被输出")
	
	}
}

func deferTest(){
	//defer 语句会将函数调用推迟到外围函数返回时执行。被延迟调用的参数会立即求值，但函数调用直到外围函数返回时才执行。
	x := 0
	defer fmt.Println("当前x = ",x) // 输出0

	defer funcCalled(1) // defer之间的顺序是以堆栈的形式，先进后出
	x = 10
	fmt.Println("当前x = ",x) 
	/*
	当前x =  10
	1   func called.....
	当前x =  0
	*/
}

func pointTest(){
	i,j := 100,200

	p := &i //指向i
	fmt.Println(*p)//通过指针读取 i 。输出 100
	*p = 21
	fmt.Println(i) // 输出21

	p = &j
	*p = *p / 20 // 通过指针对 j 进行除法。 
	fmt.Println(j)// 输出10
}



func func1(x,y int) (int,int){

	return x,y
}

func func2(x,y int) int {
	return x + y
}

//函数能与参数一样，作为参数、返回值。作为返回值的时候返回类型处写函数定义
func func3() (func(x int) int,int){//函数作为返回值
	return func(x int)int{
		return func2(func1(1,3))	//函数作为入参
	},100
}


//闭包
func adder() func(x int) int{
	sum := 0
	return func(x int) int{
		sum += x
		return sum
	}
}

func callAdder(){
	pos , neg := adder(),adder()

	for i := range 10{
		fmt.Println(
			pos(i),
			neg(-2*i),
		)
	}
}

func goroutineTest(s []int , c chan int){
	sum := 0
	for _,v := range s{
		sum += v
	}
	c <- sum
}

func groutineCalled(){
	nums := []int{1,2,3,4,5,6,7,9}
	c := make(chan int)

	go goroutineTest(nums[:len(nums)/2],c)
	go goroutineTest(nums[len(nums)/2:],c)

	x,y := <- c, <- c
	fmt.Println(
		x,y,
	)
}


func main(){
	fmt.Println("============================================",time.Now())
	// arr()
	// slice()
	// makeTest()
	// sliceTruncate()
	// mapTest()
	// arrFor()
	// mapFor()

	//入参测试
	// multiParam(1,2,"测试1")
	// sameParamUnion(10,20)
	// addParam(1,2,3,4,5,6)
	// var numList = make([]int,10)
	// /* for i := 0 ; i < 10;i++{
	//  	numList[i] = i
	//  }
	// */

	// for i := range numList{
	// 	numList[i] = i
	// }
	// addParam(numList...)
	
	//返参测试
	// var n1,n2,n3 = multiReturn()
	// fmt.Println(n1,n2,n3)

	// var str = namedReturn()
	// fmt.Println(str)

	//switch 触发函数测试
	// switchTest(3)

	//defer 测试
	// deferTest()

	// pointTest()

	// my := othertwopackage.MyStruct{1,"hello",1} 第三个参数因为不可见，所以声明也报错，不声明也报错
	// my.xx 

	// structDemo := othertwopackage.MyStruct2{1,"demo"}
	// p := &structDemo
	// fmt.Println((*p).X , "==" ,p.X,"==", p.X == (*p).X)//1 == 1 == true


	//测试 函数值
	// i := func2(func1(1,2))
	// fmt.Println(i)

	//测试闭包
	// callAdder()

	//测试goroutine
	groutineCalled()

	fmt.Println("============================================")	
}