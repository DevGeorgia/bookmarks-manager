package lco.bookmarks.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan("lco.bookmarks.batch")
public class BookmarksManagerBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarksManagerBatchApplication.class, args);
	}

}
