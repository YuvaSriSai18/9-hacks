#include <WiFi.h>
#include <HTTPClient.h>
#include <DHT.h>

// WiFi credentials
const char* ssid = "abhijeet";          // Replace with your WiFi SSID
const char* password = "123456789";     // Replace with your WiFi password

// Server URLs
const char* authUrl = "https://hk9zkn3x-5502.inc1.devtunnels.ms/auth/log-in";
const char* serverUrl = "https://hk9zkn3x-5502.inc1.devtunnels.ms/weather/post-data";

// Hardcoded user credentials
const char* userEmail = "yuva@gmail.com"; // Replace with actual email
const char* userPassword = "yuva@123";   // Replace with actual password

// Sensor pin mappings for ESP32
const int mq2Pin = 34; // MQ2 sensor connected to GPIO 34
const int mq8Pin = 35; // MQ8 sensor connected to GPIO 35
const int mq7Pin = 32; // MQ7 sensor connected to GPIO 32
const int mq6Pin = 33; // MQ6 sensor connected to GPIO 33
const int dhtPin = 4;  // DHT sensor connected to GPIO D4

// DHT settings
#define DHTTYPE DHT11 // Change to DHT22 if you're using a DHT22
DHT dht(dhtPin, DHTTYPE);

float mq2Value;
float mq8Value;
float mq7Value;
float mq6Value;
float temperature;
float humidity;
bool isAuthenticated = false;

void setup() {
  Serial.begin(9600); // Initialize serial communication

  // Connect to WiFi
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi.");

  // Initialize DHT sensor
  dht.begin();

  // Perform authentication
  authenticateUser();
}

void loop() {
  if (!isAuthenticated) {
    Serial.println("User is not authenticated. Skipping data transmission.");
    delay(5000); // Wait for 5 seconds before retrying
    return;
  }

  // Read the analog values from MQ2, MQ8, MQ7, and MQ6
  mq2Value = analogRead(mq2Pin);
  mq8Value = analogRead(mq8Pin);
  mq7Value = analogRead(mq7Pin);
  mq6Value = analogRead(mq6Pin);

  // Read temperature and humidity from DHT sensor
  temperature = dht.readTemperature(); // Read temperature as Celsius
  humidity = dht.readHumidity();       // Read humidity

  // Convert temperature to Kelvin
  float temperatureKelvin = temperature + 273.15;

  // Print the sensor values to the Serial Monitor
  Serial.print("MQ2 Value: ");
  Serial.print(mq2Value);
  Serial.print(" | MQ8 Value: ");
  Serial.print(mq8Value);
  Serial.print(" | MQ7 Value: ");
  Serial.print(mq7Value);
  Serial.print(" | MQ6 Value: ");
  Serial.print(mq6Value);
  Serial.print(" | Temperature: ");
  Serial.print(temperatureKelvin);
  Serial.print(" K | Humidity: ");
  Serial.print(humidity);
  Serial.println(" %");

  // Send data to the server
  sendDataToServer(temperatureKelvin, humidity);
  
  delay(1000); // Delay for 1 second (adjust as needed)
}

void authenticateUser() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(authUrl); // Specify the URL
    http.addHeader("Content-Type", "application/json"); // Specify content type as JSON

    // Create JSON object with user credentials
    String jsonData = String("{\"email\":\"") + userEmail +
                      "\",\"password\":\"" + userPassword + "\"}";

    // Send the HTTP POST request
    int httpResponseCode = http.POST(jsonData);

    // Check response code
    if (httpResponseCode > 0) {
      String response = http.getString(); // Get the response from the server
      Serial.println("Auth server response: " + response);
      
      // Check if the response contains "isAuthenticated": true
      if (response.indexOf("\"isAuthenticated\":true") != -1) {
        isAuthenticated = true;
        Serial.println("User authenticated successfully.");
      } else {
        Serial.println("Authentication failed.");
      }
    } else {
      Serial.println("Error sending authentication request: " + String(httpResponseCode));
    }

    http.end(); // Close connection
  } else {
    Serial.println("WiFi disconnected");
  }
}

void sendDataToServer(float temperatureKelvin, float humidity) {
  if (WiFi.status() == WL_CONNECTED) { // Check if the ESP32 is connected to WiFi
    HTTPClient http;
    http.begin(serverUrl); // Specify the URL
    http.addHeader("Content-Type", "application/json"); // Specify content type as JSON

// Create JSON object with sensor values and email
String jsonData = String("{\"smoke\":") + mq2Value + 
                  ",\"hydrogen\":" + mq8Value + 
                  ",\"carbon\":" + mq7Value + 
                  ",\"gasLeak\":" + mq6Value + 
                  ",\"temperature\":" + temperatureKelvin + 
                  ",\"humidity\":" + humidity + 
                  ",\"email\":\"" + userEmail + "\"}";


    // Send the HTTP POST request
    int httpResponseCode = http.POST(jsonData);

    // Check response code
    if (httpResponseCode > 0) {
      String response = http.getString(); // Get the response from the server
      Serial.println("Data server response: " + response);
    } else {
      Serial.println("Error sending POST request: " + String(httpResponseCode));
    }

    http.end(); // Close connection
  } else {
    Serial.println("WiFi disconnected");
  }
}
