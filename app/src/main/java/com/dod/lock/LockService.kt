package com.dod.lock

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class LockService: Service() {

    private val ALARM_ID = "com.dod.lock.lock"

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
                if(intent != null){
                    when(intent.action){
                        Intent.ACTION_SCREEN_OFF -> {
                            val newIntent = Intent(context, LockActivity::class.java)
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(newIntent)
                        }
                    }
                }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notiManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ALARM_ID, "DOD LOCK SCREEN", NotificationManager.IMPORTANCE_DEFAULT)
            notiManager.createNotificationChannel(channel)
        }

        val pending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, ALARM_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("lock service")
                .setContentText("lock...")
                .setContentIntent(pending)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("lock service")
                .setContentText("lock...")
                .setContentIntent(pending)
                .build()
        }

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)

        registerReceiver(receiver, filter)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}