package pl.edu.pk.ztp.library.db.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.pk.ztp.library.db.dto.BookDTO;
import pl.edu.pk.ztp.library.db.dto.UserDTO;
import pl.edu.pk.ztp.library.db.repository.BookRepository;
import pl.edu.pk.ztp.library.db.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/internal/books")
public class BooksController {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<BookDTO> getAllBooks(@RequestParam(value = "available", required = false) final boolean showOnlyAvailable){
        return bookRepository.findAll(showOnlyAvailable);
    }

    @GetMapping("/{id}")
    public BookDTO getBookRentals(@PathVariable("id") final Integer bookID){
        return bookRepository.findBookById(bookID);
    }

    @PatchMapping("/rent/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO patchRentBook(@PathVariable("id") final Integer bookID, @RequestHeader(value = "user") final Integer userID) {
        try {
            UserDTO user = userRepository.findByUserId(userID);
            return bookRepository.rentBook(bookID, user);
        } catch (Exception e) {
            if (e.getMessage().equals("Podany identyfikator książki nie istnieje lub jest nieprawidłowy"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            else if (e.getMessage().equals("Nie ma dostępnych wolnych egzemplarzy dla podanego identyfikatora książki"))
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Podany identyfikator użytkownika nie istnieje lub jest nieprawidłowy");
            }
        }
    }

    @PatchMapping("/return/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO patchReturnBook(@PathVariable("id") final Integer bookID, @RequestHeader(value = "user") final Integer userID){
        try {
            UserDTO user = userRepository.findByUserId(userID);
            return bookRepository.returnBook(bookID, user);
        } catch (Exception e) {
            if (e.getMessage().equals("Podany identyfikator książki nie istnieje lub jest nieprawidłowy"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            else if (e.getMessage().equals("Użytkownik nie ma aktualnie wypożyczonych egzemplarzy dla podanego identyfikatora książki"))
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Podany identyfikator użytkownika nie istnieje lub jest nieprawidłowy");
            }
        }
    }
}
