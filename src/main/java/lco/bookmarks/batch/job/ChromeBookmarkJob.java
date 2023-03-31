package lco.bookmarks.batch.job;

import lco.bookmarks.batch.listener.BookmarkJobListener;
import lco.bookmarks.batch.listener.BookmarkStepListener;
import lco.bookmarks.batch.model.BookmarkCsv;
import lco.bookmarks.batch.model.ChromeBookmark;
import lco.bookmarks.batch.model.ChromePath;
import lco.bookmarks.batch.model.WorkSpace;
import lco.bookmarks.batch.reader.ChromeJsonObjectReader;
import lco.bookmarks.batch.tasklet.BookmarkTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;

@Configuration
public class ChromeBookmarkJob {

    Logger logger = LoggerFactory.getLogger(ChromeBookmarkJob.class);

    @Autowired
    private BookmarkStepListener bookmarkStepListener;

    @Autowired
    private BookmarkTasklet bookmarkTasklet;


    @Bean
    public Job exportFromChromeJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, BookmarkJobListener bookmarkJobListener) {
        // Basic Job with 2 steps
        return jobBuilderFactory.get("ChromeExportJob")
                .incrementer(new RunIdIncrementer())
                .start(checkStep(stepBuilderFactory))
                .next(exportChromeBookmarksStep(stepBuilderFactory))
                .listener(bookmarkJobListener)
                .build();
    }


    private Step checkStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
                .get("CheckStep")
                .tasklet(bookmarkTasklet)
                .listener(bookmarkStepListener)
                .build();
    }


    private Step exportChromeBookmarksStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("ExportChromeBookmarksStep")
                .<ChromeBookmark, BookmarkCsv>chunk(5)
                .reader(chromeBookmarkReader())
                .writer(bookmarkCsvWriter())
                .build();
    }


    @StepScope
    @Bean
    public JsonItemReader<ChromeBookmark> chromeBookmarkReader() {

        ChromePath chromePath = new ChromePath();
        FileSystemResource chromeFile = new FileSystemResource(chromePath.getChromeBookmarkPath());

        JsonItemReader<ChromeBookmark> jsonItemReader = new JsonItemReader<>();

        jsonItemReader.setResource(chromeFile);
        jsonItemReader.setJsonObjectReader(
                new ChromeJsonObjectReader<>(ChromeBookmark.class, "children"));

        return jsonItemReader;
    }


    @StepScope
    @Bean
    public FlatFileItemWriter<BookmarkCsv> bookmarkCsvWriter() {

        FlatFileItemWriter<BookmarkCsv> flatFileItemWriter = new FlatFileItemWriter<>();

        WorkSpace workSpace = new WorkSpace();
        String fileName = "export_chrome_bookmarks.csv";
        String outputPath = Paths.get(workSpace.getWorkDestinationPath(), fileName).toString();

        FileSystemResource workDir = new FileSystemResource(outputPath);
        flatFileItemWriter.setResource(workDir);

        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("folder;name;url");
            }
        });

        BeanWrapperFieldExtractor<BookmarkCsv> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(new String[]{"folder", "name", "url"});

        DelimitedLineAggregator<BookmarkCsv> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(";");
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);

        flatFileItemWriter.setLineAggregator(delimitedLineAggregator);

        return flatFileItemWriter;
    }


}
