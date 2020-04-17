package com.example.crudapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class CreateUpdateMhsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_MEDIA = 2;
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;

    private Button btnAdd;
    private EditText editTextNama, editTextAlamat;
    private CircleImageView imgView;
    private  String currentPhotoPath;
    private  boolean isUpdate = false;
    public  final static  String EXTRA_MAHASISWA = "extra_mahasiswa";
    private Mahasiswa mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update);

        btnAdd = findViewById(R.id.btn_add);
        editTextNama = findViewById(R.id.et_nama);
        editTextAlamat = findViewById(R.id.et_alamat);
        imgView = findViewById(R.id.iv_profile);
        mahasiswa = getIntent().getParcelableExtra(EXTRA_MAHASISWA);
        if(mahasiswa!=null){
            isUpdate = true;
            editTextNama.setText(mahasiswa.getNama());
            editTextAlamat.setText(mahasiswa.getAlamat());
            Glide.with(this)
                    .load(ApiClient.getImageUrl(mahasiswa.getFoto()))
                    .placeholder(R.drawable.iconprofile)
//                    .apply(new RequestOptions().override(60, 60))
                    .into(imgView);
        }
        btnAdd.setOnClickListener(this);
        imgView.setOnClickListener(this);

    }
    
    public void verifikasiData() {
        boolean ready = true;
        String nama = editTextNama.getText().toString();
        String alamat = editTextAlamat.getText().toString();

        if (nama.isEmpty()) {
            editTextNama.setError("Nama masih belum terisi");
            ready = false;
        }
        if (alamat.isEmpty()) {
            editTextAlamat.setError("Alamat masih belum terisi");
            ready = false;
        }

        if (currentPhotoPath == null && !isUpdate) {
            showMessage("Gambar belum dipilih");
            ready = false;
        }

        Mahasiswa mahasiswa = new Mahasiswa(nama, alamat);
        if (ready) {
            if (isUpdate) {
                updateMahasiswa(mahasiswa);
            } else {
                createMahasiswa(mahasiswa);
            }
        }
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_add){
            verifikasiData();
        }else if(v.getId() == R.id.iv_profile){
            showDialogSelectImage();
        }
    }

    private void showDialogSelectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gambar dari")
                .setItems(R.array.arr_media, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else if (which == 1) {
                        getImageFromMedia();
                    }
                });
        builder.create().show();
    }

    private void getImageFromMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_MEDIA);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showMessage(ex.getMessage());
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),
                        "com.example.crudapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            Glide.with(this)
                    .load(file)
                    .placeholder(R.drawable.iconprofile)
                    .into(imgView);
        }
        if (requestCode == REQUEST_IMAGE_MEDIA && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            currentPhotoPath = getRealPathFromUri(uri);
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.iconprofile)
                    .into(imgView);
        }
    }

    private String getRealPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getBaseContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void createMahasiswa(Mahasiswa mahasiswa){
        File photo = new File(currentPhotoPath);
        RequestBody requestBody = RequestBody.create(photo, MediaType.parse("image/*"));
        MultipartBody.Part foto = MultipartBody.Part.createFormData("foto", photo.getName(), requestBody);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("nama", createPartFromString(mahasiswa.getNama()));
        map.put("alamat", createPartFromString(mahasiswa.getAlamat()));

        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Services.class).createMahasiswa(foto, map)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        boolean error = response.body().isError();
                        if (!error) {
                            setResult(RESULT_ADD);
                            finish();
                        } else {
                            showMessage(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        t.printStackTrace();
                        showMessage(t.getMessage());
                    }
                });
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(descriptionString, okhttp3.MultipartBody.FORM);
    }

    private void updateMahasiswa(Mahasiswa mahasiswa) {
        MultipartBody.Part foto = null;
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
            foto = MultipartBody.Part.createFormData("foto", file.getName(), requestBody);
        }

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("id", createPartFromString(this.mahasiswa.getId()));
        map.put("nama", createPartFromString(mahasiswa.getNama()));
        map.put("alamat", createPartFromString(mahasiswa.getAlamat()));

        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Services.class).updateMahasiswa(foto, map)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        boolean error = response.body().isError();
                        if (!error) {
                            setResult(RESULT_UPDATE);
                            finish();
                        } else {
                            showMessage(response.body().getMessage());
                            Log.d(CreateUpdateMhsActivity.class.getSimpleName(), response.body().getErrors().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        t.printStackTrace();
                        showMessage(t.getMessage());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isUpdate) {
            getMenuInflater().inflate(R.menu.form_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteMahasiswa();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMahasiswa(){
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Services.class).deleteMahassiswa(mahasiswa.getId())
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        boolean error = response.body().isError();
                        if (!error) {
                            setResult(RESULT_DELETE);
                            finish();
                        } else {
                            showMessage(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        t.printStackTrace();
                        showMessage(t.getMessage());
                    }
                });
    }
}
