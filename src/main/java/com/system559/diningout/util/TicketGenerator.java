package com.system559.diningout.util;

import com.google.zxing.WriterException;
import com.system559.diningout.model.Ticket;
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
import java.util.List;

public class TicketGenerator {
    private final PDFont font;
    private final PDImageXObject borderImage;
    private final PDImageXObject phoenixImage;
    private final PDImageXObject projectPhoenixQrImage;
    private final PDDocument document;

    private final Color primaryColor = new Color(117,98,0);
    private final String qrLink = "tel:520-732-0613";

    private PDPage page;
    private PDPageContentStream contentStream;

    private int count = 0;
    private final int ticketOffsetY = 396;

    public TicketGenerator() throws IOException, WriterException {
        document = new PDDocument();
        page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        font = PDType0Font.load(document, new ClassPathResource("static/tickets/grenadier.regular.ttf").getFile());

        File borderImageFile = new ClassPathResource("static/tickets/button2-drk-gold.png").getFile();
        borderImage = PDImageXObject.createFromFileByContent(borderImageFile, document);

        File phoenixImageFile = new ClassPathResource("static/tickets/111th_MI_BDE_Patch.png").getFile();
        phoenixImage = PDImageXObject.createFromFileByContent(phoenixImageFile, document);

        BufferedImage qrImage = QRGenerator.generateQRCodeImage(qrLink);
        projectPhoenixQrImage = JPEGFactory.createFromImage(document, qrImage);
    }

    public static InputStreamSource getInputStream(PDDocument document) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    public PDDocument createTickets(List<Ticket> tickets) throws IOException {
        for (Ticket ticket : tickets) {
            createTicket(ticket);
        }
        contentStream.close();
        return document;
    }

    private void createTicket(Ticket ticket) throws IOException {
        count++;
        if (count > 1 && count % 2 != 0) {
            contentStream.close();
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
        }

        TicketData ticketData = getTicketData(ticket);

        drawStaticItems();
        drawDynamicItems(ticketData);
    }

    private void drawStaticItems() throws IOException {
        drawBorder();
        drawHeadline();
        drawDate();
        drawPhoenix();
        drawQRandLink();
        drawPhoenixBlurb();
    }

    private void drawDynamicItems(TicketData ticketData) throws IOException {
        drawTicketNumber(ticketData.ticketSerial);
        drawTicketHolderName(ticketData.ticketHolderName);
    }

    private void drawBorder() throws IOException {
        contentStream.drawImage(borderImage, 36, 36 + offsetY());
    }

    private void drawHeadline() throws IOException {
        drawCenteredText("111th Military Intelligence Brigade Dining Out", 18, 306 + offsetY());
    }

    private void drawDate() throws IOException {
        drawCenteredText("April 1, 2022 at 5:00PM",18,270 + offsetY());
    }

    private void drawPhoenix() throws IOException {
        contentStream.drawImage(phoenixImage,432,90 + offsetY());
    }

    private void drawQRandLink() throws IOException {
        contentStream.drawImage(projectPhoenixQrImage,72,90 + offsetY());
        PDRectangle position = new PDRectangle();
        position.setLowerLeftX(72);
        position.setLowerLeftY(90 + offsetY());
        position.setUpperRightX(198);
        position.setUpperRightY(216 + offsetY());
        PDAnnotationLink projectPhoenixQrLink = getProjectPhoenixQrLink();
        projectPhoenixQrLink.setRectangle(position);
        page.getAnnotations().add(projectPhoenixQrLink);
    }

    private void drawPhoenixBlurb() throws IOException {
        contentStream.setFont(font,12);
        contentStream.beginText();
        contentStream.setNonStrokingColor(primaryColor);
        contentStream.newLineAtOffset(72,72 + offsetY());
        contentStream.showText("Need a ride? Scan the QR code or tap it on your phone to call Task Force Phoenix");
        contentStream.endText();
    }

    private PDAnnotationLink getProjectPhoenixQrLink() {
        PDAnnotationLink link = new PDAnnotationLink();
        PDActionURI uri = new PDActionURI();
        uri.setURI(qrLink);
        link.setAction(uri);
        return link;
    }

    private void drawTicketNumber(long ticketSerial) throws IOException {
        contentStream.setFont(font,18);

        contentStream.setNonStrokingColor(primaryColor);
        contentStream.beginText();
        drawN();
        drawO();
        drawNumber(ticketSerial);
        contentStream.endText();
        drawUnderline();
    }

    private void drawN() throws IOException {
        contentStream.newLineAtOffset(486f,306 + offsetY());
        contentStream.showText("N");
    }

    private void drawO() throws IOException{
        contentStream.setFont(font,12);
        contentStream.setTextRise(6f);
        contentStream.showText("O ");
    }

    private void drawNumber(long ticketSerial) throws IOException {
        contentStream.setFont(font, 18);
        contentStream.setTextRise(0);
        contentStream.showText(String.format("%03d",ticketSerial));
    }

    private void drawUnderline() throws IOException {
        float offsetX = (font.getStringWidth("N") * 18 / 1000f) + 487f;
        float width = font.getStringWidth("o") * 12 / 1000f;
        contentStream.setLineWidth(1.0f);
        contentStream.moveTo(offsetX,311 + offsetY());
        contentStream.lineTo(offsetX + width, 311 + offsetY());
        contentStream.stroke();
    }

    private void drawTicketHolderName(String ticketHolderName) throws IOException {
        drawCenteredText(ticketHolderName,28,234 + offsetY());
    }

    private void drawCenteredText(String text, int fontSize, int offSetY) throws IOException {
        contentStream.setFont(font,fontSize);
        contentStream.beginText();

        float pageWidth = page.getMediaBox().getWidth();
        float textWidth = font.getStringWidth(text) * fontSize / 1000F;
        float offsetX = (pageWidth - textWidth) /2F;

        contentStream.newLineAtOffset(offsetX,offSetY);
        contentStream.setNonStrokingColor(primaryColor);
        contentStream.showText(text);
        contentStream.endText();
    }

    private int offsetY() {
        return ticketOffsetY * (count % 2);
    }

    private TicketData getTicketData(Ticket ticket) {
        return new TicketData(ticket.getTicketSerial(), getTicketHolderName(ticket));
    }

    public static String getTicketHolderName(Ticket ticket) {
        String address = ticket.getGuest().getGrade().getName().equals("CIV") ? "" : ticket.getGuest().getGrade().getName() + " ";
        return address + ticket.getGuest().getFirstName() + " " + ticket.getGuest().getLastName();
    }

    @AllArgsConstructor
    @Data
    public static class TicketData {
        private long ticketSerial;
        private String ticketHolderName;

        public TicketData() {

        }
    }
}
