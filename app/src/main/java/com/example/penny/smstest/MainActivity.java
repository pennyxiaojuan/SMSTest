package com.example.penny.smstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView sender;
    private TextView content;
    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.content);
        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        MessageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver,receiveFilter);
    }
    @Override
    protected void onDestory(){
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }
    /**
     * 创建一个广播接收器来接收系统发出的短信广播。在MainActivity新建MessageReceive内部类继承自
     * BroadcastReceiver,并在onReceive()方法中编写获取短信数据的逻辑
     */
    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle bundle = intent.getExtras();//从Intent参数中取出了一个Bundle对象
            Object[] puds = (Object[]) bundle.get("pdus");//提取短信消息，使用pdu密钥
            SmsManager[] messages = new SmsManager[pdus.length];

            for (int i= 0; i<messages.length; i++){
                messages[i] = SmsManager.createFrompdu((byte[]) pdus[i]);
            }
            SmsManager address = messages[0].getOriginatingAddress();//获取发送方号码
            String fullMessage = "";

            for (SmsManager message : messages){
                fullMessage += message.getmessageBody();//获取短信内容
            }
            sender.setText(address);
            content.setText(fullMessage);
        }
    }
}
