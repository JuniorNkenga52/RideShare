package com.app.rideWhiz.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.ChatAdapterAdapter;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class WebSocketActivity extends AppCompatActivity {
    WebSocketClient mWebSocketClient;
    EditText editText;
    Button btnSend;

    private ListView mChatLv;
    ChatAdapterAdapter mAdapter;

    ArrayList<String> mlist;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.websocket_activity);
        connectWebSocket();
        mlist=new ArrayList<>();

        mAdapter=new ChatAdapterAdapter(this,mlist);
        mChatLv=(ListView)findViewById(R.id.chatlv);
        mChatLv.setAdapter(mAdapter);

        editText = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_Send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()) {
                    sendMessage(editText.getText().toString());
                }
            }
        });

    }

    private void connectWebSocket() {
        URI uri;
        try {
            //"ws://192.168.0.30:8090/websocketnew/php-socket.php"
            //ws://18.218.151.202:8090/ride-share-websocket/php-socket.php
                //uri = new URI("ws://www.myridewhiz.com:8090/ride-share-websocket/php-socket.php");
            uri = new URI("ws://18.218.151.202:8090/ride-share-websocket/php-socket.php");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                try{

                    final JSONObject jobj=new JSONObject(s);

                    if(!jobj.getString("message_type").equals("chat-connection-ack"))
                    {
                        if(!jobj.getString("chat_message").equals("null") && jobj.getString("sender_user").equals("1"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        mAdapter.updatelist(jobj.getString("username")+" - "+jobj.getString("chat_message"));
                                        mChatLv.setSelection(mAdapter.getCount() - 1);
                                    }catch (Exception e){

                                    }
                                }
                            });

                        }
                    }

                }catch (Exception e){
                    Log.d("error",e.toString());
                }

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();

    }

    public void sendMessage(String message) {
        try {
            JSONObject jmessage = new JSONObject();
            jmessage.put("chat_message", message);
            jmessage.put("chat_user", "Dhaiyur");
            jmessage.put("sender_user","1");
            jmessage.put("message_type", "chat-box-html");
            jmessage.put("message_new", "");
            mWebSocketClient.send(jmessage.toString());
            editText.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
