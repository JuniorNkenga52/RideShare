package com.app.rideshare.chat;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.app.rideshare.R;
import com.app.rideshare.activity.ChatActivity;
import com.app.rideshare.activity.HomeNewActivity;
import com.app.rideshare.fragment.MessagesFragment;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.DateUtils;
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
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyXMPP implements PingFailedListener {

    private static final String TAG = "MyXMPP";
    private static final String DOMAIN = "192.168.0.30";
    private static final String RESOURCE_NAME = "Connecter";
    private static final int PORT = 5222;
//    private static final int PORT = 9090;

    private final String delimiter = "\\@";

    private static XMPPTCPConnection connection;
    private Chat myChat;

    private static MyXMPP instance = null;
    private MyService context;

    private static byte[] dataReceived;

    private static boolean instanceCreated = false;
    private static boolean connected = false;
    private static boolean isConnecting = false;
    private static boolean isToasted = true;

    private boolean loggedIn = false;
    private boolean chatCreated = false;

    private static String loginUser;
    private static String passwordUser;

    private FileTransferManager manager;
    private ChatManagerListenerImpl mChatManagerListener;
    private MMessageListener mMessageListener;

    private User user;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MyXMPP() {}

    public MyXMPP(MyService context, String logiUser, String password) {

        this.context = context;

        this.loginUser = logiUser;
        this.passwordUser = password;

        mMessageListener = new MMessageListener(context);
        mChatManagerListener = new ChatManagerListenerImpl();

        PrefUtils.initPreference(context);

        user = PrefUtils.getUserInfo();

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
//        config.setPort(PORT);
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

        manager = FileTransferManager.getInstanceFor(connection);
        manager.addFileTransferListener(new FileTransferIMPL());

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

                if (connection.isConnected())
                    return false;

                isConnecting = true;

                /*if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, caller + "=>connecting....", Toast.LENGTH_LONG).show();
                        }
                    });*/

                Log.d(TAG, "Connect() Function" + caller + "=>connecting....");

                try {

                    connection.connect();

                    DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid, final String toid, final String msgid, final Stanza packet) {

                        }
                    });

                    connected = true;

                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(context, "(" + caller + ")" + "IOException: ", Toast.LENGTH_SHORT).show();
                            }
                        });

                    Log.e(TAG, "(" + caller + ")"+ "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, "(" + caller + ")" + "SMACKException: ", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e(TAG, "(" + caller + ")"+ "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(context, "(" + caller + ")" + "XMPPException: ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    Log.e(TAG, "connect(" + caller + ")"+ "XMPPException: " + e.getMessage());
                }

                return isConnecting = false;
            }
        };

        connectionThread.execute();
    }

    public void login() throws Exception {

        try {

            Log.e(TAG, "login: " + loginUser + "\n" + passwordUser);
            connection.login(loginUser, passwordUser);

            try {
                Presence presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.available);
                presence.setPriority(1);
                presence.setStatus("Online");
                connection.sendPacket(presence);
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
            Log.e(TAG,"xmpp.SendMessage() msg Not sent!-Not Connected!");
        } catch (Exception e) {
            Log.e(TAG,"xmpp.SendMessage()-Exception msg Not sent!" + e.getMessage());
        }
    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d(TAG,"xmpp Connected!");

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
                        Toast.makeText(context, "ConnectionCLosed!", Toast.LENGTH_SHORT).show();
                    }
                });

            Log.d(TAG,"xmpp ConnectionClosed!");

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
                        Toast.makeText(context, "ConnectionClosedOn Error!!", Toast.LENGTH_SHORT).show();
                    }
                });

            Log.d(TAG,"xmpp ConnectionClosedOn Error!");

            connected = false;

            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d(TAG,"xmpp Reconnectingin " + arg0);
            loggedIn = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "ReconnectionFailed!", Toast.LENGTH_SHORT).show();
                    }
                });

            Log.d(TAG,"xmpp ReconnectionFailed!");

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
                        Toast.makeText(context, "REConnected!", Toast.LENGTH_SHORT).show();
                    }
                });

            Log.d(TAG,"xmpp ReconnectionSuccessful");

            connected = true;

            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {

            Log.d(TAG,"xmpp Authenticated!");

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
                        Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private class MMessageListener implements ChatMessageListener {

        public MMessageListener(Context context) {
        }

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
            final String sender = temp[0];
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

            // TODO: 4/6/2018  
            try {

                if (HomeNewActivity.currentChat != null && HomeNewActivity.currentChat.length() > 0
                        && HomeNewActivity.currentChat.equals(messageModel.getSender())) {

                    ChatActivity.listAllMsg.add(messageModel);

                    Log.e(TAG,"ChatActivity.listAllMsg >> "+ChatActivity.listAllMsg.size());
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

    public class FileTransferIMPL implements FileTransferListener {

        @Override
        public void fileTransferRequest(final FileTransferRequest request) {

            final IncomingFileTransfer transfer = request.accept();

            try {

                InputStream is = transfer.recieveFile();

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                int nRead;

                byte[] buf = new byte[1024];

                try {
                    while ((nRead = is.read(buf, 0, buf.length)) != -1) {
                        os.write(buf, 0, nRead);
                    }
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                dataReceived = os.toByteArray();

                FileUtils.createRecDirectoryAndSaveFile(dataReceived, request.getFileName());

                Log.i("File Received", transfer.getFileName());

                processMessage(request);

            } catch (XMPPException ex) {
                Logger.getLogger(MyXMPP.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SmackException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(final FileTransferRequest request) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {

                Log.i("MSG RECE", "LOOPER");

                Random random = new Random();

                int iend = request.getRequestor().lastIndexOf("@");
                String requester = request.getRequestor().substring(0, iend);

//                String rec = user.getFacebookId();
                String rec = user.getmUserId();

                final MessageModel chatMessage = new MessageModel();
                chatMessage.setSender(requester);
                chatMessage.setType(MessageModel.MEG_TYPE_IMAGE);
                chatMessage.setReceiver(rec);
                chatMessage.setMsgIdl(random.nextInt(1000));
                chatMessage.setIsMine(false);
                chatMessage.setMessageText(request.getFileName());
                chatMessage.setTime(DateUtils.getCurrentDate("hh:mm a"));

                CommonMethods commonMethods = new CommonMethods(context);
                commonMethods.createTable(requester);
                commonMethods.insertIntoTable(requester, requester, rec, request.getFileName(), "r",
                        MessageModel.MEG_TYPE_IMAGE, chatMessage.getTime());

                // TODO: 4/6/2018  
                try {

                    if (HomeNewActivity.currentChat != null && HomeNewActivity.currentChat.length() > 0
                            && HomeNewActivity.currentChat.equals(chatMessage.getSender())) {

                        ChatActivity.listAllMsg.add(chatMessage);

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
                        sendNotification("Photo: " + chatMessage.getMessageText());
                    }
                } catch (Exception e) {
                    sendNotification("Photo: " + chatMessage.getMessageText());
                }
            }
        });
    }

    public void fileTransfer(String user, String filename, String file) throws XMPPException {

        //Roster roster = Roster.getInstanceFor(connection);
        //Presence presence = roster.getPresence(user);
        //String destination = presence.getFrom() + "@" + connection.getServiceName() + "/" + connection.getConfiguration().getResource();
        String destination = user;

        // Create the file transfer manager
        FileTransferManager manager = FileTransferManager.getInstanceFor(connection);
        // Create the outgoing file transfer
        final OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(destination);
        // Send the file

        try {

            OutgoingFileTransfer.setResponseTimeout(30000);

            transfer.sendFile(new File(file), "This is a Test!");

            System.out.println("Status :: " + transfer.getStatus() + " Error :: " + transfer.getError() + " Exception :: " + transfer.getException());

            while (!transfer.isDone()) {

                try {

                    Thread.sleep(1000);

                    System.out.println("Is it done? " + transfer.isDone());

                    Log.i("transfer file", "sending file status " + transfer.getStatus() + "progress: " + transfer.getProgress());

                    if (transfer.getStatus().equals(FileTransfer.Status.refused))
                        System.out.println("refused  " + transfer.getError());
                    else if (transfer.getStatus().equals(FileTransfer.Status.error))
                        System.out.println(" error " + transfer.getError());
                    else if (transfer.getStatus().equals(FileTransfer.Status.cancelled))
                        System.out.println(" cancelled  " + transfer.getError());
                    else
                        System.out.println("Success");

                    if (transfer.getStatus() == FileTransfer.Status.error) {
                        transfer.cancel();
                        Log.e("", "EEEEEERRRRRRRROOORRRRR");
                        break;
                    }
                } catch (InterruptedException e) {
                    Log.e("aaaaaaaaaaaaaaa", "aaaa" + e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] convertFileToByte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void configure() {

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

            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            long when = Calendar.getInstance().getTimeInMillis();

            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(context).setWhen(when)
                    .setSmallIcon(R.drawable.ic_messaging_normal).setLargeIcon(largeIcon)
                    .setTicker(context.getResources().getString(R.string.app_name)).setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setOngoing(false).setAutoCancel(true).setSound(sound);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, noBuilder.build()); //0 = ID of notification

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}