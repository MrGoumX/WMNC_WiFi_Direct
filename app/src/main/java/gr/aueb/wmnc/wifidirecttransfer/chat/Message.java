package gr.aueb.wmnc.wifidirecttransfer.chat;

import java.io.Serializable;

public class Message implements Serializable{

    private String message;
    private MemberData data;
    private boolean our;

    public Message(String message, MemberData data, boolean our) {
        this.message = message;
        this.data = data;
        this.our = our;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public MemberData getData() {
        return data;
    }

    public boolean isOur() {
        return our;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(MemberData data) {
        this.data = data;
    }

    public void setOur(boolean our) {
        this.our = our;
    }
}
