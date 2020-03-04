package com.fydp.backend.controllers;

import com.fydp.backend.kafka.KafkaProducer;
import com.fydp.backend.kafka.MessageListener;
import com.fydp.backend.model.Bookmark;
import com.fydp.backend.model.ChapterTextModel;
import com.fydp.backend.model.PdfInfo;
import com.fydp.backend.service.SummaryService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Note: A lot of code are basically recreating the PDF document as well as variables that should only be
 * retrieved once.
 * TODO: Refactor the code such that we can store and get data from database once the database is implemented
 */

@CrossOrigin
@RestController
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);
    private static final String UPLOAD_PATH = System.getProperty("user.dir") + "/upload_files/";
    private static final String END_OF_CHAPTER = "End of Last Chapter";
    private static final String CHAPTER_REGEX = "(\\bchapter|\\bch|\\bch\\.|\\bchap|\\bchap\\.|\\bpart|\\bsection|^)\\s*\\d+";

    @Autowired
    private PdfInfo pdfInfo;

    @Autowired
    private ChapterTextModel chapterTextModel;

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private SummaryService summaryService;

    @RequestMapping("/")
    public String welcome() {
        logger.debug("Welcome endpoint hit");
        return "index";
    }

    @GetMapping(value = ("/summaries/{id}"))
    public Map<String, String> getSummaries(@PathVariable String id) throws InterruptedException {
        logger.info("GET summary endpoint hit");
        Long summary_id = Long.parseLong(id);

        // wait up to 30s for summary to finish
        long end = System.currentTimeMillis() + 30000;

        Map<String, String> ret = new HashMap<>();

        while (System.currentTimeMillis() < end) {
            var summary = summaryService.findById(summary_id);

            if (summary.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No summary job for this id");
            }

            var summ = summary.get();

            if (summ.isFinished()){
                ret.put("title", summ.getTitle());
                ret.put("data", summ.getData());
                break;
            }

            TimeUnit.SECONDS.sleep(5);
        }

        if (ret.isEmpty()) throw new ResponseStatusException(HttpStatus.ACCEPTED, "Summary still processing");

        return ret;
    }

    // returns pdfInfo which contains the summaryId to hit /summaries/{id}
    @PostMapping(value = ("/upload"), headers = ("content-type=multipart/*"))
    public PdfInfo upload(@RequestParam("file") MultipartFile file) throws IOException {
        logger.debug("Upload endpoint hit");

        PDDocument document = parsePDF(loadPdfFile(file));
        if (document == null) {
            logger.error("Not able to load PDF");
            return pdfInfo;
        }

        var bookmarks = getBookmarks(document);

        if (!bookmarks.isEmpty()) {
            var chapters = findChapters(bookmarks, CHAPTER_REGEX);

            // If no bookmarks match the chapter format, consider all bookmarks as chapters
            if (chapters.isEmpty()) {
                logger.info("No chapters found. Using all bookmarks.");
                chapters = bookmarks;
            }

            pdfInfo.setChapters(chapters);
            pdfInfo.setPdfText("");
            // summary id of 0 means no summary job submitted yet
            pdfInfo.setSummaryId(0L);
        } else {
            logger.info("No bookmarks found in PDF. Summarizing entire document.");
            String pdfText = new PDFTextStripper().getText(document);
            pdfInfo.setPdfText(pdfText);

            //create the summary entry and get id of this summary
            var summary_id = summaryService.createSummary("Summary");
            pdfInfo.setSummaryId(summary_id);

            if (!pdfText.isEmpty()) {
                //send text with associated summary id to kafka
                producer.sendMessageWithKey(pdfText, summary_id);
            }

        }

        pdfInfo.setFileName(file.getOriginalFilename());

        document.close();
        return pdfInfo;
    }

    //@return: returns map of chapter titles to summary_ids
    // can use these summary ids to hit the /summaries/{id} endpoint to retrieve summary for that chapter
    @PostMapping(value = "/upload/chapters", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ChapterTextModel parseChapters(@RequestBody PdfInfo response) throws IOException {
        List<Bookmark> chapters = response.getChapters();
        Map<String, String> chapterTxt = new HashMap<>();
        PDDocument document = parsePDF(new File(UPLOAD_PATH + response.getFileName()));
        for (var chapter : chapters) {
            try {
                PDFTextStripper reader = new PDFTextStripper();
                reader.setStartPage(chapter.getStartPage());
                reader.setEndPage(chapter.getEndPage() - 1);
                chapterTxt.put(chapter.getTitle(), reader.getText(document));
            } catch (IOException ex) {
                logger.error("Unable to create text stripper", ex);
            }
        }

        chapterTextModel.setChpTextMap(chapterTxt);
        Map<String, Long> chapterIds = new HashMap<>();
        for (var entry : chapterTxt.entrySet()) {
            var summary_id = summaryService.createSummary(entry.getKey());
            chapterIds.put(entry.getKey(), summary_id);
            if (!entry.getValue().isEmpty()) {
                producer.sendMessageWithKey(entry.getValue(), summary_id);
            }
        }

        chapterTextModel.setChpId(chapterIds);

        document.close();
        return chapterTextModel;
    }

    private File loadPdfFile(MultipartFile file) {
        File pdfFile = new File(UPLOAD_PATH + file.getOriginalFilename());
        try {
            if (!Files.exists(Paths.get(UPLOAD_PATH))) {
                Files.createDirectory(Paths.get(UPLOAD_PATH));
            }
            if (!pdfFile.exists()) {
                pdfFile.createNewFile();
            }
        } catch (IOException ex) {
            logger.error("Unable to create new File", ex);
        }

        try (FileOutputStream os = new FileOutputStream(pdfFile);) {
            os.write(file.getBytes());
        } catch (IOException ex) {
            logger.error("Error occurred while writing to file", ex);
        }

        return pdfFile;
    }

    private PDDocument parsePDF(File file) {
        PDDocument doc = null;
        try {
            doc = PDDocument.load(file);
            return doc;
        } catch (IOException ex) {
            logger.error("Error loading the pdf file", ex);
        }
        return doc;
    }

    /**
     * Retrieves the bookmarks of a PDF document in a flattened map structure
     * @param doc PDF document
     * @return Map of bookmarks
     * @throws IOException
     */
    private List<Bookmark> getBookmarks(PDDocument doc) throws IOException {
        var bookmarks = new ArrayList<Bookmark>();
        var bookmarkRoot = doc.getDocumentCatalog().getDocumentOutline();
        if (bookmarkRoot != null) {
            storeBookmarks(bookmarkRoot, bookmarks, 0, doc);
        }
        return bookmarks;
    }

    private void storeBookmarks(PDOutlineNode bookmark, List<Bookmark> bookmarks, int depth, PDDocument doc) throws IOException {
        PDOutlineItem current = bookmark.getFirstChild();

        while (current != null) {
            if (depth == 3) break;

            int currIndex = bookmarks.size();
            var b = new Bookmark();
            b.setTitle(current.getTitle());
            b.setDepth(depth);
            bookmarks.add(b);

            int startPage = getBookmarkStartPage(current, doc);
            if (startPage != -1) {
                b.setStartPage(startPage);
            } else {
                logger.error("Could not find start page for bookmark \"{}\"", current.getTitle());
            }
            int endPage = getBookmarkEndPage(current, currIndex, bookmarks, doc);
            if (endPage != -1) {
                b.setEndPage(endPage);
            } else {
                logger.error("Could not find end page for bookmark \"{}\"", current.getTitle());
            }

            // Store current bookmark's children
            storeBookmarks(current, bookmarks, depth + 1, doc);

            current = current.getNextSibling();
        }
    }

    private int getBookmarkStartPage(PDOutlineItem bookmark, PDDocument document) throws IOException {
        // Taken from PDFBox example
        if (bookmark.getDestination() instanceof PDPageDestination)
        {
            PDPageDestination pd = (PDPageDestination) bookmark.getDestination();
            return pd.retrievePageNumber() + 1;
        }
        else if (bookmark.getDestination() instanceof PDNamedDestination)
        {
            PDPageDestination pd = document.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) bookmark.getDestination());
            if (pd != null)
            {
                return pd.retrievePageNumber() + 1;
            }
        }

        if (bookmark.getAction() instanceof PDActionGoTo)
        {
            PDActionGoTo gta = (PDActionGoTo) bookmark.getAction();
            if (gta.getDestination() instanceof PDPageDestination)
            {
                PDPageDestination pd = (PDPageDestination) gta.getDestination();
                return pd.retrievePageNumber() + 1;
            }
            else if (gta.getDestination() instanceof PDNamedDestination)
            {
                PDPageDestination pd = document.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) gta.getDestination());
                if (pd != null)
                {
                    return pd.retrievePageNumber() + 1;
                }
            }
        }

        return -1;
    }

    private int getBookmarkEndPage(PDOutlineItem bookmark, int bookmarkIndex, List<Bookmark> bookmarks, PDDocument document) throws IOException {
        var next = bookmark.getNextSibling();
        int endPage = -1;
        if (next != null) {
            endPage = getBookmarkStartPage(next, document);
        } else {
            var parent = getParentBookmark(bookmarkIndex, bookmarks);
            if (parent != null) {
                endPage = parent.getEndPage();
            } else if (bookmarks.get(bookmarkIndex).getDepth() == 0) {
                endPage = document.getNumberOfPages();
            }
        }
        return endPage;
    }

    private Bookmark getParentBookmark(int childIndex, List<Bookmark> bookmarks) {
        var child = bookmarks.get(childIndex);
        if (child.getDepth() == 0) return null;
        int curr = childIndex;
        while (bookmarks.get(curr).getDepth() >= child.getDepth()) {
            curr--;
            if (curr < 0) {
                logger.error("Could not find parent for bookmark \"{}\"", bookmarks.get(curr).getTitle());
                return null;
            }
        }
        return bookmarks.get(curr);
    }

    /**
     * Finds bookmarks matching the specified chapter regex
     * @param bookmarks Bookmarks to search
     * @param chapterRegex Regex to match with chapters
     * @return Matching bookmarks
     */
    private List<Bookmark> findChapters(List<Bookmark> bookmarks, String chapterRegex) {
        var chapters = new ArrayList<Bookmark>();
        var pattern = Pattern.compile(chapterRegex);
        for (var b : bookmarks) {
            var match = pattern.matcher(b.getTitle());
            if (match.find()) {
                chapters.add(b);
            }
        }
        return chapters;
    }

}
