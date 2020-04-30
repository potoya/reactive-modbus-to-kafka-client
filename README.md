# Non Blocking ModbusTCP to Kafka
Scheduled thread that polls data from Siemenes PLC using Modbus TCP reactive Netty Client.
Apache Kafka producer and sends collected data in serialized Avro format using Confluent Schema Registry.

At the moment, the modbus reader is blocking the "ReadHoldingRegisters" request using "future.get()". Will create a switch in a further version to turn on/off the non-blocking/blocking behavior. Is important to evaluate if the PLC has enough computing power to handle concurrent TCP requests without robbing executing time from more important automation logic to signal move of actuators.

However, if the PLC is better configured and allows multiple requests at the same time then the future can be just processed as a callback and the reading would be non blocking (processed in netty fork join), while data collection would be the same.

# Credits
Special thanks to open source project https://github.com/digitalpetri/modbus for the amazing Modbus library.
