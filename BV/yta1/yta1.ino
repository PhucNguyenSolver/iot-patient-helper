#include <WiFi.h>
#include <PubSubClient.h>

#include <Wire.h>
#include "SSD1306.h"


SSD1306  display(0x3c, 4, 15);
int thoi_gian = 0;
// Cập nhật thông tin
// Thông tin về wifi
#define ssid "THPT_THD"  // Tên Wifi
#define password "12341234" // Mật khẩu
// Thông tin về MQTT Broker
#define mqtt_server "broker.hivemq.com" // Thay bằng thông tin của bạn
String clientId="y ta";

int btnPin = 0; 
int Buzzer = 5;

const uint16_t mqtt_port = 1883; //Port của CloudMQTT
WiFiClient espClient;
PubSubClient client(espClient);

long lastMsg = 0;
char msg[50];
char text[50];
char text1[50];
char text2[50];
int value = 0;
void setup() {
  Serial.begin(9600);
  pinMode(btnPin, INPUT);
  pinMode(5, OUTPUT);
  setup_wifi();
  pinMode(16,OUTPUT);
  digitalWrite(16, LOW);
  delay(50); 
  digitalWrite(16, HIGH);
  
  display.init();
  display.flipScreenVertically(); //đảo chiều
  display.setFont(ArialMT_Plain_24);
  display.drawString(0, 0, "TPHT THD");
  display.drawString(0, 30, String(text));
  display.display();
  display.clear();
  client.setServer(mqtt_server, mqtt_port); 
  client.setCallback(callback);
}
// Hàm kết nối wifi
void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}
// Hàm call back để nhận dữ liệu
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
        Serial.print((char)payload[i]);
        text[i]=payload[i];
        }
      
      display.clear();
      display.drawString(0, 0, topic);
      display.drawString(0, 30, String(text));
      display.display();

      if ((text[0]=='H')||(text[0]=='C')) {
        digitalWrite(5, HIGH);
        }
      
      for (int i = 0; i < 50; i++) {
        text[i]=0;
      }

  
  
 
   
}
// Hàm reconnect thực hiện kết nối lại khi mất kết nối với MQTT Broker
void reconnect() {
  // Chờ tới khi kết nối
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Thực hiện kết nối với mqtt user và pass
    if (client.connect(clientId.c_str(),"ee", "ee")) {
      Serial.println("connected");
      client.subscribe("401");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Đợi 5s
      delay(1000);
    }
  }
}
void loop() {
  // Kiểm tra kết nối
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  if(digitalRead(btnPin)==LOW){
    Serial.println("LOW");
    client.publish("401_re", "accept");
    for (int i = 0; i < 50; i++) {
        text1[i]=0;
        text2[i]=0;
      }
      
    display.clear();
    display.drawString(0, 0, String(text1));
    display.display();
    digitalWrite(5, LOW);
    delay(500);
    }
}
