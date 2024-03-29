package sk.stu.fei.mproj.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter requests using JWT (json web token). Token is stored in HTTP header.
 */
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.token.header}")
    private String tokenHeader;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private AuthenticatedUserDetailsService userDetailsService;

    private static String extractAuthToken(String authHeader) {
        if ( authHeader != null ) {
            Pattern p = Pattern.compile("[Bb]earer (.*)");
            Matcher m = p.matcher(authHeader);
            if ( m.find() ) {
                return m.group(1);
            }
        }

        return null;
    }

    private static List<GrantedAuthority> anonymousAuthority() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return authorities;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = extractAuthToken(httpRequest.getHeader(this.tokenHeader));

        if ( authToken == null ) {
            authToken = httpRequest.getHeader("api_key");
        }

        final String username = this.tokenUtils.getUsernameFromToken(authToken);

        if ( username != null && isNotAuthenticated(SecurityContextHolder.getContext().getAuthentication()) ) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if ( this.tokenUtils.validateToken(authToken, userDetails) ) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        else if ( SecurityContextHolder.getContext().getAuthentication() == null ) {
            SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("anonymousToken", "anonymous", anonymousAuthority()));
        }

        chain.doFilter(request, response);
    }

    private boolean isNotAuthenticated(Authentication authentication) {
        if ( authentication == null ) {
            return true;
        }
        if ( !(authentication.isAuthenticated()) ) {
            return true;
        }
        if ( authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS")) ) {
            return true;
        }

        return false;
    }
}
