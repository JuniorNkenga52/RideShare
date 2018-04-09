package com.app.rideshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.chat.MessageModel;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.widget.SquareImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
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
        SquareImageView img_msg_media;

        layout_chat_you = (LinearLayout) vi.findViewById(R.id.layout_chat_you);
        layout_chat_me = (LinearLayout) vi.findViewById(R.id.layout_chat_me);

        if (message.isMine()) {

            layout_chat_you.setVisibility(View.GONE);
            layout_chat_me.setVisibility(View.VISIBLE);

            txt_msg_time = (TextView) vi.findViewById(R.id.txt_msg_time_me);
            txt_msg_time.setTypeface(latoRegularFont);

            txt_msg_content = (TextView) vi.findViewById(R.id.txt_msg_content_me);
            txt_msg_content.setTypeface(latoRegularFont, Typeface.BOLD);

            img_msg_media = (SquareImageView) vi.findViewById(R.id.img_msg_media_me);

        } else {

            layout_chat_you.setVisibility(View.VISIBLE);
            layout_chat_me.setVisibility(View.GONE);

            txt_msg_time = (TextView) vi.findViewById(R.id.txt_msg_time_you);
            txt_msg_time.setTypeface(latoRegularFont);

            txt_msg_content = (TextView) vi.findViewById(R.id.txt_msg_content_you);
            txt_msg_content.setTypeface(latoRegularFont, Typeface.BOLD);

            img_msg_media = (SquareImageView) vi.findViewById(R.id.img_msg_media_you);
        }

        txt_msg_time.setText(message.getTime());

        if (message.getType().contains(MessageModel.MEG_TYPE_TEXT)) {

            txt_msg_content.setVisibility(View.VISIBLE);
            img_msg_media.setVisibility(View.GONE);

            txt_msg_content.setText(message.getMessageText());

        } else if (message.getType().contains(MessageModel.MEG_TYPE_IMAGE)) {

            txt_msg_content.setVisibility(View.GONE);
            img_msg_media.setVisibility(View.VISIBLE);

            String zipFile;

            if (message.isMine())
                zipFile = Environment.getExternalStorageDirectory() + "/Connecter/sent/" + message.getMessageText();
            else
                zipFile = Environment.getExternalStorageDirectory() + "/Connecter/Received/" + message.getMessageText();

            Picasso.with(activity).load(new File(zipFile)).into(img_msg_media);
        }

        return vi;
    }

    public void add(MessageModel object) {
        chatMessageList.add(object);
    }
}