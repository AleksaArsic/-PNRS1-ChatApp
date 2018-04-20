package arsic.aleksa.chatapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Cisra on 3/30/2018.
 */

public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MessageRow> mMessage;

    public MessageAdapter(Context context){
        mContext = context;
        mMessage = new ArrayList<MessageRow>();
    }

    public void addMessage(MessageRow messageRow){
        mMessage.add(messageRow);
        notifyDataSetChanged();
    }

    public void removeMessage(int position){
        mMessage.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessage.size();
    }

    @Override
    public Object getItem(int position) {

        Object rv = null;

        try{
            rv = mMessage.get(position);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        return rv;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.message_row, null);

            ViewHolder holder = new ViewHolder();

            holder.message = view.findViewById(R.id.MessageMsg);

            view.setTag(holder);
        }

        MessageRow messageRow = (MessageRow) getItem(position);
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.message.setText(messageRow.message);

        /* Set Gravity and Background color for viewHolder */
        setGravitySetBackground(viewHolder, position);

        viewHolder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeMessage(position);
                return true;
            }
        });

        return view;
    }

    private void setGravitySetBackground(ViewHolder viewHolder, int position){
        if(position%2 == 1){
            viewHolder.message.setBackgroundColor(Color.GRAY);
            viewHolder.message.setGravity(Gravity.RIGHT);
        }else{
            viewHolder.message.setBackgroundColor(Color.WHITE);
            viewHolder.message.setGravity(Gravity.LEFT);
        }
    }

    private class ViewHolder{
        public TextView message = null;
    }
}
