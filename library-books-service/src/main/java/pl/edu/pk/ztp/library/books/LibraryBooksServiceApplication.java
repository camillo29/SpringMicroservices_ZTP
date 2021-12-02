package pl.edu.pk.ztp.library.books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LibraryBooksServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryBooksServiceApplication.class, args);
	}

}
