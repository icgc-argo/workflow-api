package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;

/** A message that allows one to describe default parameters for a workflow engine. */
@ApiModel(
    description = "A message that allows one to describe default parameters for a workflow engine.")
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
public class DefaultWorkflowEngineParameter {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("default_value")
  private String defaultValue = null;

  public DefaultWorkflowEngineParameter name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the parameter
   *
   * @return name
   */
  @ApiModelProperty(value = "The name of the parameter")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DefaultWorkflowEngineParameter type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Describes the type of the parameter, e.g. float.
   *
   * @return type
   */
  @ApiModelProperty(value = "Describes the type of the parameter, e.g. float.")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public DefaultWorkflowEngineParameter defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * The stringified version of the default parameter. e.g. \"2.45\".
   *
   * @return defaultValue
   */
  @ApiModelProperty(value = "The stringified version of the default parameter. e.g. \"2.45\".")
  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultWorkflowEngineParameter defaultWorkflowEngineParameter =
        (DefaultWorkflowEngineParameter) o;
    return Objects.equals(this.name, defaultWorkflowEngineParameter.name)
        && Objects.equals(this.type, defaultWorkflowEngineParameter.type)
        && Objects.equals(this.defaultValue, defaultWorkflowEngineParameter.defaultValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, defaultValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DefaultWorkflowEngineParameter {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
