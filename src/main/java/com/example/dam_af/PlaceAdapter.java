package com.example.dam_af;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private List<Place> places;

    public interface OnPlaceClickListener {

        void onPlaceClick(Place place);
    }

    private OnPlaceClickListener listener;

    public interface LocationCallback {
        void onLocationReceived(Location location);
    }

    public interface PlacesCallback {
        void onSuccess(List<Place> places);

        void onError(String error);
    }

    public PlaceAdapter(List<Place> places, OnPlaceClickListener listener) {
            this.places = places; this.listener = listener;
        }

        @NonNull
        @Override
        public PlaceViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
        int viewType) {


            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(
                                    R.layout.item_place,
                                    parent,
                                    false);

            return new PlaceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull PlaceViewHolder holder,
        int position) {

            Place place = places.get(position);

            holder.txtNome.setText(place.getNome());
            holder.txtEndereco.setText(place.getEndereco());
            holder.txtTipo.setText("Tipo: " + place.getTipo());
            holder.txtDistancia.setText("Distância: " + place.getDistancia());

            holder.itemView.setOnClickListener(v -> {

                if(listener != null){

                    listener.onPlaceClick(place);
                }
            });
        }

        @Override
        public int getItemCount() {
            return places.size();
        }

        static class PlaceViewHolder extends RecyclerView.ViewHolder {

            TextView txtNome;
            TextView txtEndereco;
            TextView txtTipo;
            TextView txtDistancia;

            public PlaceViewHolder(@NonNull View itemView) {
                super(itemView);

                txtNome = itemView.findViewById(R.id.txtNome);

                txtEndereco = itemView.findViewById(R.id.txtEndereco);

                txtTipo = itemView.findViewById(R.id.txtTipo);

                txtDistancia = itemView.findViewById(R.id.txtDistancia);
            }
        }


}
