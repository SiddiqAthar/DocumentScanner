package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ExtractImages;
import me.sid.smartcropper.utils.InterstitalAdsInner;
import me.sid.smartcropper.utils.PDFUtils;
import me.sid.smartcropper.utils.SharePrefData;
import me.sid.smartcropper.utils.StringUtils;

public class PDFViewerAcitivity extends BaseActivity implements OnErrorListener, GenericCallback,
        OnLoadCompleteListener, View.OnClickListener {
    Toolbar toolbar;
    PDFView pdfView;
    File file;
    DirectoryUtils mDirectory;
    Uri uri;
    Boolean firstTry = true;
    PDFUtils pdfUtils;
    TextView backWordTv, forwordTv;
    LinearLayout addPagesLayout, pagerNumberLL;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_viewer_acitivity);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        pdfUtils = new PDFUtils(PDFViewerAcitivity.this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PDF Reader");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        pdfView = findViewById(R.id.pdfView);
        dialog = new ProgressDialog(PDFViewerAcitivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Opening pdf file");

        if (getIntent().getExtras() != null) {
            String path = getIntent().getStringExtra("path");
            if (path != null) {
                file = new File(Objects.requireNonNull(path));
                toolbar.setTitle(file.getName().substring(0, file.getName().lastIndexOf(".")));
            } else {
                uri = Uri.parse(getIntent().getStringExtra("uri"));
            }
            dialog.show();
            loadPDFFile(file == null ? uri : file, "");
        }

        mDirectory = new DirectoryUtils(PDFViewerAcitivity.this);
        addPagesLayout = findViewById(R.id.addPagesLayout);
        pagerNumberLL = findViewById(R.id.pagerNumberLL);
        backWordTv = findViewById(R.id.backWordTv);
        backWordTv.setOnClickListener(this);
        forwordTv = findViewById(R.id.forwordTv);
        forwordTv.setOnClickListener(this);

    }

    private void loadPDFFile(Comparable<? extends Comparable<?>> comparable, String password) {
        if (comparable instanceof File) {
            pdfView.fromFile(file).defaultPage(0)
                    .enableDoubletap(true)
                    .enableSwipe(true)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .onError(this)
                    .onLoad(this)
                    .password(password)
                    .load();
        } else if (comparable instanceof Uri) {
            pdfView.fromUri(uri).defaultPage(0)
                    .enableDoubletap(true)
                    .enableSwipe(true)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .onLoad(this)
                    .onError(this)
                    .password(password)
                    .load();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            InterstitalAdsInner adsInner=new InterstitalAdsInner();
            if(SharePrefData.getInstance().getIsAdmobPdfInter().equals("true") && !SharePrefData.getInstance().getADS_PREFS()){
                adsInner.adMobShowCloseOnly(this);
            }else if (SharePrefData.getInstance().getIsAdmobPdfInter().equals("false") && !SharePrefData.getInstance().getADS_PREFS()) {
                adsInner.showFbClose(this);
            }else{
                finish();
            }
            return true;
        }
        else if (item.getItemId() == R.id.share) {
            Constants.shareFile(PDFViewerAcitivity.this, file);
            return true;
        }
        else if (item.getItemId() == R.id.pdf_to_img) {
                            ExtractImages extractImages= new ExtractImages(this);
                            extractImages.extract(String.valueOf(file));
            return true;
        }

//        else if (item.getItemId() == R.id.moveToFolder) {
//            new MoveFileDialog(PDFViewerAcitivity.this, file, new GenericCallback() {
//                @Override
//                public void callback(Object o) {
//                    if ((boolean) o) {
//                        Toast.makeText(PDFViewerAcitivity.this, "File moved", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(PDFViewerAcitivity.this, "File not moved", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }).show();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        InterstitalAdsInner adsInner=new InterstitalAdsInner();
        if(SharePrefData.getInstance().getIsAdmobPdfInter().equals("true") && !SharePrefData.getInstance().getADS_PREFS()){
            adsInner.adMobShowCloseOnly(this);
        }else if (SharePrefData.getInstance().getIsAdmobPdfInter().equals("false") && !SharePrefData.getInstance().getADS_PREFS()) {
            adsInner.showFbClose(this);
        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_menu, menu);
        return true;
    }


    @Override
    public void onError(Throwable t) {
        dialog.dismiss();

    }


    @Override
    public void callback(Object o) {
        loadPDFFile(file == null ? uri : file, (String) o);

    }

    @Override
    public void loadComplete(int nbPages) {
        dialog.dismiss();
        if (pdfView.getPageCount() > 0 && pdfView.getPageCount() < 7) {
            pagerNumberLL.setVisibility(View.VISIBLE);
            addPageNumbers();
        } else {
            pagerNumberLL.setVisibility(View.GONE);
        }

    }

    private void addPageNumbers() {
        for (int i = 0; i < pdfView.getPageCount(); i++) {
            TextView textView = new TextView(this);
            textView.setText(i + "");
            textView.setBackgroundResource(R.drawable.left_right_swipe_bg);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            linearParams.weight = 1;
            linearParams.setMarginEnd(5);
            linearParams.setMarginStart(5);
            textView.setLayoutParams(linearParams);
            textView.setGravity(Gravity.CENTER);
            addPagesLayout.addView(textView);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pdfView.jumpTo(finalI, true);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backWordTv) {
            if (pdfView.getCurrentPage() > 0) {
                pdfView.jumpTo(pdfView.getCurrentPage() - 1, true);
            } else {
                StringUtils.getInstance().showSnackbar(PDFViewerAcitivity.this, "No more page left");
            }
        } else if (view.getId() == R.id.forwordTv) {
            if (pdfView.getCurrentPage() < pdfView.getPageCount()) {
                pdfView.jumpTo(pdfView.getCurrentPage() + 1, true);
            } else {
                StringUtils.getInstance().showSnackbar(PDFViewerAcitivity.this, "No more page left");
            }
        }
    }
}