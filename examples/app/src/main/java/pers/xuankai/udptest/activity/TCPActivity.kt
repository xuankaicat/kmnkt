package pers.xuankai.udptest.activity

import android.os.Bundle
import com.dylanc.longan.immerseStatusBar
import com.dylanc.longan.startActivity
import com.gitee.xuankaicat.kmnkt.socket.dsl.tcp
import pers.xuankai.udptest.BaseActivity
import pers.xuankai.udptest.databinding.ActivityTcpBinding
import java.nio.charset.Charset

class TCPActivity : BaseActivity<ActivityTcpBinding>() {
    private val socket = tcp {
        address = "10.0.2.2"
        port = 9000
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

            socket.send(sendText)
            binding.textView.text = "等待数据..."
            socket.startReceive { str, _ ->
                binding.textView.text = str
                return@startReceive false
            }
        }

        binding.btnUdp.setOnClickListener {
            startActivity<UDPActivity>()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.stopReceive()
        socket.close()
    }
}