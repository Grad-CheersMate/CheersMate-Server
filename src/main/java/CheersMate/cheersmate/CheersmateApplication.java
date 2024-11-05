package CheersMate.cheersmate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
@EnableBatchProcessing // 배치 처리 활성화
public class CheersmateApplication {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job importLiquorJob;

	public static void main(String[] args) {
		SpringApplication.run(CheersmateApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			jobLauncher.run(importLiquorJob, new JobParameters());
		};
	}

}
