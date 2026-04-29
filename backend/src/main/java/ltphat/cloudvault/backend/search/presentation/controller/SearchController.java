package ltphat.cloudvault.backend.search.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.search.application.dto.SearchResponse;
import ltphat.cloudvault.backend.search.application.service.ISearchService;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Global and scoped search APIs")
public class SearchController {

    private final ISearchService searchService;

    @GetMapping
    @Operation(summary = "Search files and folders", description = "Searches for files and folders by name globally or within a specific scope")
    public ResponseEntity<ApiResponse<List<SearchResponse>>> search(
            @RequestParam String q,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID folderId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<SearchResponse> results = searchService.search(q, projectId, folderId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
