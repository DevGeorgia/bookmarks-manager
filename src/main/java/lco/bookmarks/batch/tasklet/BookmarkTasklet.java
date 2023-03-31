package lco.bookmarks.batch.tasklet;

import lco.bookmarks.batch.model.ChromePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BookmarkTasklet implements Tasklet {

    Logger logger = LoggerFactory.getLogger(BookmarkTasklet.class);


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        String jobName = chunkContext.getStepContext().getJobName();

        File bookmarkFile = null;

        if(jobName.equals("ChromeExportJob")) {
            ChromePath chromePath = new ChromePath();
            logger.info("Checking if bookmark file exists : {} ", chromePath.getChromeBookmarkPath());
            bookmarkFile = new File(chromePath.getChromeBookmarkPath());
        }

        assert bookmarkFile != null;
        if(bookmarkFile.exists()) {
            stepContribution.setExitStatus(ExitStatus.COMPLETED);
        } else {
            stepContribution.setExitStatus(ExitStatus.FAILED);
        }
        return RepeatStatus.FINISHED;
    }
}
