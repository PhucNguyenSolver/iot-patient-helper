package com.example.thpt_thd

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.thpt_thd.databinding.ActivityMainBinding
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {
    private var shouldAlarmShown = false
    private var client: MqttAndroidClient? = null
    private var requestQueue: RequestQueue = RequestQueue()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.schoolName)

        binding.cardView1.roomText.text = "Phòng học 4A1"
        binding.cardView2.roomText.text = "Phòng học 4A2"
        binding.cardView3.roomText.text = "Phòng học 4A3"
        binding.cardView1.roomImage.setOnClickListener { showCameraViewer() }
        binding.cardView2.roomImage.setOnClickListener { showCameraViewer() }
        binding.cardView3.roomImage.setOnClickListener { showCameraViewer() }

        binding.alarmAcceptButton.setOnClickListener { handleBtnResolveClick() }
        binding.alarmWebView.visibility = View.INVISIBLE
        shouldAlarmShown = false
        refreshView(this.shouldAlarmShown)
        initMqtt("tcp://test.mosquitto.org:1883")
        setupOnClickListenerForCheckboxs()
    }

    private fun refreshView(shouldAlarmShown: Boolean) {
        binding.dashboard.visibility = if (!shouldAlarmShown) View.VISIBLE else View.INVISIBLE
        binding.alarmAcceptButton.visibility = if (shouldAlarmShown) View.VISIBLE else View.INVISIBLE
        binding.alarmText.visibility = if (shouldAlarmShown) View.VISIBLE else View.INVISIBLE
    }

    private fun showCameraViewer(webURL: String) {
        binding.alarmWebView.visibility = View.VISIBLE
        Toast.makeText(applicationContext, "loading..", Toast.LENGTH_SHORT).show()
        binding.alarmWebView.settings.javaScriptEnabled = true
        binding.alarmWebView.loadUrl(webURL)
    }

    private fun showCameraViewer() {
        val webURL = "https://rtsp.gasbinhminh.vn/stream"
        showCameraViewer(webURL)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // 🔹 Update the intent for this activity

        // 🔹 Handle the updated "room" value
        val room = intent.getStringExtra("room")
        if (room != null) {
            showCameraViewer()
            shouldAlarmShown = true
            refreshView(this.shouldAlarmShown)
        }
    }

    private fun setupOnClickListenerForCheckboxs() {
        binding.cardView1.subscribeCheckBox.setOnClickListener { v: View ->
            val checked = (v as CheckBox).isChecked
            // Check which checkbox was clicked
            if (checked) {
                sub("401")
            } else {
                unsub("401")
            }
        }
        binding.cardView2.subscribeCheckBox.setOnClickListener { v: View ->
            val checked = (v as CheckBox).isChecked
            // Check which checkbox was clicked
            if (checked) {
                sub("402")
            } else {
                unsub("402")
            }
        }
        binding.cardView3.subscribeCheckBox.setOnClickListener { v: View ->
            val checked = (v as CheckBox).isChecked
            // Check which checkbox was clicked
            if (checked) {
                sub("403")
            } else {
                unsub("403")
            }
        }
    }

    private fun initMqtt(serverURL: String) {
        val clientId = MqttClient.generateClientId()
        client = MqttAndroidClient(applicationContext, serverURL, clientId)
        client!!.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                Log.d("mqtt", "connectionLost")
                Toast.makeText(application, "mqtt disconnected", Toast.LENGTH_SHORT).show()
            }

            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.d("mqtt", "messageArrived:$mqttMessage")
                handleMessageArrived(topic, mqttMessage.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.d("mqtt", "deliveryComplete")
            }
        })
        try {
            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true
            val token = client!!.connect(options)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d("mqtt", "client connected")
                    Toast.makeText(
                        application,
                        "mqtt server connected: $serverURL",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d("mqtt", "client failed to connect")
                    Toast.makeText(application, "mqtt client failed to connect", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun handleMessageArrived(room: String, message: String) {
        // TODO: handle incoming messages
        when (message) {
            "Cap cuu" -> {
                requestQueue.add(room, true)
                sendNoti(room, message)
            }
            "Ho tro" -> {
                requestQueue.add(room, false)
                sendNoti(room, message)
            }
            "OK" -> {
                requestQueue.remove(room)
                // clearNoti(room);
                Toast.makeText(this, "$room đã được tiếp nhận", Toast.LENGTH_LONG).show()
            }
            else -> {
                Log.d("mqtt", "warning received invalid message: $message")
            }
        }
        updateAlarm()
    }

    private fun handleBtnResolveClick() {
        if (!requestQueue.isEmpty) {
            val latestAlarm = requestQueue.remove()
            val room = latestAlarm.first
            pub(room, "OK")
        }
        shouldAlarmShown = false
        refreshView(this.shouldAlarmShown)
        updateAlarm()
    }

    /***
     * Manage displayed text and alarm sound
     */
    private fun updateAlarm() {
        if (requestQueue.isEmpty) {
            binding.alarmText.text = ""
            stopSound()
        } else {
            val req = requestQueue.peek()
            val room = req.first
            val isUrgent = req.second
            val msg = if (isUrgent) "$room: Tín hiệu khẩn cấp 🆘" else "$room: Cần hỗ trợ ✋"
            binding.alarmText.text = msg
            playSound(isUrgent)
        }
    }

    fun pub(topic: String?, content: String) {
        val encodedPayload: ByteArray
        try {
            encodedPayload = content.toByteArray(StandardCharsets.UTF_8)
            val message = MqttMessage(encodedPayload)
            client!!.publish(topic, message)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
        Log.d("mqtt", "pub")
        Toast.makeText(this, "message sent", Toast.LENGTH_SHORT).show()
    }

    private fun sub(topic: String) {
        val qos = 0
        try {
            val subToken = client!!.subscribe(topic, qos)
            subToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // The message was published
                    Log.d("mqtt", "sub success")
                    Toast.makeText(application, "subscribed. topic $topic", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.d("mqtt", "sub fail")
                    Toast.makeText(application, "failed to subscribe topic", Toast.LENGTH_SHORT)
                        .show()
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun unsub(topic: String) {
        try {
            val unsubToken = client!!.unsubscribe(topic)
            unsubToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // The subscription could successfully be removed from the client
                    Toast.makeText(application, "unsubscribed. topic $topic", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Toast.makeText(application, "failed to unsubscribe topic", Toast.LENGTH_SHORT)
                        .show()
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun sendNoti(topic: String, msg: String) {
        // 🔹 Intent to bring MainActivity to the foreground
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("room", topic) // Pass the room as an extra
        intent.putExtra("isUrgent", true) // Pass the room as an extra
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        // 🔹 Wrap the intent in a PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Ensure it updates with new extras
        )
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
        @SuppressLint("WrongConstant") val noti =
            NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(topic)
                .setContentText(msg)
                .setSmallIcon(R.drawable.small)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSilent(true)
                .build()
        val notificationId: Int = try {
            topic.toInt()
        } catch (e: NumberFormatException) {
            1
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, noti)
    }

    fun playSound(isUrgent: Boolean) {
        stopSound()
        val context = applicationContext
        val startIntent = Intent(context, RingtoneService::class.java)
        startIntent.putExtra("isUrgent", isUrgent)
        context.startService(startIntent)
    }

    fun stopSound() {
        val context = applicationContext
        val stopIntent = Intent(context, RingtoneService::class.java)
        context.stopService(stopIntent)
    }
}