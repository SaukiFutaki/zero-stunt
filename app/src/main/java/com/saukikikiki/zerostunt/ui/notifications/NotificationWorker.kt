package com.saukikikiki.zerostunt.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.saukikikiki.zerostunt.MainActivity
import com.saukikikiki.zerostunt.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Log untuk memastikan pekerjaan dimulai
        Log.d("NotificationWorker", "doWork is called")

        // Menampilkan notifikasi
        showNotification()

        // Kembalikan status pekerjaan sukses
        return Result.success()
    }

    private fun showNotification() {
        Log.d("NotificationWorker", "showNotification called")

        val channelId = "weekly_reminder_channel"
        val channelName = "Weekly Reminder Notifications"

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("openFragment", "TambahDataAnakFragment")
        }

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Pengingat Mingguan")
            .setContentText("Jangan lupa isi data anak seminggu sekali!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        Log.d("NotificationWorker", "Notification built")

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NotificationWorker", "Permission not granted, notification not shown")
            return
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, notification)
        }

        Log.d("NotificationWorker", "Notification shown")
    }

}


