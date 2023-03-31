package lco.bookmarks.batch.services;

import lco.bookmarks.batch.exceptions.BatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookmarkService {

    Logger logger = LoggerFactory.getLogger(BookmarkService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("exportFromChromeJob")
    @Autowired
    private Job exportFromChromeJob;

    @Async
    public void startJob(String jobName) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, BatchException {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis()));

        JobParameters jobParameters = new JobParameters(params);

        if (jobName.equals("ChromeExportJob")) {
            logger.info("Starting job exportFromChromeJob");
            JobExecution jobExec = jobLauncher.run(exportFromChromeJob, jobParameters);
            ExitStatus exitStatus = jobExec.getExitStatus();
            logger.info("EXIT STATUS {}", exitStatus);
            if(exitStatus.getExitCode().equals("FAILED")) {
                throw new BatchException("Job has failed");
            }
        }

    }

}
