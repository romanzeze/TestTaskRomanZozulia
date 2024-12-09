package com.example.demo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class DocumentManagerTest {
    @Mock
    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void saveCreateNewDocument() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Java Tutorial")
                .content("Java was released in May 1995.")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author Name")
                        .build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals("Java Tutorial", savedDocument.getTitle());
        assertEquals("Java was released in May 1995.", savedDocument.getContent());
        assertEquals("author1", savedDocument.getAuthor().getId());
    }

    @Test
    void saveUpdateExistingDocument() {
        String documentId = UUID.randomUUID().toString();
        Instant createdTime = Instant.now();

        DocumentManager.Document existingDocument = DocumentManager.Document.builder()
                .id(documentId)
                .title("Java Tutorial")
                .content("Java was released in May 1995.")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author Name")
                        .build())
                .created(createdTime)
                .build();

        documentManager.save(existingDocument);

        DocumentManager.Document updatedDocument = DocumentManager.Document.builder()
                .id(documentId)
                .title("JS Tutorial")
                .content("JS was released in 1993.")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author Name")
                        .build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(updatedDocument);

        assertEquals(documentId, savedDocument.getId());
        assertEquals(createdTime, savedDocument.getCreated());
        assertEquals("JS Tutorial", savedDocument.getTitle());
        assertEquals("JS was released in 1993.", savedDocument.getContent());
    }

    @Test
    void searchFindDocumentsWhichMatchWithRequest() {
        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Java Tutorial")
                .content("Java was released in May 1995.")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author First")
                        .build())
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("JS Tutorial")
                .content("JS was released in 1993.")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author Second")
                        .build())
                .created(Instant.now())
                .build();

        DocumentManager.Document doc3 = DocumentManager.Document.builder()
                .title("Python Tutorial")
                .content("Python was released in 1991.")
                .author(DocumentManager.Author.builder()
                        .id("author3")
                        .name("Author Third")
                        .build())
                .created(Instant.now())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);
        documentManager.save(doc3);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("P", "J"))
                .containsContents(List.of("Java", "Python"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getTitle().equals("Java Tutorial")));
        assertTrue(results.stream().anyMatch(doc -> doc.getTitle().equals("Python Tutorial")));
    }
    @Test
    void searchFindDocumentsWhichMatchWithRequestIsNull() {
        DocumentManager.Document doc1 = DocumentManager.Document.builder().title("Java Tutorial").build();
        DocumentManager.Document doc2 = DocumentManager.Document.builder().title("Python Tutorial").build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        List<DocumentManager.Document> results = documentManager.search(null);


        assertEquals(2, results.size());
    }


    @Test
    void findByIdExists() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(documentId)
                .title("Java Tutorial")
                .build();

        documentManager.save(document);

        Optional<DocumentManager.Document> result = documentManager.findById(documentId);

        assertTrue(result.isPresent());
        assertEquals("Java Tutorial", result.get().getTitle());
    }
    @Test
    void findByIdDoesNotExist() {
        Optional<DocumentManager.Document> result = documentManager.findById("nonexistent-id");

        assertTrue(result.isEmpty());
    }


}
