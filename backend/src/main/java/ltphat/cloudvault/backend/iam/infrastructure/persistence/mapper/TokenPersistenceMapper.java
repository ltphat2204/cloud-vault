package ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.iam.domain.model.VerificationToken;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaVerificationToken;

public class TokenPersistenceMapper {
    
    public static VerificationToken toDomain(JpaVerificationToken jpaToken) {
        if (jpaToken == null) return null;
        return VerificationToken.builder()
                .token(jpaToken.getToken())
                .userEmail(jpaToken.getUserEmail())
                .expiryDate(jpaToken.getExpiryDate())
                .build();
    }

    public static JpaVerificationToken toEntity(VerificationToken token) {
        if (token == null) return null;
        return JpaVerificationToken.builder()
                .token(token.getToken())
                .userEmail(token.getUserEmail())
                .expiryDate(token.getExpiryDate())
                .build();
    }

    public static ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken toDomain(ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaPasswordResetToken jpaToken) {
        if (jpaToken == null) return null;
        return ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken.builder()
                .token(jpaToken.getToken())
                .userEmail(jpaToken.getUserEmail())
                .expiryDate(jpaToken.getExpiryDate())
                .build();
    }

    public static ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaPasswordResetToken toEntity(ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken token) {
        if (token == null) return null;
        return ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaPasswordResetToken.builder()
                .token(token.getToken())
                .userEmail(token.getUserEmail())
                .expiryDate(token.getExpiryDate())
                .build();
    }
}
