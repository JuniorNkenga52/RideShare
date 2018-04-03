package com.app.rideshare.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.app.rideshare.R;

public class CommonDialog {

    public static void shareInviteLinkDialog(final Activity activity, final String shareData) {
        final Dialog dialog = new Dialog(activity, R.style.InviteDialogAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite_share);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;

        dialog.getWindow().setAttributes(lp);

        LinearLayout llInviteEmail, llInviteSMS, llInviteCopy, llInviteMoreOption;
        llInviteEmail = (LinearLayout) dialog.findViewById(R.id.llInviteEmail);
        llInviteSMS = (LinearLayout) dialog.findViewById(R.id.llInviteSMS);
        llInviteCopy = (LinearLayout) dialog.findViewById(R.id.llInviteCopy);
        llInviteMoreOption = (LinearLayout) dialog.findViewById(R.id.llInviteMoreOption);

        llInviteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AppUtils.composeEmail(activity, "", shareData);
            }
        });

        llInviteSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("sms_body", shareData);
                sendIntent.setType("vnd.android-dir/mms-sms");
                activity.startActivity(sendIntent);
            }
        });

        llInviteCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("invite_link", shareData);
                clipboard.setPrimaryClip(clip);
            }
        });

        llInviteMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareData);
                sendIntent.setType("text/plain");
                activity.startActivity(sendIntent);
            }
        });

        dialog.show();
    }
}
