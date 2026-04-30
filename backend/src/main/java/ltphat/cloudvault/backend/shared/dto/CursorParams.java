package ltphat.cloudvault.backend.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorParams {
    private String cursor;
    private Integer size;
    private String sortBy;
    private String direction;

    public int getPageSize() {
        return (size == null || size <= 0) ? 20 : size;
    }

    public String getSortField() {
        return (sortBy == null || sortBy.isEmpty()) ? "createdAt" : sortBy;
    }

    public boolean isAscending() {
        return "ASC".equalsIgnoreCase(direction);
    }
}
