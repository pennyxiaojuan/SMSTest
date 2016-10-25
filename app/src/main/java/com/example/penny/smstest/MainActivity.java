package com.example.penny.smstest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView sender;
    private TextView content;
    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;
    private EditText to;
    private EditText msgInput;
    private Button send;
    private IntentFilter sendFilter;
    private SendStatusReceiver sendStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.content);
        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver,receiveFilter);

        to = (EditText) findViewById(R.id.to);
        msgInput = (EditText) findViewById(R.id.msg_input);
        send = (Button) findViewById(R.id.send);

        sendFilter = new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver,sendFilter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();

                Intent sentIntent = new Intent("SENT_SMS_ACTION");
                PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this,0,sentIntent,0);
                smsManager.sendTextMessage(to.getText().toString(),null,msgInput.getText().toString(),pi,null);
                smsManager.sendTextMessage(to.getText().toString(),null,
                        msgInput.getText().toString(), null,null);

            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(messageReceiver);
        unregisterReceiver(sendStatusReceiver);
    }
    /**
     * 创建一个广播接收器来接收系统发出的短信广播。在MainActivity新建MessageReceive内部类继承自
     * BroadcastReceiver,并在onReceive()方法中编写获取短信数据的逻辑
     */
    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle bundle = intent.getExtras();//从Intent参数中取出了一个Bundle对象
            Object[] pdus = (Object[]) bundle.get("pdus");//提取短信消息，使用pdu密钥
            SmsMessage[] messages = new SmsMessage[pdus.length];

            for (int i= 0; i<messages.length; i++){
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String address = messages[0].getOriginatingAddress();//获取发送方号码
            String fullMessage = "";

            for (SmsMessage message : messages){
                fullMessage += message.getMessageBody();//获取短信内容
            }
            sender.setText(address);
            content.setText(fullMessage);
        }
    }

    class SendStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            if (getResultCode()== RESULT_OK){
                //发送短信成功
                Toast.makeText(context, "Send succeed", Toast.LENGTH_SHORT).show();
            }else{
                //发送短信失败
                Toast.makeText(context, "Send failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
