package pers.xuankai.udptestjava.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dylanc.longan.SystemBarsKt;

import java.nio.charset.Charset;

import com.gitee.xuankaicat.communicate.Communicate;
import pers.xuankai.udptestjava.BaseActivity;
import pers.xuankai.udptestjava.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private final Communicate communicate = Communicate.getUDP(c -> {
        c.setAddress("10.0.2.2");
        c.setPort(9000);
        c.setInCharset(Charset.forName("gb2312"));
        c.setOutCharset(Charset.forName("gb2312"));
        c.open();
        return null;
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarsKt.immerseStatusBar(this, true, true);

        ActivityMainBinding binding = getBinding();

        binding.btn.setOnClickListener(v -> {
            String sendText = binding.editText.getText().toString();
            if(sendText.equals("")) return;

            communicate.send(sendText);
            binding.textView.setText("等待数据...");
            communicate.startReceive((result, ignore) -> {
                binding.textView.setText(result);
                return false;
            });
        });

        binding.btnTcp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TCPActivity.class);
            startActivity(intent);
        });

        binding.btnMQTT.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MQTTActivity.class);
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
