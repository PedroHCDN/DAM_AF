package com.example.dam_af;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    private TextView txtLocation;
    private Spinner spinnerCategoria;
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private RecyclerView recyclerViewSalvos;
    private BancoHelper bancoHelper;


    private FusedLocationProviderClient fusedLocationClient;

    private double latitude;
    private double longitude;

    private LocationHelper locationHelper;
    private OverpassService overpassService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtLocation = findViewById(R.id.txtLocation);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationHelper = new LocationHelper(this);
        overpassService = new OverpassService();
        recyclerViewSalvos = findViewById(R.id.recyclerViewSalvos);
        recyclerViewSalvos.setLayoutManager(new LinearLayoutManager(this));
        bancoHelper = new BancoHelper(this);

        String[] categorias = {
                "hospital",
                "pharmacy",
                "restaurant",
                "school",
                "park"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categorias);

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerCategoria.setAdapter(spinnerAdapter);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationHelper.getCurrentLocation(location -> {

            latitude = location.getLatitude();
            longitude = location.getLongitude();

        });

//        List<Place> teste = new ArrayList<>();
//
//        teste.add(new Place("Teste 1", "Rua 1"));
//        teste.add(new Place("Teste 2", "Rua 2"));
//
//        recyclerView.setAdapter(new PlaceAdapter(teste));

        findViewById(R.id.btnBuscar).setOnClickListener(v -> {
            String categoria =
                    spinnerCategoria
                            .getSelectedItem()
                            .toString();

            if(latitude == 0.0 && longitude == 0.0){
                Toast.makeText(
                        this,
                        "Aguardando localização...",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            overpassService.buscarLocais(
                    latitude,
                    longitude,
                    categoria,

                    new PlaceAdapter.PlacesCallback() {

                        @Override
                        public void onSuccess(List<Place> places) {
                            System.out.println("CHEGOU NO ONSUCCESS: " + places.size());
                            Log.d("MAIN_ACTIVITY", "Recebi " + places.size() + " locais");

                            runOnUiThread(() -> {
                                Log.d("MAIN_ACTIVITY", "Setando adapter");

                                recyclerView.setAdapter(
                                        new PlaceAdapter(
                                                places,
                                                place -> {

                                                    mostrarDialogSalvar(place);
                                                }
                                        ));
                            });
                        }

                        @Override
                        public void onError(String error) {
                            System.out.println(error);
                        }
                    });
        });

        obterLocalizacao();
        carregarLocaisSalvos();
    }

    private void obterLocalizacao() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    100);

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        txtLocation.setText(
                                latitude + ", " + longitude);
                    }
                });
    }

    private void mostrarDialogSalvar(Place place) {

        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_salvar_local,
                        null);

        Spinner spinnerCategoria =
                view.findViewById(
                        R.id.spinnerCategoriaSalvar);

        EditText edtObservacao =
                view.findViewById(
                        R.id.edtObservacao);

        String[] categorias = {
                "Estudo",
                "Saúde",
                "Lazer",
                "Alimentação",
                "Compras",
                "Outros"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categorias);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerCategoria.setAdapter(adapter);

        new AlertDialog.Builder(this)

                .setTitle(place.getNome())

                .setView(view)

                .setPositiveButton(
                        "Salvar",
                        (dialog, which) -> {

                            String categoria =
                                    spinnerCategoria
                                            .getSelectedItem()
                                            .toString();

                            String observacao =
                                    edtObservacao
                                            .getText()
                                            .toString();

                            long resultado =
                                    bancoHelper.salvarLocal(
                                            place.getNome(),
                                            categoria,
                                            observacao,
                                            place.getLatitude(),
                                            place.getLongitude());

                            if(resultado != -1){

                                Toast.makeText(
                                        this,
                                        "Local salvo com sucesso!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                carregarLocaisSalvos();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Erro ao salvar",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })

                .setNegativeButton(
                        "Cancelar",
                        null)

                .show();
    }

    private void carregarLocaisSalvos() {

        List<LocalPlace> lista = bancoHelper.listarLocais();

        recyclerViewSalvos.setAdapter(
                new LocalPlaceAdapter(
                        lista,

                        new LocalPlaceAdapter.OnLocalPlaceListener() {

                            @Override
                            public void onClick(LocalPlace local) {

                                mostrarDialogEditar(local);
                            }

                            @Override
                            public void onLongClick(LocalPlace local) {

                                mostrarDialogExcluir(local);
                            }
                        }
                ));
    }

    private void mostrarDialogEditar(
            LocalPlace local) {

        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_salvar_local,
                        null);

        Spinner spinner = view.findViewById(R.id.spinnerCategoriaSalvar);

        EditText edtObs = view.findViewById(R.id.edtObservacao);

        String[] categorias = {
                "Estudo",
                "Saúde",
                "Lazer",
                "Alimentação",
                "Compras",
                "Outros"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categorias);

        spinner.setAdapter(adapter);

        edtObs.setText(
                local.getObservacao());

        for(int i=0;i<categorias.length;i++){

            if(categorias[i].equals(
                    local.getCategoria())){

                spinner.setSelection(i);
                break;
            }
        }

        new AlertDialog.Builder(this)

                .setTitle(
                        "Editar")

                .setView(view)

                .setPositiveButton(
                        "Salvar",

                        (dialog, which) -> {

                            bancoHelper.atualizarLocal(
                                    local.getId(),

                                    spinner
                                            .getSelectedItem()
                                            .toString(),

                                    edtObs
                                            .getText()
                                            .toString());

                            carregarLocaisSalvos();
                        })

                .setNegativeButton(
                        "Cancelar",
                        null)

                .show();
    }

    private void mostrarDialogExcluir(LocalPlace local) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Excluir")

                .setMessage(
                        "Deseja excluir "
                                + local.getNome()
                                + "?")

                .setPositiveButton(
                        "Sim",

                        (dialog, which) -> {

                            bancoHelper.excluirLocal(
                                    local.getId());

                            carregarLocaisSalvos();
                        })

                .setNegativeButton(
                        "Não",
                        null)

                .show();
    }
}