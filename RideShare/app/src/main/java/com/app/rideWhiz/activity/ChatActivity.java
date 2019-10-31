package com.app.rideWhiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.ChatAdapter;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.api.xmpp.ConnectorApi;
import com.app.rideWhiz.chat.CommonMethods;
import com.app.rideWhiz.chat.MessageModel;
import com.app.rideWhiz.chat.MyService;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.DateUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.utils.TypefaceUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@SuppressLint("StaticFieldLeak")
public class ChatActivity extends AppCompatActivity {

    private Random random;

    public static ChatAdapter chatAdapter;

    public static ArrayList<MessageModel> listAllMsg;

    public static ListView list_messages;

    private String senderUser;

    private SQLiteDatabase myDb;

    private ImageView btn_chat_send;

    private EditText edt_chat_msg;

//    private User selChatUser;//todo set the data according to my data

    public static Chat newChat;

    private AcceptRider selChatUser;
    private Rider toRider; // other person detail with whom we are chatting
    private String toJabberId = "";
    RideShareApp mApp;
    public static Activity activity;
    public static String chat_sender_id="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mApp = (RideShareApp) getApplicationContext();

        activity=this;
        senderUser = PrefUtils.getUserInfo().getmUserId();

        Intent intent = getIntent();

        if (intent != null)
            selChatUser = (AcceptRider) intent.getSerializableExtra(Constants.intentKey.SelectedChatUser);


        if (senderUser.equals(selChatUser.getFromRider().getnUserId()))
            toRider = selChatUser.getToRider();
        else
            toRider = selChatUser.getFromRider();

        Toolbar tbChatHeader = findViewById(R.id.tbChatHeader);
        setSupportActionBar(tbChatHeader);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            chat_sender_id=toRider.getnUserId();
            actionBar.setTitle("" + toRider.getmFirstName() + " " + toRider.getmLastName());
        }


        tbChatHeader.setTitleTextColor(getResources().getColor(R.color.white));
        tbChatHeader.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        PrefUtils.initPreference(getApplicationContext());

        random = new Random();

        Typeface mRobotoMediam = TypefaceUtils.getTypefaceRobotoMedium(getApplicationContext());

        toJabberId = Constants.intentKey.jabberPrefix + toRider.getnUserId();
        toJabberId = toJabberId.toLowerCase();
//        HomeNewActivity.currentChat = toRider.getnUserId();
        HomeNewActivity.currentChat = toJabberId;

        if (AppUtils.isInternetAvailable(getApplicationContext()))
            new AsyncUserPresence().execute();
        else
            MessageUtils.showNoInternetAvailable(ChatActivity.this);

        list_messages = findViewById(R.id.list_messages);

        edt_chat_msg = findViewById(R.id.edt_chat_msg);
        edt_chat_msg.setTypeface(mRobotoMediam);

        edt_chat_msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (newChat != null) {
                    try {
                        Message msg = new Message();
                        msg.setBody(null);
                        msg.addExtension(new ChatStateExtension(ChatState.composing));
                        newChat.sendMessage(msg);
                    } catch (Exception ignore) {
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_chat_send = findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(clickIt);

        listAllMsg = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, listAllMsg);


        if (isTableExists(toJabberId)) {
            loadDataFromLocal(toJabberId);
            CommonMethods commonMethods = new CommonMethods(getApplicationContext());
            commonMethods.updateMessages(toJabberId);
        }
        list_messages.setAdapter(chatAdapter);

        try {
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.
                            NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private View.OnClickListener clickIt = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_chat_send:
                    if (AppUtils.isInternetAvailable(getApplicationContext())) {
                        sendMessage();
                        btn_chat_send.requestFocus();
                    } else
                        MessageUtils.showNoInternetAvailable(ChatActivity.this);
                    break;
            }
        }
    };


    public void sendMessage() {

        if (edt_chat_msg.getText().toString().isEmpty()) {
            MessageUtils.showFailureMessage(getApplicationContext(), getResources().getString(R.string.txt_msg_warning));
            return;
        }

        String message = edt_chat_msg.getEditableText().toString();

        if (!message.equalsIgnoreCase("")) {

            edt_chat_msg.setText("");

            MessageModel msg = new MessageModel();
            msg.setSender(senderUser);
            msg.setReceiver(toJabberId);
            msg.setMessageText(message);
            msg.setType(MessageModel.MEG_TYPE_TEXT);
            msg.setIsMine(true);
            msg.setMsgIdl(random.nextInt(1000));
            msg.setTime(DateUtils.getCurrentDate("hh:mm a"));

            chatAdapter.add(msg);

            chatAdapter.notifyDataSetChanged();

            MyService.xmpp.sendMessage(msg);

            CommonMethods commonMethods = new CommonMethods(getApplicationContext());

            commonMethods.createTable(toJabberId);

            commonMethods.insertIntoTable(toJabberId, senderUser, toJabberId, message, "m", MessageModel.MEG_TYPE_TEXT, msg.getTime(), "true");

            chatAdapter.notifyDataSetChanged();

            list_messages.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    list_messages.setSelection(listAllMsg.size());
                }
            });
        }
    }

    public boolean isTableExists(String tableName) {

        myDb = openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);

        Cursor cursor = myDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

        if (cursor != null) {

            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }

            cursor.close();
        }

        return false;
    }

    public void loadDataFromLocal(String tableName) {
        String tblName = "'" + tableName + "'";

        boolean w = false;

        myDb = openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);

        Cursor allRows = myDb.rawQuery("SELECT * FROM " + tblName, null);

        System.out.println("COUNT : " + allRows.getCount());

        if (allRows.moveToFirst()) {

            do {

                String sender = allRows.getString(allRows.getColumnIndex("sender"));
                String receiver = allRows.getString(allRows.getColumnIndex("receiver"));
                String msg = allRows.getString(allRows.getColumnIndex("msg"));
                String who = allRows.getString(allRows.getColumnIndex("who"));
                String type = allRows.getString(allRows.getColumnIndex("type"));
                String time = allRows.getString(allRows.getColumnIndex("time"));

                if (who.equals("m"))
                    w = true;
                else if (who.equals("r"))
                    w = false;

                chatAdapter.add(new MessageModel(sender, receiver, msg, type, w, random.nextInt(1000), time));
            }
            while (allRows.moveToNext());

            list_messages.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    list_messages.setSelection(listAllMsg.size());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeNewActivity.currentChat = Constants.intentKey.jabberPrefix + toRider.getnUserId();
    }

    @Override
    public void onPause() {
        super.onPause();
        HomeNewActivity.currentChat = "";
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncUserPresence extends AsyncTask<Object, Void, String> {

        protected String doInBackground(Object... params) {

            try {

                String presInfo = ConnectorApi.getUserPresence(toRider.getnUserId());

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();

                is.setCharacterStream(new StringReader(presInfo));

                Document doc = db.parse(is);

                NodeList nl = doc.getElementsByTagName("presence");

                return nl.item(0).getAttributes().getNamedItem("from").getNodeValue();
            } catch (Exception e) {
                return toRider.getnUserId();
            }
        }

        @Override
        protected void onPostExecute(String fullJid) {
            super.onPostExecute(fullJid);
//            actionBar.setSubtitle(fullJid);
//            selChatUser.setJabberId(fullJid);
        }
    }

    private void ClearNotification(){

    }
}