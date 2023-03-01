package pers.xuankai.udptestjava.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dylanc.longan.SystemBarsKt;
import com.gitee.xuankaicat.kmnkt.socket.IMqttSocket;
import com.gitee.xuankaicat.kmnkt.socket.ISocket;
import com.gitee.xuankaicat.kmnkt.socket.OnOpenCallback;
import com.gitee.xuankaicat.kmnkt.socket.utils.CharsetUtils;
import pers.xuankai.udptestjava.BaseActivity;
import pers.xuankai.udptestjava.databinding.ActivityMqttactivityBinding;

import java.nio.charset.Charset;

public class MQTTActivity extends BaseActivity<ActivityMqttactivityBinding> {
    private final IMqttSocket mqtt = IMqttSocket.getMQTT(s -> {
        s.setAddress("10.0.2.2");
        s.setPort(1883);
        CharsetUtils.setInCharset(s, Charset.forName("gb2312"));
        CharsetUtils.setOutCharset(s, Charset.forName("gb2312"));
        s.setUsername("siot");
        s.setPassword("siot");
        s.setInMessageTopic("DeviceTest/000000");
        s.setOutMessageTopic("DeviceTest/123456");
        s.open(new OnOpenCallback(s) {
            @Override
            public void success(@NonNull ISocket socket) {
                super.success(socket);
                socket.startReceive((result, ignore) -> {
                    getBinding().textView.setText(result);
                    return true;
                });
            }
        });
        return null;
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarsKt.immerseStatusBar(this, true, true);

        ActivityMqttactivityBinding binding = getBinding();

        binding.btn.setOnClickListener(v -> {
            String sendText = binding.editText.getText().toString();
            if(sendText.equals("")) return;

            mqtt.send(sendText);
            binding.textView.setText("等待数据...");
            mqtt.startReceive((result, ignore) -> {
                binding.textView.setText(result);
                return false;
            });
        });

        /*
          以一个收到status决定亮灭的小灯为例，按下发送开启时小灯亮，按下发送关闭时小灯灭
         */

        binding.btnOpen.setOnClickListener(v -> {
            String sendText = "{cmd:\"set\", status:1}";
            mqtt.send(sendText);
        });

        binding.btnClose.setOnClickListener(v -> {
            String sendText = "{cmd:\"set\", status:0}";
            mqtt.send(sendText);
        });

        binding.btnUdp.setOnClickListener(v -> {
            Intent intent = new Intent(MQTTActivity.this, UDPActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqtt.stopReceive();
        mqtt.close();
    }
}
