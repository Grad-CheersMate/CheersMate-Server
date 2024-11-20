package CheersMate.cheersmate.config;

import CheersMate.cheersmate.domain.entity.Liquor;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LiquorBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final PlatformTransactionManager transactionManager;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public FlatFileItemReader<Liquor> liquorReader() {
        FlatFileItemReader<Liquor> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("all_liquor_data.csv"));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("name", "alcohol", "image_link", "category");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Liquor.class);
            }});
        }});
        return reader;
    }

    @Bean
    public JpaItemWriter<Liquor> liquorWriter() {
        JpaItemWriter<Liquor> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Job importLiquorJob() {
        return jobBuilderFactory.get("importLiquorJob")
                .start(liquorStep())
                .build();
    }

    @Bean
    public Step liquorStep() {
        return stepBuilderFactory.get("liquorStep")
                .<Liquor, Liquor>chunk(10)
                .reader(liquorReader())
                .writer(liquorWriter())
                .transactionManager(transactionManager)
                .build();
    }
}


