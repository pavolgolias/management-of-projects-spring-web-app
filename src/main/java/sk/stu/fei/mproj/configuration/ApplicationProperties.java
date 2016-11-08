package sk.stu.fei.mproj.configuration;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties(prefix = "application")
@Configuration
@Getter
@Setter
public class ApplicationProperties {
    private URL backendUrl;
    private URL frontendUrl;
    private Boolean enableMailSending;

    @NotNull
    public URL buildBackendUrl(String relativePath) throws MalformedURLException {
        return concat(backendUrl, relativePath);
    }

    @NotNull
    public URL buildFrontendUrl(String relativePath) throws MalformedURLException {
        return concat(frontendUrl, relativePath);
    }

    private URL concat(URL rootUrl, String relative) throws MalformedURLException {
        final String root = rootUrl.toString();
        final String relativeSanitized = StringUtils.trimToEmpty(relative);

        if ( !(StringUtils.endsWith(root, "/")) && !(StringUtils.startsWith(relativeSanitized, "/")) ) {
            return new URL(root + "/" + relativeSanitized);
        }
        else {
            return new URL(root + relativeSanitized);
        }
    }
}
