# communicate-android

[![](https://jitpack.io/v/com.gitee.xuankaicat/communicate.svg)](https://jitpack.io/#com.gitee.xuankaicat/communicate)

communicate是Android实现socket通信统一接口的实现。
可以使用同一套接口快速实现UDP/TCPClient/MQTT连接。

**优点**
- 简单配置就可以快速使用
- 对于实现多种通信方式的需求快速转换
- 使用kotlin实现，对kotlin语法支持好(DSL)

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
    implementation 'com.gitee.xuankaicat.communicate:communicate:1.5.0-M1'//UDP、TCPClient
}
```

如果需要MQTT支持只需要添加以下依赖：

```groovy
dependencies {
    implementation 'com.gitee.xuankaicat.communicate:communicate-mqtt:1.5.0-M1'//UDP、TCPClient、MQTT
}
```

## 示例


### 创建UDP连接对象

kotlin：
```kotlin
private val communicate = Communicate.UDP.apply {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
}
```

java：
```java
//普通构造
private Communicate communicate = Communicate.UDP();
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    communicate.setAddress("10.0.2.2");//设置ip地址
    communicate.setPort(9000);//设置端口号
    communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
    communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
}

//lambda构造
private final Communicate communicate = Communicate.getUDP(c -> {
    c.setAddress("10.0.2.2");//设置ip地址
    c.setPort(9000);//设置端口号
    c.setInCharset(Charset.forName("gb2312"));//设置输入编码
    c.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    return null;
});
```

### 创建TCPClient对象

kotlin:
```kotlin
private val communicate = Communicate.TCPClient.apply {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
}
```

java:
```java
//普通构造
private Communicate communicate = Communicate.TCPClient();
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    communicate.setAddress("10.0.2.2");//设置ip地址
    communicate.setPort(9000);//设置端口号
    communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
    communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
}

//lambda构造
private final Communicate communicate = Communicate.getTCPClient(c -> {
    c.setAddress("10.0.2.2");//设置ip地址
    c.setPort(9000);//设置端口号
    c.setInCharset(Charset.forName("gb2312"));//设置输入编码
    c.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    return null;
});
```

### 创建MQTT对象

kotlin:
```kotlin
private val communicate = Communicate.MQTT.apply {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
    /*MQTT必须的额外配置*/
    username = "siot"//设置用户名
    password = "siot"//设置密码
    publishTopic = "DeviceTest/000000"//设置订阅消息Topic
    responseTopic = "DeviceTest/123456"//设置发送消息Topic
    /*MQTT自定义配置*/
    timeOut = 10//设置超时时间
    cleanSession = true//断开连接后是否清楚缓存，如果清除缓存则在重连后需要手动恢复订阅。
    keepAliveInterval = 20//检测连接是否中断的间隔
}
```

java:
```java
//普通构造
private MQTTCommunicate communicate = MQTTCommunicate.MQTT();
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    communicate.setAddress("10.0.2.2");//设置ip地址
    communicate.setPort(9000);//设置端口号
    communicate.setInCharset(Charset.forName("gb2312"));//设置输入编码
    communicate.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    /*MQTT必须的额外配置*/
    communicate.setUsername("siot");//设置用户名
    communicate.setPassword("siot");//设置密码
    communicate.setInMessageTopic("DeviceTest/000000");//设置输入消息Topic
    communicate.setOutMessageTopic("DeviceTest/123456");//设置输出消息Topic
    /*MQTT自定义配置*/
    communicate.setTimeOut(10)//设置超时时间
    communicate.setCleanSession(true);//断开连接后是否清楚缓存，如果清除缓存则在重连后需要手动恢复订阅。
    communicate.setKeepAliveInterval(20);//检测连接是否中断的间隔
}

//lambda构造
private final Communicate communicate = Communicate.getTCPClient(c -> {
    c.setAddress("10.0.2.2");//设置ip地址
    c.setPort(9000);//设置端口号
    c.setInCharset(Charset.forName("gb2312"));//设置输入编码
    c.setOutCharset(Charset.forName("gb2312"));//设置输出编码
    /*MQTT必须的额外配置*/
    c.setUsername("siot");//设置用户名
    c.setPassword("siot");//设置密码
    c.setInMessageTopic("DeviceTest/000000");//设置输入消息Topic
    c.setOutMessageTopic("DeviceTest/123456");//设置输出消息Topic
    c.setTimeOut(10)//设置超时时间
    c.setCleanSession(true);//断开连接后是否清楚缓存，如果清除缓存则在重连后需要手动恢复订阅。
    c.setKeepAliveInterval(20);//检测连接是否中断的间隔
    return null;
});
```

> MQTT的其他参数配置可以参考`MQTTCommunicate`接口

### 开启连接

**默认设置**

| 回调函数 | 返回值 |触发时机 |默认行为 |
| -------- | -------- | -------- |-------- |
| success  | 无返回值     | 连接成功后 | 打印日志 |
| failure  | 是否重新连接 | 连接失败后 | 打印日志，5秒后尝试重新连接 |
| loss     | 是否重新连接 | 失去连接后 | 打印日志，尝试重新连接 |

> `loss`回调函数尝试重新连接后的连接成功与失败将会触发`success`与`failure`回调

kotlin:

```kotlin
communicate.open()
```

java:
```java
communicate.open();
```

需要自定义开启成功或失败的回调函数则使用以下方法：

> 如果要自行实现`failure`回调函数并重新连接请在函数中增加等待函数，如`Thread.sleep(5000)`。

kotlin:
```kotlin
communicate.open {
    success {
        //开启连接成功时执行
    }
    failure {
        //开启连接失败时执行
        return@failure false//是否继续尝试连接
    }
    loss {
        //失去连接时执行
        return@loss false//是否尝试重连
    }
}
```

java:
```java
communicate.open(new OnOpenCallback() {
    @Override
    public void success(@NonNull Communicate communicate) {
        //开启连接成功时执行
    }

    @Override
    public boolean failure(@NonNull Communicate communicate) {
        //开启连接失败时执行
        return false;//是否继续尝试连接
    }
    
    @Override
    public boolean loss(@NonNull Communicate communicate) {
        //失去连接时执行
        return false;//是否尝试重连
    }
});
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
communicate.startReceive { str, data ->
    //处理接收到的数据str
    return@startReceive false//返回是否继续接收消息
}
```

java:
```java
communicate.startReceive((str, data) -> {
    //处理接收到的数据str
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

## 反馈

有任何使用上的问题或者想要的功能都可以通过提交issue提出，本人看到后会尽快处理回复。