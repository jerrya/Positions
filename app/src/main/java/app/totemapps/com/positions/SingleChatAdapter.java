package app.totemapps.com.positions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleChatAdapter extends ArrayAdapter<Chats> {

    String myAddress = LoginFragment.instance().conn1.getUser().replace("/Smack", "");

    public SingleChatAdapter(Context context, ArrayList<Chats> chats) {
        super(context, 0, chats);
    }

    private static class ViewHolder {
        TextView message;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Chats chat = getItem(position);
        if(chat.getSender().equals(myAddress)) {
            return 0;
        }
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chats chat = getItem(position);

        ViewHolder viewHolder;
        if(convertView== null) {
            viewHolder = new ViewHolder();
            if(getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sending_text_item, parent, false);
                viewHolder.message = (TextView) convertView.findViewById(R.id.singleChatMessage);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.receiving_text_item, parent, false);
                viewHolder.message = (TextView) convertView.findViewById(R.id.singleChatMessageReceiving);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.message.setText(chat.getMessage());

        return convertView;
    }
}
