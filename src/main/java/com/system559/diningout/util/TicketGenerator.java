package com.system559.diningout.util;

import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class TicketGenerator {
    private final PDDocument document;
    private final PDPage page;
    private final List<TicketData> tickets;
    private final PDPageContentStream contentStream;
    private final PDFont font;

    private final int guestOffsetY = 396;

    public TicketGenerator() throws IOException {
        document = new PDDocument();
        page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        font = PDType0Font.load(document, new ClassPathResource("static/tickets/grenadier.regular.ttf").getFile());
        tickets = new ArrayList<>();
    }

    public List<TicketData> getTickets() {
        return tickets;
    }

    public void buildPdf() throws IOException, WriterException {
        drawBorder(0);
        int bannerOffsetY = 702;
        drawCenteredText("111th Military Intelligence Brigade Dining Out", 18, bannerOffsetY);
        int dateOffsetY = 666;
        drawCenteredText("April 1, 2022 at 5:00PM", 18, dateOffsetY);
        int nameOffsetY = 630;
        drawCenteredText(tickets.get(0).ticketHolderName, 28, nameOffsetY);
        drawTicketNumber(0);
        drawPhoenix(0);
        drawQRCode(0);
        if (tickets.size() == 2) {
            drawBorder(1);
            drawCenteredText("111th Military Intelligence Brigade Dining Out", 18, bannerOffsetY - guestOffsetY);
            drawCenteredText("April 1, 2022 at 5:00PM", 18, dateOffsetY - guestOffsetY);
            drawCenteredText(tickets.get(1).ticketHolderName, 28, nameOffsetY - guestOffsetY);
            drawTicketNumber(1);
            drawPhoenix(1);
            drawQRCode(1);
        }
    }

    private void drawPhoenix(int guestIndex) throws IOException {
        float offsetX = 432;
        float offsetY = 486;
        File phoenixImageFile = new ClassPathResource("static/tickets/111th_MI_BDE_Patch.png").getFile();
        PDImageXObject image = PDImageXObject.createFromFileByContent(phoenixImageFile, document);
        contentStream.drawImage(image, offsetX, offsetY - (guestIndex * guestOffsetY));
    }

    private void drawQRCode(int guestIndex) throws WriterException, IOException {
        float offsetX = 72;
        float offsetY = 486;
        String link = "https://111th-dining-out.system559.com/verify/" + tickets.get(guestIndex).ticketId;
        BufferedImage qrImage = QRGenerator.generateQRCodeImage(link);
        PDImageXObject image = JPEGFactory.createFromImage(document, qrImage);
        contentStream.drawImage(image, offsetX, offsetY - (guestIndex * guestOffsetY));

        PDAnnotationLink boxLink = new PDAnnotationLink();
        PDActionURI action = new PDActionURI();
        action.setURI(link);
        boxLink.setAction(action);

        PDRectangle position = new PDRectangle();
        position.setLowerLeftX(offsetX);
        position.setLowerLeftY(offsetY - (guestIndex * guestOffsetY));
        position.setUpperRightX(offsetX + 126);
        position.setUpperRightY((offsetY - (guestIndex * guestOffsetY) + 126));
        boxLink.setRectangle(position);
        page.getAnnotations().add(boxLink);
    }

    public void outputToStream(OutputStream stream) throws IOException {
        contentStream.close();
        document.save(stream);
        document.save("src/main/resources/test.pdf");
        document.close();
    }

    public InputStreamSource getInputStream() throws IOException {
        contentStream.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    private void drawBorder(int guestIndex) throws IOException {
        File borderImageFile = new ClassPathResource("static/tickets/button2-drk-gold.png").getFile();
        PDImageXObject image = PDImageXObject.createFromFileByContent(borderImageFile, document);
        int borderPositionY = 432;
        int borderPositionX = 36;
        contentStream.drawImage(image, borderPositionX, borderPositionY - (guestIndex * guestOffsetY));
    }

    private void drawCenteredText(String text, int size, int offsetY) throws IOException {
        contentStream.setFont(font, size);
        contentStream.beginText();

        float pageWidth = page.getMediaBox().getWidth();
        float textWidth = font.getStringWidth(text) * size / 1000F;
        float offsetX = (pageWidth - textWidth) / 2F;

        contentStream.newLineAtOffset(offsetX, offsetY);
        contentStream.setNonStrokingColor(new Color(117, 98, 0));

        contentStream.showText(text);
        contentStream.endText();
    }

    private void drawTicketNumber(int guestIndex) throws IOException {
        contentStream.setFont(font, 18);

        float offsetX = (font.getStringWidth("N") * 18 / 1000f) + 487f;
        float offsetY = 702f - (guestIndex * guestOffsetY);
        float width = font.getStringWidth("o") * 12 / 1000f;

        contentStream.beginText();
        contentStream.newLineAtOffset(486f, offsetY);
        contentStream.setNonStrokingColor(new Color(117, 98, 0));
        contentStream.showText("N");
        contentStream.setFont(font, 12);
        contentStream.setTextRise(6f);
        contentStream.showText("O ");
        contentStream.setFont(font, 18);
        contentStream.setTextRise(0);
        contentStream.showText(String.format("%03d", tickets.get(guestIndex).ticketSerial));
        contentStream.endText();

        contentStream.setStrokingColor(new Color(117, 98, 0));
        contentStream.setLineWidth(1.0f);
        contentStream.moveTo(offsetX, offsetY + 5f);
        contentStream.lineTo(offsetX + width, offsetY + 5f);
        contentStream.stroke();
    }

    @AllArgsConstructor
    @Data
    public static class TicketData {
        private long ticketSerial;
        private String ticketHolderName;
        private String ticketId;

        public TicketData() {

        }
    }
}
