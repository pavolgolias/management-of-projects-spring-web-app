package sk.stu.fei.mproj.security;


import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import sk.stu.fei.mproj.domain.enums.AccountRole;

import java.util.ArrayList;
import java.util.Collection;

public class RoleSecuredAnnotationMetadataExtractor implements AnnotationMetadataExtractor<RoleSecured> {
    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends ConfigAttribute> extractAttributes(RoleSecured roleSecured) {
        AccountRole[] attributeTokens = roleSecured.value();
        if ( attributeTokens.length == 0 ) {
            attributeTokens = AccountRole.values();
        }

        ArrayList<SecurityConfig> attributes = new ArrayList<>(attributeTokens.length);

        for ( AccountRole role : attributeTokens ) {
            // add prefix ROLE_ to enable RoleVoter
            attributes.add(new SecurityConfig("ROLE_" + role.name().toUpperCase()));
        }

        return attributes;
    }
}
