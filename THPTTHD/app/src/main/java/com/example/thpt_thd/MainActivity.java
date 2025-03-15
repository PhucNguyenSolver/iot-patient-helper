package com.example.thpt_thd;

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
import android.util.Log;
import android.util.Pair;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private String serverURL = "tcp://test.mosquitto.org:1883";
    MqttAndroidClient client;
    RequestQueue requestQueue;
    private Button resolveButton;
    private TextView text;
    private CheckBox g1, g2, g3, g4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestQueue = new RequestQueue();
        setTitle(getString(R.string.schoolName));
        text = (TextView) this.findViewById(R.id.text);
        resolveButton = (Button) this.findViewById(R.id.button_accept);

        g1 = (CheckBox) findViewById(R.id.checkBox1);
        g2 = (CheckBox) findViewById(R.id.checkBox2);
        g3 = (CheckBox) findViewById(R.id.checkBox3);
        g4 = (CheckBox) findViewById(R.id.checkBox4);

        initMqtt(serverURL);

        resolveButton.setOnClickListener(v -> handleBtnResolveClick());

        setupOnClickListenerForCheckboxs();
    }

    private void setupOnClickListenerForCheckboxs() {
        g1.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("401");
            } else {
                unsub("401");
            }
        });

        g2.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("402");
            } else {
                unsub("402");
            }
        });

        g3.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("403");
            } else {
                unsub("403");
            }
        });

        g4.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("404");
            } else {
                unsub("404");
            }
        });
    }

    private void initMqtt(String serverURL) {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), serverURL, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("mqtt", "connectionLost");
                Toast.makeText(getApplication(), "mqtt disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.d("mqtt", "messageArrived:" + mqttMessage.toString());
                handleMessageArrived(topic, mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("mqtt", "deliveryComplete");
            }
        });

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt", "client connected");
                    Toast.makeText(getApplication(), "mqtt server connected: " + serverURL, Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("mqtt", "client failed to connect");
                    Toast.makeText(getApplication(), "mqtt client failed to connect", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void handleMessageArrived(String room, String message) {
        // TODO: handle incoming messages
        switch (message) {
            case "Cap cuu": {
                requestQueue.add(room, true);
                sendNoti(room, message);
                break;
            }
            case "Ho tro": {
                requestQueue.add(room, false);
                sendNoti(room, message);
                break;
            }
            case "OK": {
                requestQueue.remove(room);
                // clearNoti(room);
                Toast.makeText(this, room + " đã được tiếp nhận", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Log.d("mqtt", "warning received invalid message: " + message);
            }
        }
        updateAlarm();
    }

    private void handleBtnResolveClick() {
        if (requestQueue.isEmpty()) return;

        Pair<String, Boolean> victim = requestQueue.remove();
        String room = victim.first;
//        clearNoti(room);
        pub(room + "_re", "accept");
        pub(room, "OK");
        updateAlarm();
        Toast.makeText(this, "Đã tiếp nhận", Toast.LENGTH_SHORT).show();
    }

    /***
     * Manage displayed text and alarm sound
     */
    private void updateAlarm() {
        if (requestQueue.isEmpty()) {
            text.setText(R.string.textHolder);
            stopSound();
        } else {
            Pair<String, Boolean> req = requestQueue.peek();
            String room = req.first;
            Boolean isUrgent = req.second;
            if (isUrgent) {
                text.setText(room + ": Cấp cứu");
            } else {
                text.setText(room + ": Hỗ trợ");
            }
            playSound(isUrgent);
        }
    }

    void pub(String topic, String content) {
        byte[] encodedPayload;
        try {
            encodedPayload = content.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Log.d("mqtt", "pub");
        Toast.makeText(this, "message sent", Toast.LENGTH_SHORT).show();
    }

    void sub(String topic) {
        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("mqtt", "sub success");
                    Toast.makeText(getApplication(), "subscribed. topic " + topic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("mqtt", "sub fail");
                    Toast.makeText(getApplication(), "failed to subscribe topic", Toast.LENGTH_SHORT).show();
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unsub(String topic) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Toast.makeText(getApplication(), "unsubscribed. topic " + topic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(getApplication(), "failed to unsubscribe topic", Toast.LENGTH_SHORT).show();
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
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
}