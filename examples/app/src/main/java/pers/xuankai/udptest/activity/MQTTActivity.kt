package pers.xuankai.udptest.activity

import android.os.Bundle
import com.dylanc.longan.immerseStatusBar
import com.dylanc.longan.startActivity
import com.gitee.xuankaicat.kmnkt.socket.dsl.mqtt
import com.gitee.xuankaicat.kmnkt.socket.open
import pers.xuankai.udptest.BaseActivity
import pers.xuankai.udptest.databinding.ActivityMqttactivityBinding
import java.nio.charset.Charset

class MQTTActivity : BaseActivity<ActivityMqttactivityBinding>() {
    private val mqtt = mqtt {
        address = "10.0.2.2"
        port = 1883
        inCharset = Charset.forName("gb2312")
        outCharset = Charset.forName("gb2312")
        username = "xuankai"
        password = "xuankai"
        inMessageTopic = "DeviceTest/111111"
        outMessageTopic = "DeviceTest/123456"
        open {
            success {
                it.startReceive { str, _ ->
                    binding.textView.text = str
                    return@startReceive true
                }
            }
            failure {
                Log.i("zyq","链接失败，1秒后重连")
                Thread.sleep(1000)
                //communicate.open(this)
                //开启连接失败时执行
                return@failure true//是否继续尝试连接
            }
            loss {
                Log.v("MQTT", "失去连接，尝试重连")
                return@loss true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        immerseStatusBar()

        binding.btn.setOnClickListener {
            val sendText = binding.editText.text.toString()
            if(sendText.isEmpty()) return@setOnClickListener

            mqtt.send(sendText)
            binding.textView.text = "等待数据..."
            mqtt.startReceive { str, _ ->
                binding.textView.text = str
                return@startReceive false
            }
        }

        /**
         * 以一个收到status决定亮灭的小灯为例，按下发送开启时小灯亮，按下发送关闭时小灯灭
         */

        binding.btnOpen.setOnClickListener {
            val sendText = "{cmd:\"set\", status:1}"
            mqtt.send(sendText)
        }

        binding.btnClose.setOnClickListener {
            val sendText = "{cmd:\"set\", status:0}"
            mqtt.send(sendText)
        }

        binding.btnUdp.setOnClickListener {
            startActivity<UDPActivity>()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqtt.stopReceive()
        mqtt.close()
    }
}