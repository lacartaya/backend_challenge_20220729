package com.celonis.challenge.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ApiError
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-19T22:54:46.396694+02:00[Europe/Madrid]", comments = "Generator version: 7.6.0")
public class ApiError {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;

  private Integer status;

  private String error;

  private String message;

  private String path;

  private String code;

  @Valid
  private List<String> details = new ArrayList<>();

  public ApiError() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ApiError(OffsetDateTime timestamp, Integer status, String error, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public ApiError timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * When the error was produced.
   * @return timestamp
  */
  @NotNull @Valid 
  @Schema(name = "timestamp", description = "When the error was produced.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public ApiError status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * HTTP status code (e.g. 404, 500).
   * @return status
  */
  @NotNull 
  @Schema(name = "status", description = "HTTP status code (e.g. 404, 500).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public ApiError error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Short error name.
   * @return error
  */
  @NotNull 
  @Schema(name = "error", description = "Short error name.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public ApiError message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Human-readable description.
   * @return message
  */
  @NotNull 
  @Schema(name = "message", description = "Human-readable description.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ApiError path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Request path.
   * @return path
  */
  
  @Schema(name = "path", description = "Request path.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ApiError code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Application-specific error code.
   * @return code
  */
  
  @Schema(name = "code", description = "Application-specific error code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ApiError details(List<String> details) {
    this.details = details;
    return this;
  }

  public ApiError addDetailsItem(String detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  /**
   * Optional list of extra error details.
   * @return details
  */
  
  @Schema(name = "details", description = "Optional list of extra error details.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public List<String> getDetails() {
    return details;
  }

  public void setDetails(List<String> details) {
    this.details = details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiError apiError = (ApiError) o;
    return Objects.equals(this.timestamp, apiError.timestamp) &&
        Objects.equals(this.status, apiError.status) &&
        Objects.equals(this.error, apiError.error) &&
        Objects.equals(this.message, apiError.message) &&
        Objects.equals(this.path, apiError.path) &&
        Objects.equals(this.code, apiError.code) &&
        Objects.equals(this.details, apiError.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, status, error, message, path, code, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiError {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

