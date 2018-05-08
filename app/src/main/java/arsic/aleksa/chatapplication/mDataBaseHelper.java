package arsic.aleksa.chatapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.renderscript.ScriptIntrinsicYuvToRGB;

/**
 * Created by Cisra on 4/20/2018.
 */

public class mDataBaseHelper extends SQLiteOpenHelper {

    /* Database information */
    public static final String DATA_BASE_NAME = "mDataBase.db";
    public static final int DATA_BASE_VERSION = 1;

    /* Contacts table information */
    public static final String CONTACTS_TABLE_NAME = "Contacts";

    public static final String CONTACTS_COLUMN_CONTACT_ID = "contact_id";
    public static final String CONTACTS_COLUMN_USERNAME = "username";
    public static final String CONTACTS_COLUMN_FIRST_NAME = "firstname";
    public static final String CONTACTS_COLUMN_LAST_NAME = "lastname";

    /* Messages table information */
    public static final String MESSAGE_TABLE_NAME = "Message";

    public static final String MESSAGE_COLUMN_MESSAGE_ID = "message_id";
    public static final String MESSAGE_COLUMN_SENDER_ID = "sender_id";
    public static final String MESSAGE_COLUMN_RECEIVER_ID = "receiver_id";
    public static final String MESSAGE_COLUMN_MESSAGE = "message";

    public mDataBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* Define Contacts table structure */
        String ContactsTable = " CREATE TABLE " + CONTACTS_TABLE_NAME +
                " (" +
                CONTACTS_COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                CONTACTS_COLUMN_USERNAME + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_LAST_NAME + " TEXT NOT NULL " +
                ")";

        /* Define Message table structure */
        String MessageTable = " CREATE TABLE " + MESSAGE_TABLE_NAME +
                " (" +
                MESSAGE_COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                MESSAGE_COLUMN_SENDER_ID + " INTEGER NOT NULL, " +
                MESSAGE_COLUMN_RECEIVER_ID + " INTEGER NOT NULL, " +
                MESSAGE_COLUMN_MESSAGE + " TEXT NOT NULL " +
                ")";

        /* Create Contacts Table */
        db.execSQL(ContactsTable);
        /* Create Message Table */
        db.execSQL(MessageTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void insertContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CONTACTS_COLUMN_USERNAME, contact.username);
        values.put(CONTACTS_COLUMN_FIRST_NAME, contact.firstName);
        values.put(CONTACTS_COLUMN_LAST_NAME, contact.lastName);

        db.insert(CONTACTS_TABLE_NAME, null, values);
    }

    public Contact[] readContacts(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE_NAME, null, null, null, null, null, null);

        if(cursor.getCount() <= 0) return null;

        Contact[] contacts = new Contact[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            contacts[i] = makeContact(cursor);
            i++;
        }

        close();

        return contacts;
    }

    public Contact readContact(String username){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[]{username};
        Cursor cursor = db.query(CONTACTS_TABLE_NAME, null, CONTACTS_COLUMN_USERNAME + "=?",
                args,null, null, null, null);

        cursor.moveToFirst();
        Contact contact = makeContact(cursor);

        close();

        return  contact;
    }

    public void insertMessage(Message message){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MESSAGE_COLUMN_SENDER_ID, message.sender_id);
        values.put(MESSAGE_COLUMN_RECEIVER_ID, message.receiver_id);
        values.put(MESSAGE_COLUMN_MESSAGE, message.message);

        db.insert(MESSAGE_TABLE_NAME, null, values);
    }

    public Message[] readMessages(int userID, int contactID){
        SQLiteDatabase db = getReadableDatabase();

        String[] args = new String[]{ String.valueOf(userID),
                                      String.valueOf(contactID),
                                      String.valueOf(contactID),
                                      String.valueOf(userID)
        };

        Cursor cursor = db.query(MESSAGE_TABLE_NAME, null,
                MESSAGE_COLUMN_RECEIVER_ID + "=?" + " AND " +  MESSAGE_COLUMN_SENDER_ID + "=?" +
                " OR " + MESSAGE_COLUMN_RECEIVER_ID + "=?" + " AND " + MESSAGE_COLUMN_SENDER_ID + "=?",
                args,
                null, null, null);

        Message[] messages = new Message[cursor.getCount()];

        if(cursor.getCount() <= 0) return null;


        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            messages[i] = makeMessage(cursor);
            i++;
        }

        close();

        return messages;
    }

    private Contact makeContact(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex(CONTACTS_COLUMN_CONTACT_ID));
        String username = cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_USERNAME));
        String firstname = cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_FIRST_NAME));
        String lastname = cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_LAST_NAME));

        return new Contact(username, firstname, lastname, id);
    }

    private Message makeMessage(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex(MESSAGE_COLUMN_MESSAGE_ID));
        int sender_id = cursor.getInt(cursor.getColumnIndex(MESSAGE_COLUMN_SENDER_ID));
        int receiver_id = cursor.getInt(cursor.getColumnIndex(MESSAGE_COLUMN_RECEIVER_ID));
        String message = cursor.getString(cursor.getColumnIndex(MESSAGE_COLUMN_MESSAGE));

        return new Message(id, message, sender_id, receiver_id);
    }
}
