package ltphat.cloudvault.backend.shared.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CursorUtils {

    public static String encode(String fieldValue, String id) {
        if (fieldValue == null || id == null) return null;
        String combined = fieldValue + "|" + id;
        return Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
    }

    public static String[] decode(String cursor) {
        if (cursor == null || cursor.isEmpty()) return null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
            return decoded.split("\\|", 2);
        } catch (Exception e) {
            return null;
        }
    }
}
