package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.adapter.ChatAdapter;
import com.app.rideshare.api.response.AcceptRider;
import com.app.rideshare.api.xmpp.ConnectorApi;
import com.app.rideshare.chat.CommonMethods;
import com.app.rideshare.chat.FileUtils;
import com.app.rideshare.chat.MessageModel;
import com.app.rideshare.chat.MyService;
import com.app.rideshare.model.Rider;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.Constant;
import com.app.rideshare.utils.DateUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.google.gson.Gson;

import org.jivesoftware.smack.XMPPException;
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

public class ChatActivity extends AppCompatActivity {


    private Random random;

    public static ChatAdapter chatAdapter;

    public static ArrayList<MessageModel> listAllMsg;

    public static ListView list_messages;

    private String senderUser;

    private SQLiteDatabase mydb;

    private EditText edt_chat_msg;
    private ImageView btn_chat_send;
    private ImageView img_chat_smiley;

    //private ImageView img_chat_attachment;

//    private User selChatUser;//todo set the data according to my data

    public static Chat newChat;

    private Typeface latoBoldFont;

    private User user;

    private ActionBar actionBar;
    private AcceptRider selChatUser;
    private Rider toRider; // other person detail with whom we are chatting
    private String toJabberId = "";
    RideShareApp mApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mApp = (RideShareApp) getApplicationContext();

        user = PrefUtils.getUserInfo();
        senderUser = user.getmUserId();

        Intent intent = getIntent();
        String selUser = "";
        if (intent != null)
            selChatUser = (AcceptRider) intent.getSerializableExtra(Constant.intentKey.SelectedChatUser);

        if (senderUser.equals(selChatUser.getFromRider().getnUserId())) {
            toRider = selChatUser.getToRider();
        } else {
            toRider = selChatUser.getFromRider();
        }

        Toolbar tbChatHeader = (Toolbar) findViewById(R.id.tbChatHeader);
        setSupportActionBar(tbChatHeader);
        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(""+toRider.getmFirstName() + " " + toRider.getmLastName());

        tbChatHeader.setTitleTextColor(getResources().getColor(R.color.white));
        tbChatHeader.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        PrefUtils.initPreference(getApplicationContext());

        random = new Random();

        latoBoldFont = TypefaceUtils.getTypefaceRobotoMediam(getApplicationContext());

        toJabberId = Constant.intentKey.jabberPrefix + toRider.getnUserId();
        HomeNewActivity.currentChat = toRider.getnUserId();

        if (AppUtils.isInternetAvailable(getApplicationContext()))
            new AsyncUserPresence().execute();
        else
            AppUtils.showNoInternetAvailable(ChatActivity.this);

        list_messages = (ListView) findViewById(R.id.list_messages);

        edt_chat_msg = (EditText) findViewById(R.id.edt_chat_msg);
        edt_chat_msg.setTypeface(latoBoldFont);

        btn_chat_send = (ImageView) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(clickIt);

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

//        img_chat_smiley = (ImageView) findViewById(R.id.img_chat_smiley);
//        img_chat_smiley.setOnClickListener(clickIt);
        //img_chat_attachment = (ImageView) findViewById(R.id.img_chat_attachment);
        //img_chat_attachment.setOnClickListener(clickIt);

        btn_chat_send = (ImageView) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(clickIt);

        listAllMsg = new ArrayList<MessageModel>();
        chatAdapter = new ChatAdapter(ChatActivity.this, listAllMsg);

        if (isTableExists(toRider.getnUserId()))
            loadDataFromLocal(toRider.getnUserId());

