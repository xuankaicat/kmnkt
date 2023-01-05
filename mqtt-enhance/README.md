# kmnkt:mqtt-enhance
MQTT通信增强模块，支持MQTT控制器式订阅。

## 示例项目

* [springbootDemo](../examples/springbootDemo) - 在springboot中的mqtt示例，包含socket模块与mqtt-enhance模块示例

## 基本用法

### 创建MQTTManager

```kotlin
val mqtt = mqtt {
    // ...
}
val mqttManager = MQTTManager(mqtt, MoshiConvertFactory.create())
```

> 该模块默认支持了Moshi用于解析JSON对象，也可以自行创建实现了[PayloadConvertFactory](src/commonMain/kotlin/com/gitee/xuankaicat/kmnkt/mqtt/enhance/convert/PayloadConvertFactory.kt)
> 接口的对象用于转换。
> </br>如果没有使用非String类型的Payload，则无需传入转换工厂。

### 自定义MQTT控制器

```kotlin
class MQTTController {
    /**
     * 订阅消息
     * 首个不带注解的String类型参数会传入接收到的topic
     * 带有Payload注解的参数会传入接收到的消息
     * 在创建MQTTManager时传入了转换工厂则Payload可以声明为非String类型，参见第三个例子
     */
    @Subscribe("service/time")
    fun mqttGetTime(topic: String, @Payload payload: String) {
        // 处理消息
    }

    /**
     * 订阅带有+通配符的消息
     * 如果收到的topic为service/user/12 则id为12
     * Param可以使用基本类型，如Int, String, Boolean等
     */
    @Subscribe("service/user/{id}")
    fun mqttGetUsername(topic: String, @Payload payload: String, @Param id: Int) {
        // 处理消息
    }

    /**
     * 订阅带有#通配符的消息
     * 如果消息的topic以reply结尾则无视，参见TopicIgnore注解
     * 在创建MQTTManager时传入了转换工厂则Payload可以声明为非String类型，接收到JSON字符串后将自动转换为对应对象
     * 如果收到的topic为service/extra/home/1 则extra中的参数为["home", "1"]
     * extra也可以声明为String类型，则受到的参数为"home/1"
     */
    @Subscribe("service/extra/{extra}")
    @TopicIgnore("reply")
    fun mqttPhaseExtraPath(topic: String, @Payload myUser: MyUser, @AnyParam extra: List<String>) {
        // 处理消息
    }
}
```

### 向MQTTManager注册服务实例

```kotlin
mqttManager.enable(MQTTController::class, MQTTController())
```

在Springboot中，自行实现MQTTComponent后，可以在控制器内注入MQTTComponent以实现注册服务：

```kotlin
@Component
class MQTTController(
    mqttComponent: MQTTComponent
) {
    init {
        mqttComponent.mqttManager.enable(MQTTController::class, this)
    }
    
    // ...
}
```

## 注解

### @Subscribe

订阅指定Topic。

带有该注解的函数需要两个参数，首个不带注解的String类型参数会传入接收到的topic，带有Payload注解的参数会传入接收到的消息。

用法：

```kotlin
@Subscribe("需要订阅的Topic")
fun func(topic: String, @Payload payload: String) {
    // 处理消息
}
```

### @Payload

指定订阅函数中作为Payload的参数。

在创建MQTTManager时传入了转换工厂则Payload可以声明为非String类型，接收到JSON字符串后将自动转换为对应对象。

用法：

```Kotlin
class MyUser(val id: Int,val name: String)

@Subscribe("service/user/register")
fun func1(topic: String, @Payload payload: String) {
    // payload: "{"id":1,"name":"xuankai"}" => payload="{"id":1,"name":"xuankai"}"
}

@Subscribe("service/user/register")
fun func2(topic: String, @Payload payload: MyUser) {
    // payload: "{"id":1,"name":"xuankai"}" => payload=MyUser(1, "xuankai")
}
```

### @Param

用于对MQTT Topic通配符+参数的处理。

订阅函数中带有该注解的参数会映射Topic中与参数名相同的参数。

可以通过value指定参数名，或者默认取与函数形参同名的参数。

用法：

```Kotlin
@Subscribe("service/user/{id}")
fun func1(topic: String, @Payload payload: String, @Param id: Int) {
    // topic: "service/user/1"  => id=1
    // topic: "service/user/12" => id=12
    // topic: "service/user/ab" => 转换错误，不会调用该函数。
}

@Subscribe("service/user/name/{name}")
fun func2(topic: String, @Payload payload: String, @Param("name") result: String) {
    // topic: "service/user/name/xuankai"  => result="xuankai"
}
```

### @AnyParam

用于对MQTT Topic通配符#参数的处理。

AnyParam只能作用于订阅函数的最后一个参数上，对应通配符#也只能放在Topic末尾。

AnyPAram修饰的参数类型可以指定为`String`或`List<String>`，参见用法。

订阅函数中带有该注解的参数会映射Topic中与参数名相同的参数。

可以通过value指定参数名，或者默认取与函数形参同名的参数。

用法：

```Kotlin
@Subscribe("service/{extra}")
fun func1(topic: String, @Payload payload: String, @AnyParam extra: List<String>) {
    // topic: "service/user/1"  => extra=["user", "1"]
    // topic: "service/user/hello/world" => extra=["user, "hello", "world"]
    // topic: "service" => extra=[]
}

@Subscribe("service/{extra}")
fun func2(topic: String, @Payload payload: String, @AnyParam("extra") result: String) {
    // topic: "service/user/1"  => result="user/1"
    // topic: "service/user/hello/world" => result="user/hello/world"
    // topic: "service" => result=""
}
```

### @TopicIgnore

指定需要无视的Topic。

Topic仍然会被接收到但是不会调用订阅函数。

接收匹配参数与匹配模式两个参数，匹配模式默认为`TopicIgnoreType.DEFAULT`，具体参见[TopicIgnoreType](#TopicIgnoreType)。

用法：

```Kotlin
@Subscribe("service/hello/{name}")
@TopicIgnore("reply")
fun func1(topic: String, @Payload payload: String, @Param name: String) {
    // topic: "service/hello/xuankai" => 处理消息
    // topic: "service/hello/xuankai_reply" => 无视消息
    // topic: "service/hello/reply" => 无视消息
}

@Subscribe("service/hello/{name}")
@TopicIgnore("service/hello/xuankai", TopicIgnoreType.FULL)
fun func2(topic: String, @Payload payload: String, @Param name: String) {
    // topic: "service/hello/cky" => 处理消息
    // topic: "service/hello/xuankai" => 无视消息
}

@Subscribe("service/hello/{name}")
@TopicIgnore("\\d+", TopicIgnoreType.REGEX)
fun func3(topic: String, @Payload payload: String, @Param name: String) {
    // topic: "service/hello/cky" => 处理消息
    // topic: "service/hello/123" => 无视消息
}
```

#### TopicIgnoreType

| 枚举值                  | 作用                                                         |
| ----------------------- | ------------------------------------------------------------ |
| TopicIgnoreType.DEFAULT | 默认匹配方式，匹配接收到的Topic的尾部，相同则无视该消息      |
| TopicIgnoreType.FULL    | 全字匹配Topic，与接收到的Topic完全相同则无视该消息           |
| TopicIgnoreType.REGEX   | 使用正则表达式匹配Topic，若在接收到的Topic中可以匹配成功则无视该消息 |

