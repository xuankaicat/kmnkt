# kmnkt

[![](https://jitpack.io/v/com.gitee.xuankaicat/kmnkt.svg)](https://jitpack.io/#com.gitee.xuankaicat/kmnkt)

kmnkt（原communicate）是基于Kotlin Multiplatform的跨平台socket通信统一接口的实现。
可以使用同一套接口快速实现UDP/TCPClient/MQTT连接。

**支持平台**
- Android
- Jvm

**优点**
- 简单配置就可以快速使用
- 对于实现多种通信方式的需求快速转换
- 使用kotlin实现，对kotlin语法支持好(DSL)

## Gradle

### Groovy DSL

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
    // udp/tcp/mqtt支持
    implementation 'com.gitee.xuankaicat.kmnkt:socket-android:2.0.0-alpha02'// 适用于Android
    implementation 'com.gitee.xuankaicat.kmnkt:socket-desktop:2.0.0-alpha02'// 适用于Desktop
    // 阿里云alink支持
    implementation 'com.gitee.xuankaicat.kmnkt:aliyun-iot-android:2.0.0-alpha02'// 适用于Android
    implementation 'com.gitee.xuankaicat.kmnkt:aliyun-iot-desktop:2.0.0-alpha02'// 适用于Desktop
}
```

### Kotlin DSL

在根目录的 build.gradle.kts 添加：

```kotlin
allprojects {
    repositories {
        // ...
        maven("https://www.jitpack.io")
    }
}
```

添加依赖：

```kotlin
dependencies {
    // udp/tcp/mqtt支持
    implementation("com.gitee.xuankaicat.kmnkt:socket-android:2.0.0-alpha02")// 适用于Android
    implementation("com.gitee.xuankaicat.kmnkt:socket-desktop:2.0.0-alpha02")// 适用于Desktop
    // 阿里云alink支持
    implementation("com.gitee.xuankaicat.kmnkt:aliyun-iot-android:2.0.0-alpha02")// 适用于Android
    implementation("com.gitee.xuankaicat.kmnkt:aliyun-iot-desktop:2.0.0-alpha02")// 适用于Desktop
}
```

## 示例项目

* [app](examples/app) - 安卓中的udp/tcp/mqtt示例
* [app-java](examples/app-java) - 在安卓中使用java语言进行开发的udp/tcp/mqtt示例
* [AliyunIotDemo](examples/AliyunIotDemo) - 基于JetBrains Compose for Desktop的阿里云Iot桌面应用示例

## 示例代码

### 创建UDP连接对象

```kotlin
import com.gitee.xuankaicat.kmnkt.socket.dsl.udp

private val socket = udp {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
}
```

### 创建TCPClient对象

```kotlin
import com.gitee.xuankaicat.kmnkt.socket.dsl.tcp

private val socket = tcp {
    address = "10.0.2.2"//设置ip地址
    port = 9000//设置端口号
    inCharset = Charset.forName("gb2312")//设置输入编码
    outCharset = Charset.forName("gb2312")//设置输出编码
}
```

### 创建Mqtt对象

```kotlin
import com.gitee.xuankaicat.kmnkt.socket.dsl.mqtt

private val socket = mqtt {
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

> MQTT的其他参数配置可以参考[`IMqttSocket`](socket/src/commonJvmMain/kotlin/com/gitee/xuankaicat/kmnkt/socket/IMqttSocket.kt)接口

### 创建阿里云IotMqtt对象

```kotlin
import com.gitee.xuankaicat.kmnkt.aliyuniot.AliyunMqtt
import com.gitee.xuankaicat.kmnkt.aliyuniot.mqtt

private val socket = mqtt(AliyunMqtt(
    productKey = "",
    deviceName = "",
    deviceSecret = "",
    regionId = "cn-shanghai"
))
```

### 开启连接

```kotlin
socket.open()
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

```kotlin
socket.open {
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

```kotlin
socket.send(sendText)
```

### 接收数据

> 一般来说我们会在`open`的`success`回调中使用接收数据，在其他地方使用你需要确保连接已经成功打开。

```kotlin
socket.startReceive { str, data ->
    //处理接收到的数据str
    return@startReceive false//返回是否继续接收消息
}
```

### 关闭连接

```kotlin
socket.stopReceive()//停止接收数据
socket.close()//关闭连接
```

## 反馈

有任何使用上的问题或者想要的功能都可以通过提交issue提出，本人看到后会尽快处理回复。