        list_messages.setAdapter(chatAdapter);

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
                        AppUtils.showNoInternetAvailable(ChatActivity.this);
                    break;
            }
        }
    };


    public void sendMessage() {

        if (edt_chat_msg.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.txt_msg_warning, Toast.LENGTH_SHORT).show();
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

            commonMethods.createTable(toRider.getnUserId());

            commonMethods.insertIntoTable(toRider.getnUserId(), senderUser, toRider.getnUserId(), message, "m", MessageModel.MEG_TYPE_TEXT, msg.getTime());

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

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase("chat", null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }

    public boolean isTableExists(String tableName) {
        mydb = openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = mydb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void loadDataFromLocal(String tablename) {
        String tblname = "'" + tablename + "'";
        boolean w = false;
        mydb = openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);
        Cursor allrows = mydb.rawQuery("SELECT * FROM " + tblname, null);
        System.out.println("COUNT : " + allrows.getCount());

        if (allrows.moveToFirst()) {

            do {

                String sender = allrows.getString(allrows.getColumnIndex("sender"));
                String receiver = allrows.getString(allrows.getColumnIndex("receiver"));
                String msg = allrows.getString(allrows.getColumnIndex("msg"));
                String who = allrows.getString(allrows.getColumnIndex("who"));
                String type = allrows.getString(allrows.getColumnIndex("type"));
                String time = allrows.getString(allrows.getColumnIndex("time"));

                if (who.equals("m"))
                    w = true;
                else if (who.equals("r"))
                    w = false;

                chatAdapter.add(new MessageModel(sender, receiver, msg, type, w, random.nextInt(1000), time));
            }
            while (allrows.moveToNext());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            // When an Image is picked
//            if (requestCode == 123 && resultCode == RESULT_OK && null != data) {
//                // Get the Image from data
//                final Uri selectedImage = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                // Get the cursor
//                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                // Move to first row
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String imgDecodableString = cursor.getString(columnIndex);
//                cursor.close();
//                final Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
//                new Handler(Looper.getMainLooper())
//                        .post(new Runnable() {
//                            @Override
//                            public void run() {
//                                //  try {
//                                String message = FileUtils.getFileName(selectedImage);
//
//                                final MessageModel chatMessage = new MessageModel();
//                                chatMessage.setMessageText(message);
//                                chatMessage.setSender(senderUser);
//                                chatMessage.setIsMine(true);
//                                chatMessage.setType(MessageModel.MEG_TYPE_IMAGE);
//                                chatMessage.setReceiver(toRider.getnUserId()());
//                                chatMessage.setMsgIdl(random.nextInt(1000));
//                                chatMessage.setTime(DateUtils.getCurrentDate("hh:mm a"));
//
//                                edt_chat_msg.setText("");
//                                listAllMsg.add(chatMessage);
//
//                                new SendPicture(bitmap, selectedImage, chatMessage).execute();
//
//                                chatAdapter.notifyDataSetChanged();
//
//                                list_messages.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // Select the last row so it will scroll into view...
//                                        list_messages.setSelection(listAllMsg.size());
//                                    }
//                                });
//
//                                //  String tablename, String s, String r, String m, String w,String datatype
//                                //commonMethods.insertIntoTable(selMessageHead.getJid(), selMessageHead.getJid(), senderUser, message, "m", MessageModel.MEG_TYPE_IMAGE);
//                                //MyService.xmpp.fileTransfer(selMessageHead.getJid() + "@win-2i67mca8hqp/Smack", bitmap, getFileName(selectedImage));
//                                // } catch (XMPPException e) {
//                                //   e.printStackTrace();
//                                //}
//                            }
//                        });
//
//            } else {
//                Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
//        }
    }

    public class SendPicture extends AsyncTask<Void, Void, Void> {
        private Uri uri;
        private Bitmap bitmap;
        private MessageModel chatMessage;
        private CommonMethods commonMethods;

        public SendPicture(Bitmap bitmap, Uri uri, MessageModel chatMessage) {
            this.bitmap = bitmap;
            this.uri = uri;
            this.chatMessage = chatMessage;
            this.commonMethods = new CommonMethods(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {

            FileUtils.createSentDirectoryAndSaveFile(bitmap, uri);

            try {

                commonMethods.createTable(toRider.getnUserId());
                commonMethods.insertIntoTable(toRider.getnUserId(), toRider.getnUserId(), senderUser, chatMessage.getMessageText(), "m", MessageModel.MEG_TYPE_IMAGE, chatMessage.getTime());

                String filePath = "";

                try {
                    filePath = FileUtils.getPath(getApplicationContext(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                MyService.xmpp.fileTransfer(selChatUser.getJabberId(), FileUtils.getFileName(uri), filePath);
                MyService.xmpp.fileTransfer(toJabberId, FileUtils.getFileName(uri), filePath);
            } catch (XMPPException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        HomeNewActivity.currentChat = toRider.getnUserId()();
    }

    @Override
    public void onPause() {
        super.onPause();
//        HomeNewActivity.currentChat = "";
    }

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
            actionBar.setSubtitle(fullJid);
//            selChatUser.setJabberId(fullJid);
        }
    }
}