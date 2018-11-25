package gr.aueb.wmnc.wifidirecttransfer.chat;

import android.view.View;
import android.widget.TextView;

public class MessageView {

    private View avatar;
    private TextView name;
    private TextView messageBody;

    public MessageView() {

    }

    public MessageView(View avatar, TextView name, TextView messageBody) {
        this.avatar = avatar;
        this.name = name;
        this.messageBody = messageBody;
    }

    public View getAvatar() {
        return avatar;
    }

    public TextView getName() {
        return name;
    }

    public TextView getMessageBody() {
        return messageBody;
    }

    public void setAvatar(View avatar) {
        this.avatar = avatar;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public void setMessageBody(TextView messageBody) {
        this.messageBody = messageBody;
    }
}
