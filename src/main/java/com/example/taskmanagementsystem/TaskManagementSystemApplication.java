package com.example.taskmanagementsystem;

import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.security.JwtProvider;
import com.example.taskmanagementsystem.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementSystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			UserService userService,
			JwtProvider jwtProvider
	) {
		return args -> {
			User user = User.builder()
					.email("default_user@mail.test")
					.password("defoult-user-password")
					.name("Default User")
					.build();
			try {
				userService.saveUser(user);
			} catch (Exception ignored){}
			System.out.println("Demo user token: " + jwtProvider.generateToken(user.getEmail()));
		};
	}
}
