package app.totemapps.com.positions;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.jivesoftware.smack.util.Async;

import java.util.ArrayList;
import java.util.HashMap;

public class NewChatFragment extends ListFragment {

    protected final static String TAG = "NewChatFragment";

    static HashMap<String, ArrayList<Chats>> chatList = new HashMap<>();

    String user = "";
    String fName = "";

    EditText messageText;
    ImageButton imageSendButton;

    private static NewChatFragment inst;
    public static NewChatFragment instance() {
        return inst;
    }

    SingleChatAdapter singleChatAdapter;

    String myAddress = LoginFragment.instance().conn1.getUser().replace("/Smack", "");

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
        Bundle args = getArguments();
        if(args != null) {
            user = args.getString("user");
            fName = args.getString("firstname");
            Log.e(TAG, "Got: " + user + " and " + fName);

            if(!chatList.containsKey(user)) {
                chatList.put(user, new ArrayList<Chats>());
                Log.e(TAG, "Added list. Size: " + chatList.size());
            }
            resetCount();
            singleChatAdapter = new SingleChatAdapter(getActivity(), chatList.get(user));
            getListView().setAdapter(singleChatAdapter);
            getActivity().getActionBar().setTitle(fName);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_chat_fragment, container, false);

        messageText = (EditText) view.findViewById(R.id.enterMessage);
        imageSendButton = (ImageButton) view.findViewById(R.id.imageSendButton);

        imageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(user, messageText.getText().toString());
            }
        });

        return view;
    }

    public void sendMessage(String recipient, String message) {
        LoginFragment.instance().sendMessage(recipient, message);
        chatList.get(recipient).add(new Chats(myAddress, message, 0));
        messageText.setText("");
        updateList();
    }

    public void updateList() {
        singleChatAdapter.notifyDataSetChanged();
        getListView().setSelection(singleChatAdapter.getCount()-1);
        resetCount();
    }

    public void resetCount() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for(NavChats navChats : MainActivity.navChatList) {
                    if(navChats.getSender().equals(user)) {
                        navChats.setCount(0);
                    }
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
