package demo.springbatch.taskletexample.config;

import demo.springbatch.taskletexample.listener.PersonJobListener;
import demo.springbatch.taskletexample.task.PersonReader;
import demo.springbatch.taskletexample.task.PersonProcessor;
import demo.springbatch.taskletexample.task.PersonWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
@EnableBatchProcessing
public class PersonBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PersonJobListener jobListener;

    @Value("${file.input}")
    private String fileInput;

    @Value("${file.output}")
    private String fileOutput;

    @Bean
    public Job jobAgeInAscendingOrder() {
        return jobBuilderFactory.get("jobAgeInAscendingOrder")
                .listener(jobListener)
                .start(stepRead())
                .next(stepProcess())
                .next(stepWrite())
                .build();
    }

    @Bean
    public Step stepRead() {
        return stepBuilderFactory.get("stepRead")
                .tasklet(new PersonReader(fileInput))
                .build();
    }

    @Bean
    public Step stepProcess() {
        return stepBuilderFactory.get("stepProcess")
                .tasklet(new PersonProcessor())
                .build();
    }

    @Bean
    public Step stepWrite() {
        return stepBuilderFactory.get("stepWrite")
                .tasklet(new PersonWriter(fileOutput))
                .build();
    }

}
