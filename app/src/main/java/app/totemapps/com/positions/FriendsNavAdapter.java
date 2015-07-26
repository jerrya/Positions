package app.totemapps.com.positions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsNavAdapter extends ArrayAdapter<Friends> {

    private static class ViewHolder {
        TextView sender;
        TextView count;
    }

    public FriendsNavAdapter(Context context, ArrayList<Friends> friendsList) {
        super(context, 0, friendsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friends friends = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.drawer_list, parent, false);
            viewHolder.sender = (TextView) convertView.findViewById(R.id.userTextView);
            viewHolder.count = (TextView) convertView.findViewById(R.id.messageCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.sender.setText(friends.getSender());
        if(friends.getCount() == 0) {
            viewHolder.count.setVisibility(View.GONE);
        } else {
            viewHolder.count.setVisibility(View.VISIBLE);
            viewHolder.count.setText("" + friends.getCount());
        }

        return convertView;
    }

}

