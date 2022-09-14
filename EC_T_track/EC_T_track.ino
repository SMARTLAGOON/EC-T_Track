#include <TinyGPSPlus.h>
#include <OneWire.h>
#include <Wire.h>
#include <Time.h>
#include <TimeLib.h>

#define GPSBaud 9600 
#define BTBaud 9600 
#define Serial_Monitor_Baud 115200   

#define StartConvert 0
#define ReadTemperature 1

TinyGPSPlus gps;

const byte numReadings = 20; 
byte ECsensorPin = A1;  
byte DS18B20_Pin = 2; 
unsigned int AnalogSampleInterval = 25, printInterval = 700, tempSampleInterval = 850;
unsigned int readings[numReadings];     
byte index = 0;                 
unsigned long AnalogValueTotal = 0;                
unsigned int AnalogAverage = 0, averageVoltage = 0;         
unsigned long AnalogSampleTime, printTime, tempSampleTime;
float temperature, ECcurrent;

OneWire ds(DS18B20_Pin);

void setup()
{
  Serial.begin(Serial_Monitor_Baud); 
  Serial1.begin(BTBaud);
  Serial3.begin(GPSBaud);

  for (byte thisReading = 0; thisReading < numReadings; thisReading++)
    readings[thisReading] = 0;
  TempProcess(StartConvert);
  AnalogSampleTime = millis();
  printTime = millis();
  tempSampleTime = millis();

}
    
void loop()
{
  while (Serial3.available() > 0)
    if (gps.encode(Serial3.read()))
      displayInfo();
      
  if (millis() > 5000 && gps.charsProcessed() < 10)
  {
    Serial1.println("V;000000;000000;00;00;00;00");
  }
}

void displayInfo()
{
     if (Serial1.available()){
     String msg = "";
   
     String clc_format = "";
    
     if (millis() - AnalogSampleTime >= AnalogSampleInterval)
     {
       if (gps.location.isValid()) {
          msg += "G";
          msg += ";";
          msg += String(gps.location.lat(), 6);
          msg += ";";
          msg += String(gps.location.lng(), 6);
          msg += ";";
        }else{
          msg += "B";
          msg += ";";
          msg += "000000";
          msg += ";";
          msg += "000000";
          msg += ";";
          }
      }
      if (millis() - AnalogSampleTime >= AnalogSampleInterval)
      {
        AnalogSampleTime = millis();
        AnalogValueTotal = AnalogValueTotal - readings[index];
        readings[index] = analogRead(ECsensorPin);
        AnalogValueTotal = AnalogValueTotal + readings[index];
        index = index + 1;
        if (index >= numReadings)
          index = 0;
        AnalogAverage = AnalogValueTotal / numReadings;
      }
      if (millis() - tempSampleTime >= tempSampleInterval)
      {
        tempSampleTime = millis();
        temperature = TempProcess(ReadTemperature);
        TempProcess(StartConvert);
      }
      if (millis() - printTime >= printInterval)
      {
        printTime = millis();
        averageVoltage = AnalogAverage * (float)5000 / 1024;
        clc_format += String(AnalogAverage) + ";";
        clc_format += String(averageVoltage) + ";";
        clc_format += String(temperature) + ";";
        float TempCoefficient = 1.0 + 0.0185 * (temperature - 25.0);
        float CoefficientVolatge = (float)averageVoltage / TempCoefficient;
        if (CoefficientVolatge <= 448)ECcurrent = 6.84 * CoefficientVolatge - 64.32;
        else if (CoefficientVolatge <= 1457)ECcurrent = 6.98 * CoefficientVolatge - 127;
        else ECcurrent = 5.3 * CoefficientVolatge + 2278;
        ECcurrent /= 1000;
        clc_format += String(ECcurrent) + ";";
        msg += clc_format;
        Serial1.println(msg);
      }

     }
    
}

float TempProcess(bool ch)
{
  static byte data[12];
  static byte addr[8];
  static float TemperatureSum;
  if (!ch) {
    if ( !ds.search(addr)) {
      ds.reset_search();
      return 0;
    }
    if ( OneWire::crc8( addr, 7) != addr[7]) {
      return 0;
    }
    if ( addr[0] != 0x10 && addr[0] != 0x28) {
      return 0;
    }
    ds.reset();
    ds.select(addr);
    ds.write(0x44, 1);
  }
  else {
    byte present = ds.reset();
    ds.select(addr);
    ds.write(0xBE);
    for (int i = 0; i < 9; i++) { 
      data[i] = ds.read();
    }
    ds.reset_search();
    byte MSB = data[1];
    byte LSB = data[0];
    float tempRead = ((MSB << 8) | LSB);
    TemperatureSum = tempRead / 16;
  }
  return TemperatureSum;
}
