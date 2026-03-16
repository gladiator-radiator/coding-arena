package cz.arena.coding_arena.interceptor;

import cz.arena.coding_arena.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String headerId = request.getHeader("X-User-Id");

        if (headerId != null) {
            try {
                UserContext.setUserId(Long.parseLong(headerId));
            } catch (NumberFormatException e) {
                // Sent error
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id must be a number");
            }
        }

        if (headerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required X-User-Id header");
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }
}