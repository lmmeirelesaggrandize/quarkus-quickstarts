package org.acme.kafka;

import java.util.Collections;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.kafka.client.producer.RecordMetadata;
import io.vertx.mutiny.kafka.admin.KafkaAdminClient;
import io.vertx.mutiny.kafka.client.consumer.KafkaConsumer;
import io.vertx.mutiny.kafka.client.producer.KafkaProducer;
import io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord;

@Path("/vertx-kafka")
@ApplicationScoped
public class VertxKafkaEndpoint {

    public static final String TOPIC = "hello-vertx";

    @Inject
    KafkaConsumer<String, String> consumer;

    @Inject
    KafkaProducer<String, String> producer;

    @Inject
    KafkaAdminClient admin;

    volatile String last;

    public void initialize(@Observes StartupEvent ev) {
        consumer.subscribe(Collections.singleton(TOPIC))
                .onItem().transformToMulti(x -> consumer.toMulti())
                .subscribe().with(
                record -> {
                    System.out.printf("Polled Record:(%s, %s, %d, %d)\n",
                            record.key(), record.value(),
                            record.partition(), record.offset());
                    last = record.key() + "-" + record.value();
                }
        );
    }

    public void terminate(@Observes ShutdownEvent ev) {
        consumer.closeAndAwait();
        producer.closeAndAwait();
        admin.closeAndAwait();
    }

    @Path("/topics")
    @GET
    public Uni<Set<String>> getTopics() {
        return admin.listTopics();
    }

    @GET
    public String getLast() {
        return last;
    }

    @Path("/")
    @POST
    public Uni<Long> post(@RestQuery String key, @RestQuery String value) {
        return producer.send(KafkaProducerRecord.create(TOPIC, key, value))
                .onItem().transform(RecordMetadata::getOffset);
    }

}
