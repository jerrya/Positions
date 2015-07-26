package app.totemapps.com.positions;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountFragment extends Fragment {

    EditText usernameView, passwordView, emailView;
    Button registerButton, resultButton;

    protected final String TAG = "CreateAccountTAG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_fragment, container, false);

        usernameView = (EditText) view.findViewById(R.id.usernameView);
        passwordView = (EditText) view.findViewById(R.id.passwordView);
        emailView = (EditText) view.findViewById(R.id.emailView);

        registerButton = (Button) view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(usernameView.getText().toString(),
                        passwordView.getText().toString(),
                        emailView.getText().toString());
            }
        });

        resultButton = (Button) view.findViewById(R.id.resultButton);
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        return view;
    }

    public void createAccount(final String username, final String password, final String email) {
        final Map<String, String> emailMap = new HashMap<>();
        emailMap.put("email", email);

        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setServiceName("positions.example.com")
                .setHost("123.456.789.369")
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled) // change this later
                .build();

        final XMPPTCPConnection con = new XMPPTCPConnection(conf);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    con.connect();
                    Log.d(TAG, "Client connected");
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                AccountManager accountManager = AccountManager.getInstance(con);
                accountManager.sensitiveOperationOverInsecureConnection(true);
                try {
                    accountManager.createAccount(username, password, emailMap);
                    Log.d(TAG, "Account created");
                    resultButton.setText(username + " account created!");
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
