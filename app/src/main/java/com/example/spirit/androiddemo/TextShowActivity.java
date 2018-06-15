package com.example.spirit.androiddemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spirit.androiddemo.fragment.SmsFragment;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.List;

public class TextShowActivity extends Activity implements View.OnClickListener {
    private ImageView ivExit;
    private TextView tvTitle;
    private EditText etBody;
    private ImageView ivSend;
    private EditText etInput;
    private SmsManager smsManager;
    private String address;
    private PendingIntent sentPI;
    private Button btnSend;
    private SmsBroadCast smsBroadCast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_show);
        initView();
        initVariable();
        initUI();
    }

    private void initUI() {
        ivExit.setColorFilter(Color.WHITE);
        ivExit.setOnClickListener(this);

        Intent intent = getIntent();
        String smsBody = intent.getStringExtra(ConstanceField.SMS_BODY);
        String topTitle = intent.getStringExtra(ConstanceField.TOP_TITLE);
        address = intent.getStringExtra(ConstanceField.ADDRESS);

        etBody.setText(String.format("%s", smsBody));
        tvTitle.setText(topTitle);

        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        smsBroadCast = new SmsBroadCast();
        IntentFilter filter = new IntentFilter(SENT_SMS_ACTION);
        registerReceiver(smsBroadCast, filter);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = etInput.getText().toString();
                etBody.append(s);
                etInput.setText("");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etBody.getText().toString();
                List<String> divideContents = smsManager.divideMessage(message);
                for (String text : divideContents) {
                    smsManager.sendTextMessage(address, null, text, sentPI, null);
                }
            }
        });
    }

    private void initVariable() {
        smsManager = SmsManager.getDefault();
    }

    private void initView() {
        ivExit = findViewById(R.id.iv_exit);
        tvTitle = findViewById(R.id.tv_title);
        etBody = findViewById(R.id.et_body);
        ivSend = findViewById(R.id.iv_send);
        etInput = findViewById(R.id.et_input);
        btnSend = findViewById(R.id.btn_send);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadCast);
    }

    class SmsBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Util.toast("短信发送成功");
                    SmsFragment fragment = (SmsFragment) DataUtil.getFragment(1);
                    fragment.updateSms();
                    finish();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Util.toast("短信发送失败");
                    break;
            }
        }
    }
}
