#include <ESP8266WiFi.h>
#include <PubSubClient.h>

// Cập nhật thông tin
// Thông tin về wifi
#define ssid "THPT_THD"  // Tên Wifi
#define password "12341234" // Mật khẩu
// Thông tin về MQTT Broker
#define mqtt_server "192.168.0.100" // Thay bằng thông tin của bạn
const char* mqtt_topic_pub ="y_ta";   //Giữ nguyên nếu bạn tạo topic tên là demo
const char* mqtt_topic_sub ="y_ta";
String clientId="benhnhan1";

const uint16_t mqtt_port = 1883; //Port của CloudMQTT
WiFiClient espClient;
PubSubClient client(espClient);

long lastMsg = 0;
char msg[50];
int value = 0;
int tt = 0;

int ledPin1 = 12;                 // LED connected to digital pin 16 ( D0)
int ledPin2 = 10;
int btnPin1 = 4;                  // BUTTON connected to digital pin 0 ( D3)
int btnPin2 = 5;
int ledState = LOW;
char text[50];
void setup() {
  Serial.begin(9600);
  setup_wifi();
  client.setServer(mqtt_server, mqtt_port); 
  client.setCallback(callback);
  
  pinMode(ledPin1, OUTPUT);      // sets the digital pin as output
  digitalWrite ( ledPin1, LOW);
    pinMode(ledPin2, OUTPUT);      // sets the digital pin as output
  digitalWrite ( ledPin2, LOW);
  
  pinMode(4, INPUT);       // sets the digital pin as input
  pinMode(5, INPUT);
//  digitalWrite(btnPin1, HIGH);
//  digitalWrite(btnPin2, HIGH);
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
  text[0]=payload[0];
  if (text[0]=='a') {
    digitalWrite ( ledPin1, LOW);
    digitalWrite ( ledPin2, LOW);
    }
  
}
// Hàm reconnect thực hiện kết nối lại khi mất kết nối với MQTT Broker
void reconnect() {
  // Chờ tới khi kết nối
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Thực hiện kết nối với mqtt user và pass
    if (client.connect(clientId.c_str(),"benh nhan", "ee")) {
      Serial.println("connected");
      // ... và nhận lại thông tin này
      client.subscribe("401_re");
      
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
  long now = millis();
  if(digitalRead(4)==LOW){
    digitalWrite ( ledPin1, HIGH);
    Serial.println("LOW");
    client.publish("401", "Ho tro");
    delay(500);
  }

  if(digitalRead(5)==LOW){
    lastMsg = now;
    tt = 1;
    digitalWrite ( ledPin2, HIGH);
    Serial.println("LOW");
    client.publish("401", "Cap cuu");
    delay(500);
  }

  if ((now - lastMsg > 10000)&&(tt==1)){
    tt=0;
    client.publish("boss", "401:Cap cuu");
    for (int i = 0; i < 50; i++) {
      text[i] = 0;
      }
    }

//  client.publish("vl", "tuan");
//  delay(1000);
  
}
