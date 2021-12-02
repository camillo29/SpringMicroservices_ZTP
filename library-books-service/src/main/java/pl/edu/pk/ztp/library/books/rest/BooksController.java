package pl.edu.pk.ztp.library.books.rest;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
public class BooksController {
    @Autowired
    private EurekaClient eurekaClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getAllBooks(@RequestParam(value = "available", required = false) boolean showOnlyAvailable){
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient.get().uri("/internal/books?available=" + showOnlyAvailable)
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getBookRentals(@PathVariable(value = "id") Integer bookID){
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient.get().uri("/internal/books/" + bookID)
                .retrieve()
                .bodyToMono(String.class);
    }

    @PatchMapping(value = "/return/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> patchReturnBook(@PathVariable(value = "id") Integer bookID, @RequestHeader(value = "user") Integer userID){
        if(userID == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nie podano ID użytkownika");
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient.patch().uri("/internal/books/return/" + bookID)
                .header("user", String.valueOf(userID))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(String.class);
    }

    @PatchMapping(value = "/rent/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> patchRentBook(@PathVariable(value = "id") Integer bookID, @RequestHeader("user") Integer userID){
        if(userID == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nie podano ID użytkownika");
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient.patch().uri("/internal/books/rent/" + bookID)
                .header("user", String.valueOf(userID))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(String.class);
    }

    private WebClient getLibraryDBServiceWebClient(){
        InstanceInfo service = eurekaClient.getApplication("library-db-service").getInstances().get(0);
        return WebClient.builder()
                .baseUrl(String.format("http://%s:%s", service.getHostName(), service.getPort()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
