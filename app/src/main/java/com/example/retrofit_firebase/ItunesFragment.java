package com.example.retrofit_firebase;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.SearchView;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.*;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.retrofit_firebase.databinding.FragmentItunesBinding;
import com.example.retrofit_firebase.databinding.ViewholderContenidoBinding;

import java.util.List;

public class ItunesFragment extends Fragment {
    private FragmentItunesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentItunesBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItunesViewModel itunesViewModel = new ViewModelProvider(this).get(ItunesViewModel.class);

        binding.texto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { return false; }

            @Override
            public boolean onQueryTextChange(String s) {
                itunesViewModel.buscar(s);
                return false;
            }
        });


        ContenidosAdapter contenidosAdapter = new ContenidosAdapter();
        binding.recyclerviewContenidos.setAdapter(contenidosAdapter);

        itunesViewModel.respuestaMutableLiveData.observe(getViewLifecycleOwner(), new Observer<Itunes.Result>() {
            @Override
            public void onChanged(Itunes.Result respuesta) {
                contenidosAdapter.establecerListaContenido(respuesta);
                // respuesta.results.forEach(r -> Log.e("ABCD", r.artistName + ", " + r.trackName + ", " + r.artworkUrl100));
            }
        });

    }

    static class ContenidoViewHolder extends RecyclerView.ViewHolder {
        ViewholderContenidoBinding binding;

        public ContenidoViewHolder(@NonNull ViewholderContenidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class ContenidosAdapter extends RecyclerView.Adapter<ContenidoViewHolder>{
        Itunes.Result pokemonList;

        @NonNull
        @Override
        public ContenidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContenidoViewHolder(ViewholderContenidoBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ContenidoViewHolder holder, int position) {
            Itunes.Pokemon pokemon = pokemonList.documents.get(position);

            holder.binding.title.setText(pokemon.fields.nombre.stringValue);
            holder.binding.artist.setText(pokemon.fields.elemento.stringValue);
            Glide.with(requireActivity()).load(pokemon.fields.imagen.stringValue).into(holder.binding.artwork);
        }

        @Override
        public int getItemCount() {
            return pokemonList == null ? 0 : pokemonList.documents.size();
        }

        void establecerListaContenido(Itunes.Result pokemonList){
            this.pokemonList = pokemonList;
            notifyDataSetChanged();
        }
    }

}