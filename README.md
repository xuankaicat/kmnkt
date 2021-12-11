# communicate-android

[![](https://jitpack.io/v/com.gitee.xuankaicat/communicate.svg)](https://jitpack.io/#com.gitee.xuankaicat/communicate)

communicate是Android实现socket通信统一接口的实现。
可以使用同一套接口快速实现UDP/TCPClient/MQTT连接。

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
    implementation 'com.gitee.xuankaicat.communicate:communicate:1.1.3'//UDP、TCPClient
}
```

如果需要MQTT支持只需要添加以下依赖：

```groovy
dependencies {
    implementation 'com.gitee.xuankaicat.communicate:communicate-mqtt:1.1.3'//UDP、TCPClient、MQTT
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
//普通构造
private Communicate communicate = Communicate.UDP();
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    communicate.setAddress("192.168.200.1");//设置ip地址
    communicate.setServerPort(9000);//设置端口号
    communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
    communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    communicate.open();//开启连接
}

//lambda构造
private final Communicate communicate = Communicate.getUDP(c -> {
    c.setAddress("192.168.200.1");//设置ip地址
    c.setServerPort(9000);//设置端口号
    c.setInCharset(Charset.forName("gb2312"));//设置输入编码
    c.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    c.open();//开启连接
    return null;
});
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
//普通构造
private Communicate communicate = Communicate.TCPClient();
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    communicate.setAddress("192.168.200.1");//设置ip地址
    communicate.setServerPort(9000);//设置端口号
    communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
    communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    communicate.open();//开启连接
}

//lambda构造
private final Communicate communicate = Communicate.getTCPClient(c -> {
    c.setAddress("192.168.200.1");//设置ip地址
    c.setServerPort(9000);//设置端口号
    c.setInCharset(Charset.forName("gb2312"));//设置输入编码
    c.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    c.open();//开启连接
    return null;
});
```

### 创建MQTT对象

kotlin:
```kotlin
private val communicate = Communicate.MQTT.apply {
    address = "192.168.200.1"//设置ip地址
    serverPort = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
    /*MQTT必须的额外配置*/
    username = "siot"//设置用户名
    password = "siot"//设置密码
    publishTopic = "DeviceTest/000000"//设置订阅消息Topic
    responseTopic = "DeviceTest/123456"//设置发送消息Topic
    open()//开启连接
}
```

java:
```java
//普通构造
private MQTTCommunicate communicate = MQTTCommunicate.MQTT();
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    communicate.setAddress("192.168.200.1");//设置ip地址
    communicate.setServerPort(9000);//设置端口号
    communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
    communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    /*MQTT必须的额外配置*/
    communicate.setUsername("siot");//设置用户名
    communicate.setPassword("siot");//设置密码
    communicate.setInMessageTopic("DeviceTest/000000");//设置输入消息Topic
    communicate.setOutMessageTopic("DeviceTest/123456");//设置输出消息Topic
    communicate.open();//开启连接
}

//lambda构造
private final Communicate communicate = Communicate.getTCPClient(c -> {
    c.setAddress("192.168.200.1");//设置ip地址
    c.setServerPort(9000);//设置端口号
    c.setInCharset(Charset.forName("gb2312"));//设置输入编码
    c.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    /*MQTT必须的额外配置*/
    c.setUsername("siot");//设置用户名
    c.setPassword("siot");//设置密码
    c.setInMessageTopic("DeviceTest/000000");//设置输入消息Topic
    c.setOutMessageTopic("DeviceTest/123456");//设置输出消息Topic
    c.open();//开启连接
    return null;
});
```

> MQTT的其他参数配置可以参考`MQTTCommunicate`接口

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