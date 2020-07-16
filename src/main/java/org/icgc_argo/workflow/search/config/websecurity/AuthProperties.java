package org.icgc_argo.workflow.search.config.websecurity;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    String jwtPublicKeyUrl;

    String jwtPublicKeyStr;

    GraphqlScopes graphqlScopes;

    @Value
    @ConstructorBinding
    public static class GraphqlScopes {
        ImmutableList<String> queryOnly;
        ImmutableList<String> queryAndMutation;

        public GraphqlScopes(List<String> queryOnly, List<String> queryAndMutation) {
            this.queryOnly = ImmutableList.copyOf(queryOnly);
            this.queryAndMutation = ImmutableList.copyOf(queryAndMutation);
        }
    }
}
