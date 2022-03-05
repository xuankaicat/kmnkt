import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.gitee.xuankaicat.kmnkt.aliyuniot.alink.desiredGet
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@Composable
@Preview
fun App() {
    var board by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var humidity by remember { mutableStateOf("") }
    var brightness by remember { mutableStateOf("") }
    var isSwitchOpen by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }

    fun log(message: String) {
        board += "$message\n"
    }

    fun setData(key: String, data: JsonElement) {
        when(key) {
            "LightSwitch" -> {
                val value = Json.decodeFromJsonElement<Int>(data)
                isSwitchOpen = value == 1
            }
            "mtemp" -> {
                temperature = Json.decodeFromJsonElement<Int>(data).toString()
            }
            "mhumi" -> {
                humidity = Json.decodeFromJsonElement<Int>(data).toString()
            }
            "LightLuminance" -> {
                brightness = Json.decodeFromJsonElement<Int>(data).toString()
            }
        }
    }

    val onConnectClicked = block@{
        if(isConnected) {
            //断开连接
            log("主动断开连接")
            Mqtt.mqtt.close()
            isConnected = false
            return@block
        }
        log("开始尝试连接")
        Mqtt.connect(
            onSuccess = {
                isConnected = true
                log("连接成功")

                Mqtt.mqtt.desiredGet(listOf("LightSwitch", "LightLuminance", "mtemp", "mhumi")) {
                    it.data.forEach { entry ->
                        val u = (entry.value as JsonObject)["value"] as JsonElement
                        setData(entry.key, u)
                    }
                }

                Mqtt.subscribe {
                    log("接收到设备属性设置$it")
                    (it.params as JsonObject).forEach { entry -> setData(entry.key, entry.value) }
                }
            },
            onFailure = { log("连接失败") },
            onLoss = {
                log("连接中断")
                isConnected = false
            }
        )
    }

    val onOpenClicked = block@{
        if(isSwitchOpen) {
            log("小灯已开启")
            return@block
        }
        log("发送开启小灯")
        Mqtt.setSwitch(true,
            onSuccess = {
                isSwitchOpen = true
                log("开启成功")
            },
            onFailure = {
                log("开启失败")
            }
        )
    }

    val onCloseClicked = block@{
        if(!isSwitchOpen) {
            log("小灯已关闭")
            return@block
        }
        log("发送关闭小灯")
        Mqtt.setSwitch(false,
            onSuccess = {
                isSwitchOpen = false
                log("关闭成功")
            },
            onFailure = {
                log("关闭失败")
            }
        )
    }

    val onUploadClicked = {
        val map = mutableMapOf<String, Int>()
        if(temperature.toIntOrNull() != null) map["mtemp"] = temperature.toInt()
        if(humidity.toIntOrNull() != null) map["mhumi"] = humidity.toInt()
        if(brightness.toIntOrNull() != null) map["LightLuminance"] = brightness.toInt()
        log("发送上传信息$map")
        Mqtt.uploadData(map,
            onSuccess = {
                log("上传成功")
            },
            onFailure = {
                log("上传失败")
            }
        )
    }

    MaterialTheme {
        IotContent(
            modifier = Modifier,
            board = board,
            temperature = temperature,
            onTemperatureChanged = { temperature = it },
            humidity = humidity,
            onHumidityChanged = { humidity = it },
            brightness = brightness,
            onBrightnessChanged = { brightness = it },
            onConnectClicked = onConnectClicked,
            onOpenClicked = onOpenClicked,
            onCloseClicked = onCloseClicked,
            onUploadClicked = onUploadClicked,
            isSwitchOpen = isSwitchOpen,
            isConnected = isConnected)
    }
}

fun main() = application {
    Window(title = "MainPage",
        state = rememberWindowState(
            position = WindowPosition(alignment = Alignment.Center),
        ),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
