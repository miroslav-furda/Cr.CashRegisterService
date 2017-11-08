package sk.codxa.cr.cashregisterservice.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.auth0.jwt.JWT.decode;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Filter that gets invoked first with every http request made to any controller of this service.
 * Serves as a security solution. Filter checks every incoming request's header for Authorization key.
 * If "Authorization" header contains bearer token, then this token is verified with Authorization server.
 * Only requests with authorized tokens are permitted to fly further.
 */
@Component
@Log4j
@Order(value = HIGHEST_PRECEDENCE)
public class SecurityFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";
    private static final Integer BEARER_OFFSET = 7;
    private static final String TOKEN_REGEX = "^Bearer .+";

    private TokenRepository tokenRepository;

    /**
     * Constructor.
     *
     * @param tokenRepository token repo for retrieving valid tokens.
     */
    @Autowired
    public SecurityFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRequestURI().equals("/v2/api-docs") || request.getRequestURI().startsWith("/webjars") || request.getRequestURI().startsWith("/swagger")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String requestToken = request.getHeader(AUTHORIZATION);
        if (requestToken == null) {
            response.sendError(BAD_REQUEST.value(), "Missing token!");
            log.warn("Missing authorization token in http request");
            return;
        }

        if (!requestToken.matches(TOKEN_REGEX)) {
            response.sendError(BAD_REQUEST.value(), "Invalid token!");
            log.warn("Token " + requestToken + " is invalid.");
            return;
        }
        String token = requestToken.substring(BEARER_OFFSET);

        try {
            decode(token);
        } catch (JWTDecodeException e) {
            response.sendError(UNAUTHORIZED.value(), "Invalid token!");
            log.warn("Not possible to decode token " + token);
            return;
        }

        CallResponse callResponse = tokenRepository.checkTokenValidity(token);

        if (callResponse.hasError()) {
            response.sendError(UNAUTHORIZED.value(), callResponse.getError());
            log.warn("Not possible to authorize token. Call returned error " + callResponse.getError());
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
