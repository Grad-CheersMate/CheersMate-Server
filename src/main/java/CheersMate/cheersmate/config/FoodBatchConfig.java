package CheersMate.cheersmate.config;

import CheersMate.cheersmate.domain.entity.Food;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FoodBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public FlatFileItemReader<Food> foodReader() {
        FlatFileItemReader<Food> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("food_images.csv"));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");
        reader.setLineMapper(new DefaultLineMapper<Food>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("category", "name", "imageLink");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Food.class);
            }});
        }});
        return reader;
    }

    @Bean
    public JpaItemWriter<Food> foodWriter() {
        JpaItemWriter<Food> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Job importFoodJob() {
        return jobBuilderFactory.get("importFoodJob")
                .start(foodStep())
                .build();
    }

    @Bean
    public Step foodStep() {
        return stepBuilderFactory.get("foodStep")
                .<Food, Food>chunk(10)
                .reader(foodReader())
                .writer(foodWriter())
                .transactionManager(transactionManager)
                .build();
    }
}
