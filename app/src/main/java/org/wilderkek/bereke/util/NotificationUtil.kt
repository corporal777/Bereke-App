package org.wilderkek.bereke.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import org.wilderkek.bereke.R
import org.wilderkek.bereke.ui.main.MainActivity


class NotificationUtil constructor(
    private val context: Context
) {


    fun createSimpleNotification(map: Map<String, String?>) {
        val title = map["title"]?:""
        val body = map["body"]?:""
        val requestCode = map["code"]?.toInt()?:0

        val intent = createNotificationIntent(context, requestCode)

        val channelId = "channel"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_done)
                .setContentTitle("•$title•")
                .setContentText(body)
                .setTicker(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(intent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(requestCode, notificationBuilder.build())
    }


    companion object {
        fun createNotificationIntent(
            context: Context,
            requestCode: Int
        ): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        }
    }
}