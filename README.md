# Modbus TCP to Kafka Producer for Siemens PLC
Scheduled thread that polls data from Siemenes PLC using Modbus TCP reactive Non Blocking Netty Client.
Uses Apache Kafka producer and sends collected data in serialized Avro format using Confluent Schema Registry.
