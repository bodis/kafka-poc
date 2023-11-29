package com.nitrowise.kafkapoc.utils;

import com.nitrowise.data.avro.OrderMessage;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaOrderDeserializer implements Deserializer<OrderMessage> {

    @Override
    public OrderMessage deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                DatumReader<OrderMessage> reader = new SpecificDatumReader<>(OrderMessage.getClassSchema());
                Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
                return reader.read(null, decoder);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
