package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val enFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))

    fun taka(amount: Number): String {
        val v = amount.toLong()
        return "৳ ${enFormat.format(v)}"
    }

    fun numberFormatted(amount: Number): String {
        return enFormat.format(amount.toLong())
    }
}

object DocumentExporter {

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun sharePlainText(context: Context, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, subject))
    }

    fun createPdfFile(
        context: Context,
        fileName: String,
        headerTitle: String,
        subHeader: String,
        bodyText: String
    ): File {
        val pdfDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val sanitized = fileName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val file = File(pdfDir, "$sanitized.pdf")

        val document = PdfDocument()
        // A4 in points (72 dpi): 595 x 842
        val pageWidth = 595
        val pageHeight = 842
        val margin = 48

        val headerPaint = Paint().apply {
            color = Color.rgb(11, 87, 61) // Emerald #0B573D
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subPaint = Paint().apply {
            color = Color.rgb(80, 92, 86)
            textSize = 10.5f
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.rgb(11, 87, 61)
            strokeWidth = 1.5f
        }
        val bodyPaint = TextPaint().apply {
            color = Color.rgb(22, 29, 25)
            textSize = 11.5f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            isAntiAlias = true
        }

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var yPos = 54f
        canvas.drawText(headerTitle, margin.toFloat(), yPos, headerPaint)
        yPos += 18f
        if (subHeader.isNotBlank()) {
            canvas.drawText(subHeader, margin.toFloat(), yPos, subPaint)
            yPos += 12f
        }
        canvas.drawLine(margin.toFloat(), yPos, (pageWidth - margin).toFloat(), yPos, linePaint)
        yPos += 20f

        val contentWidth = pageWidth - (margin * 2)
        val staticLayout = StaticLayout.Builder
            .obtain(bodyText, 0, bodyText.length, bodyPaint, contentWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(4f, 1.15f)
            .build()

        canvas.save()
        canvas.translate(margin.toFloat(), yPos)
        staticLayout.draw(canvas)
        canvas.restore()

        document.finishPage(page)

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()
        return file
    }

    fun sharePdf(
        context: Context,
        fileName: String,
        headerTitle: String,
        subHeader: String,
        bodyText: String
    ) {
        val file = createPdfFile(context, fileName, headerTitle, subHeader, bodyText)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, headerTitle)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, headerTitle))
    }

    fun printPdf(
        context: Context,
        jobName: String,
        headerTitle: String,
        subHeader: String,
        bodyText: String
    ) {
        val file = createPdfFile(context, jobName, headerTitle, subHeader, bodyText)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val adapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder("$jobName.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    FileInputStream(file).use { input ->
                        FileOutputStream(destination?.fileDescriptor).use { output ->
                            input.copyTo(output)
                        }
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }
        printManager.print(jobName, adapter, PrintAttributes.Builder().build())
    }
}
