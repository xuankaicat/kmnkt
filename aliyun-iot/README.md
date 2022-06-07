# kmnkt:aliyun-iot
阿里云alink协议实现模块。

## 示例项目

* [AliyunIotDemo](../examples/AliyunIotDemo) - 基于JetBrains Compose for Desktop的阿里云Iot桌面应用示例

## 基本用法

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

### 设备属性设置

```kotlin
socket.propertySet(
    receiveOnce = false // 是否只接收一次，默认为false表示一直接收
) {
    // 回调函数
}
```

### 设备属性上报

```kotlin
val map = mutableMapOf<String, Int>()
if(temperature.toIntOrNull() != null) map["mtemp"] = temperature.toInt()
if(humidity.toIntOrNull() != null) map["mhumi"] = humidity.toInt()
if(brightness.toIntOrNull() != null) map["LightLuminance"] = brightness.toInt()

socket.propertyPost(
    params = map, // 上报参数
    receiveOnce = false // 期待服务端返回信息
) {
    // 回调函数
}
```

### 获取期望属性值

```kotlin
val list = listOf("LightSwitch", "LightLuminance", "mtemp", "mhumi")

socket.desiredGet(list) {
    // 回调函数
}
```

### 自定义方法

> 由于alink的实现还不完善，如果有需要可以自行实现所需请求的函数。
> 实现时可以参考[event.kt](src/commonJvmMain/kotlin/com/gitee/xuankaicat/kmnkt/aliyuniot/alink/event.kt)。

```kotlin
fun IMqttSocket.函数名(
    // ... 自定义参数
    onReceive: OnReceiveAlinkResultFunc = {} // 回调函数
) {
    this as AlinkMQTT

    val id = nextId
    
    // 构建AlinkBase对象，以下供参考
    val msgObj = AlinkBase(id,
        params = params.toJsonObject(),
        sys = AlinkBase.Sys("0"),
        method = "thing.event.property.post"
    )

    // 发送并接收消息，如果只想接收消息也可以用addInMessageTopic
    sendAndReceiveAlink(id, "/sys/${productKey}/${deviceName}/topic", msgObj, false, onReceive)
}
```