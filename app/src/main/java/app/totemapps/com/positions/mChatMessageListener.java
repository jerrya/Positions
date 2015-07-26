package app.totemapps.com.positions;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mChatMessageListener implements ChatMessageListener {

    protected static final String TAG = "ChatMessageListener";

    @Override
    public void processMessage(Chat chat, final Message message) {
        final String fromAddress = message.getFrom().replace("/Smack", "");
        Log.e(TAG, chat.getParticipant());

        if(!message.getBody().isEmpty()) {
            MainActivity.instance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!NewChatFragment.chatList.containsKey(fromAddress)) {
                        NewChatFragment.chatList.put(fromAddress, new ArrayList<Chats>());
                        Log.e(TAG, "Created: " + fromAddress);
                    }

                    navListLoop(fromAddress);

                    NewChatFragment.chatList.get(fromAddress).add(new Chats(fromAddress, message.getBody(), 0));

                    if(MainActivity.instance().getFragmentManager().findFragmentByTag("newchatfragment") != null) {
                        if(MainActivity.instance().getFragmentManager().findFragmentByTag("newchatfragment").isVisible()) {
                            NewChatFragment.instance().updateList();
                            Log.e(TAG, "Notified");
                        }
                    }
                    Toast.makeText(MainActivity.instance(), "You have received a new message", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Added message: " + message.getBody());
                }
            });
        }
    }

    public void navListLoop(String username) {
        boolean doesExist = false;
        for(NavChats navChats : MainActivity.navChatList) {
            if(navChats.getSender().equals(username)) {
                navChats.increaseCount();
                MainActivity.instance().navChatAdapter.notifyDataSetChanged();
                doesExist = true;
            }
        }

        if(!doesExist) {
            getNameAndUpdate(username);
        }
    }

    String firstName = "";
    public void getNameAndUpdate(final String sender) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                VCardManager vCardManager = VCardManager.getInstanceFor(LoginFragment.instance().conn1);
                try {
                    VCard loaded = vCardManager.loadVCard(sender);
                    firstName = loaded.getFirstName();
                    Log.e(TAG, "Got first name: " + firstName);
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                MainActivity.navChatList.add(new NavChats(sender, firstName, 1));
                MainActivity.instance().navChatAdapter.notifyDataSetChanged();
                Log.e(TAG, "Inserted into list");
            }
        }.execute();
    }
}