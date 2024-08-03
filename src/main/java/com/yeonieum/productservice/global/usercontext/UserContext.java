package com.yeonieum.productservice.global.usercontext;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * HTTP 요청 사용자 정보 바인딩 객체
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserContext {
    public static final String TRANSACTION_ID = "transaction-id";
    public static final String AUTH_TOKEN = "auth-token";
    public static final String USER_ID = "user-id";
    public static final String SERVICE_ID = "service-id";
    public static final String UNIQUE_ID = "unique-id";

    @Builder.Default
    private String transactionId = new String();
    @Builder.Default
    private String authToken = new String();
    @Builder.Default
    private String userId = new String();
    @Builder.Default
    private String serviceId = new String();
    @Builder.Default
    private String uniqueId = new String();

}
