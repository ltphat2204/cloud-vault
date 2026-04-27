package ltphat.cloudvault.backend.shared.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieUtils {

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.refresh-expiration}")
    private int refreshExpiration;

    public void createCookie(HttpServletResponse response, String value) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshExpiration / 1000); // convert to seconds
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public String getCookieValue(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return (cookie != null) ? cookie.getValue() : null;
    }
}
