package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1, book2;

    @BeforeEach
    void setUp() {
        book1 = new Book("The Hunger Games", "Suzanne Collins", BigDecimal.valueOf(10.99), LocalDate.of(2008, 9, 14));
        book2 = new Book("Divergent", "Veronica Roth", BigDecimal.valueOf(12.99), LocalDate.of(2011, 5, 3));
    }

    @Test
    void addBook_shouldSaveAndReturnBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book1);
        Book savedBook = bookService.addBook(book1);
        assertNotNull(savedBook);
        assertEquals("The Hunger Games", savedBook.getTitle());
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    void getAllBooks_shouldReturnListOfBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_shouldReturnBook_whenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        Book foundBook = bookService.getBookById(1L);
        assertNotNull(foundBook);
        assertEquals("The Hunger Games", foundBook.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(100L));
        verify(bookRepository, times(1)).findById(100L);
    }

    @Test
    void updateBook_shouldUpdateAndReturnBook_whenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        Book updatedBook = bookService.updateBook(1L, book2);
        assertNotNull(updatedBook);
        assertEquals("Divergent", updatedBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(100L, book2));
        verify(bookRepository, times(1)).findById(100L);
    }

    @Test
    void deleteBook_shouldDeleteBook_whenBookExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);
        bookService.deleteBook(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.existsById(100L)).thenReturn(false);
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(100L));
        verify(bookRepository, times(1)).existsById(100L);
    }
}

