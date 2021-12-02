package pl.edu.pk.ztp.library.db.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.pk.ztp.library.db.dto.UserDTO;
import pl.edu.pk.ztp.library.db.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class UsersController {
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable("id") final Integer userID){
        try{
            if(userID == null || userID != (Integer)userID) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brak ID użytkownika lub ID w złym formacie");
            }
            return userRepository.findByUserId(userID);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Użytkownik o podanym ID nie istnieje");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") final Integer userID){
        try {
            if(userID == null || userID != (Integer)userID) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brak ID użytkownika lub ID w złym formacie");
            }
            if(!userRepository.deleteUserById(userID)){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Użytkownik o podanym ID nie istnieje");
            }
        } catch(final UnsupportedOperationException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @PostMapping
    public UserDTO postUser(@RequestBody final UserDTO user){
        return userRepository.createUser(user);
    }





}
