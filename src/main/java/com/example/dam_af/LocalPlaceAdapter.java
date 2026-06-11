package com.example.dam_af;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocalPlaceAdapter extends RecyclerView.Adapter<LocalPlaceAdapter.ViewHolder> {
    private List<LocalPlace> lista;

    public interface OnLocalPlaceListener {

        void onClick(LocalPlace local);

        void onLongClick(LocalPlace local);
    }

    private OnLocalPlaceListener listener;

    public LocalPlaceAdapter(
            List<LocalPlace> lista, OnLocalPlaceListener listener) {

        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                                parent.getContext())
                        .inflate(
                                R.layout.item_local_salvo,
                                parent,
                                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        LocalPlace local =
                lista.get(position);

        holder.txtNome.setText(
                local.getNome());

        holder.txtCategoria.setText(
                "Categoria: " +
                        local.getCategoria());

        holder.txtCoordenadas.setText(
                "Lat: " +
                        local.getLatitude() +
                        "\nLon: " +
                        local.getLongitude());

        holder.txtObservacao.setText(
                "Obs: " +
                        local.getObservacao());

        holder.itemView.setOnClickListener(v -> {

            if(listener != null){

                listener.onClick(local);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {

            if(listener != null){

                listener.onLongClick(local);
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {

        return lista.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNome;
        TextView txtCategoria;
        TextView txtCoordenadas;
        TextView txtObservacao;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtNome = itemView.findViewById(R.id.txtNomeSalvo);

            txtCategoria = itemView.findViewById(R.id.txtCategoriaSalvo);

            txtCoordenadas = itemView.findViewById(R.id.txtCoordenadasSalvo);

            txtObservacao = itemView.findViewById(R.id.txtObservacaoSalvo);
        }
    }
}
