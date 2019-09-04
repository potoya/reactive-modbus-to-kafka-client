/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedro.plc.collector.kafka.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.StringJoiner;


/**
 * @author PC
 */
public class MessageLogUtils {

    public static String buildLogMessage(RecordMetadata record) {
        return new StringJoiner("\n").add("\nWrote record to")
                .add("Topic:" + record.topic())
                .add("Partition:" + record.partition())
                .add("Offset:" + record.offset())
                .add("Timestamp:" + record.timestamp())
                .toString();
    }
}
