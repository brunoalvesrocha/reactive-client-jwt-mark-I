package com.example.reactiveclientjwtmarkI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ReactiveClientJwtMarkIApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveClientJwtMarkIApplication.class, args);
	}
	@Bean
	CommandLineRunner runner() {
		return args ->
				authenticate(createToken());
	}

	public static String createToken() {

		WebClient client = WebClient.builder()
				.baseUrl("http://localhost:9966/api")
				.defaultHeader("Content-Type", "application/json")
				.defaultHeader("X-Requested-With", "XMLHttpRequest")
				.build();
		return client
				.post().uri("/auth/login")
				.body(Mono.just("{\n" +
						"    \"username\": \"bruno@bruno.com\",\n" +
						"    \"password\": \"test1234\"\n" +
						"}"), String.class)
				.retrieve()
				.bodyToMono(TokenUser.class)
				.map(ret -> ret.getToken()).block();
	}

	public static void authenticate(String token) {
		WebClient.create("http://localhost:9966/api/me")
				.get()
				.header("Content-Type", "application/json")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("Authorization", "Bearer " + token)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class)
				.subscribe(System.out::println);
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TokenUser {
	String token;
	String refreshToken;
}
