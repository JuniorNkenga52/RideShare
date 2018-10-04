package com.app.rideshare.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.activity.MyGroupActivity;

public class CommonDialog {

    public static void shareInviteLinkDialog(final Activity activity, final String shareData, final int type) {

        final Dialog dialog = new Dialog(activity, R.style.InviteDialogAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite_share);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;

        dialog.getWindow().setAttributes(lp);

        LinearLayout llInviteEmail, llInviteSMS, llInviteCopy, llInviteMoreOption;
        llInviteEmail = dialog.findViewById(R.id.llInviteEmail);
        llInviteSMS = dialog.findViewById(R.id.llInviteSMS);
        llInviteCopy = dialog.findViewById(R.id.llInviteCopy);
        llInviteMoreOption = dialog.findViewById(R.id.llInviteMoreOption);

        llInviteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.composeEmail(activity, "", shareData);
                //dialog.cancel();
            }
        });

        llInviteSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", shareData);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    activity.startActivity(sendIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //dialog.cancel();
            }
        });

        llInviteCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("invite_link", shareData);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity,"Group invite link Copied successfully",Toast.LENGTH_SHORT).show();
                //dialog.cancel();
            }
        });

        llInviteMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareData);
                sendIntent.setType("text/plain");
                try {
                    activity.startActivity(sendIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //dialog.cancel();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (type == 0) {
                    Intent i = new Intent(activity, MyGroupActivity.class);
                    activity.startActivity(i);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    activity.finish();
                }
            }
        });

        dialog.show();
    }
}