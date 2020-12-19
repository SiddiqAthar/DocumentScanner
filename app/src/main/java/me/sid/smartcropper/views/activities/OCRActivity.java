package me.sid.smartcropper.views.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.SaveOCRFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.OCRUtils;

import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class OCRActivity extends BaseActivity {

    public TextView ocrText;
    public Button btn_done;
    private ProgressBar extractingProgress;
    public static String FILE_PATH = "file_path";
    ArrayList<Bitmap> bitmaps;
    StringBuffer extractedText;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("OCR");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ocrText = findViewById(R.id.ocrText);
        btn_done = findViewById(R.id.btn_done);
        extractingProgress = findViewById(R.id.extractingProgress);
        extractingProgress.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        ocrText.setText("Extracting Text Please Wait");
        bitmaps = new ArrayList<>();
//        bitmaps.add(croppedBitmap);
        bitmaps.addAll(croppedArrayBitmap);

        new OCRExtractTask( this, getApplicationContext(), bitmaps ).execute();


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!extractedText.equals("")) {
                    new SaveOCRFileDialog(OCRActivity.this, new GenericCallback() {
                        @Override
                        public void callback(Object o) {
                            String name = (String) o;
                            if (!name.equals("")) {
                                writeToFile(name, String.valueOf(extractedText));
                            } else {
                                Toast.makeText(OCRActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).show();
                }


            }
        });
    }


    private void writeToFile(String name, String data) {
        try {

            File directory = new File(Environment.getExternalStorageDirectory().toString(), folderDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, name + ".txt");
            if (file.exists()) {
                file.delete();
            }


            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();

            Toast.makeText(this, "Text file Saved !", Toast.LENGTH_LONG).show();
            finish();
        } catch (java.io.IOException e) {

            //do something if an IOException occurs.
            Toast.makeText(this, "ERROR - Text could't be added", Toast.LENGTH_LONG).show();
        }
    }

    public void setText(String content) {
        this.ocrText.setText(content);
    }

      private class OCRExtractTask extends AsyncTask<String, Void, String>
      {

          private OCRActivity ocrActivity;
          private Context context;
          private ArrayList<Bitmap> arrayList;

          public OCRExtractTask(OCRActivity ocrActivity, Context context, ArrayList<Bitmap> bitmaps){
              this.ocrActivity = ocrActivity;
              this.context = context;
              this.arrayList=bitmaps;
           }

          @Override
          protected String doInBackground(String... strings) {
              try {

                  extractedText = new StringBuffer();

                  for (Bitmap eachPage : bitmaps) {
                      try {
                          extractedText.append(
                                  OCRUtils.getTextFromBitmap(OCRActivity.this, eachPage)
                          );

/*                          Log.d(  "Clean Scan", "detected text : " + extractedText );
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  btn_done.setVisibility(View.VISIBLE);
                                  extractingProgress.setVisibility(View.GONE);
                                  ocrText.setText(extractedText);
                              }
                          });*/

                      } catch (InterruptedException e) {
                          e.printStackTrace();

                          btn_done.setVisibility(View.GONE);
                          extractingProgress.setVisibility(View.GONE);
                          ocrText.setText("No Text Found");
                      }


                  }
              }
              catch (Exception e)
              {
                  return "";
              }
                  return String.valueOf(extractedText);

          }

          @Override
          protected void onPostExecute(String s) {
              super.onPostExecute(s);
              if (s.equals(""))
              {
                  btn_done.setVisibility(View.GONE);
                  extractingProgress.setVisibility(View.GONE);
                  ocrText.setText("No Text Found");
              }
              else
              {
                  btn_done.setVisibility(View.VISIBLE);
                  extractingProgress.setVisibility(View.GONE);
                  ocrText.setText(s);
              }
          }
      }


    public void processText() {

        StringBuffer extractedText = new StringBuffer();

        for (Bitmap eachPage : bitmaps ) {
            try {
                extractedText.append(
                        OCRUtils.getTextFromBitmap(OCRActivity.this, eachPage)
                );

                Log.d(  "Clean Scan", "detected text : " + extractedText );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_done.setVisibility(View.VISIBLE);
                        extractingProgress.setVisibility(View.GONE);
                        ocrText.setText(extractedText);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();

                btn_done.setVisibility(View.GONE);
                extractingProgress.setVisibility(View.GONE);
                ocrText.setText("No Text Found");
            }


        }




//        new OCRExtractTask( this, getApplicationContext(), filePath ).execute();

     /*   TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(getApplicationContext(), "Detector is not available yet", Toast.LENGTH_LONG);
        }
        Frame frame = new Frame.Builder().setBitmap(bitmaps.get(0)).build();
        final SparseArray<TextBlock> items = textRecognizer.detect(frame);
        if (items.size() != 0) {
            ocrText.post(new Runnable() {
                @Override
                public void run() {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    fetchedText = stringBuilder.toString();
                    ocrText.setText(fetchedText);
                    extractingProgress.setVisibility(View.GONE);
                    btn_done.setVisibility(View.VISIBLE);
                }
            });
        } else {
            ocrText.setText("No Text Found");
            extractingProgress.setVisibility(View.GONE);
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}