package com.example.srv_twry.studentcompanion;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.Adapters.PDFFilesAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class PDFCreatorHomeActivity extends AppCompatActivity implements PDFFilesAdapter.onPDFDeleted {

    @BindView(R.id.pdf_list_view_files)
    ListView pdfFilesListView;
    @BindView(R.id.fab_add_pdf_files)
    FloatingActionButton addPdfFab;
    @BindView(R.id.message_pdf_creator_home)
    TextView messagePdfCreator;

    private PDFFilesAdapter pdfFilesAdapter;
    private File pdfFolder;
    private File[] filesInDirectory;
    private ArrayList<String> filePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator_home);
        setTitle(getResources().getString(R.string.pdf_creator));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        //create or open the folder
        pdfFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ getResources().getString(R.string.created_pdf)+"/");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }

        //Initialize the list view with blank data while the files are being loaded
        filesInDirectory = pdfFolder.listFiles();
        filePaths = new ArrayList<>();
        pdfFilesAdapter = new PDFFilesAdapter(PDFCreatorHomeActivity.this,filePaths,this);
        pdfFilesListView.setAdapter(pdfFilesAdapter);

        //setting the onClickListener
        addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PDFCreatorHomeActivity.this,CreatePDFActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFiles() {
        filePaths = new ArrayList<>();
        filesInDirectory = pdfFolder.listFiles();

        if (filesInDirectory == null){
            //No files in the directory.
            Timber.v("[Load files] No files found");
        }else{
            for (File file : filesInDirectory) {
                if (!file.isDirectory() && file.getName().endsWith(".pdf")) {
                    filePaths.add(file.getPath());
                    Timber.v("addingFile: " + file.getName());
                }
            }
        }

        //Now that loading is finished , reset the adapter
        if (filePaths.size() == 0){
            messagePdfCreator.setVisibility(View.VISIBLE);
        }
        pdfFilesAdapter = new PDFFilesAdapter(PDFCreatorHomeActivity.this,filePaths,this);
        pdfFilesListView.setAdapter(pdfFilesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messagePdfCreator.setVisibility(View.GONE);
        loadFiles();    //Refresh the layout
    }


    @Override
    public void onPDFDeleted() {
        loadFiles();
    }
}
