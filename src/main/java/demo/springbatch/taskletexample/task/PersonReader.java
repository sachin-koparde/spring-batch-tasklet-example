package demo.springbatch.taskletexample.task;

import com.opencsv.CSVReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PersonReader implements Tasklet, StepExecutionListener {

    private final Logger logger = Logger.getLogger(getClass().getName());

    String fileInput;
    private List<String[]> persons;
    private FileReader fileReader = null;
    private CSVReader csvReader;


    public PersonReader(String fileInput) {
        this.fileInput = fileInput;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

        logger.info("Step execution started. Step name: " + stepExecution.getStepName());

        persons = new ArrayList<>();

        try {
            fileReader = new FileReader(fileInput);

            csvReader = new CSVReader(fileReader);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        try {

            List<String[]> addPersons;
            addPersons = csvReader.readAll();

            persons.addAll(addPersons);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return RepeatStatus.FINISHED;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        logger.info("Step execution completed. Step name: " + stepExecution.getStepName());

        stepExecution.getJobExecution()
                .getExecutionContext()
                .put("persons", this.persons);

        return ExitStatus.COMPLETED;
    }
}
