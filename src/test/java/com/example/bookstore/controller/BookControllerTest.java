package com.example.bookstore.controller;

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book1, book2;

    @BeforeEach
    void setUp() {
        // Ensure these match your DB values
        book1 = new Book("The Hunger Games - Special Edition", "Suzanne Collins", BigDecimal.valueOf(31.99), LocalDate.of(2010, 5, 1));
        book2 = new Book("The Divergent Series", "Veronica Roth", BigDecimal.valueOf(29.50), LocalDate.of(2011, 4, 26));
    }

    @Test
    void addBook_shouldReturnCreatedBook() throws Exception {
        when(bookService.addBook(any(Book.class))).thenReturn(book1);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Hunger Games - Special Edition"));
    }

    @Test
    void getAllBooks_shouldReturnListOfBooks() throws Exception {
        List<Book> books = Arrays.asList(book1, book2);
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(book1);
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Hunger Games - Special Edition"));
    }

    @Test
    void getBookById_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        when(bookService.getBookById(100L)).thenThrow(new BookNotFoundException("Book not found with id: 100"));

        mockMvc.perform(get("/books/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id: 100"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Book Not Found"));
    }


    @Test
    void updateBook_shouldUpdateAndReturnBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(book2);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Divergent Series"));
    }

    @Test
    void updateBook_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        when(bookService.updateBook(100L, book2))
            .thenThrow(new BookNotFoundException("Book not found with id: 100"));
    }

    @Test
    void deleteBook_shouldReturnNoContent_whenBookExists() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        Mockito.doThrow(new BookNotFoundException("Book not found with id: 100"))
               .when(bookService).deleteBook(100L);

        mockMvc.perform(delete("/books/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id: 100"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Book Not Found"));
    }
}
