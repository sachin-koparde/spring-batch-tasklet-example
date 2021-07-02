package demo.springbatch.taskletexample.task;

import com.opencsv.CSVWriter;
import demo.springbatch.taskletexample.model.Person;
import demo.springbatch.taskletexample.util.PersonUtil;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PersonWriter implements Tasklet, StepExecutionListener {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private List<Person> persons;
    private List<String[]> personMetadata;

    private String outputFile;

    private FileWriter fileWriter = null;

    private CSVWriter csvWriter;

    public PersonWriter(String outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

        logger.info("Step execution started. Step Name: " + stepExecution.getStepName());

        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();

        persons = (List<Person>) executionContext.get("persons");

        personMetadata = new ArrayList<String[]>();

        try {
            fileWriter = new FileWriter(outputFile);

            csvWriter = new CSVWriter(fileWriter, ',', '\'');
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        for(Person person : persons) {
            String[] personData = new PersonUtil().personToStringArray(person);
            personMetadata.add(personData);

        }

        csvWriter.writeAll(personMetadata);

        return RepeatStatus.FINISHED;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        logger.info("Step execution completed. Step Name: " + stepExecution.getStepName());

        try {
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ExitStatus.COMPLETED;
    }
}
