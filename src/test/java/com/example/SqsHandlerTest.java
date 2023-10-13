package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@MicronautTest
class SqsHandlerTest {

  @MockBean(Service.class)
  Service serviceConsumer() {
    return mock(Service.class);
  }

  @Inject
  Service serviceMock;
  @Inject
  SqsHandler unitUnderTest;

  @Test
  void test() {
    // Given
    String dtoJsonInput = """
        {
          "testString": "hello",
          "testDouble": 3.14,
          "testBoolean": true,
          "testList": [
            {
              "testString": "child",
              "testList": []
            }
          ]
        }
        """;

    SQSEvent sqsEvent = new SQSEvent();
    SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
    message.setBody(dtoJsonInput);
    sqsEvent.setRecords(List.of(message));
    ArgumentCaptor<TestDto> dtoCaptor = ArgumentCaptor.forClass(TestDto.class);
    doNothing().when(serviceMock).consume(dtoCaptor.capture());

    // When
    SQSBatchResponse response = unitUnderTest.execute(sqsEvent);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getBatchItemFailures()).isNotNull().isEmpty();
    verify(serviceMock).consume(any(TestDto.class));
    TestDto dtoConsumed = dtoCaptor.getValue();
    SoftAssertions.assertSoftly(a -> {
      a.assertThat(dtoConsumed.getTestString()).isEqualTo("hello");
      a.assertThat(dtoConsumed.getTestDouble()).isEqualTo(3.14);
      a.assertThat(dtoConsumed.getTestBoolean()).isTrue();
      a.assertThat(dtoConsumed.getTestList()).isNotNull().hasSize(1);
    });
  }
}
