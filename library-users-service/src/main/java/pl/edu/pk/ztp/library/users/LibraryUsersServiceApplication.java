package pl.edu.pk.ztp.library.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LibraryUsersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryUsersServiceApplication.class, args);
	}

}
