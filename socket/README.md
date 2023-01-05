# kmnkt:socket
实现了统一接口[`ISocket`](src/commonMain/kotlin/com/gitee/xuankaicat/kmnkt/socket/ISocket.kt)的基础模块。

## 示例项目

* [app](../examples/app) - 安卓中的udp/tcp/mqtt示例
* [app-java](../examples/app-java) - 在安卓中使用java语言进行开发的udp/tcp/mqtt示例
* [springbootDemo](../examples/springbootDemo) - 在springboot中的mqtt示例，包含socket模块与mqtt-enhance模块示例

## 基本用法

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

### 创建Mqtt对象(简化)

```kotlin
import com.gitee.xuankaicat.kmnkt.socket.dsl.mqtt

private val socket = mqtt {
    address = "10.0.2.2" //设置ip地址
    port = 9000 //设置端口号
    username = "siot" //设置用户名
    password = "siot" //设置密码
    // 在这里设置的Topic并不代表订阅或发布只能用这个Topic，详见MQTT扩展用法。
    inMessageTopic = "DeviceTest/000000" //设置订阅消息Topic
    outMessageTopic = "DeviceTest/123456" //设置发送消息Topic
}
```

### 创建Mqtt对象

```kotlin
import com.gitee.xuankaicat.kmnkt.socket.dsl.mqtt

private val socket = mqtt {
    address = "10.0.2.2"
    port = 9000
    inCharset = Charset.forName("gb2312") //设置输入编码
    outCharset = Charset.forName("gb2312") //设置输出编码
    username = "siot"
    password = "siot"
    inMessageTopic = "DeviceTest/000000"
    outMessageTopic = "DeviceTest/123456"
    /*自定义配置*/
    qos = MqttQuality.ExactlyOnce // 服务质量 详见MqttQuality
    uriType = "tcp" //通信方式 默认为tcp
    clientId = "" //客户端ID，如果为空则为随机值
    timeOut = 10 //设置超时时间
    cleanSession = true //断开连接后是否清楚缓存，如果清除缓存则在重连后需要手动恢复订阅。
    keepAliveInterval = 20 //检测连接是否中断的间隔
    /*行为配置*/
    threadLock = false //是否启用线程同步锁 默认false
}
```

> MQTT的其他参数配置可以参考[`IMqttSocket`](src/commonMain/kotlin/com/gitee/xuankaicat/kmnkt/socket/IMqttSocket.kt)接口

### 开启连接

```kotlin
socket.open()
socket.openSync() //同步式开启连接，在连接成功前将阻塞
```

**回调默认设置**

| 回调函数    | 返回值    | 触发时机  | 默认行为           |
|---------|--------|-------|----------------|
| success | 无返回值   | 连接成功后 | 打印日志           |
| failure | 是否重新连接 | 连接失败后 | 打印日志，5秒后尝试重新连接 |
| loss    | 是否重新连接 | 失去连接后 | 打印日志，尝试重新连接    |

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

## MQTT扩展用法

### 订阅Topic

```kotlin
socket.addInMessageTopic("topic") { str, topic ->
    // str: 收到的消息
    // topic: 消息topic
}
```

### 取消订阅Topic

```kotlin
socket.removeInMessageTopic("topic")
```

### 发布消息

```kotlin
socket.send("hello") // 根据outMessageTopic发布消息
socket.send("topic", "hello") // 根据指定topic发布消息
```

### 订阅并发布消息

```kotlin
socket.sendAndReceive("发布topic", "订阅topic", "hello") { str, topic ->
    // str: 收到的消息
    // topic: 消息topic
}
// 简化版本
socket.sendAndReceive("发布topic", "订阅topic", "hello") { 
    // it: 收到的消息
}
```

### 同步发送消息

```kotlin
socket.sendSync("hello") // 根据outMessageTopic发布消息
socket.sendSync("topic", "hello") // 根据指定topic发布消息
```

### 同步订阅并发布消息

```kotlin
socket.sendAndReceiveSync("发布topic", "订阅topic", "hello") { str, topic ->
    // str: 收到的消息
    // topic: 消息topic
}
// 简化版本
socket.sendAndReceiveSync("发布topic", "订阅topic", "hello") { 
    // it: 收到的消息
}
// 订阅并发布后等待至拿到响应消息并赋值给result
// 如果超过10秒没有收到消息则将result设为"消息响应超时"，并取消订阅topic
val result = socket.sendAndReceiveSync(
    "发布topic", "订阅topic", "hello", 10000L
) ?: "消息响应超时"
```