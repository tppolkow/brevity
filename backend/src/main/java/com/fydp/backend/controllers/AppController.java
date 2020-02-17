package com.fydp.backend.controllers;

import com.fydp.backend.kafka.KafkaProducer;
import com.fydp.backend.kafka.MessageListener;
import com.fydp.backend.model.ChapterTextModel;
import com.fydp.backend.model.PdfInfo;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    private MessageListener listener;

    @Autowired
    private KafkaProducer producer;

    @RequestMapping("/")
    public String welcome() {
        logger.debug("Welcome endpoint hit");
        return "index";
    }

    @GetMapping(value = ("/summaries"))
    public Map<String, String> getSummaries() {
        logger.info("GET summary endpoint hit");

        try {
            listener.getLatch().await();
        } catch (InterruptedException e) {
            logger.error("Error while waiting for kafka response : " + e.getMessage());
            e.printStackTrace();
        }

        return listener.getMessages();
    }

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

            var bookmarkTitles = new ArrayList<>(bookmarks.keySet());
            var chapterTitles = new ArrayList<>(chapters.keySet());

            if (bookmarkTitles.size() == chapterTitles.size()) {
                chapters.put(END_OF_CHAPTER, document.getNumberOfPages());
            } else {
                String lastChapter = chapterTitles.get(chapterTitles.size() - 1);
                String endOfLastChapter = bookmarkTitles.get(bookmarkTitles.indexOf(lastChapter) + 1);
                chapters.put(endOfLastChapter, bookmarks.get(endOfLastChapter));
            }

            pdfInfo.setChapters(chapterTitles);
            pdfInfo.setChapterPgMap(chapters);
            pdfInfo.setPdfText("");
        } else {
            logger.info("No bookmarks found in PDF. Summarizing entire document.");
            String pdfText = new PDFTextStripper().getText(document);
            pdfInfo.setPdfText(pdfText);
            if (!pdfText.isEmpty()) {
                producer.sendMessage(pdfText);
                listener.setMessages(1);
            }
        }

        pdfInfo.setFileName(file.getOriginalFilename());

        document.close();
        return pdfInfo;
    }

    @PostMapping(value = "/upload/chapters", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ChapterTextModel parseChapters(@RequestBody PdfInfo response) throws IOException {
        List<String> chapters = response.getChapters();
        Map<String, Integer> pgMap = response.getChapterPgMap();
        List<String> refChapters = new ArrayList<>(pgMap.keySet());
        Map<String, String> chapterTxt = new HashMap<>();
        PDDocument document = parsePDF(new File(UPLOAD_PATH + response.getFileName()));
        for (String chapter : chapters) {
            int startPg = pgMap.get(chapter);
            int endPg = pgMap.get(refChapters.get(refChapters.indexOf(chapter) + 1));
            try {
                PDFTextStripper reader = new PDFTextStripper();
                reader.setStartPage(startPg);
                reader.setEndPage(endPg - 1);
                chapterTxt.put(chapter, reader.getText(document));
            } catch (IOException ex) {
                logger.error("Unable to create text stripper", ex);
            }
        }

        chapterTextModel.setChpTextMap(chapterTxt);
        for (Map.Entry entry : chapterTxt.entrySet()) {
            producer.sendMessageWithKey(entry.getValue().toString(), entry.getKey().toString());
        }
        listener.setMessages(chapterTxt.size());

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
    private LinkedHashMap<String, Integer> getBookmarks(PDDocument doc) throws IOException {
        var bookmarks = new LinkedHashMap<String, Integer>();
        var bookmarkRoot = doc.getDocumentCatalog().getDocumentOutline();
        if (bookmarkRoot != null) {
            storeBookmarks(bookmarkRoot, bookmarks, 0, doc);
        }
        return bookmarks;
    }

    private void storeBookmarks(PDOutlineNode bookmark, Map<String, Integer> map, int depth, PDDocument doc) throws IOException {
        PDOutlineItem current = bookmark.getFirstChild();

        while (current != null) {
            if (depth == 3) break;

            int pageNum = getBookmarkPageNumber(current, doc);
            if (pageNum == -1) {
                logger.error("Cannot retrieve page destination for bookmark \"{}\"", current.getTitle());
            } else {
                map.put(current.getTitle(), pageNum);
            }

            // Store current bookmark's children
            storeBookmarks(current, map, depth + 1, doc);

            current = current.getNextSibling();
        }
    }

    private int getBookmarkPageNumber(PDOutlineItem bookmark, PDDocument document) throws IOException {
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

    /**
     * Finds bookmarks matching the specified chapter regex
     * @param bookmarks Bookmarks to search
     * @param chapterRegex Regex to match with chapters
     * @return Matching bookmarks
     */
    private LinkedHashMap<String, Integer> findChapters(LinkedHashMap<String, Integer> bookmarks, String chapterRegex) {
        var chapters = new LinkedHashMap<String, Integer>();
        var pattern = Pattern.compile(chapterRegex);
        for (var entry : bookmarks.entrySet()) {
            var match = pattern.matcher(entry.getKey());
            if (match.find()) {
                chapters.put(entry.getKey(), entry.getValue());
            }
        }
        return chapters;
    }

}
