package com.srobles.crud.controllers;

import com.srobles.crud.entities.Book;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.srobles.crud.services.BookServiceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:4200/")
public class BookController {

    @Autowired
    BookServiceImpl bookServiceImpl;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(){
        List<Book> books = bookServiceImpl.getBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Optional<Book> book = bookServiceImpl.getBookById(id);
        if (book.isPresent()){
        return ResponseEntity.ok(book.get());
        }else
        {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<?> saveBook(@Valid @RequestPart("book") Book book, @RequestPart("file") MultipartFile file, BindingResult result) {
        if (result.hasErrors()) {
            return funcionValidar(result);
        }
        try {
            Book savedBook = bookServiceImpl.saveBook(book, file);
            return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
        } catch (Exception e) {
            // Agrega un log detallado para entender el error
            e.printStackTrace(); // Esto imprimirá el stacktrace para entender mejor el problema
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> updateBookImg(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        Optional<Book> bookOptional = bookServiceImpl.getBookById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            try {
                Book bookDb = bookServiceImpl.updateBookImg(file, book);
                return ResponseEntity.status(HttpStatus.CREATED).body(bookDb);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book){
        try {
            Book savedBook = bookServiceImpl.updateBook(book);
            return new ResponseEntity<>(savedBook, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) throws IOException {
        Optional<Book> book = bookServiceImpl.getBookById(id);
        if (book.isPresent()){
            bookServiceImpl.deleteBook(book.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    private static ResponseEntity<Map<String, String>> funcionValidar(BindingResult result) {
        Map<String,String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
