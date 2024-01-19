package com.nitrowise.kafkapoc.rest;

import com.nitrowise.data.avro.OrderMessage;
import com.nitrowise.kafkapoc.repository.UserRepository;
import com.sagemcom.avro.hes.IReading;
import com.sagemcom.avro.hes.IntervalBlock;
import com.sagemcom.avro.hes.Meter;
import com.sagemcom.avro.hes.MeterReading;
import com.sagemcom.avro.hes.MeterReadsReplyMessage;
import com.sagemcom.avro.hes.MeterReadsReplyMessageHeader;
import com.sagemcom.avro.hes.MeterReadsReplyMessagePayload;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meters")
@Slf4j
public class MeterController {

    @Autowired
    private KafkaTemplate<Long, OrderMessage> orderKafkaTemplate;

    @Autowired
    private UserRepository userRepository;

    private static Faker FAKER = new Faker();

    @PostMapping("/generate")
    public void generate() {
        log.info("generate meter");

        MeterReadsReplyMessage message = new MeterReadsReplyMessage();
        MeterReadsReplyMessageHeader header = new MeterReadsReplyMessageHeader();
        header.setVerb("reply");
        header.setNoun("MeterReads");
        header.setRevision(2);
        header.setDateTime("2023-07-06T09:04:14.922+02:00");
        header.setSource("SICONIA-HES");
        header.setMessageID("69ea0eef-15bf-4d3b-a486-5c1c43fd1cff");
        message.setHeader(header);

        MeterReadsReplyMessagePayload payload = new MeterReadsReplyMessagePayload();
        message.setPayload(payload);

        List<MeterReading> meterReadings = new ArrayList<>();
        payload.setMeterReading(meterReadings);

        Meter meter = new Meter();
        meter.setIdType("METER_X_ELECTRONIC_ID");
        meter.setMRID("3245432342344441");
        meter.setPathName("SICONIA-HES");

        for (int i = 0; i < 3; i++) {
            MeterReading meterReading = new MeterReading();
            meterReading.setMeter(meter);

            List<IntervalBlock> blocks = new ArrayList<>();
            for (int x = 0; x < 2; x++) {
                IntervalBlock block = new IntervalBlock();
                block.setReadingTypeId("P15MIN_1-1:1.5.0");
                List<IReading> iReadings = new ArrayList<>();
                block.setIReadings(iReadings);

                for (int y = 0; y < 96; y++) {
                    IReading reading = new IReading();
                    reading.setEndTime("2023-07-02T00:00:00.000+02:00");
                    reading.setIntervalLength("900");
                    reading.setValue("1.0");
                    reading.setFlags("0");
                    reading.setCollectionTime("2023-07-02T23:59:59");
                    iReadings.add(reading);
                }
                blocks.add(block);
            }
            meterReading.setIntervalBlock(blocks);
            meterReadings.add(meterReading);
        }

        try {
            ByteBuffer bb = message.toByteBuffer();
            System.out.println(bb.array().length);
        } catch (Exception e) {

        }
    }

}
