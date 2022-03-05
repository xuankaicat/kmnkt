import com.gitee.xuankaicat.communicate.aliyuniot.AliyunMqtt
import com.gitee.xuankaicat.communicate.aliyuniot.alink.AlinkBase
import com.gitee.xuankaicat.communicate.aliyuniot.alink.propertyPost
import com.gitee.xuankaicat.communicate.aliyuniot.alink.propertySet
import com.gitee.xuankaicat.communicate.aliyuniot.mqtt
import com.gitee.xuankaicat.communicate.open

object Mqtt {
    val mqtt = mqtt(AliyunMqtt(
        productKey = "",
        deviceName = "",
        deviceSecret = "",
        regionId = "cn-shanghai"
    ))

    fun connect(
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
        onLoss: () -> Unit = {},
    ) {
        mqtt.open {
            success {
                onSuccess()
            }
            failure {
                onFailure()
                false
            }
            loss {
                onLoss()
                false
            }
        }
    }

    fun subscribe(
        onReceive: (AlinkBase) -> Unit
    ) {
        mqtt.propertySet(onReceive = onReceive)
    }

    fun setSwitch(
        state: Boolean,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        mqtt.propertyPost(
            params = mapOf("LightSwitch" to if(state) 1 else 0),
            expectResponse = true
        ) {
            when(it.code) {
                200 -> onSuccess()
                else -> onFailure()
            }
        }
    }

    fun uploadData(
        data: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        mqtt.propertyPost(params = data,
            expectResponse = true
        ) {
            when(it.code) {
                200 -> onSuccess()
                else -> onFailure()
            }
        }
    }
}