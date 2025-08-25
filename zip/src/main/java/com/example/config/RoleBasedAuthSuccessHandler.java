// src/main/java/com/example/security/RoleBasedAuthSuccessHandler.java
package com.example.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class RoleBasedAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Mặc định về trang chủ
        String targetUrl = "/";

        if (roles.contains("ROLE_ADMIN")) {
            targetUrl = "/admin/dashboard";
        } else if (roles.contains("ROLE_STYLIST")) {
            targetUrl = "/stylist"; // nếu bạn có dashboard stylist
        }

        // Cho SavedRequest hoạt động (nếu user bị chặn tại 1 trang, sau login sẽ quay lại),
        // còn nếu không có SavedRequest thì dùng targetUrl ở trên.
        setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
