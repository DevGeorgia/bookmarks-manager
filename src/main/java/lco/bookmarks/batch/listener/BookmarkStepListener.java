package lco.bookmarks.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class BookmarkStepListener implements StepExecutionListener {

    Logger logger = LoggerFactory.getLogger(BookmarkStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Before step : {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("After step : {}", stepExecution.getStepName());
        logger.info("After step : {}", stepExecution.getExecutionContext());
        return null;
    }
}
