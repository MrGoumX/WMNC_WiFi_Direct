package gr.aueb.wmnc.wifidirecttransfer.chat;

import android.os.AsyncTask;

public class ReceiveMessage extends AsyncTask<Object, Void, Void> {

    private MessageAdapter adapter;
    private Message message;

    @Override
    protected Void doInBackground(Object... objects) {
        adapter = (MessageAdapter) objects[0];
        message = (Message) objects[1];
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        super.onPostExecute(aVoid);
    }
}
