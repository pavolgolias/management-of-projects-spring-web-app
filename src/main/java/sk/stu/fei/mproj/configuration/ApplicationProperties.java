package sk.stu.fei.mproj.configuration;


import lombok.Getter;
import lombok.Setter;
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
    private Boolean enableFileUpload;
    private String fileUploadRootFolder;

    public URL buildBackendUrl(@NotNull String relativePath) throws MalformedURLException {
        return new URL(concatWithSlashes(backendUrl.toString(), relativePath));
    }

    public URL buildFrontendUrl(@NotNull String relativePath) throws MalformedURLException {
        return new URL(concatWithSlashes(frontendUrl.toString(), relativePath));
    }

    public String buildFilePath(@NotNull String relativePath) {
        return concatWithSlashes(fileUploadRootFolder, relativePath);
    }

    private String concatWithSlashes(String root, String relative) {
        final String rootSanitized = root.trim();
        final String relativeSanitized = relative.trim();

        if ( !rootSanitized.endsWith("/") && !relativeSanitized.startsWith("/") ) {
            return rootSanitized + "/" + relativeSanitized;
        }
        else if ( rootSanitized.endsWith("/") && relativeSanitized.startsWith("/") ) {
            return rootSanitized + relativeSanitized.substring(1);
        }
        return rootSanitized + relativeSanitized;
    }
}
