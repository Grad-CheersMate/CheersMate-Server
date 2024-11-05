package CheersMate.cheersmate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
@EnableBatchProcessing // 배치 처리 활성화
public class CheersmateApplication {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job importLiquorJob;

	@Autowired
	private Job importFoodJob;

	public static void main(String[] args) {
		SpringApplication.run(CheersmateApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner run() {
//		return args -> {
//			// 최초 실행 시에만 사용
//			jobLauncher.run(importLiquorJob, new JobParameters());
//			jobLauncher.run(importFoodJob, new JobParameters());
//		};
//	}

}
