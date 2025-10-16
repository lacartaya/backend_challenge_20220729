package com.celonis.challenge.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import org.openapitools.jackson.nullable.JsonNullable;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CreateTaskRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T23:18:20.974247+02:00[Europe/Madrid]", comments = "Generator version: 7.6.0")
public class CreateTaskRequest {

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    PROJECT_GENERATION("PROJECT_GENERATION"),
    
    COUNTER("COUNTER");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  private JsonNullable<Integer> x = JsonNullable.<Integer>undefined();

  private JsonNullable<Integer> y = JsonNullable.<Integer>undefined();

  public CreateTaskRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateTaskRequest(TypeEnum type) {
    this.type = type;
  }

  public CreateTaskRequest type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public CreateTaskRequest x(Integer x) {
    this.x = JsonNullable.of(x);
    return this;
  }

  /**
   * Get x
   * @return x
  */
  
  @Schema(name = "x", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("x")
  public JsonNullable<Integer> getX() {
    return x;
  }

  public void setX(JsonNullable<Integer> x) {
    this.x = x;
  }

  public CreateTaskRequest y(Integer y) {
    this.y = JsonNullable.of(y);
    return this;
  }

  /**
   * Get y
   * @return y
  */
  
  @Schema(name = "y", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("y")
  public JsonNullable<Integer> getY() {
    return y;
  }

  public void setY(JsonNullable<Integer> y) {
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateTaskRequest createTaskRequest = (CreateTaskRequest) o;
    return Objects.equals(this.type, createTaskRequest.type) &&
        equalsNullable(this.x, createTaskRequest.x) &&
        equalsNullable(this.y, createTaskRequest.y);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, hashCodeNullable(x), hashCodeNullable(y));
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[]{a.get()}) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateTaskRequest {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    x: ").append(toIndentedString(x)).append("\n");
    sb.append("    y: ").append(toIndentedString(y)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

