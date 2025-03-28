package com.example.recipeprojectv2.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.recipeprojectv2.R

class NotificationHelper(private val context: Context) {
    private val channelId = "inventory_notification_channel"

    fun showNotification(itemName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Inventory Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(androidx.core.R.drawable.notification_bg) // Ganti dengan ikon notifikasi Anda
            .setContentTitle("Reminder: Item Expiring Soon!")
            .setContentText("Item '$itemName' will expire in 30 days.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(itemName.hashCode(), notification)
    }
}
