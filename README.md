# communicate-kmm

[![](https://jitpack.io/v/com.gitee.xuankaicat/communicate.svg)](https://jitpack.io/#com.gitee.xuankaicat/communicate)

communicate是基于Kotlin Multiplatform的跨平台socket通信统一接口的实现。
可以使用同一套接口快速实现UDP/TCPClient/MQTT连接。

**支持平台**
- Android
- Desktop

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
    implementation 'com.gitee.xuankaicat.communicate:communicate-android:2.0.0-dev01'//适用于Android
    implementation 'com.gitee.xuankaicat.communicate:communicate-desktop:2.0.0-dev01'//适用于Desktop
}
```

## 示例


### 创建UDP连接对象

kotlin：
```kotlin
import com.gitee.xuankaicat.communicate.dsl.udp

private val communicate = udp {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
}
```

> `com.gitee.xuankaicat.communicate.dsl.udp`于2.0.0-dev02后支持使用

### 创建TCPClient对象

kotlin:
```kotlin
import com.gitee.xuankaicat.communicate.dsl.tcp

private val communicate = tcp {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
}
```

> `com.gitee.xuankaicat.communicate.dsl.tcp`于2.0.0-dev02后支持使用

### 创建MQTT对象

kotlin:
```kotlin
import com.gitee.xuankaicat.communicate.dsl.mqtt

private val communicate = mqtt {
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

> `com.gitee.xuankaicat.communicate.dsl.mqtt`于2.0.0-dev02后支持使用

> MQTT的其他参数配置可以参考`MQTTCommunicate`接口

### 开启连接

kotlin:

```kotlin
communicate.open()
```

**回调默认设置**

| 回调函数 | 返回值 |触发时机 |默认行为 |
| -------- | -------- | -------- |-------- |
| success  | 无返回值     | 连接成功后 | 打印日志 |
| failure  | 是否重新连接 | 连接失败后 | 打印日志，5秒后尝试重新连接 |
| loss     | 是否重新连接 | 失去连接后 | 打印日志，尝试重新连接 |

> `loss`回调函数尝试重新连接后的连接成功与失败将会触发`success`与`failure`回调。

需要自定义回调函数则使用以下方法：

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

### 发送数据

kotlin:
```kotlin
communicate.send(sendText)
```

### 接收数据

> 一般来说我们会在`open`的`success`回调中使用接收数据，在其他地方使用你需要确保连接已经成功打开。

kotlin:
```kotlin
communicate.startReceive { str, data ->
    //处理接收到的数据str
    return@startReceive false//返回是否继续接收消息
}
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

## 反馈

有任何使用上的问题或者想要的功能都可以通过提交issue提出，本人看到后会尽快处理回复。