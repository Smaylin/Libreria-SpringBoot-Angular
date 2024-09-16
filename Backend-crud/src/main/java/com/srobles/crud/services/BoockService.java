package com.srobles.crud.services;

import com.srobles.crud.entities.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BoockService {

    Book saveBook(Book book, MultipartFile file) throws Exception;
    Book updateBook( Book book);
    void deleteBook(Book book) throws IOException;
    List<Book> getBooks();
    Optional<Book> getBookById(Long id);
    Book updateBookImg(MultipartFile file, Book book ) throws Exception;

}
