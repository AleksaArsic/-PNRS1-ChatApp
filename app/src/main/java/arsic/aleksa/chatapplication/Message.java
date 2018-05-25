package arsic.aleksa.chatapplication;

/**
 * Created by Cisra on 3/30/2018.
 */

public class Message {

    public int message_id;
    public int sender_id;
    public int receiver_id;
    public String message;
    public String sender;
    public String receiver;

    public Message(String msg, int sender_id){
        this.sender_id = sender_id;
        message = msg;
    }

    public Message(int id, String msg, int sender_id, int receiver_id){
        message_id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        message = msg;
    }

    public Message(String msg, int sender_id, int receiver_id){
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        message = msg;
    }

    public Message(String msg, String sender_id, String receiver_id){
        this.sender = sender_id;
        this.receiver = receiver_id;
        message = msg;
    }

    public Message(String msg, String sender, String receiver, int sender_id, int receiver_id){
        this.sender = sender;
        this.receiver = receiver;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        message = msg;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
