package ltphat.cloudvault.backend.search.application.service.impl;

import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.search.application.dto.SearchResponse;
import ltphat.cloudvault.backend.search.domain.model.SearchResourceType;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private IFileRepository fileRepository;

    @Mock
    private IFolderRepository folderRepository;

    @Mock
    private ShareService shareService;

    @InjectMocks
    private SearchServiceImpl searchService;

    private UUID userId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
    }

    @Test
    void search_Global_Success() {
        String query = "test";
        List<UUID> accessibleProjectIds = List.of(projectId);
        
        when(shareService.getAccessibleProjectIds(userId)).thenReturn(accessibleProjectIds);
        
        File file = File.builder().id(UUID.randomUUID()).name("test.txt").projectId(projectId).ownerId(userId).build();
        Folder folder = Folder.builder().id(UUID.randomUUID()).name("test-folder").projectId(projectId).ownerId(userId).build();
        
        when(fileRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(accessibleProjectIds, query))
                .thenReturn(List.of(file));
        when(folderRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(accessibleProjectIds, query))
                .thenReturn(List.of(folder));

        List<SearchResponse> results = searchService.search(query, null, null, userId);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(SearchResponse::getName).containsExactlyInAnyOrder("test.txt", "test-folder");
        assertThat(results).extracting(SearchResponse::getType).containsExactlyInAnyOrder(SearchResourceType.FILE, SearchResourceType.FOLDER);
    }

    @Test
    void search_InProject_Success() {
        String query = "test";
        
        when(shareService.getAccessibleProjectIds(userId)).thenReturn(List.of(projectId));
        
        File file = File.builder().id(UUID.randomUUID()).name("test.txt").projectId(projectId).ownerId(userId).build();
        
        when(fileRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(anyList(), eq(query)))
                .thenReturn(List.of(file));
        when(folderRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(anyList(), eq(query)))
                .thenReturn(List.of());

        List<SearchResponse> results = searchService.search(query, projectId, null, userId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("test.txt");
    }

    @Test
    void search_InProject_AccessDenied() {
        String query = "test";
        when(shareService.getAccessibleProjectIds(userId)).thenReturn(List.of());

        assertThatThrownBy(() -> searchService.search(query, projectId, null, userId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void search_EmptyQuery_ReturnsEmptyList() {
        List<SearchResponse> results = searchService.search("", null, null, userId);
        assertThat(results).isEmpty();
    }
}
