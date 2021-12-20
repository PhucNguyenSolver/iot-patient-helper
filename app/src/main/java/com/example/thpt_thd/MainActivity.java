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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
    String dem;
    private Button button;
    private TextView text;
    private CheckBox g1, g2, g3, g4, g5, g6, g7, g8, g9, g10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.text = (TextView) this.findViewById(R.id.text);

        this.button = (Button) this.findViewById(R.id.button_accept);

        this.g1 = (CheckBox) this.findViewById(R.id.checkBox1);
        this.g2 = (CheckBox) this.findViewById(R.id.checkBox2);
        this.g3 = (CheckBox) this.findViewById(R.id.checkBox3);
        this.g4 = (CheckBox) this.findViewById(R.id.checkBox4);
        this.g5 = (CheckBox) this.findViewById(R.id.checkBox5);
        this.g6 = (CheckBox) this.findViewById(R.id.checkBox6);
        this.g7 = (CheckBox) this.findViewById(R.id.checkBox7);
        this.g8 = (CheckBox) this.findViewById(R.id.checkBox8);
        this.g9 = (CheckBox) this.findViewById(R.id.checkBox9);
        this.g10 = (CheckBox) this.findViewById(R.id.checkBox10);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.100:1883", clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", message.toString());
                dem = topic;
                text.setText(topic + ": " + message.toString());

                boolean isUrgent = message.toString().equals("Cap cuu") ? true : false;
                sendNoti(topic, message.toString(), isUrgent);
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


////Test unit
//        this.findViewById(R.id.button_emit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int num = new Random().nextInt(10);
//                if (num < 4) {
//                    sendNoti("401", "Cap cuu", true);
//                } else {
//                    sendNoti("401", "Ho tro", false);
//                }
//            }
//        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();
                pub(dem + "_re", "accept");
                text.setText(R.string.textHolder);
            }
        });

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

    void pub(String top, String content) {
        String topic = top;
        String payload = content;
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
        Log.d("mqtt", "pub");
    }

    void sub(String content) {
        String topic = content;
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

    void unsub(String top) {
        final String topic = top;
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

    void sendNoti(@NonNull String topic, @NonNull String msg, boolean isUrgent) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
        @SuppressLint("WrongConstant") Notification noti = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setContentTitle(topic)
            .setContentText(msg)
            .setSmallIcon(R.drawable.small)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
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
            playSound(isUrgent);
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