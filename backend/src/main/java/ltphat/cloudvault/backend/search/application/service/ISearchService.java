package ltphat.cloudvault.backend.search.application.service;

import ltphat.cloudvault.backend.search.application.dto.SearchResponse;

import java.util.List;
import java.util.UUID;

public interface ISearchService {
    List<SearchResponse> search(String query, UUID projectId, UUID folderId, UUID userId);
}
