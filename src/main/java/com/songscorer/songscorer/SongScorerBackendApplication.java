package com.songscorer.songscorer;

import com.songscorer.songscorer.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfiguration.class)
public class SongScorerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SongScorerBackendApplication.class, args);
	}

}
