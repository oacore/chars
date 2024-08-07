package uk.ac.core.workers.item.pdf;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.workers.item.pdf.conditions.DocumentDecorationCondition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Service
public class PdfDecoratingService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(PdfDecoratingService.class);

    private final RepositoryDocumentDAO repositoryDocumentDAO;
    private final FilesystemDAO filesystemDAO;
    private final DataProviderService dataProviderService;

    // Page decorations setup
    final static PDColor surface = new PDColor(new float[]{1.0f, 1.0f, 1.0f}, PDDeviceRGB.INSTANCE);
    final static PDColor onSurface = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);
    final static PDColor primary = new PDColor(new float[]{0.718f, 0.329f, 0.0f}, PDDeviceRGB.INSTANCE);
    final static PDColor onPrimary = new PDColor(new float[]{1.0f, 1.0f, 1.0f}, PDDeviceRGB.INSTANCE);
    final static PDFont fontNormal = PDType1Font.HELVETICA;
    final static PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    final static PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
    final static float leading = 1.0f;
    final static float fontSizeDefault = 8f;
    final static float lineHeightDefault = fontNormal.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSizeDefault * leading;
    final static float fontSizeSmall = 6f;
    final static float lineHeightSmall = fontNormal.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSizeSmall * leading;
    final static float paddingVerticalMedium = fontSizeDefault / 2f;
    final static float paddingVerticalSmall = fontSizeDefault / 4f;
    final static float paddingHorizontal = fontSizeDefault * 1.5f;


    public PdfDecoratingService(RepositoryDocumentDAO repositoryDocumentDAO, FilesystemDAO filesystemDAO, DataProviderService dataProviderService) {
        this.repositoryDocumentDAO = repositoryDocumentDAO;
        this.filesystemDAO = filesystemDAO;
        this.dataProviderService = dataProviderService;
    }

    public static PDDocument loadDocument(String filePath) throws IOException {
        File file = new File(filePath);
        PDDocument pdf = PDDocument.load(file);
        return pdf;
    }

    public static void saveDocument(PDDocument document, String filePath) throws IOException {
        document.save(filePath);
    }

    // We use PDF default font to keep the document size small preventing external fonts embedding.
    // Because of that we are required to used ANSI characters only.
    // See note about WinAnsi at https://pdfbox.apache.org/2.0/faq.html
    public static boolean hasIllegalCharacters(String targetString) {
        for (char c : targetString.toCharArray()) {
            if (!WinAnsiEncoding.INSTANCE.contains(c)) return true;
        }
        return false;
    }

    // Blindly copied from https://stackoverflow.com/a/19683618/7205866
    // If you need the rest of code for printing text, visit that link.
    public static List<String> breakLines(String text, float width, PDFont font, float fontSize) throws IOException {
        List<String> lines = new ArrayList<String>();

        int lastSpace = -1;
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0) spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);
            float size = fontSize * font.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0) lastSpace = spaceIndex;
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }

        return lines;
    }

    public static String shortenText(String text, float lineWidth, PDFont font, float fontSize) throws IOException {
        if (hasIllegalCharacters(text)) return "";

        List<String> lines = breakLines(text, lineWidth, font, fontSize);
        if (lines.size() > 1) {
            // NOTE: If the line is full, dots will go out on margin. We hope this happens extremely rarely
            return lines.get(0) + "...";
        }

        return lines.get(0);
    }

    static PDRectangle drawMenuLine(PDDocument document, PDPage page, AnnotationContext context) throws IOException {
        final float fontSize = fontSizeDefault;
        final float lineHeight = lineHeightDefault;
        final float capHeight = fontSize * fontNormal.getFontDescriptor().getCapHeight() / 1000;
        final float paddingVertical = paddingVerticalMedium;

        final float containerWidth = page.getArtBox().getWidth();
        final float containerHeight = lineHeight + 2 * paddingVertical;
        final float containerX = page.getArtBox().getLowerLeftX();
        final float containerY = page.getArtBox().getHeight() - containerHeight;

        final float textLineY = containerY + paddingVertical + (lineHeight - capHeight) / 2 - 0.1f * fontSize;
        final float textUnderlineShift = -1f;

        byte[] imageData = IOUtils.toByteArray(PdfDecoratingService.class.getResourceAsStream("/images/logo.png"));

        final PDImageXObject logoImage = PDImageXObject.createFromByteArray(document, imageData, "logo.png");
        final PDFont logoFont = fontBold;
        final String logoText = context.getProductName();

        final float logoImageHeight = 1.5f * fontSize;
        final float logoImageWidth = logoImageHeight / logoImage.getHeight() * logoImage.getWidth();
        final float logoTextWidth = logoFont.getStringWidth(logoText) / 1000 * fontSize;
        final float logoWidth = logoImageWidth + logoTextWidth;
        final float logoX = page.getMediaBox().getUpperRightX() - logoWidth - paddingHorizontal;
        final float logoImageX = logoX;
        final float logoImageY = textLineY - (0.1f * logoImageHeight); // manually correcting image vertically to match the text line
        final float logoTextX = logoX + logoImageWidth;

        final PDFont logoPrefixFont = fontItalic;
        final float logoPrefixFontSize = fontSizeSmall;
        final String logoPrefixText = context.getLogoPrefix();
        final float logoPrefixWidth = logoPrefixFont.getStringWidth(logoPrefixText) / 1000 * logoPrefixFontSize;
        final float logoPrefixX = logoX - logoPrefixWidth;

        final String linkText = context.getLinkCaption();
        final String linkWebsitePart = context.getProductWebsite();
        final PDFont linkFont = fontNormal;
        final float linkCaptionWidth = linkFont.getStringWidth(linkText) / 1000 * fontSize;
        final float linkCaptionX = page.getMediaBox().getLowerLeftX() + paddingHorizontal;
        final float linkWebsiteWidth = linkFont.getStringWidth(linkWebsitePart) / 1000 * fontSize;

        // Setup
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

        // Draw background box
        contentStream.setNonStrokingColor(surface);
        contentStream.addRect(containerX, containerY, containerWidth, containerHeight);
        contentStream.fill();

        // Drawing logo image
        contentStream.drawImage(logoImage, logoImageX, logoImageY, logoImageWidth, logoImageHeight);

        // Drawing logo prefix
        contentStream.setFont(logoPrefixFont, logoPrefixFontSize);
        contentStream.setLeading(leading);
        contentStream.setNonStrokingColor(onSurface);
        contentStream.beginText();
        contentStream.newLineAtOffset(logoPrefixX, textLineY);
        contentStream.showText(logoPrefixText);
        contentStream.endText();

        // Drawing logo text
        contentStream.setFont(logoFont, fontSize);
        contentStream.setLeading(leading);
        contentStream.setNonStrokingColor(onSurface);
        contentStream.beginText();
        contentStream.newLineAtOffset(logoTextX, textLineY);
        contentStream.showText(logoText);
        contentStream.endText();

        // Drawing link description
        contentStream.setFont(linkFont, fontSize);
        contentStream.setNonStrokingColor(primary);
        contentStream.beginText();
        contentStream.newLineAtOffset(linkCaptionX, textLineY);
        contentStream.showText(linkText);
        contentStream.endText();

        // Underlining the link
        contentStream.setStrokingColor(primary);
        contentStream.moveTo(linkCaptionX + linkCaptionWidth - linkWebsiteWidth, textLineY + textUnderlineShift);
        contentStream.lineTo(linkCaptionX + linkCaptionWidth, textLineY + textUnderlineShift);
        contentStream.stroke();

        contentStream.close();

        // Making menu boundaries for other methods
        return new PDRectangle(containerX, containerY, containerWidth, containerHeight);
    }

    static PDRectangle drawCreditLine(PDDocument document, PDPage page, AnnotationContext context, float topRightY) throws IOException {
        final String dataProviderName = context.getDataProviderName();
        final String prefixText = context.getDataProviderPrefix();

        // Using only half a page for "provided by" string
        final float lineWidth = page.getMediaBox().getWidth() / 2 - 2 * paddingHorizontal;

        final PDFont font = fontNormal;
        final PDFont prefixFont = fontItalic;
        final float fontSize = fontSizeSmall;
        final float lineHeight = lineHeightSmall;
        final float capHeight = fontSize * font.getFontDescriptor().getCapHeight() / 1000;
        final float paddingVertical = paddingVerticalSmall;

        // We neglecting difference between normal style and the oblique one to simplify calculations
        final String shortenedText = shortenText(prefixText + dataProviderName, lineWidth, font, fontSize);

        final String providerText = shortenedText.length() > 0 ? shortenedText.substring(prefixText.length()) : "";

        final int lineCount = shortenedText.length() > 0 ? 1 : 0;
        final float containerWidth = page.getArtBox().getWidth();
        final float containerHeight = lineCount * lineHeight + 2 * paddingVertical;
        final float containerX = page.getArtBox().getLowerLeftX();
        final float containerY = topRightY - containerHeight;

        final float textWidth = font.getStringWidth(shortenedText) / 1000 * fontSize;
        final float textX = page.getMediaBox().getUpperRightX() - textWidth - paddingHorizontal;
        final float textY = containerY + paddingVertical + (lineHeight - capHeight) / 2;

        // Setup
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

        // Drawing background box
        contentStream.setStrokingColor(primary);
        contentStream.addRect(containerX, containerY, containerWidth, containerHeight);
        contentStream.fill();

        // Drawing credit text
        if (lineCount > 0) {
            contentStream.setLeading(leading);
            contentStream.setNonStrokingColor(onPrimary);

            contentStream.beginText();

            contentStream.newLineAtOffset(textX, textY);
            contentStream.setFont(prefixFont, fontSize);
            contentStream.showText(prefixText);
            contentStream.setFont(font, fontSize);
            contentStream.showText(providerText);

            contentStream.endText();
        }

        contentStream.close();

        // Making menu boundaries for other methods
        return new PDRectangle(containerX, containerY, containerWidth, containerHeight);
    }

    static PDAnnotation addLink(PDPage page, PDRectangle rectangle, String url) throws IOException {
        PDAnnotationLink link = new PDAnnotationLink();
        PDActionURI action = new PDActionURI();
        action.setURI(url);
        link.setAction(action);
        link.setRectangle(rectangle);
        link.setPrinted(false);
        link.setColor(primary);

        // Our annotations must be in the beginning to prevent overriding original document annotations.
        List<PDAnnotation> annotations = page.getAnnotations();
        annotations.add(0, link);

        return link;
    }

    static void injectHeader(PDDocument document, PDPage page, AnnotationContext context) throws IOException {
        PDRectangle menuRectangle = drawMenuLine(document, page, context);
        PDRectangle creditRectangle = drawCreditLine(document, page, context, menuRectangle.getLowerLeftY());

        // Joining 2 rectangles to make a single link
        PDRectangle headerRectangle = new PDRectangle();
        headerRectangle.setLowerLeftX(creditRectangle.getLowerLeftX());
        headerRectangle.setLowerLeftY(creditRectangle.getLowerLeftY());
        headerRectangle.setUpperRightX(menuRectangle.getUpperRightX());
        headerRectangle.setUpperRightY(menuRectangle.getUpperRightY());

        addLink(page, headerRectangle, context.getUrl());
    }

    public AnnotationContext createAnnotationContext(RepositoryDocument repositoryDocument) throws DataProviderNotFoundException {
        final Integer repositoryId = repositoryDocument.getIdRepository();
        final DataProviderBO dataProvider = this.dataProviderService.findById(repositoryId);
        return new AnnotationContext(repositoryDocument, dataProvider);
    }

    public String resolveInputFilePath(RepositoryDocument repositoryDocument) {
        return this.filesystemDAO.getPdfPath(repositoryDocument.getIdDocument(), repositoryDocument.getIdRepository());
    }

    public void backupFile(String inputFilePath) {
        try (
                PDDocument document = PDDocument.load(new File(inputFilePath));
                PDDocument backupDocument = new PDDocument()
        ) {
            String backupFilePath = inputFilePath.replace(".pdf", ".pdf.backup");
            File backupFile = new File(backupFilePath);
            if (!backupFile.exists()) {
                backupFile.createNewFile();
                logger.info("Backup file created");
            }
            backupDocument.addPage(document.getPage(0));
            backupDocument.save(backupFile);
        } catch (Exception e) {
            logger.error("Backup of file {} failed", inputFilePath);
            logger.error("Exception occurred", e);
        }
    }

    private Boolean decorateOneDocument(RepositoryDocument repositoryDocument, Callable<Boolean> precondition) throws Exception {
        try {
            // If the precondition for decorating haven't succeed, we stop further processing
            logger.info("Testing preconditions...");
            if (!precondition.call()) return false;
        } catch (Exception preconditionFailedException) {
            // On any error, stop further processing assuming precondition was falsy
            logger.error("Error on the precondition", preconditionFailedException);
            throw preconditionFailedException;
        }

        logger.info("Preconditions passed");
        logger.info("Decorating item ...");
        long start = System.currentTimeMillis();
        final String filePath = resolveInputFilePath(repositoryDocument);
        final AnnotationContext annotationContext = createAnnotationContext(repositoryDocument);
        try (PDDocument document = loadDocument(filePath)) {
            PDPage firstPage = document.getPage(0);
            injectHeader(document, firstPage, annotationContext);
            logger.info("Backing up " + filePath);
            this.backupFile(filePath);
            logger.info("Saving " + filePath);
            saveDocument(document, filePath);
            logger.info("Decorated document written in " + filePath);
        }
        long end = System.currentTimeMillis();
        logger.info("Decorating item finished successfully in {} ms", end - start);
        return true;
    }

    public static boolean isDecorated(PDDocument document) throws IOException {
        List<PDAnnotation> annotations = document.getPage(0).getAnnotations();
        for (PDAnnotation annotation : annotations) {
            PDAnnotationLink link = (PDAnnotationLink) annotation;
            PDAction action = link.getAction();
            if (action instanceof PDActionURI) {
                PDActionURI uri = (PDActionURI) action;
                if (uri.getURI().contains("core.ac.uk") && uri.getURI().contains(AnnotationContext.UTM_CAMPAIGN_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void decorateOneDocument(Integer documentId) throws Exception {
        try {
            final RepositoryDocument repositoryDocument = repositoryDocumentDAO.getRepositoryDocumentById(documentId);
            this.decorateOneDocument(repositoryDocument, () -> true);
        } catch (IOException | DataProviderNotFoundException e) {
            logger.error("Skipping document due to", e);
        }
    }


    public Boolean decorateOneDocumentWithPreconditions(Integer documentId) throws Exception {
        try {
            final RepositoryDocument repositoryDocument = repositoryDocumentDAO.getRepositoryDocumentById(documentId);
            Callable<Boolean> precondition = new DocumentDecorationCondition(resolveInputFilePath(repositoryDocument));
            return decorateOneDocument(repositoryDocument, precondition);
        } catch (DataProviderNotFoundException e) {
            logger.error("Skipping document due to", e);
            return false;
        }
    }

    public String decorateRepository(Integer repositoryId) {
        logger.info("Start decorating repository {}", repositoryId);

        final int BATCH_SIZE = 1000;
        int offset = 0;
        List<RepositoryDocument> documents;

        do {
            logger.info("Start query offset {} limit {}", offset, BATCH_SIZE);

            documents = repositoryDocumentDAO.getRepositoryDocumentsByRepositoryId(repositoryId, offset, BATCH_SIZE);
            offset += documents != null ? documents.size() : 0;

            logger.info("Query fetched {} documents", documents.size());

            for (RepositoryDocument doc : documents) {
                try {
                    Callable<Boolean> precondition = new DocumentDecorationCondition(resolveInputFilePath(doc));
                    decorateOneDocument(doc, precondition);
                } catch (Exception e) {
                    logger.warn("Skipping document {} due to: {}", doc.getIdDocument(), e.getMessage());
//                    logger.error("Exception", e);
                }
            }
            logger.info("Batch decorated");
        } while (documents.size() == BATCH_SIZE);

        TaskDescription taskDescription = new TaskDescription();
        logger.info("Repository {} decorated", repositoryId);
        return new Gson().toJson(taskDescription);
    }
}
