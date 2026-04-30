package ltphat.cloudvault.backend.notifications.presentation.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry the messages back to the client
        // on destinations prefixed with /topic or /queue
        config.enableSimpleBroker("/topic", "/queue");
        
        // Designate the /app prefix for messages that are bound for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // Designate the /user prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws-notifications endpoint, enabling the SockJS fallback options
        // so that alternate transports can be used if WebSocket is not available.
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                .withSockJS();
                
        // Also register without SockJS for direct WebSocket clients
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:3000");
    }
}
