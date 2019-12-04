package com.example.trabajofinalmoviles

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat


class AlertasService: FirebaseMessagingService(){
    val TAG = "FIREBASE"
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val from = remoteMessage?.from
        Log.d(TAG, "Mensaje recibido de: $from")

        if (remoteMessage?.notification != null) {
            Log.d(TAG, "Notificación: " + remoteMessage?.notification?.body)

            mostrarNotificacion(remoteMessage?.notification?.title, remoteMessage?.notification?.body)
        }

        if (remoteMessage?.data?.size!! > 0) {
            Log.d(TAG, "Data: " + remoteMessage.data)
        }
    }

    override fun onNewToken(p0: String?) {
        //super.onNewToken(p0)
    }

    companion object {
        var identificador = 0
    }

    private fun mostrarNotificacion(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(identificador++, notificationBuilder.build())
    }
}