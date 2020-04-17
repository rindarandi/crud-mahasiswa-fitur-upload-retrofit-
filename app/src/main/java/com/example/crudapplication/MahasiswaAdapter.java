package com.example.crudapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {

    private ArrayList<Mahasiswa> listMahasiswa;
    private OnCLickListener onCLickListener;

    public MahasiswaAdapter() {
        listMahasiswa = new ArrayList<>();
    }

    public void setListMahasiswa(ArrayList<Mahasiswa> listMahasiswa) {
        this.listMahasiswa = listMahasiswa;
        notifyDataSetChanged();
    }

    public void setOnCLickListener(OnCLickListener onCLickListener) {
        this.onCLickListener = onCLickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mahasiswa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mahasiswa mahasiswa = listMahasiswa.get(position);
        holder.tvNama.setText(mahasiswa.getNama());
        holder.tvAlamat.setText(mahasiswa.getAlamat());
        Glide.with(holder.itemView)
                .load(ApiClient.getImageUrl(mahasiswa.getFoto()))
                .placeholder(R.drawable.iconprofile)
//                .apply(new RequestOptions().override(60, 60))
                .into(holder.imgMahasiswa);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCLickListener.onClick(mahasiswa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNama;
        public TextView tvAlamat;
        public CircleImageView imgMahasiswa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMahasiswa = itemView.findViewById(R.id.iv_profile);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);

        }
    }

    public interface OnCLickListener {
        void onClick(Mahasiswa mahasiswa);
    }
}
