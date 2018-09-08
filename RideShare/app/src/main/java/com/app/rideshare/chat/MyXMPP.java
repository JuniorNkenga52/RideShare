package com.app.rideshare.chat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.rideshare.R;
import com.app.rideshare.activity.ChatActivity;
import com.app.rideshare.activity.HomeNewActivity;
import com.app.rideshare.utils.DateUtils;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class MyXMPP implements PingFailedListener {

    private static final String NOTIF_CHANNEL_ID = "practice_chat_channel";

    private static final String TAG = "MyXMPP";
    //http://chat.myridewhiz.com:9090
    //private static final String DOMAIN = "13.58.7.10";
    //private static final String DOMAIN = "chat.myridewhiz.com";
    //private static final String DOMAIN = "ec2-18-218-151-202.us-east-2.compute.amazonaws.com";
    //private static final String DOMAIN = "win-2i67mca8hqp";
    //private static final String DOMAIN = "http://192.168.0.30";
    //http://ec2-18-218-151-202.us-east-2.compute.amazonaws.com:9090
    private static final String DOMAIN = "ec2-18-218-151-202.us-east-2.compute.amazonaws.com";
    //private static final String DOMAIN = " http://18.222.137.245";
    //private static final String DOMAIN = "192.168.0.30";
    private static final String RESOURCE_NAME = "RideShare";
    private static final int PORT = 5222;
    //private static final int PORT = 9090;

    private final String delimiter = "\\@";

    private static XMPPTCPConnection connection;
    private Chat myChat;

    private static MyXMPP instance = null;
    private MyService context;

    private static boolean instanceCreated = false;
    private static boolean connected = false;
    private static boolean isConnecting = false;
    private static boolean isToasted = true;

    private boolean loggedIn = false;
    private boolean chatCreated = false;

    private static String loginUser;
    private static String passwordUser;

    private ChatManagerListenerImpl mChatManagerListener;
    private MMessageListener mMessageListener;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MyXMPP() {
    }

    private MyXMPP(MyService context, String lUser, String lpassword) {

        this.context = context;

        loginUser = lUser;
        passwordUser = lpassword;

        mMessageListener = new MMessageListener();
        mChatManagerListener = new ChatManagerListenerImpl();

        PrefUtils.initPreference(context);

        initConnection();
    }

    public XMPPTCPConnection getConnection() {
        return connection;
    }

    public static MyXMPP getInstance(MyService context, String user) {

        if (instance == null) {
            instance = new MyXMPP(context, user, user);
            instanceCreated = true;
        }

        return instance;
    }

    private void initConnection() {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(DOMAIN);
        config.setHost(DOMAIN);
        config.setPort(PORT);
        config.setDebuggerEnabled(true);
        config.setResource(RESOURCE_NAME);
        config.setUsernameAndPassword(loginUser, passwordUser);
        config.setCompressionEnabled(false);

        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);

        connection = new XMPPTCPConnection(config.build());

        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);

        configure();

        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.registerPingFailedListener(this);

        FileTransferNegotiator.getInstanceFor(connection);
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    public void connect(final String caller) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {

                if (connection.isConnected()) {
                    return false;
                }
                isConnecting = true;
                /*if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            MessageUtils.showSuccessMessage(context, caller + "=>connecting....");
                        }
                    });*/

                Log.d(TAG, "Connect() Function" + caller + "=>connecting....");

                try {

                    connection.connect();

                    DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);
                    dm.autoAddDeliveryReceiptRequests();
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid, final String toid, final String msgid, final Stanza packet) {
                            Log.d("Delivered", ">>>>>>>>>>>>");
                        }
                    });

                    connected = true;

                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                MessageUtils.showFailureMessage(context, "(" + caller + ")" + "IOException: ");
                            }
                        });

                    Log.e(TAG, "(" + caller + ")" + "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            MessageUtils.showFailureMessage(context, "(" + caller + ")" + "SMACKException: ");
                        }
                    });

                    Log.e(TAG, "(" + caller + ")" + "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                MessageUtils.showFailureMessage(context, "(" + caller + ")" + "XMPPException: ");
                            }
                        });
                    Log.e(TAG, "connect(" + caller + ")" + "XMPPException: " + e.getMessage());
                }

                return isConnecting = false;
            }
        };

        connectionThread.execute();
    }

    private void login() {

        try {

            Log.e(TAG, "login: " + loginUser + "\n" + passwordUser);
            connection.login(loginUser, passwordUser);

            try {
                Presence presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.available);
                presence.setPriority(1);
                presence.setStatus("Online");
                connection.sendPacket(presence);
                /*ReadReceipt read = new ReadReceipt(messagePacketID);
                message.addExtension(read);
                connection.sendPacket(sendReadStatus);*/
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Yey! We're connected to the Xmpp server!");
            loggedIn = true;

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pingFailed() {

    }

    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final Chat chat, final boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);
        }
    }

    public void sendMessage(MessageModel chatMessage) {

        if (!chatCreated) {
            myChat = ChatManager.getInstanceFor(connection).createChat(chatMessage.getReceiver() + "@" + connection.getServiceName(), mMessageListener);
            chatCreated = true;
        }

        final Message message = new Message();


        message.setBody(chatMessage.getMessageText());
        message.setStanzaId(String.valueOf(chatMessage.getMsgIdl()));
        message.setType(Message.Type.chat);

        try {
            if (connection.isAuthenticated())
                myChat.sendMessage(message);
            else
                login();
        } catch (NotConnectedException e) {
            Log.e(TAG, "xmpp.SendMessage() msg Not sent!-Not Connected!");
        } catch (Exception e) {
            Log.e(TAG, "xmpp.SendMessage()-Exception msg Not sent!" + e.getMessage());
        }
    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d(TAG, "xmpp Connected!");

            connected = true;

            if (!connection.isAuthenticated()) {
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void connectionClosed() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        MessageUtils.showFailureMessage(context, "Connection Closed!");
                    }
                });

            Log.d(TAG, "xmpp ConnectionClosed!");

            connected = false;
            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MessageUtils.showFailureMessage(context, "ConnectionClosedOn Error!!");
                    }
                });

            Log.d(TAG, "xmpp ConnectionClosedOn Error!");

            connected = false;

            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d(TAG, "xmpp Reconnectingin " + arg0);
            loggedIn = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MessageUtils.showFailureMessage(context, "Reconnection Failed!");
                    }
                });

            Log.d(TAG, "xmpp ReconnectionFailed!");

            connected = false;

            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        MessageUtils.showSuccessMessage(context, "Reconnected!");
                    }
                });

            Log.d(TAG, "xmpp ReconnectionSuccessful");

            connected = true;

            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {

            Log.d(TAG, "xmpp Authenticated!");

            loggedIn = true;

            ChatManager.getInstanceFor(connection).addChatListener(mChatManagerListener);

            chatCreated = false;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        MessageUtils.showSuccessMessage(context, "Connected!");
                    }
                });
        }
    }

    private class MMessageListener implements ChatMessageListener {

        @Override
        public void processMessage(final Chat chat, final Message message) {

            Log.e(TAG, "MyXMPP_MESSAGE_LISTENER message received: " + message);

            if (message.getType() == Message.Type.chat && message.getBody() != null)
                processMessage(message);
        }

        private void processMessage(final Message message) {

            String sender1 = message.getFrom();
            String receiver = message.getTo();
            final Random random = new Random();

            String[] temp = sender1.split(delimiter);
            String[] temp1 = receiver.split(delimiter);
            final String sender = temp[0].toLowerCase();
            Log.d("USERS" + sender, temp1[0]);

            final MessageModel messageModel = new MessageModel();
            messageModel.setIsMine(false);
            messageModel.setMsgIdl(random.nextInt(1000));
            messageModel.setType(MessageModel.MEG_TYPE_TEXT);
            messageModel.setReceiver(temp1[0]);
            messageModel.setSender(sender);
            messageModel.setMessageText(message.getBody());
            messageModel.setTime(DateUtils.getCurrentDate("hh:mm a"));

            CommonMethods commonMethods = new CommonMethods(context);
            commonMethods.createTable(sender);
            commonMethods.insertIntoTable(messageModel.getSender(), messageModel.getSender(), messageModel.getReceiver(),
                    messageModel.getMessageText(), "r", MessageModel.MEG_TYPE_TEXT, messageModel.getTime());

            try {

                if (HomeNewActivity.currentChat != null && HomeNewActivity.currentChat.length() > 0
                        && HomeNewActivity.currentChat.equalsIgnoreCase(messageModel.getSender())) {

                    ChatActivity.listAllMsg.add(messageModel);

                    Log.e(TAG, "ChatActivity.listAllMsg >> " + ChatActivity.listAllMsg.size());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            ChatActivity.chatAdapter.notifyDataSetChanged();

                            ChatActivity.list_messages.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Select the last row so it will scroll into view...
                                    ChatActivity.list_messages.setSelection(ChatActivity.listAllMsg.size() - 1);
                                }
                            });
                        }
                    });

                } else {
                    sendNotification(message.getBody());
                }
            } catch (Exception e) {
                sendNotification(message.getBody());
            }
        }
    }

    private void configure() {

        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        sdm.addFeature("http://jabber.org/protocol/disco#info");
        sdm.addFeature("jabber:iq:privacy");

        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider()); // Service Discovery # Items
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider()); // Service Discovery # Info
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider()); // MUC Admin
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider()); // MUC Owner

        ProviderManager.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider()); // Offline Message Requests
        ProviderManager.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider()); // SharedGroupsInfo
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider()); // FileTransfer
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());

        ProviderManager.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider()); // Private Data Storage
        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider()); // VCard
        ProviderManager.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider()); // Last Activity
        ProviderManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider()); // User Search
        ProviderManager.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider()); // Privacy

        // ProviderManager.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());
        // ProviderManager.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());

        try {
            ProviderManager.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));  // Time
        } catch (ClassNotFoundException e) {
            Log.w("Practice Client", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        try {
            ProviderManager.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version")); // Version
        } catch (ClassNotFoundException e) {
            Log.w("Practice Client", "Can't load class for org.jivesoftware.smackx.packet.Version");
        }

        ProviderManager.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider()); // Chatting State
        ProviderManager.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider()); // XHTML
        ProviderManager.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider()); // MUC User
        ProviderManager.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider()); // Offline Message Indicator
        ProviderManager.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider()); // JEP-33: Extended Stanza Addressing
        ProviderManager.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        ProviderManager.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        ProviderManager.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        ProviderManager.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        ProviderManager.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());

        ProviderManager.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider()); // Delayed Delivery
        ProviderManager.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider()); // Group Chatting Invitations
        ProviderManager.addExtensionProvider("x", "jabber:x:data", new DataFormProvider()); // Data Forms

        // ProviderManager.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());
        // ProviderManager.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider()); // Roster Exchange
        // ProviderManager.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider()); // Message Events
    }

    private void sendNotification(String message) {

        try {

            String title = context.getString(R.string.app_name);
            int currenttime = (int) System.currentTimeMillis();
            Bitmap mainIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            long when = Calendar.getInstance().getTimeInMillis();
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                // Configure the notification channel.
                NotificationChannel notifChannel = new NotificationChannel(NOTIF_CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
                notifChannel.setDescription(message);
                notifChannel.enableLights(true);
                notifChannel.setLightColor(Color.MAGENTA);
                notifChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notifChannel.enableVibration(true);

                notificationManager.createNotificationChannel(notifChannel);
            }

            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, NOTIF_CHANNEL_ID);

            notifBuilder.setTicker(context.getResources().getString(R.string.app_name))
                    .setContentTitle(title).setContentText(message)
                    .setSmallIcon(R.drawable.ic_notification).setLargeIcon(mainIcon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false).setAutoCancel(true).setWhen(when).setSound(sound)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{0, 100, 100, 100, 100, 100});

            //notificationManager.notify(2403, notifBuilder.build()); //0 = ID of notification
            notificationManager.notify(2403, notifBuilder.build()); //0 = ID of notification

        } catch (Exception ignore) {
        }
    }
}