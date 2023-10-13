package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Getter
@With
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

  @JsonProperty("testString")
  private String testString;
  @JsonProperty("testDouble")
  private Double testDouble;
  @JsonProperty("testBoolean")
  private Boolean testBoolean;
  @JsonProperty("testList")
  private List<TestDto> testList;

}
