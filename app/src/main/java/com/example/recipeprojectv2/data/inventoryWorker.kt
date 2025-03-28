package com.example.recipeprojectv2.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.recipeprojectv2.helper.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

@HiltWorker
class InventoryWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WorkManagerDebug", "InventoryWorker DIJALANKAN")

        val currentTime = System.currentTimeMillis()
        val database = AppDatabase.getDatabase(applicationContext, CoroutineScope(Dispatchers.IO))
        val inventoryDao = database.inventoryDao()

        // Ambil data dari Flow menggunakan first()
        val inventoryList = inventoryDao.getAllInventory().first()

        Log.d("WorkManagerDebug", "Jumlah data inventory: ${inventoryList.size}")

        inventoryList.forEach { item ->
            val remainingDays = TimeUnit.MILLISECONDS.toDays(item.expire - currentTime)
            Log.d("WorkManagerDebug", "Item: ${item.name}, Sisa hari: $remainingDays")

            if (remainingDays <= 30) {
                Log.d("WorkManagerDebug", "NOTIFIKASI DIKIRIM untuk ${item.name}")
                NotificationHelper(applicationContext).showNotification(item.name)
            }
        }

        return Result.success()

    }
}