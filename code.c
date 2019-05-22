#include <LiquidCrystal.h>

//LCD's pin definition
#define LCD_RS P2_2
#define LCD_EN P2_1
#define LCD_D4 P2_0
#define LCD_D5 P1_5
#define LCD_D6 P1_4
#define LCD_D7 P1_3

//ESP8266's pin definition
#define RX P1_1
#define TX P1_2

//supersonic sensor's pin definition
#define TRIG P2_4
#define ECHO P2_3
#define TIME_OUT 5000

//pipe's pin definition
#define BUZZER P1_0

//Thong tin ve wifi
#define SSID "UrbanStation"
#define PASS "22taquangbuu"
#define DST_IP "192.168.199.61"
#define PORT "6000"
long old_warning_distance = 100;
boolean warned = false;
boolean tcp_init = false;

//Khoi tao LCD
LiquidCrystal lcd(LCD_RS, LCD_EN, LCD_D4, LCD_D5, LCD_D6, LCD_D7);


/////////////////////////////////////////////////////////////////////////////////////

void lcd_setup(){
  lcd.begin(16, 2);
  lcd.setCursor(0, 0);
  lcd.print("Start");
  delay(500);
  lcd.clear();
}

void pinmode_setup(){
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(BUZZER, OUTPUT);
  digitalWrite(BUZZER, HIGH);
}


boolean connectWiFi(){
  Serial.print("AT+CWMODE=1");
  String cmd = "AT+CWJAP=\"";
  cmd += SSID;
  cmd += "\",\"";
  cmd += PASS;
  cmd += "\"";
  Serial.println(cmd);
  delay(2000);
  if (Serial.find("OK")){
    return true;
  }
  return false;
}

void esp_setup(){
  Serial.begin(9600);
  Serial.setTimeout(5000);
  // Test if the module is ready
  Serial.println("AT+RST");
  delay(1000);
  if (Serial.find("ready")){
    lcd.setCursor(0, 0);
    lcd.print("ESP ready");
    delay(100);
    lcd.clear();
  }
  else{
    lcd.setCursor(0, 0);
    lcd.print("ESP 0 res");
    while(1);
  }
  delay(1000);
  // connect to wifi
  lcd.setCursor(0, 0);
  lcd.print("Connecting");
  boolean conned;
  conned = false;
  for (int i = 0; i < 5; i++){
    if (connectWiFi()){
      conned = true;
      break;
    }
  }
  if (!conned){
    lcd.setCursor(0, 0);
    lcd.print("fail conn");
    while(1);
  }
  delay(5000);
  Serial.println("AT+CIPMUX=0"); // configure the multiple connections mode, this is single
  delay(500);
  lcd.clear();
}

void setup(){
  lcd_setup(); // OK
  pinmode_setup(); // OK
//  esp_setup(); // OK
}

void lcd_distance(long distance){
  lcd.setCursor(0, 0);
  lcd.print("Distance:");
  lcd.setCursor(9, 0);
  lcd.print(distance);
  lcd.setCursor(12, 0);
  lcd.print("(cm)");
}

void lcd_timeout(){
  lcd.setCursor(0, 0);
  lcd.print("Echo timeout");
  delay(200);
  lcd.clear();
}


void send_data(String to_send){
  long len_to_send = to_send.length();
  String cmd = "AT+CIPSEND=" + String(len_to_send);
  Serial.println(cmd);
  if (Serial.find(">")){
    Serial.print(">");
  }
  else{
    Serial.println("AT+CIPCLOSE");
    delay(1000);
    return;
  }
  Serial.print(to_send);
}

void send_warning(long distance){
  if (warned){
    String to_send = "Warning!!!, Something is too close to your car. Distance is: " + String(distance);
    send_data(to_send);
  }
  else{
    String to_send = "Obstacle gone!";
    send_data(to_send);
  }
}



float GetDistance()
{
  long duration, distanceCm;
   
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);
  
  duration = pulseIn(ECHO, HIGH, TIME_OUT);
 
  // convert to distance
  distanceCm = (long)duration / 29.1 / 2;
  
  return distanceCm;
}


boolean initial_TCP() {
  if (!tcp_init) {
    // If have not yet warned before, then init tcp connection
    lcd.setCursor(0, 0);
    lcd.print("TCP...");
    String cmd = "AT+CIPSTART=\"TCP\",\"";
    cmd += DST_IP;
    cmd += "\",";
    cmd += PORT;
    Serial.println(cmd);
    if (Serial.find("Error")) {
      lcd.clear();
      delay(200);
      lcd.setCursor(0, 0);
      lcd.print("TCP fail");
      delay(200);
      lcd.clear();
      return false;
    }
    else {
      lcd.setCursor(0, 0);
      lcd.print("TCP succ");
      delay(200);
      lcd.clear();
    }
  }
  tcp_init = true;
  return true;
}


void loop(){
  /* If distance > 30 cm: Nothing happend
   * If distance <= 30 cm: Buzzer 
   * If distance <= 10 cm: Send message to server
   */
  long distance;
  distance = GetDistance();
  if (distance <= 0){
    digitalWrite(BUZZER, HIGH);
    lcd_timeout();
  }
  else{ // distance > 0
    lcd_distance(distance);
    if (distance <= 30){
      digitalWrite(BUZZER, LOW);
      if ((distance <= 10) && (distance <= old_warning_distance - 1)){
//        if (!initial_TCP()){
//          return;
//        }
        warned = true;
//        send_warning(distance);
//        old_warning_distance = distance;
      }
    }else{
      digitalWrite(BUZZER, HIGH);
    }
//    if (warned && (distance > 10)){
//      warned = false;
//      send_warning(distance);
//      old_warning_distance = 100;
//    }
    delay(500);
    lcd.clear();
  }
}
//void loop(){
//  
//}