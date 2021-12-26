
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

#include <Wire.h>
#include <SSD1306.h>

SSD1306  display(0x3c, 5, 4); //SDA,SCL
int thoi_gian = 0;
// Cập nhật thông tin
// Thông tin về wifi
#define ssid "THPT_THD"  // Tên Wifi
#define password "12341234" // Mật khẩu
// Thông tin về MQTT Broker
#define mqtt_server "broker.hivemq.com" // Thay bằng thông tin của bạn
const char* mqtt_topic_pub ="y_ta";   //Giữ nguyên nếu bạn tạo topic tên là demo
const char* mqtt_topic_sub ="boss";
String clientId="quan ly";

const uint16_t mqtt_port = 1883; //Port của CloudMQTT
WiFiClient espClient;
PubSubClient client(espClient);

long lastMsg = 0;
char msg[50];
char text[50];
int value = 0;
void setup() {
  pinMode(2, OUTPUT);
  Serial.begin(9600);
  pinMode(0, INPUT);
  setup_wifi();
  display.init();
  //display.flipScreenVertically(); //đảo chiều
  display.setFont(ArialMT_Plain_24);
  display.drawString(0, 0, "Hello world");
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
  display.drawString(0, 0, String(text));
  display.display();
  Serial.println();
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    text[i]=payload[i];
  }

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
    if (client.connect(clientId.c_str(),"quanly", "ee")) {
      Serial.println("connected");
      client.subscribe("boss");
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
  if(digitalRead(0)==LOW) {
    client.publish("401_re", "accept");
    client.publish("402_re", "accept");
    digitalWrite(2, HIGH);
    display.clear();
    display.drawString(0, 0, "");
    display.display();
    delay(500);
    }
}
