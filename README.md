# Non Blocking ModbusTCP to Kafka
Scheduled thread that polls data from Siemenes PLC using Modbus TCP reactive Netty Client.
Apache Kafka producer and sends collected data in serialized Avro format using Confluent Schema Registry.

At the moment, the modbus reader is blocking the "ReadHoldingRegisters" request using "future.get()". This can be non blocking, but many PLC systems can have bad configuration and limited resources so blocking seems a good default alternative.

However, if the PLC is better configured and allows multiple requests at the same time then a the future can be just processed as a callback and everything would be non blocking, while data collection would be the same.
