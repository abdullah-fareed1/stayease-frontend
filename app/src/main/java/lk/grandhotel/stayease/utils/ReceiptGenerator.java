package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ReceiptGenerator {

    public static File generateReceipt(Context context, String bookingId, String roomTitle,
                                       double totalAmount, double amountPaid,
                                       String paymentType) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint brandPaint = new Paint();
        brandPaint.setTextSize(22f);
        brandPaint.setFakeBoldText(true);
        brandPaint.setColor(Color.parseColor("#7C3AED"));

        Paint headingPaint = new Paint();
        headingPaint.setTextSize(14f);
        headingPaint.setFakeBoldText(true);
        headingPaint.setColor(Color.parseColor("#1F2937"));

        Paint bodyPaint = new Paint();
        bodyPaint.setTextSize(12f);
        bodyPaint.setColor(Color.parseColor("#374151"));

        Paint dividerPaint = new Paint();
        dividerPaint.setColor(Color.parseColor("#E5E7EB"));
        dividerPaint.setStrokeWidth(1f);

        Paint accentPaint = new Paint();
        accentPaint.setTextSize(20f);
        accentPaint.setFakeBoldText(true);
        accentPaint.setColor(Color.parseColor("#7C3AED"));

        Paint footerPaint = new Paint();
        footerPaint.setTextSize(10f);
        footerPaint.setColor(Color.parseColor("#9CA3AF"));

        int y = 60;

        canvas.drawText("Grand Horizon Hotels", 40, y, brandPaint);
        y += 20;
        canvas.drawText("Payment Receipt", 40, y, headingPaint);
        y += 30;
        canvas.drawLine(40, y, 555, y, dividerPaint);
        y += 25;

        drawRow(canvas, "Booking Reference", bookingId != null ? bookingId : "N/A",
                40, y, headingPaint, bodyPaint);
        y += 22;

        drawRow(canvas, "Room", roomTitle != null ? roomTitle : "N/A",
                40, y, headingPaint, bodyPaint);
        y += 22;

        drawRow(canvas, "Payment Type",
                "PARTIAL".equals(paymentType) ? "50% Advance" : "Full Payment",
                40, y, headingPaint, bodyPaint);
        y += 22;

        drawRow(canvas, "Total Booking Value",
                String.format(Locale.getDefault(), "$%.2f", totalAmount),
                40, y, headingPaint, bodyPaint);
        y += 30;

        canvas.drawLine(40, y, 555, y, dividerPaint);
        y += 20;

        canvas.drawText("Amount Paid", 40, y, headingPaint);
        canvas.drawText(String.format(Locale.getDefault(), "$%.2f", amountPaid), 380, y, accentPaint);
        y += 30;

        canvas.drawLine(40, y, 555, y, dividerPaint);
        y += 20;

        canvas.drawText("Thank you for choosing Grand Horizon Hotels.", 40, y, footerPaint);

        document.finishPage(page);

        File receiptsDir = new File(context.getFilesDir(), "receipts");
        if (!receiptsDir.exists()) {
            receiptsDir.mkdirs();
        }

        String safeBookingId = bookingId != null ? bookingId.replaceAll("[^a-zA-Z0-9]", "_") : "receipt";
        File file = new File(receiptsDir, safeBookingId + ".pdf");

        FileOutputStream fos = new FileOutputStream(file);
        document.writeTo(fos);
        document.close();
        fos.close();

        return file;
    }

    private static void drawRow(Canvas canvas, String label, String value,
                                int x, int y, Paint labelPaint, Paint valuePaint) {
        canvas.drawText(label + ":", x, y, labelPaint);
        canvas.drawText(value, x + 180, y, valuePaint);
    }
}