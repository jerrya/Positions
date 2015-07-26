package app.totemapps.com.positions;

public class Chats {

    public String sender;
    public String message;
    int count;

    public Chats(String sender, String message, int count) {
        this.sender = sender;
        this.message = message;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount() {
        this.count++;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
