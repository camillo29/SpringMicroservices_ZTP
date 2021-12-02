package pl.edu.pk.ztp.library.users.rest;

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
@RequestMapping("/users")
public class UserController {
    @Autowired
    private EurekaClient eurekaClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getAllUsers(){
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient
                .get().uri("/internal/users")
                .retrieve().bodyToMono(String.class);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getUserById(@PathVariable(value = "id") final Integer userID){
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient
                .get().uri("/internal/users/" + userID)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new ResponseStatusException(response.statusCode(),
                                response.statusCode().getReasonPhrase())))
                .bodyToMono(String.class);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable(value = "id") final Integer userID){
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient.delete().uri("/internal/users/"+userID)
                .retrieve()
                .onStatus(HttpStatus::isError,
                        response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(Void.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> postUser(@RequestBody final String user){
        final WebClient webClient = getLibraryDBServiceWebClient();
        return webClient.post().uri("/internal/users")
                .bodyValue(user)
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
