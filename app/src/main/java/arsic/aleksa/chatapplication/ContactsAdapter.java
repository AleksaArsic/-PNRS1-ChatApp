package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Cisra on 3/29/2018.
 */

public class ContactsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Contact> mContacts;

    public ContactsAdapter(Context context){
        mContext = context;
        mContacts = new ArrayList<Contact>();
    }

    public void addContact(Contact contactsRow){
        mContacts.add(contactsRow);
        notifyDataSetChanged();
    }

    public void removeContact(int position){
        mContacts.remove(position);
        notifyDataSetChanged();
    }

    public void clearAdapter(){
        mContacts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mContacts.size();
    }

    @Override
    public Object getItem(int position){
        Object rv = null;
        try{
            rv = mContacts.get(position);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return rv;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_row, null);

            ViewHolder holder = new ViewHolder();

            holder.contactsName = view.findViewById(R.id.ContactName);
            holder.firstLetter = view.findViewById(R.id.ContactIcon);
            holder.sendBtn = view.findViewById(R.id.ContactSendButtonIV);
            view.setTag(holder);
        }

        final Contact contactsRow = (Contact) getItem(position);
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.contactsName.setText(contactsRow.contactName);
        viewHolder.firstLetter.setText(contactsRow.firstLetter);
        viewHolder.sendBtn.setImageDrawable(contactsRow.sendBtn);

        /* Randomize background for List Views first letter Text Views */
        randomFirstLetterBackground(viewHolder);

        /* Predefined mod 8 backgrounds */
        // BackgroundFirstLetter(viewHolder, position);

        viewHolder.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ContactName", contactsRow.contactName.toString());
                bundle.putInt("ContactID", contactsRow.getId());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    /* Randomize background for List Views first letter Text Views */
    private void randomFirstLetterBackground(ViewHolder viewHolder){

        Random rand = new Random();
        char alpha = (char) rand.nextInt(256);
        char red = (char) rand.nextInt(256);
        char green = (char) rand.nextInt(200);
        char blue = (char) rand.nextInt(200);

        viewHolder.firstLetter.setBackgroundColor(Color.argb(alpha, red, green, blue));
    }

    /* Predefined mod 8 backgrounds */
    private void BackgroundFirstLetter(ViewHolder viewHolder, int position){
        if(position%8 == 0){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#FF5733"));
        }else if(position%8 == 1){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#AF7AC5"));
        }else if(position%8 == 2){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#7FB3D5"));
        }else if(position%8 == 3){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#A3E4D7"));
        }else if(position%8 == 4){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#27AE60"));
        }else if(position%8 == 5){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#F4D03F"));
        }else if(position%8 == 6){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#F39C12"));
        }else if(position%8 == 7){
            viewHolder.firstLetter.setBackgroundColor(Color.parseColor("#DC7633"));
        }
    }

    private class ViewHolder{
        public TextView contactsName = null;
        public TextView firstLetter = null;
        public ImageView sendBtn = null;
    }

}
