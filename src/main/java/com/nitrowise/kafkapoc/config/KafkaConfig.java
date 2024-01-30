package com.nitrowise.kafkapoc.config;

import com.nitrowise.data.avro.OrderMessage;
import com.nitrowise.data.avro.UserMessage;
import com.nitrowise.kafkapoc.utils.KafkaOrderDeserializer;
import com.nitrowise.kafkapoc.utils.KafkaOrderSerializer;
import com.nitrowise.kafkapoc.utils.KafkaUserDeserializer;
import com.nitrowise.kafkapoc.utils.KafkaUserSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.EntityManagerFactory;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> orderKafkaConfig() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(null));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaOrderSerializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaOrderDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-client");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ProducerFactory<Long, OrderMessage> orderProducerFactory() {
        return new DefaultKafkaProducerFactory<>(orderKafkaConfig());
    }

    @Bean
    public KafkaTemplate<Long, OrderMessage> orderKafkaTemplate() {
        KafkaTemplate<Long, OrderMessage> kafkaTemplate = new KafkaTemplate<>(orderProducerFactory());
        kafkaTemplate.setDefaultTopic("RawOrderTopic");
        return kafkaTemplate;
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, OrderMessage>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, OrderMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, OrderMessage> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(orderKafkaConfig());
    }

    @Bean
    public Map<String, Object> userKafkaConfig() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(null));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaUserSerializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaUserDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-client");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ProducerFactory<Long, UserMessage> userProducerFactory() {
        return new DefaultKafkaProducerFactory<>(userKafkaConfig());
    }

    @Bean
    public KafkaTemplate<Long, UserMessage> userKafkaTemplate() {
        KafkaTemplate<Long, UserMessage> kafkaTemplate = new KafkaTemplate<>(userProducerFactory());
        kafkaTemplate.setDefaultTopic("UserTopic");
        return kafkaTemplate;
    }

    @Bean
    public Map<String, Object> transactionalUserKafkaConfig() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(null));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaUserSerializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaUserDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-client");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Enable auto commit false
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // Isolation level to read committed messages only
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // Ensure that the transactionIdPrefix is set to enable transaction support
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "txn-id-" + UUID.randomUUID().toString());

        return props;
    }

    @Bean
    public ProducerFactory<Long, UserMessage> transactionalUserProducerFactory() {
        return new DefaultKafkaProducerFactory<>(transactionalUserKafkaConfig());
    }

    @Bean
    public KafkaTransactionManager<Long, UserMessage> kafkaTransactionManager(ProducerFactory<Long, UserMessage> transactionalUserProducerFactory) {
        return new KafkaTransactionManager<>(transactionalUserProducerFactory);
    }

    @Bean
    public KafkaTemplate<Long, UserMessage> transactionalUserKafkaTemplate() {
        KafkaTemplate<Long, UserMessage> kafkaTemplate = new KafkaTemplate<>(transactionalUserProducerFactory());
        kafkaTemplate.setDefaultTopic("UserTopic");
        return kafkaTemplate;
    }

    @Primary
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
