

package com.leon.datalink.web.filter;


import com.leon.datalink.web.security.JwtTokenManager;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.util.BaseContextUtil;
import org.apache.commons.lang3.StringUtils;

import com.leon.datalink.core.common.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * jwt auth token filter.
 *
 * @author Leon
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    
    private static final String TOKEN_PREFIX = "Bearer ";
    
    private final JwtTokenManager tokenManager;
    
    public JwtAuthenticationTokenFilter(JwtTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String jwt = resolveToken(request);
        
        if (StringUtils.isNotBlank(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            this.tokenManager.validateToken(jwt);
            Authentication authentication = this.tokenManager.getAuthentication(jwt);
            User user = (User)authentication.getPrincipal();
            BaseContextUtil.set(BaseContextUtil.USER_NAME, user.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // TODO: 鉴权失败
        }

        try {
            chain.doFilter(request, response);
        } finally {
            BaseContextUtil.remove();
        }
    }
    
    /**
     * Get token from header.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        String jwt = request.getParameter(Constants.ACCESS_TOKEN);
        if (StringUtils.isNotBlank(jwt)) {
            return jwt;
        }
        return null;
    }
}
