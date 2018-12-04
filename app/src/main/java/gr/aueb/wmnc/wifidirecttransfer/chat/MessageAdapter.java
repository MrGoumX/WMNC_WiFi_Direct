package gr.aueb.wmnc.wifidirecttransfer.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gr.aueb.wmnc.wifidirecttransfer.R;

public class MessageAdapter extends BaseAdapter {

    private List<Message> messages = new ArrayList<Message>();
    private Context context;

    public MessageAdapter(Context context){
        this.context = context;
    }

    public void add(Message message){
        System.out.println("123");
        this.messages.add(message);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageView messageView = new MessageView();
        LayoutInflater messageInf = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(position);
        if(message.isOur()){
            convertView = messageInf.inflate(R.layout.message, null);
            messageView.messageBody = (TextView) convertView.findViewById(R.id.m_body);
            convertView.setTag(messageView);
            messageView.messageBody.setText(message.getMessage());
        }
        else{
            convertView = messageInf.inflate(R.layout.message_o, null);
            messageView.avatar = ((View) convertView.findViewById(R.id.avatar));
            messageView.name = ((TextView) convertView.findViewById(R.id.name));
            messageView.messageBody = ((TextView) convertView.findViewById(R.id.m_body));
            convertView.setTag(messageView);
            messageView.name.setText(message.getData().getName());
            messageView.messageBody.setText(message.getMessage());
            GradientDrawable drawable = (GradientDrawable) messageView.avatar.getBackground();
            drawable.setColor(Color.parseColor(message.getData().getColor()));
        }
        System.out.println("Called");
        return convertView;
    }

}
