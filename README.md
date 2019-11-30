# Non Blocking ModbusTCP to Kafka Data Collection
Scheduled thread that polls data from Siemenes PLC using Modbus TCP reactive Non Blocking Netty Client.
Apache Kafka producer and sends collected data in serialized Avro format using Confluent Schema Registry.
