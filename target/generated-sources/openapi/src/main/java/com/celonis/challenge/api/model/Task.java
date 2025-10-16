package com.celonis.challenge.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Task
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T23:18:20.974247+02:00[Europe/Madrid]", comments = "Generator version: 7.6.0")
public class Task {

  private String id;

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

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    PENDING("PENDING"),
    
    RUNNING("RUNNING"),
    
    COMPLETED("COMPLETED"),
    
    CANCELED("CANCELED"),
    
    FAILED("FAILED");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private JsonNullable<OffsetDateTime> startedAt = JsonNullable.<OffsetDateTime>undefined();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private JsonNullable<OffsetDateTime> finishedAt = JsonNullable.<OffsetDateTime>undefined();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private JsonNullable<OffsetDateTime> canceledAt = JsonNullable.<OffsetDateTime>undefined();

  private JsonNullable<String> storageLocation = JsonNullable.<String>undefined();

  private JsonNullable<Integer> x = JsonNullable.<Integer>undefined();

  private JsonNullable<Integer> y = JsonNullable.<Integer>undefined();

  private JsonNullable<Integer> current = JsonNullable.<Integer>undefined();

  private JsonNullable<Float> progressPct = JsonNullable.<Float>undefined();

  public Task() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Task(String id, TypeEnum type, StatusEnum status, OffsetDateTime createdAt) {
    this.id = id;
    this.type = type;
    this.status = status;
    this.createdAt = createdAt;
  }

  public Task id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Task type(TypeEnum type) {
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

  public Task status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @NotNull 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public Task createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  */
  @NotNull @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Task startedAt(OffsetDateTime startedAt) {
    this.startedAt = JsonNullable.of(startedAt);
    return this;
  }

  /**
   * Get startedAt
   * @return startedAt
  */
  @Valid 
  @Schema(name = "startedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startedAt")
  public JsonNullable<OffsetDateTime> getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(JsonNullable<OffsetDateTime> startedAt) {
    this.startedAt = startedAt;
  }

  public Task finishedAt(OffsetDateTime finishedAt) {
    this.finishedAt = JsonNullable.of(finishedAt);
    return this;
  }

  /**
   * Get finishedAt
   * @return finishedAt
  */
  @Valid 
  @Schema(name = "finishedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("finishedAt")
  public JsonNullable<OffsetDateTime> getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(JsonNullable<OffsetDateTime> finishedAt) {
    this.finishedAt = finishedAt;
  }

  public Task canceledAt(OffsetDateTime canceledAt) {
    this.canceledAt = JsonNullable.of(canceledAt);
    return this;
  }

  /**
   * Get canceledAt
   * @return canceledAt
  */
  @Valid 
  @Schema(name = "canceledAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("canceledAt")
  public JsonNullable<OffsetDateTime> getCanceledAt() {
    return canceledAt;
  }

  public void setCanceledAt(JsonNullable<OffsetDateTime> canceledAt) {
    this.canceledAt = canceledAt;
  }

  public Task storageLocation(String storageLocation) {
    this.storageLocation = JsonNullable.of(storageLocation);
    return this;
  }

  /**
   * Get storageLocation
   * @return storageLocation
  */
  
  @Schema(name = "storageLocation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("storageLocation")
  public JsonNullable<String> getStorageLocation() {
    return storageLocation;
  }

  public void setStorageLocation(JsonNullable<String> storageLocation) {
    this.storageLocation = storageLocation;
  }

  public Task x(Integer x) {
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

  public Task y(Integer y) {
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

  public Task current(Integer current) {
    this.current = JsonNullable.of(current);
    return this;
  }

  /**
   * Get current
   * @return current
  */
  
  @Schema(name = "current", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("current")
  public JsonNullable<Integer> getCurrent() {
    return current;
  }

  public void setCurrent(JsonNullable<Integer> current) {
    this.current = current;
  }

  public Task progressPct(Float progressPct) {
    this.progressPct = JsonNullable.of(progressPct);
    return this;
  }

  /**
   * 0..100 (opcional)
   * @return progressPct
  */
  
  @Schema(name = "progressPct", description = "0..100 (opcional)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("progressPct")
  public JsonNullable<Float> getProgressPct() {
    return progressPct;
  }

  public void setProgressPct(JsonNullable<Float> progressPct) {
    this.progressPct = progressPct;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Task task = (Task) o;
    return Objects.equals(this.id, task.id) &&
        Objects.equals(this.type, task.type) &&
        Objects.equals(this.status, task.status) &&
        Objects.equals(this.createdAt, task.createdAt) &&
        equalsNullable(this.startedAt, task.startedAt) &&
        equalsNullable(this.finishedAt, task.finishedAt) &&
        equalsNullable(this.canceledAt, task.canceledAt) &&
        equalsNullable(this.storageLocation, task.storageLocation) &&
        equalsNullable(this.x, task.x) &&
        equalsNullable(this.y, task.y) &&
        equalsNullable(this.current, task.current) &&
        equalsNullable(this.progressPct, task.progressPct);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, status, createdAt, hashCodeNullable(startedAt), hashCodeNullable(finishedAt), hashCodeNullable(canceledAt), hashCodeNullable(storageLocation), hashCodeNullable(x), hashCodeNullable(y), hashCodeNullable(current), hashCodeNullable(progressPct));
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
    sb.append("class Task {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    startedAt: ").append(toIndentedString(startedAt)).append("\n");
    sb.append("    finishedAt: ").append(toIndentedString(finishedAt)).append("\n");
    sb.append("    canceledAt: ").append(toIndentedString(canceledAt)).append("\n");
    sb.append("    storageLocation: ").append(toIndentedString(storageLocation)).append("\n");
    sb.append("    x: ").append(toIndentedString(x)).append("\n");
    sb.append("    y: ").append(toIndentedString(y)).append("\n");
    sb.append("    current: ").append(toIndentedString(current)).append("\n");
    sb.append("    progressPct: ").append(toIndentedString(progressPct)).append("\n");
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

