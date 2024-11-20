package CheersMate.cheersmate;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
@EnableBatchProcessing // 배치 처리 활성화
@RequiredArgsConstructor
public class CheersmateApplication {
	private final JobLauncher jobLauncher;

	private final Job importLiquorJob;

	private final Job importFoodJob;

	public static void main(String[] args) {
		SpringApplication.run(CheersmateApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			jobLauncher.run(importLiquorJob, new JobParameters());
			jobLauncher.run(importFoodJob, new JobParameters());
		};
	}
}
