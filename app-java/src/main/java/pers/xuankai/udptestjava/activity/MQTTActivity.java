package pers.xuankai.udptestjava.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dylanc.longan.SystemBarsKt;
import com.gitee.xuankaicat.communicate.MQTTCommunicate;

import java.nio.charset.Charset;

import pers.xuankai.udptestjava.BaseActivity;
import pers.xuankai.udptestjava.databinding.ActivityMqttactivityBinding;

public class MQTTActivity extends BaseActivity<ActivityMqttactivityBinding> {
    private final MQTTCommunicate communicate = MQTTCommunicate.getMQTT(c -> {
        c.setAddress("192.168.3.22");
        c.setServerPort(1883);
        c.setInCharset(Charset.forName("gb2312"));
        c.setOutCharset(Charset.forName("gb2312"));
        c.setUsername("siot");
        c.setPassword("siot");
        c.setInMessageTopic("DeviceTest/000000");
        c.setOutMessageTopic("DeviceTest/123456");
        c.open();
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

            communicate.send(sendText);
            binding.textView.setText("等待数据...");
            communicate.startReceive(result -> {
                binding.textView.setText(result);
                return false;
            });
        });

        /*
          以一个收到status决定亮灭的小灯为例，按下发送开启时小灯亮，按下发送关闭时小灯灭
         */

        binding.btnOpen.setOnClickListener(v -> {
            String sendText = "{cmd:\"set\", status:1}";
            communicate.send(sendText);
        });

        binding.btnClose.setOnClickListener(v -> {
            String sendText = "{cmd:\"set\", status:0}";
            communicate.send(sendText);
        });

        binding.btnUdp.setOnClickListener(v -> {
            Intent intent = new Intent(MQTTActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        communicate.stopReceive();
        communicate.close();
    }
}
