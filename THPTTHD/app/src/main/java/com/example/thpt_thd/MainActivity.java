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
    MqttAndroidClient client;
    RequestQueue requestQueue;
    private Button resolveButton;
    private Button emitButton;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestQueue = new RequestQueue();
        this.text = (TextView) this.findViewById(R.id.text);
        this.resolveButton = (Button) this.findViewById(R.id.button_accept);
        this.emitButton = (Button) this.findViewById(R.id.button_emit);

        CheckBox g1 = (CheckBox) this.findViewById(R.id.checkBox1);
        CheckBox g2 = (CheckBox) this.findViewById(R.id.checkBox2);
        CheckBox g3 = (CheckBox) this.findViewById(R.id.checkBox3);
        CheckBox g4 = (CheckBox) this.findViewById(R.id.checkBox4);
        CheckBox g5 = (CheckBox) this.findViewById(R.id.checkBox5);
        CheckBox g6 = (CheckBox) this.findViewById(R.id.checkBox6);
        CheckBox g7 = (CheckBox) this.findViewById(R.id.checkBox7);
        CheckBox g8 = (CheckBox) this.findViewById(R.id.checkBox8);
        CheckBox g9 = (CheckBox) this.findViewById(R.id.checkBox9);
        CheckBox g10 = (CheckBox) this.findViewById(R.id.checkBox10);

        initMqtt();
        emitButton.setOnClickListener(this::fakeRequestClick);

        resolveButton.setOnClickListener(v -> handleBtnResolveClick());

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

        g5.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("405");
            } else {
                unsub("405");
            }
        });

        g6.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("406");
            } else {
                unsub("406");
            }
        });

        g7.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("407");
            } else {
                unsub("407");
            }
        });

        g8.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("408");
            } else {
                unsub("408");
            }
        });

        g9.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("409");
            } else {
                unsub("409");
            }
        });

        g10.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            // Check which checkbox was clicked
            if (checked) {
                sub("410");
            } else {
                unsub("410");
            }
        });

    }

    private void initMqtt() {
        Log.d("Sa mqtt", "MQTT Init");
        if (1 == 1) return;
        // TODO: temporary turn of mqtt, delete above

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

    private void handleMessageArrived(String room, String message) {
//        if (1 == 1) return;
        // Manage notification
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
                clearNoti(room);
                Toast.makeText(this, room + " đã được tiếp nhận", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Log.d("Sa warning", "Not implemented request type");
            }
        }
        updateAlarm();
    }

    private void handleBtnResolveClick() {
        if (requestQueue.isEmpty()) return;

        Pair<String, Boolean> victim = requestQueue.remove();
        String room = victim.first;
        clearNoti(room);
        pub(room + "_re", "accept");
        pub(room, "OK");
        updateAlarm();
        Toast.makeText(this, "Đã tiếp nhận", Toast.LENGTH_SHORT).show();
    }

    /***
     * Manage displayed text and alarm sound
     */
    private void updateAlarm() {
        stopSound();
        if (requestQueue.isEmpty()) {
            text.setText(R.string.textHolder);
            return;
        }
        Pair<String, Boolean> req = requestQueue.peek();
        String room = req.first;
        Boolean isUrgent = req.second;

        String message = isUrgent ? "Cấp cứu" : "Hỗ trợ";
        text.setText(room + ": " + message);
        playSound(isUrgent);
    }

    void pub(String topic, String content) {
        Log.d("Sa mqtt", "MQTT Called");
        if (1 == 1) return;
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
        fakeOkMessageSent(topic);
        Log.d("Sa mqtt", "MQTT Called");
        if (1 == 1) return;
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

    void unsub(String topic) {
        Log.d("Sa mqtt", "MQTT Unsub");
        if (1 == 1) return;
        // TODO: temporary turn of pub sub, delete above

        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
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

    // TODO: UPDATE suitable sound
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
        int num = new Random().nextInt(10);
        String topic = String.valueOf(400 + num);
        String message = (num < 4) ? "Cap cuu" : "Ho tro";
        Log.d("mqtt", message);
        handleMessageArrived(topic, message);
    }

    public void fakeOkMessageSent(String topic) {
        handleMessageArrived(topic, "OK");
    }
}