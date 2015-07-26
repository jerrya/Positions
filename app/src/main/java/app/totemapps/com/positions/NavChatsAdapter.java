package app.totemapps.com.positions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class NavChatsAdapter extends ArrayAdapter<NavChats> {

    private static class ViewHolder {
        TextView firstName;
        TextView count;
    }

    public NavChatsAdapter(Context context, ArrayList<NavChats> navChatList) {
        super(context, 0, navChatList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavChats navChats = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.drawer_list, parent, false);
            viewHolder.firstName = (TextView) convertView.findViewById(R.id.userTextView);
            viewHolder.count = (TextView) convertView.findViewById(R.id.messageCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.firstName.setText(navChats.getFirstName());

        if(navChats.getCount() == 0) {
            viewHolder.count.setVisibility(View.GONE);
        } else {
            viewHolder.count.setVisibility(View.VISIBLE);
            viewHolder.count.setText("" + navChats.getCount());
        }

        return convertView;
    }

}