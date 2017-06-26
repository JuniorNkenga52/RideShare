package com.app.rideshare.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.rideshare.R;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketActivity extends AppCompatActivity {
    WebSocketClient mWebSocketClient;
    EditText editText;
    Button btnSend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.websocket_activity);
        connectWebSocket();

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
            uri = new URI("ws://192.168.0.30:8090/websocket/php-socket.php");
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
                Log.d("Websocket", s);
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
            jmessage.put("message_type", "chat-box-html");
            jmessage.put("message_new", "");
            mWebSocketClient.send(jmessage.toString());
            editText.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
