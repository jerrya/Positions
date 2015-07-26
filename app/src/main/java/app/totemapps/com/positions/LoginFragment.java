package app.totemapps.com.positions;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;

public class LoginFragment extends Fragment {

    protected final static String TAG = "Positions";

    EditText loginUsername, loginPassword;
    Button loginButton, createAccountButton;

    AbstractXMPPConnection conn1;
    XMPPTCPConnectionConfiguration config;

    LeafNode leafNode;
    PubSubManager mgr;

    static boolean connectedToNode = false;
    ItemEventCoordinator itemEventCoordinator;

    private static LoginFragment inst;
    public static LoginFragment instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    public void initiateStartScreen() {
        if(conn1.isConnected()) {
            StartScreen startScreen = new StartScreen();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, startScreen);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Toast.makeText(getActivity(), "Unable to log in. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        loginUsername = (EditText) view.findViewById(R.id.loginUsername);
        loginPassword = (EditText) view.findViewById(R.id.loginPassword);

        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin(loginUsername.getText().toString(), loginPassword.getText().toString());
            }
        });

        createAccountButton = (Button) view.findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccountFragment createAccountFragment = new CreateAccountFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, createAccountFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        getActivity().getActionBar().setTitle("Positions");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SmackConfiguration.DEBUG = true;
        ProviderManager pm = new ProviderManager();
        pm.addExtensionProvider("geoloc", "http://jabber.org/protocol/geoloc", new UserLocationProvider());
        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        Log.e(TAG, "Added provider");

        // TODO: Remove this!
        loginUsername.setText("ctest");
        loginPassword.setText("ctest");
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected static final String SERVICE_NAME2 = "positions.example.com";
    protected static final String HOST_NAME2 = "123.456.789.369";

    public void startLogin(String username, String password) {
        config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setServiceName(SERVICE_NAME2)
                .setHost(HOST_NAME2)
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled) // TODO: change this later
                .setCompressionEnabled(false)
                .build();

        conn1 = new XMPPTCPConnection(config);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    conn1.connect();
                } catch (Exception e) {
                    Log.e(TAG, "Login error: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                connectionLogin();
            }
        }.execute();
    }

    public void connectionLogin() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    conn1.login();
                } catch (Exception e) {
                    Log.e(TAG, "Conn1 login error: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(conn1.isConnected()) {
                    Log.e(TAG, "Logged in as: " + conn1.getUser());
                    Toast.makeText(getActivity(), "Logged in as: " + conn1.getUser(), Toast.LENGTH_SHORT).show();
                    createChatListener();
                    hideKeyboard();
                    initiateStartScreen();
                } else {
                    Log.e(TAG, "Login Failed");
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    ChatManager chatManager;
    public void createChatListener() {
        chatManager = ChatManager.getInstanceFor(conn1);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                if(!createdLocally) {
                    chat.addMessageListener(new mChatMessageListener());
                }
            }
        });
        chatManager.setNormalIncluded(false);
    }

    public void sendMessage(String recipient, String firstMessage) {
        Chat newChat = chatManager.createChat(recipient, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                Log.e(TAG, "Received message: " + message);
            }
        });
        try {
            newChat.sendMessage(firstMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    boolean nodeCreated = false;
    boolean isSubscribed = false;
    public void connectToNode(String postalCode) {
        mgr = new PubSubManager(conn1);
        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(false); // Was false
//        form.setPresenceBasedDelivery(true);
        form.setPublishModel(PublishModel.open);

        if(isSubscribed) {
            try {
                leafNode.unsubscribe(conn1.getUser()); // Remove any existing subscriptions
            } catch (Exception e) {
                Log.e(TAG, "Subscription error: " + e);
            }
            leafNode.removeItemEventListener(itemEventCoordinator);
            Log.e(TAG, "Subscriptions removed");
        }

        try {
            leafNode = (LeafNode) mgr.createNode(postalCode, form);
            nodeCreated = true;
            Log.e(TAG, "Leafnode created");
        } catch (Exception e) {
            Log.e(TAG, "Leaf node not created: " + e);
        }

        if(!nodeCreated) {
            try {
                leafNode = mgr.getNode(postalCode);
                Log.e(TAG, "Got node!");
            } catch (Exception e) {
                Log.e(TAG, "Get node error: " + e);
            }
        }

        if(leafNode != null) {
            // TODO: Switch up if persistent data is still being received
            // TODO: To fix you might have to use unsubscribe when activity is destroyed/paused
            itemEventCoordinator = new ItemEventCoordinator();
            leafNode.addItemEventListener(itemEventCoordinator);

            try {
                leafNode.subscribe(conn1.getUser());
                Log.e(TAG, "Subscribed to node");
                connectedToNode = true;
                isSubscribed = true;
            } catch (Exception e) {
                Log.e(TAG, "Subscribing error: " + e);
            }
        } else {
            Log.e(TAG, "Leafnode is null");
            isSubscribed = false;
        }

    }

    public void publishData(final Double lat, final Double lon) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    leafNode.send(new PayloadItem(conn1.getUser(), new SimplePayload("geoloc", "http://jabber.org/protocol/geoloc",
                            "<geoloc xmlns='http://jabber.org/protocol/geoloc' xml:lang='en'> <sender>"
                                    + conn1.getUser().replace("/Smack", "")
                                    + "</sender> <rstatus>single</rstatus> <lat>"
                                    + lat
                                    + "</lat> <lon>"
                                    + lon
                                    + "</lon> </geoloc>")));

                } catch (Exception e) {
                    Log.e(TAG, "Publish data error: " + e);
                }
                return null;
            }
        }.execute();
    }

    public void reconnect() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    conn1.connect();
                } catch (Exception e) {
                    Log.e(TAG, "reconnect error: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            conn1.login();
                        } catch (Exception e) {
                            Log.e(TAG, "reconnect login error: " + e);
                        }
                        return null;
                    }
                }.execute();
            }
        }.execute();

    }

    public void disconnect() {
        if(conn1 != null) {
            if(conn1.isConnected()) {
                conn1.disconnect();
            }
        }
        Log.e(TAG, "Disconnected");
    }

}
