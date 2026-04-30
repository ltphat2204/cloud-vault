package ltphat.cloudvault.backend.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
    private List<T> items;
    private String nextCursor;
    private boolean hasNext;

    public static <T> CursorPageResponse<T> of(List<T> items, String nextCursor, boolean hasNext) {
        return CursorPageResponse.<T>builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
