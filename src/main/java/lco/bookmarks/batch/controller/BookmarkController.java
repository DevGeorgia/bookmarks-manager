package lco.bookmarks.batch.controller;

import lco.bookmarks.batch.exceptions.BatchException;
import lco.bookmarks.batch.services.BookmarkService;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private JobOperator jobOperator;

    @GetMapping("/start/{jobName}")
    public ResponseEntity<String> startJob(@PathVariable String jobName)  {
        try {
            bookmarkService.startJob(jobName);
            return new ResponseEntity<>("Job successfully executed", HttpStatus.OK);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException | BatchException e) {
            return new ResponseEntity<>("Sorry, something went wrong during job execution", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
