package com.hyperativa.card_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class CardApiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void checkDatabasePassword() {
		// Conecte ao banco e verifique o hash exato
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// Hash que está no seu SQL
		String hashFromDB = "$2a$10$W3CNKvUWipBeZouwkX5CMekmXIlDybCUNBrxK8/nghT0O444VKDGy";

		// Teste várias senhas possíveis
		System.out.println("Testing 'admin': " + encoder.matches("admin", hashFromDB));
		System.out.println("Testing 'admin123': " + encoder.matches("admin123", hashFromDB));
		System.out.println("Testing 'password': " + encoder.matches("password", hashFromDB));
		System.out.println("Testing '123456': " + encoder.matches("123456", hashFromDB));

		// Gere um novo hash para "admin" para comparar
		String newHash = encoder.encode("admin");
		System.out.println("New hash for 'admin': " + newHash);
	}

}
