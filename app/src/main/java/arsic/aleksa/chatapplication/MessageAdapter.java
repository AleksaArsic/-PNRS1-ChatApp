package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cisra on 3/30/2018.
 */

public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Message> mMessage;

    public MessageAdapter(Context context){
        mContext = context;
        mMessage = new ArrayList<Message>();
    }

    public void addMessage(Message messageRow){
        mMessage.add(messageRow);
        notifyDataSetChanged();
    }

    public void removeMessage(int position){
        mMessage.remove(position);
        notifyDataSetChanged();
    }

    public void clearAdapter(){
        mMessage.clear();
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

        Message messageRow = (Message) getItem(position);
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.message.setText(messageRow.message);

        /* Set Gravity and Background color for viewHolder */
        setGravitySetBackground(viewHolder, messageRow.sender_id);

        viewHolder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Handler handler = new Handler();
                final Message message = mMessage.get(position);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            SharedPreferences sharedPref = mContext.getSharedPreferences("aleksa.arsic.chatapplication", Context.MODE_PRIVATE);
                            String sessionID = sharedPref.getString("sessionid", null );
                            String username = sessionID.substring(0, sessionID.indexOf('-'));

                            HttpHelper httpHelper = new HttpHelper();
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("data", message.getMessage());
                            jsonObject.put("receiver", message.getReceiver());
                            jsonObject.put("sender", username);

                            final boolean success = httpHelper.httpDelete(MainActivity.SERVER_URL + MainActivity.MESSAGE, jsonObject, sessionID);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(success){
                                        removeMessage(position);
                                    }
                                }
                            });

                        }catch (JSONException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                }).start();


                return true;
            }
        });

        return view;
    }

    private void setGravitySetBackground(ViewHolder viewHolder, int id){
        SharedPreferences sharedPref = mContext.getSharedPreferences("arsic.aleksa.chatapplication", Context.MODE_PRIVATE);
        int senderID = sharedPref.getInt(MainActivity.ID_SHARED_PREF_KEY, 0);

        Log.d("senderID", Integer.toString(senderID));

        if(senderID == id){
            viewHolder.message.setBackgroundColor(Color.WHITE);
            viewHolder.message.setGravity(Gravity.END);
            return;
        }
        viewHolder.message.setBackgroundColor(Color.GRAY);
        viewHolder.message.setGravity(Gravity.START);
    }

    private class ViewHolder{
        public TextView message = null;
    }
}
