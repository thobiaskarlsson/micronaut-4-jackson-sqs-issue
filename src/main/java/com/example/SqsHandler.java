package com.example;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Bean
public class SqsHandler extends MicronautRequestHandler<SQSEvent, SQSBatchResponse> {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Inject
  ObjectMapper objectMapper;
  @Inject
  Service service;

  @Override
  public SQSBatchResponse execute(SQSEvent sqsEvent) {
    log.info("Received SQS Event: {}", sqsEvent); // Log to ensure SQSEvent is not null in AWS runtime
    List<SQSBatchResponse.BatchItemFailure> batchItemFailures = new ArrayList<>();
    for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
      try {
        handleMessage(message);
      } catch (RuntimeException e) {
        log.warn("Unable to log message: {}", message, e);
        SQSBatchResponse.BatchItemFailure batchItemFailure =
            SQSBatchResponse.BatchItemFailure.builder()
                .withItemIdentifier(message.getMessageId())
                .build();
        batchItemFailures.add(batchItemFailure);
      }
    }
    return SQSBatchResponse.builder()
        .withBatchItemFailures(batchItemFailures)
        .build();
  }

  private void handleMessage(SQSEvent.SQSMessage sqsMessage) {
    final String body = sqsMessage.getBody();
    try {
      TestDto testDto = objectMapper.readValue(body, TestDto.class);
      service.consume(testDto);
    } catch (JsonProcessingException | RuntimeException e) {
      throw new IllegalStateException("Unable to deserialize Test DTO from JSON: " + body, e);
    }
  }
}
