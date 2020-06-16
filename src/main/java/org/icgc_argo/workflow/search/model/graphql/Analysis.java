package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Analysis {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String analysisId;

    private List<Run> inputFor;

    private String producedBy;

    @SneakyThrows
    public static Analysis parse(@NonNull Map<String, Object> sourceMap) {
        return MAPPER.convertValue(sourceMap, Analysis.class);
    }

}
