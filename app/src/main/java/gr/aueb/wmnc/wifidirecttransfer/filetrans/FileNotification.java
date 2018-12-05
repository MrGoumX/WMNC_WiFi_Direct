package gr.aueb.wmnc.wifidirecttransfer.filetrans;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import gr.aueb.wmnc.wifidirecttransfer.R;

public class FileNotification {

    private static String CHANNEL_ID = "wmnc_ft";

    private static NotificationCompat.Builder mBuilder;
    private static NotificationManagerCompat notificationManagerCompat;

    public FileNotification(Activity mActivity){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "File Transfer";
            String description = "File transfer has been completed";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(alarmSound, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
            NotificationManager notificationManager = mActivity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        mBuilder = new NotificationCompat.Builder(mActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_aueb_logo)
                .setContentTitle("File Transfer")
                .setContentText("File transfer has been completed")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(alarmSound);

        notificationManagerCompat = NotificationManagerCompat.from(mActivity);
    }

    public static void notifyUser(){
        notificationManagerCompat.notify(0, mBuilder.build());
    }
}
