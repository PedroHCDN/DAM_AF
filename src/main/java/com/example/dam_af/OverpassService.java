package com.example.dam_af;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class OverpassService {
    private static final String URL =
            "https://overpass-api.de/api/interpreter";

    public void buscarLocais(
            double latitude,
            double longitude,
            String categoria,
            PlaceAdapter.PlacesCallback callback) {

        Log.d("GPS", "LAT: " + latitude);
        Log.d("GPS", "LON: " + longitude);
        Log.d("GPS", "CAT: " + categoria);

        String query;

        if(categoria.equals("park")) {

            query =
                    "[out:json];" +
                            "(" +
                            "node[\"leisure\"=\"park\"](around:3000," +
                            latitude + "," +
                            longitude + ");" +

                            "way[\"leisure\"=\"park\"](around:3000," +
                            latitude + "," +
                            longitude + ");" +

                            "relation[\"leisure\"=\"park\"](around:3000," +
                            latitude + "," +
                            longitude + ");" +
                            ");" +
                            "out center;";

        } else {

            query =
                    "[out:json];" +
                            "(" +
                            "node[\"amenity\"=\"" + categoria + "\"](around:3000," +
                            latitude + "," +
                            longitude + ");" +

                            "way[\"amenity\"=\"" + categoria + "\"](around:3000," +
                            latitude + "," +
                            longitude + ");" +

                            "relation[\"amenity\"=\"" + categoria + "\"](around:3000," +
                            latitude + "," +
                            longitude + ");" +
                            ");" +
                            "out center;";
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new okhttp3.FormBody.Builder()
                .add("data", query)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .header(
                        "User-Agent",
                        "DAM-AF Android App")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(
                            Call call,
                            IOException e) {

                        callback.onError(
                                e.getMessage());
                    }

                    @Override
                    public void onResponse(
                            Call call,
                            Response response)
                            throws IOException {

                        try {

                            List<Place> lista = new ArrayList<>();

                            String json = response.body().string();
                            System.out.println("HTTP: " + response.code());
                            System.out.println(json);
                            Log.d("OVERPASS_RESPONSE", json);

                            JSONObject obj = new JSONObject(json);

                            JSONArray elements =
                                    obj.getJSONArray("elements");

                            for(int i=0;
                                i<elements.length();
                                i++) {

                                JSONObject item = elements.getJSONObject(i);

                                double latLocal = 0;
                                double lonLocal = 0;

                                String tipoElemento = item.optString("type");
                                Log.d(
                                        "OVERPASS_TYPE",
                                        "Tipo: " + tipoElemento);
                                if(tipoElemento.equals("node")){

                                    latLocal =
                                            item.optDouble("lat", 0);

                                    lonLocal =
                                            item.optDouble("lon", 0);
                                }
                                else if(
                                        tipoElemento.equals("way") ||
                                                tipoElemento.equals("relation")){

                                    JSONObject center =
                                            item.optJSONObject("center");

                                    if(center != null){

                                        latLocal =
                                                center.optDouble("lat", 0);

                                        lonLocal =
                                                center.optDouble("lon", 0);
                                    }
                                }

                                if(latLocal == 0 && lonLocal == 0){
                                    continue;
                                }

                                JSONObject tags = item.optJSONObject("tags");

                                if(tags != null){
                                    String tipo = "Desconhecido";

                                    if(tags.has("amenity")){
                                        tipo = tags.getString("amenity");
                                    }
                                    else if(tags.has("leisure")){
                                        tipo = tags.getString("leisure");
                                    }

                                    float[] resultado = new float[1];

                                    Location.distanceBetween(
                                            latitude,
                                            longitude,
                                            latLocal,
                                            lonLocal,
                                            resultado);

                                    float distanciaMetros = resultado[0];

                                    String distancia;

                                    if(distanciaMetros >= 1000){

                                        distancia = String.format(
                                                        "%.1f km",
                                                        distanciaMetros / 1000);

                                    } else {

                                        distancia = String.format(
                                                        "%.0f m",
                                                        distanciaMetros);
                                    }

                                    String nome =
                                            tags.optString(
                                                    "name",
                                                    "Sem nome");

                                    String endereco =
                                            tags.optString(
                                                    "addr:street",
                                                    "Endereço não informado");

                                    lista.add(
                                            new Place(
                                                    nome,
                                                    endereco,
                                                    tipo,
                                                    distancia,
                                                    latLocal,
                                                    lonLocal));
                                }
                            }


                            Log.d("OVERPASS", "Locais encontrados: " + lista.size());
                            callback.onSuccess(lista);

                        } catch (Exception e) {

                            callback.onError(
                                    e.getMessage());
                        }
                    }
                });
    }
}
