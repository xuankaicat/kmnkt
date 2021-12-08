package pers.xuankai.udptestjava.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dylanc.longan.SystemBarsKt;

import java.nio.charset.Charset;

import pers.xuankai.communicate.Communicate;
import pers.xuankai.udptestjava.BaseActivity;
import pers.xuankai.udptestjava.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private Communicate communicate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        communicate = Communicate.getUDP();
        communicate.setAddress("192.168.200.1");
        communicate.setServerPort(9000);
        communicate.setInCharset(Charset.forName("gb2312"));
        communicate.setOutCharset(Charset.forName("gb2312"));
        communicate.open();

        SystemBarsKt.immerseStatusBar(this, true, true);

        ActivityMainBinding binding = getBinding();

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

        binding.btnTcp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TCPActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        communicate.close();
    }
}
