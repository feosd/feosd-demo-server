package org.feosd.admin;


import org.feosd.auth.client.AuthJerseyResourceConfig;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Component
public class CustomJerseyResourceConfig extends AuthJerseyResourceConfig {
    public CustomJerseyResourceConfig() {
        String scanAppPackage = "com.**.api";
        ClassPathScanningCandidateComponentProvider appScanner = new ClassPathScanningCandidateComponentProvider(false);
        appScanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        appScanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
        this.registerClasses(appScanner.findCandidateComponents(scanAppPackage).stream()
                .map(beanDefinition -> ClassUtils
                        .resolveClassName(beanDefinition.getBeanClassName(), this.getClassLoader()))
                .collect(Collectors.toSet()));
    }
}
