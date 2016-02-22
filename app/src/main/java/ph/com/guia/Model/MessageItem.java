package ph.com.guia.Model;

public class MessageItem {
    public String conversation;
    public String image;
    public String name;
    public String message_part;
    String message;

    public MessageItem(String conversation,String image, String name, String message_part, String message) {
        this.conversation = conversation;
        this.image = image;
        this.name = name;
        this.message_part = message_part;
        this.message = message;
    }
}
