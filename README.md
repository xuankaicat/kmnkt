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
- 使用kotlin实现，对kotlin语法支持好
```kotlin
private val socket = udp {
    address = "10.0.2.2"
    port = 9000
    open()
}

private val socket = tcp {
    address = "10.0.2.2"
    port = 9000
    open()
}

private val mqtt = mqtt {
    address = "10.0.2.2"
    port = 1883
    username = "xuankai"
    password = "xuankai"
    open()
}
```
- MQTT控制器式订阅
```kotlin
@Subscribe("service/extra/{extra}")
@TopicIgnore("reply")
fun mqttPhaseExtraPath(topic: String, @Payload myUser: MyUser, @AnyParam extra: List<String>) {
    mqttService.sendData("${topic}/reply", """{
        "id": ${myUser.id},
        "name": "${myUser.name}",
        "extra": "$extra",
        "fullTopic": "$topic",
        "time": "${LocalTime.now()}"
    }""".trimIndent())
}
```

## 如何使用
1. 在项目中引入依赖，参阅[Gradle](#gradle)。
2. 查看[示例项目](#示例项目)或对应[模块](#模块)的文档。

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
    implementation 'com.gitee.xuankaicat.kmnkt:socket-android:2.0.0-alpha05'// 适用于Android
    implementation 'com.gitee.xuankaicat.kmnkt:socket-desktop:2.0.0-alpha05'// 适用于Desktop
    // mqtt控制器式订阅支持
    implementation 'com.gitee.xuankaicat.kmnkt:mqtt-enhance-android:2.0.0-alpha05'// 适用于Android
    implementation 'com.gitee.xuankaicat.kmnkt:mqtt-enhance-desktop:2.0.0-alpha05'// 适用于Desktop
    // 阿里云alink支持
    implementation 'com.gitee.xuankaicat.kmnkt:aliyun-iot-android:2.0.0-alpha05'// 适用于Android
    implementation 'com.gitee.xuankaicat.kmnkt:aliyun-iot-desktop:2.0.0-alpha05'// 适用于Desktop
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
    implementation("com.gitee.xuankaicat.kmnkt:socket-android:2.0.0-alpha05")// 适用于Android
    implementation("com.gitee.xuankaicat.kmnkt:socket-desktop:2.0.0-alpha05")// 适用于Desktop
    // mqtt控制器式订阅支持
    implementation("com.gitee.xuankaicat.kmnkt:mqtt-enhance-android:2.0.0-alpha05")// 适用于Android
    implementation("com.gitee.xuankaicat.kmnkt:mqtt-enhance-desktop:2.0.0-alpha05")// 适用于Desktop
    // 阿里云alink支持
    implementation("com.gitee.xuankaicat.kmnkt:aliyun-iot-android:2.0.0-alpha05")// 适用于Android
    implementation("com.gitee.xuankaicat.kmnkt:aliyun-iot-desktop:2.0.0-alpha05")// 适用于Desktop
}
```

## 示例项目

* [app](examples/app) - 安卓中的udp/tcp/mqtt示例
* [app-java](examples/app-java) - 在安卓中使用java语言进行开发的udp/tcp/mqtt示例
* [AliyunIotDemo](examples/AliyunIotDemo) - 基于JetBrains Compose for Desktop的阿里云Iot桌面应用示例
* [springbootDemo](examples/springbootDemo) - 在springboot中的mqtt示例，包含socket模块与mqtt-enhance模块示例

## 模块

* [socket](socket) - 基础模块，包含UDP/TCP/MQTT
* [mqtt-enhance](mqtt-enhance) - MQTT通信增强模块，支持MQTT控制器式订阅
* [aliyun-iot](aliyun-iot) - 阿里云alink协议实现模块

## 反馈

有任何使用上的问题或者想要的功能都可以通过提交issue提出，本人看到后会尽快处理回复。