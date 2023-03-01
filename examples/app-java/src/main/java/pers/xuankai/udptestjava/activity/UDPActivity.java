package pers.xuankai.udptestjava.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.dylanc.longan.SystemBarsKt;
import com.gitee.xuankaicat.kmnkt.socket.ISocket;
import com.gitee.xuankaicat.kmnkt.socket.UDP;
import com.gitee.xuankaicat.kmnkt.socket.utils.CharsetUtils;
import pers.xuankai.udptestjava.BaseActivity;
import pers.xuankai.udptestjava.databinding.ActivityMainBinding;

import java.nio.charset.Charset;

public class UDPActivity extends BaseActivity<ActivityMainBinding> {
    private final UDP socket = (UDP) ISocket.getUDP(s -> {
        s.setAddress("10.0.2.2");
        s.setPort(9000);
        CharsetUtils.setInCharset(s, Charset.forName("gb2312"));
        CharsetUtils.setOutCharset(s, Charset.forName("gb2312"));
        return s;
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarsKt.immerseStatusBar(this, true, true);

        ActivityMainBinding binding = getBinding();

        binding.btn.setOnClickListener(v -> {
            String sendText = binding.editText.getText().toString();
            if(sendText.equals("")) return;

            socket.send(1883, sendText);
            binding.textView.setText("等待数据...");
            socket.startReceive((result, ignore) -> {
                binding.textView.setText(result);
                return false;
            });
        });

        binding.btnTcp.setOnClickListener(v -> {
            Intent intent = new Intent(UDPActivity.this, TCPActivity.class);
            startActivity(intent);
        });

        binding.btnMQTT.setOnClickListener(v -> {
            Intent intent = new Intent(UDPActivity.this, MQTTActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.stopReceive();
        socket.close();
    }
}
