package pers.xuankai.udptest.activity

import android.os.Bundle
import com.dylanc.longan.immerseStatusBar
import com.dylanc.longan.startActivity
import com.gitee.xuankaicat.communicate.Communicate
import pers.xuankai.udptest.BaseActivity
import pers.xuankai.udptest.databinding.ActivityMainBinding
import java.nio.charset.Charset

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val communicate = Communicate.UDP.apply {
        address = "10.0.2.2"
        port = 1883
        inCharset = Charset.forName("gb2312")
        outCharset = Charset.forName("gb2312")
        open()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        immerseStatusBar()

        binding.btn.setOnClickListener {
            val sendText = binding.editText.text.toString()
            if(sendText.isEmpty()) return@setOnClickListener

            communicate.send(sendText)
            binding.textView.text = "等待数据..."
            communicate.startReceive { str, _ ->
                binding.textView.text = str
                return@startReceive false
            }
        }

        binding.btnTcp.setOnClickListener {
            startActivity<TCPActivity>()
        }

        binding.btnMQTT.setOnClickListener {
            startActivity<MQTTActivity>()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        communicate.stopReceive()
        communicate.close()
    }
}