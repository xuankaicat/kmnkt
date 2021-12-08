# android-communicate

[![](https://jitpack.io/v/com.gitee.xuankaicat/communicate.svg)](https://jitpack.io/#com.gitee.xuankaicat/communicate)

android-communicate是Android实现socket通信统一接口的实现。

## Gradle

在根目录的 build.gradle 添加：

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://www.jitpack.io' }
    }
}
```

添加依赖：

```groovy
dependencies {
    implementation 'com.gitee.xuankaicat:communicate:1.0.0'
}
```

## 示例


### 创建UDP连接对象

kotlin：
```kotlin
private val communicate = Communicate.UDP.apply {
    address = "192.168.200.1"//设置ip地址
    serverPort = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
    open()//开启连接
}
```

java：
```java
private Communicate communicate = Communicate.getUDP();
communicate.setAddress("192.168.200.1");//设置ip地址
communicate.setServerPort(9000);//设置端口号
communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
communicate.open();//开启连接
```

### 创建TCPClient对象

kotlin:
```kotlin
private val communicate = Communicate.TCPClient.apply {
    address = "192.168.200.1"//设置ip地址
    serverPort = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
    open()//开启连接
}
```

java:
```java
private Communicate communicate = Communicate.getTCPClient();
communicate.setAddress("192.168.200.1");//设置ip地址
communicate.setServerPort(9000);//设置端口号
communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
communicate.open();//开启连接
```

### 发送数据

kotlin:
```kotlin
communicate.send(sendText)
```

java:
```java
communicate.send(sendText);
```

### 接收数据

kotlin:
```kotlin
communicate.startReceive {
    //处理接收到的数据(it)
    return@startReceive false//返回是否继续接收消息
}
```

java:
```java
communicate.startReceive(result -> {
    //处理接收到的数据(result)
    return false;//返回是否继续接收消息
});
```

### 关闭连接

以Activity举例，需要写在`onDestroy`中。

kotlin:
```kotlin
override fun onDestroy() {
    super.onDestroy()
    communicate.stopReceive()//停止接收数据
    communicate.close()//关闭连接
}
```

java:
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    communicate.stopReceive();//停止接收数据
    communicate.close();//关闭连接
}
```