package com.example.crudapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvListMahasiswa;
    FloatingActionButton fab_Add;
    private MahasiswaAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvListMahasiswa = (RecyclerView) findViewById(R.id.rv_listmhs);
        fab_Add = (FloatingActionButton) findViewById(R.id.fab_add);

        fab_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateUpdateMhsActivity.class);
                startActivityForResult(intent, CreateUpdateMhsActivity.REQUEST_ADD);
            }
        });

        rvListMahasiswa.setHasFixedSize(true);
        rvListMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MahasiswaAdapter();
        rvListMahasiswa.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvListMahasiswa.addItemDecoration(decoration);
        adapter.setOnCLickListener(new MahasiswaAdapter.OnCLickListener() {
            @Override
            public void onClick(Mahasiswa mahasiswa) {
                Intent intent = new Intent(MainActivity.this, CreateUpdateMhsActivity.class);
                intent.putExtra(CreateUpdateMhsActivity.EXTRA_MAHASISWA, mahasiswa);
                startActivityForResult(intent, CreateUpdateMhsActivity.REQUEST_UPDATE);
            }
        });
        refreshData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateUpdateMhsActivity.REQUEST_ADD && resultCode == CreateUpdateMhsActivity.RESULT_ADD) {
            showMessage("Berhasil Menambahkan Mahasiswa");
            refreshData();
        } else if (requestCode == CreateUpdateMhsActivity.REQUEST_UPDATE) {
            if (resultCode == CreateUpdateMhsActivity.RESULT_UPDATE) {
                showMessage("Berhasil Mengubah Data Mahasiswa");
                refreshData();
            } else if (resultCode == CreateUpdateMhsActivity.RESULT_DELETE) {
                showMessage("Berhasil Mengapus Mahasiswa");
                refreshData();
            }
        }
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void refreshData(){
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Services.class).getMahasiswa().enqueue(new Callback<Response>() {
                @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response){
                            boolean error = response.body().isError();
                            if (!error) {
                                ArrayList<Mahasiswa> list = response.body().getMahasiswa();
                                if (list.isEmpty()){
                                    showMessage("Data Kosong");
                                } else {
                                    adapter.setListMahasiswa(list);
                                    showMessage("Succes get" + list.size() + "data");
                                }
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
