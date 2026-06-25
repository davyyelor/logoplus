package com.logopeda.shared.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

/**
 * Minimal, license-safe PDF rendering (OpenPDF / LGPL-MPL). Turns a title plus
 * a block of plain text into an A4 PDF. Used for both generated reports and
 * consent documents. This is intentionally simple: no WYSIWYG, no HTML.
 */
@Component
public class PdfRenderer {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    private static final Font META_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
    private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

    /**
     * Renders a simple document.
     *
     * @param title    main heading
     * @param subtitle optional secondary line (e.g. clinic name + date); may be null
     * @param body     free text; line breaks are preserved
     * @return PDF bytes
     */
    public byte[] render(String title, String subtitle, String body) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 56, 56, 56, 56);
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph titlePara = new Paragraph(title != null ? title : "", TITLE_FONT);
            titlePara.setSpacingAfter(8f);
            document.add(titlePara);

            if (subtitle != null && !subtitle.isBlank()) {
                Paragraph subtitlePara = new Paragraph(subtitle, META_FONT);
                subtitlePara.setSpacingAfter(16f);
                document.add(subtitlePara);
            }

            String content = body != null ? body : "";
            for (String line : content.split("\n", -1)) {
                Paragraph para = new Paragraph(line.isEmpty() ? " " : line, BODY_FONT);
                para.setAlignment(Element.ALIGN_LEFT);
                para.setSpacingAfter(4f);
                document.add(para);
            }

            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException ex) {
            throw new PdfGenerationException("Failed to generate PDF", ex);
        }
    }
}
