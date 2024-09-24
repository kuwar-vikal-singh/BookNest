package com.axel.booknest.Ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.axel.booknest.R;


public class PdfViewerActivity extends AppCompatActivity {
    private WebView webView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());

        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        String pdfUrl = getIntent().getStringExtra("pdfUrl");
        if (pdfUrl != null) {
            loadPdfFromFirebase(pdfUrl);
        } else {
            Toast.makeText(this, "PDF URL not found", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadPdfFromFirebase(String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
