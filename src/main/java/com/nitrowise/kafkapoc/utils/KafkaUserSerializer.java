package com.nitrowise.kafkapoc.utils;

import com.nitrowise.data.avro.OrderMessage;
import com.nitrowise.data.avro.UserMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class KafkaUserSerializer implements Serializer<UserMessage> {

    @Override
    public byte[] serialize(String topic, UserMessage data) {
        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
                GenericDatumWriter<UserMessage> writer = new GenericDatumWriter<>(data.getSchema());
                writer.write(data, binaryEncoder);
                binaryEncoder.flush();
                byte[] output = outputStream.toByteArray();
                log.info("output size: {}", output);
                return output;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
