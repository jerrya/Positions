package app.totemapps.com.positions;

public class NavChats {

    String sender;
    String firstName;
    int count;

    public NavChats(String sender, String firstName, int count) {
        this.sender = sender;
        this.count = count;
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return this.sender;
    }

}
