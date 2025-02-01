import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.app.getswipe.assignment.R
import com.app.getswipe.assignment.presentation.ui.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "product_notifications"
    private const val NOTIFICATION_ID = 1
    private const val TAG = "NotificationHelper"

    fun createNotificationChannel(context: Context) {

        // Check for Android 13+ permission (POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Create Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Product Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications related to products"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
                ?: Log.e(TAG, "NotificationManager is null")
        }
    }

    fun sendNotification(context: Context, title: String, message: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 🔥 Ensures heads-up (popup) notification
            .setDefaults(NotificationCompat.DEFAULT_ALL) // ✅ Enables sound, vibration, lights
            .setAutoCancel(true) // Dismiss when clicked
            .setContentIntent(pendingIntent) // Open activity when tapped
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d(TAG, "Notification sent: $title - $message")
    }
}
