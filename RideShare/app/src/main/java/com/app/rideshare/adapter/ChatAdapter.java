package com.app.rideshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.chat.MessageModel;
import com.app.rideshare.utils.TypefaceUtils;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private Activity activity;

    private ArrayList<MessageModel> chatMessageList;

    private Typeface latoRegularFont;

    public ChatAdapter(Activity activity, ArrayList<MessageModel> list) {
        chatMessageList = list;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        latoRegularFont = TypefaceUtils.getOpenSansRegular(activity);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageModel message = chatMessageList.get(position);

        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.item_messaging_chat, null);

        LinearLayout layout_chat_you;
        LinearLayout layout_chat_me;
        TextView txt_msg_time;
        TextView txt_msg_content;

        layout_chat_you = vi.findViewById(R.id.layout_chat_you);
        layout_chat_me = vi.findViewById(R.id.layout_chat_me);

        if (message.isMine()) {

            layout_chat_you.setVisibility(View.GONE);
            layout_chat_me.setVisibility(View.VISIBLE);

            txt_msg_time = (TextView) vi.findViewById(R.id.txt_msg_time_me);
            txt_msg_time.setTypeface(latoRegularFont);

            txt_msg_content = (TextView) vi.findViewById(R.id.txt_msg_content_me);
            txt_msg_content.setTypeface(latoRegularFont, Typeface.BOLD);

        } else {

            layout_chat_you.setVisibility(View.VISIBLE);
            layout_chat_me.setVisibility(View.GONE);

            txt_msg_time = vi.findViewById(R.id.txt_msg_time_you);
            txt_msg_time.setTypeface(latoRegularFont);

            txt_msg_content = vi.findViewById(R.id.txt_msg_content_you);
            txt_msg_content.setTypeface(latoRegularFont, Typeface.BOLD);
        }

        txt_msg_time.setText(message.getTime());

        txt_msg_content.setText(message.getMessageText());

        return vi;
    }

    public void add(MessageModel object) {
        chatMessageList.add(object);
    }
}