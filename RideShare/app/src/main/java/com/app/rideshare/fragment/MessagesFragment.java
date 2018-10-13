package com.app.rideshare.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.rideshare.R;
import com.app.rideshare.adapter.ChatAdapter;
import com.app.rideshare.api.xmpp.ConnectorApi;
import com.app.rideshare.chat.CommonMethods;
import com.app.rideshare.chat.MessageModel;
import com.app.rideshare.chat.MyService;
import com.app.rideshare.model.MatchedUser;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.DateUtils;
import com.app.rideshare.utils.MessageUtils;

import org.jivesoftware.smack.chat.Chat;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MessagesFragment extends Fragment {


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

    private MatchedUser selChatUser;//todo set the data according to my data

    public static Chat newChat;

    private Typeface latoBoldFont;

    private User user;


    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

//        String selUser = getArguments().getString("SelectedChatUser");
//        selChatUser = new Gson().fromJson(selUser, MatchedUser.class);
////
////        if(selChatUser.getUser_profile_status().equals("1") && selChatUser.getMatched_profile_status().equals("1"))
////            FragmentUtils.setActionBarTitle(getActivity(), selChatUser.getName(), true);
////        else
////            FragmentUtils.setActionBarTitle(getActivity(), selChatUser.getFname(), true);
////
////        ImageView btn_toolbar_right = (ImageView) getActivity().findViewById(R.id.btn_toolbar_right);
////        btn_toolbar_right.setVisibility(View.VISIBLE);
////        btn_toolbar_right.setImageResource(R.drawable.user_profile);
////        btn_toolbar_right.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////
////                Intent ii = new Intent(getActivity(), ProfileDetail.class);
////                ii.putExtra("Detail",new Gson().toJson(selChatUser));
////                startActivity(ii);
////                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
////            }
////        });
//        PrefUtils.initPreference(getActivity());
//
//        random = new Random();
//
//        user = PrefUtils.getUserInfo();
//        senderUser = user.getmUserId();
//
//        latoBoldFont = TypefaceUtils.getTypefaceRobotoMediam(getActivity());
//
//        selChatUser.setFullJid(selChatUser.getMatched_user_fb_id());
//        HomeNewActivity.currentChat = selChatUser.getMatched_user_fb_id();
//
//        if(AppUtils.isInternetAvailable(getActivity()))
//            new AsyncUserPresence().execute();
//        else
//            MessageUtils.showNoInternetAvailable(getActivity());
//
//        list_messages = (ListView) view.findViewById(R.id.list_messages);
//
//        edt_chat_msg = (EditText) view.findViewById(R.id.edt_chat_msg);
//        edt_chat_msg.setTypeface(latoBoldFont);
//
//        btn_chat_send = (ImageView) view.findViewById(R.id.btn_chat_send);
//        btn_chat_send.setOnClickListener(clickIt);
//
//        edt_chat_msg.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (newChat != null) {
//                    try {
//                        Message msg = new Message();
//                        msg.setBody(null);
//                        msg.addExtension(new ChatStateExtension(ChatState.composing));
//                        newChat.sendMessage(msg);
//                    } catch (Exception ignore) {
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
////        img_chat_smiley = (ImageView) view.findViewById(R.id.img_chat_smiley);
////        img_chat_smiley.setOnClickListener(clickIt);
//        //img_chat_attachment = (ImageView) view.findViewById(R.id.img_chat_attachment);
//        //img_chat_attachment.setOnClickListener(clickIt);
//
//        btn_chat_send = (ImageView) view.findViewById(R.id.btn_chat_send);
//        btn_chat_send.setOnClickListener(clickIt);
//
//        listAllMsg = new ArrayList<MessageModel>();
//        chatAdapter = new ChatAdapter(getActivity(), listAllMsg);
//
//        if (isTableExists(selChatUser.getMatched_user_fb_id()))
//            loadDataFromLocal(selChatUser.getMatched_user_fb_id());
//
//        list_messages.setAdapter(chatAdapter);

        return view;
    }

    private View.OnClickListener clickIt = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_chat_send:
                    if (AppUtils.isInternetAvailable(getActivity())) {
                        sendMessage();
                        btn_chat_send.requestFocus();
                    } else
                        MessageUtils.showNoInternetAvailable(getActivity());

                    break;
            }
        }
    };


    public void sendMessage() {

        if (edt_chat_msg.getText().toString().isEmpty()) {
            MessageUtils.showFailureMessage(getActivity(), getResources().getString(R.string.txt_msg_warning));
            return;
        }

        String message = edt_chat_msg.getEditableText().toString();

        if (!message.equalsIgnoreCase("")) {

            edt_chat_msg.setText("");

            MessageModel msg = new MessageModel();
            msg.setSender(senderUser);
            msg.setReceiver(selChatUser.getMatched_user_fb_id());
            msg.setMessageText(message);
            msg.setType(MessageModel.MEG_TYPE_TEXT);
            msg.setIsMine(true);
            msg.setMsgIdl(random.nextInt(1000));
            msg.setTime(DateUtils.getCurrentDate("hh:mm a"));

            chatAdapter.add(msg);

            chatAdapter.notifyDataSetChanged();

            MyService.xmpp.sendMessage(msg);

            CommonMethods commonMethods = new CommonMethods(getActivity());

            commonMethods.createTable(selChatUser.getMatched_user_fb_id());

            commonMethods.insertIntoTable(selChatUser.getMatched_user_fb_id(), senderUser, selChatUser.getMatched_user_fb_id(), message, "m", MessageModel.MEG_TYPE_TEXT, msg.getTime(),"false");

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
        mydb = getActivity().openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);
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
        mydb = getActivity().openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);
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

    }

    @Override
    public void onResume() {
        super.onResume();
//        HomeNewActivity.currentChat = selChatUser.getMatched_user_fb_id();
    }

    @Override
    public void onPause() {
        super.onPause();
//        HomeNewActivity.currentChat = "";
    }

    private class AsyncUserPresence extends AsyncTask<Object, Void, String> {

        protected String doInBackground(Object... params) {

            try {

                String presInfo = ConnectorApi.getUserPresence(selChatUser.getMatched_user_fb_id());

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();

                is.setCharacterStream(new StringReader(presInfo));

                Document doc = db.parse(is);

                NodeList nl = doc.getElementsByTagName("presence");

                return nl.item(0).getAttributes().getNamedItem("from").getNodeValue();
            } catch (Exception e) {
                return selChatUser.getMatched_user_fb_id();
            }
        }

        @Override
        protected void onPostExecute(String fullJid) {
            super.onPostExecute(fullJid);
            selChatUser.setFullJid(fullJid);
        }
    }
}