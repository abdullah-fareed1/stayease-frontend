package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.network.models.PaymentModel;

public class ReceiptGenerator {

    public static File generateReceipt(Context context, BookingModel booking, PaymentModel payment) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(22f);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.parseColor("#4F46E5"));

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

        int y = 60;
        canvas.drawText("Grand Horizon Hotels", 40, y, titlePaint);
        y += 20;
        canvas.drawText("Payment Receipt", 40, y, headingPaint);
        y += 30;
        canvas.drawLine(40, y, 555, y, dividerPaint);
        y += 25;

        canvas.drawText("Booking Reference:", 40, y, headingPaint);
        canvas.drawText(booking.id != null ? booking.id : "N/A", 220, y, bodyPaint);
        y += 22;

        String roomName = (booking.room != null && booking.room.title != null) ? booking.room.title : "N/A";
        canvas.drawText("Room:", 40, y, headingPaint);
        canvas.drawText(roomName, 220, y, bodyPaint);
        y += 22;

        canvas.drawText("Check-in:", 40, y, headingPaint);
        canvas.drawText(booking.checkIn != null ? booking.checkIn : "N/A", 220, y, bodyPaint);
        y += 22;

        canvas.drawText("Check-out:", 40, y, headingPaint);
        canvas.drawText(booking.checkOut != null ? booking.checkOut : "N/A", 220, y, bodyPaint);
        y += 22;

        canvas.drawText("Guests:", 40, y, headingPaint);
        canvas.drawText(String.valueOf(booking.guestCount), 220, y, bodyPaint);
        y += 22;

        canvas.drawText("Payment Type:", 40, y, headingPaint);
        canvas.drawText(payment.type != null ? payment.type : "N/A", 220, y, bodyPaint);
        y += 22;

        canvas.drawText("Amount Paid:", 40, y, headingPaint);
        canvas.drawText("$" + String.format("%.2f", payment.amount), 220, y, bodyPaint);
        y += 22;

        canvas.drawText("Date:", 40, y, headingPaint);
        canvas.drawText(payment.paidAt != null ? payment.paidAt : "N/A", 220, y, bodyPaint);
        y += 30;

        canvas.drawLine(40, y, 555, y, dividerPaint);
        y += 20;

        Paint footerPaint = new Paint();
        footerPaint.setTextSize(10f);
        footerPaint.setColor(Color.parseColor("#9CA3AF"));
        canvas.drawText("Thank you for choosing Grand Horizon Hotels.", 40, y, footerPaint);

        document.finishPage(page);

        File receiptsDir = new File(context.getFilesDir(), "receipts");
        if (!receiptsDir.exists()) {
            receiptsDir.mkdirs();
        }

        File file = new File(receiptsDir, booking.id + ".pdf");
        FileOutputStream fos = new FileOutputStream(file);
        document.writeTo(fos);
        document.close();
        fos.close();

        return file;
    }
}