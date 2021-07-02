package demo.springbatch.taskletexample.task;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class PersonProcessor implements Tasklet, StepExecutionListener {

    private List<String[]> personsMetadata;
    private List<Person> persons;

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void beforeStep(StepExecution stepExecution) {

        logger.info("Step execution started. Step Name: " + stepExecution.getStepName());

        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        this.personsMetadata = (List<String[]>) executionContext.get("persons");

        persons = new ArrayList<>();

    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        for (String[] mPerson : personsMetadata) {
            Person person = new PersonUtil().createPerson(mPerson);
            persons.add(person);
        }

        Collections.sort(persons, Comparator.comparing(Person::getPersonAge));

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
