package docswave.test.sj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SjApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(SjApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		int i = 0;
		while (true) {
			Thread.sleep(3000);
			if (i == Integer.MAX_VALUE)
				break;

			log.info("Hello World ~ ::: {}", ++i);
		}
	}
}
