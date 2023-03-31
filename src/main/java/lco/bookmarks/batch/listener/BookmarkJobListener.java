package lco.bookmarks.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class BookmarkJobListener implements JobExecutionListener {

    Logger logger = LoggerFactory.getLogger(BookmarkJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Before Job : {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job Params : {}", jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("After job : {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job Params : {}", jobExecution.getJobParameters());
    }
}
