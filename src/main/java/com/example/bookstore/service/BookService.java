package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    public Book addBook(Book book) {
        logger.info("Adding new book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        logger.debug("Fetching all books from database");
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        logger.info("Fetching book with ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found", id);
                    return new BookNotFoundException("Book with ID " + id + " not found");
                });
    }

    public Book updateBook(Long id, Book updatedBook) {
        logger.info("Updating book with ID: {}", id);
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setPrice(updatedBook.getPrice());
                    book.setPublishedDate(updatedBook.getPublishedDate());
                    logger.debug("Book with ID {} updated successfully", id);
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found for update", id);
                    return new BookNotFoundException("Book with ID " + id + " not found");
                });
    }

    public void deleteBook(Long id) {
        logger.warn("Attempting to delete book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            logger.error("Book with ID {} not found for deletion", id);
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        bookRepository.deleteById(id);
        logger.info("Book with ID {} deleted successfully", id);
    }
}
