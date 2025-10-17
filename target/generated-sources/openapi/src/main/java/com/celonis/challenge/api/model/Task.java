package com.celonis.challenge.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-17T15:14:54.925258+02:00[Europe/Madrid]", comments = "Generator version: 7.6.0")
public class Task {

  private String id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime creationDate;

  private JsonNullable<String> storageLocation = JsonNullable.<String>undefined();

  public Task() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Task(String id, OffsetDateTime creationDate) {
    this.id = id;
    this.creationDate = creationDate;
  }

  public Task id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Task identifier.
   * @return id
  */
  @NotNull 
  @Schema(name = "id", example = "a1b2c3", description = "Task identifier.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Task creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Task creation timestamp (UTC).
   * @return creationDate
  */
  @NotNull @Valid 
  @Schema(name = "creationDate", example = "2025-10-17T12:34:56Z", description = "Task creation timestamp (UTC).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("creationDate")
  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public Task storageLocation(String storageLocation) {
    this.storageLocation = JsonNullable.of(storageLocation);
    return this;
  }

  /**
   * Absolute file path where the generated ZIP was stored (internal use).
   * @return storageLocation
  */
  
  @Schema(name = "storageLocation", example = "/var/tmp/a1b2c3.zip", description = "Absolute file path where the generated ZIP was stored (internal use).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("storageLocation")
  public JsonNullable<String> getStorageLocation() {
    return storageLocation;
  }

  public void setStorageLocation(JsonNullable<String> storageLocation) {
    this.storageLocation = storageLocation;
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
        Objects.equals(this.creationDate, task.creationDate) &&
        equalsNullable(this.storageLocation, task.storageLocation);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, creationDate, hashCodeNullable(storageLocation));
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
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    storageLocation: ").append(toIndentedString(storageLocation)).append("\n");
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

