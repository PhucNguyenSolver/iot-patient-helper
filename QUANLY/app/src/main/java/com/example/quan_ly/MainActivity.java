package com.example.quan_ly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    boolean TEST_MODE = false;
    MqttAndroidClient client;
    RequestQueue requestQueue;
    private Button resolveButton;
    private Button emitButton;
    private TextView text;
    private Handler mHandler;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.schoolName));

        this.requestQueue = new RequestQueue();
        this.text = (TextView) this.findViewById(R.id.text);
        this.resolveButton = (Button) this.findViewById(R.id.button_accept);
        this.emitButton = (Button) this.findViewById(R.id.button_emit);
        this.checkBox = (CheckBox) this.findViewById(R.id.checkBox);

        initMqtt();
        initHandler();
        resolveButton.setOnClickListener(v -> handleBtnResolveClick());
        emitButton.setOnClickListener(v -> fakeRequestClick(v));
        checkBox.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
        subscribeAllTopic(401, 410);
        });
    }

    private void initMqtt() {
//        Log.d("Sa mqtt", "MQTT Init");
//        if (1 == 1) return;
//        // TODO: temporary turn of mqtt, delete above

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.100:1883", clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                String message = mqttMessage.toString();
                Log.d("mqtt", message);
                handleMessageArrived(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void pub(String topic, String content) {
        Log.d("Sa mqtt", "MQTT Called");
        if (TEST_MODE) return;
        // TODO: temporary turn of pub sub, delete above

        byte[] encodedPayload;
        try {
            encodedPayload = content.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Log.d("mqtt", "pub");
    }

    void sub(String topic) {
        Log.d("Sa mqtt", "MQTT Called");
        if (TEST_MODE) return;
        // TODO: temporary turn of pub sub, delete above

        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("mqtt", "sub success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("mqtt", "sub fail");
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void sendNoti(@NonNull String topic, @NonNull String msg) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
        @SuppressLint("WrongConstant") Notification noti = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setContentTitle(topic)
            .setContentText(msg)
            .setSmallIcon(R.drawable.small)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSilent(true)
            .build();

        int notificationId;
        try {
            notificationId = Integer.parseInt(topic);
        } catch (NumberFormatException e) {
            notificationId = 1;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, noti);
        }
    }

    void clearNoti(@NonNull String topic) {
        int notificationId;
        try {
            notificationId = Integer.parseInt(topic);
        } catch (NumberFormatException e) {
            notificationId = 1;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }

    void playSound(boolean isUrgent) {
        stopSound();
        Context context = getApplicationContext();
        Intent startIntent = new Intent(context, RingtoneService.class);
        startIntent.putExtra("isUrgent", isUrgent);
        context.startService(startIntent);
    }

    void stopSound() {
        Context context = getApplicationContext();
        Intent stopIntent = new Intent(context, RingtoneService.class);
        context.stopService(stopIntent);
    }

    //            For testing
    public void fakeRequestClick(View v)  {
        int num = new Random().nextInt(9) + 1;
        String topic = String.valueOf(400 + num);
        String message = (num < 4) ? "Cap cuu" : "Ho tro";
        Log.d("mqtt", message);
//        handleMessageArrived(topic, message);
        handleMessageArrived(topic, "Cap cuu");
    }

    public void fakeOkMessageSent(String topic) {
        handleMessageArrived(topic, "OK");
    }


    /** ---------------------------------------------
     * Newly defined method
     */

    private void subscribeAllTopic(int from, int to) {
//        sub("401");
        for (int i = from; i <= to; i++) {
            Log.d("Sa log subscribed:" + String.valueOf(i), String.valueOf(i));
            sub(String.valueOf(i));
        }
    }

    Queue<String> currentRequestQueue = new LinkedList<>();
    Queue<String> missedRequestQueue = new LinkedList<>();
    Timer timer = new Timer();

    public TimerTask timerTaskBuilder(String room) {
        return new TimerTask() {
            @Override
            public void run() {
                if (missedRequestQueue.contains(room)) {
                    cancel();
                }
                if (currentRequestQueue.contains(room)) {
                    missedRequestQueue.add(room);
                    sendNoti(room, "Bệnh nhân cần cấp cứu");
                    mHandler.obtainMessage(1).sendToTarget();
//                    updateAlarm();
                }
                cancel();
            }
        };
    }

    private void handleMessageArrived(String room, String message) {
        switch (message) {
            case "Cap cuu": {
                if (!currentRequestQueue.contains(room)) {
                    currentRequestQueue.add(room);
                    long waitingTimeInSeconds = 20L;
                    timer.schedule(timerTaskBuilder(room), waitingTimeInSeconds * 1000);
                }
                break;
            }
            case "Ho tro": {
                break;
            }
            case "OK": {
                currentRequestQueue.remove(room);
                if (missedRequestQueue.contains(room)) {
                    missedRequestQueue.remove(room);
                    clearNoti(room);
                }
                break;
            }
            default: {
                Log.d("Sa warning", "Not implemented request type");
            }
        }
        updateAlarm();
    }

    private synchronized void handleBtnResolveClick() {
        if (missedRequestQueue.isEmpty()) return;

        String room = missedRequestQueue.remove();

        clearNoti(room);
        pub(room + "_re", "accept");
        pub(room, "OK");
        Toast.makeText(this, "Đã tiếp nhận", Toast.LENGTH_SHORT).show();
        updateAlarm();
    }

    /***
     * Manage displayed text and alarm sound
     */
    private synchronized void updateAlarm() {
        stopSound();
        if (missedRequestQueue.isEmpty()) {
            text.setText(TEST_MODE ? "CHill" : "");
            return;
        }
        String room = missedRequestQueue.peek();
        Log.d("Sa log", room);
        text.setText(room);
        playSound(true);
    }


    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                updateAlarm();
            }
        };
    }
}