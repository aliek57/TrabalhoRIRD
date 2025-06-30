package com.example.receitas.persistencia;

import com.example.receitas.Receita;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReceitaDAO {

    @Value("${receita.data.file.receiteria:./receitas_receiteria.json}")
    private String receiteriaFileName;

    @Value("${receita.data.file.cozinhalegal:./receitas_cozinhalegal.json}")
    private String cozinhaLegalFileName;

    @Value("${receita.data.file.receitadelicia:./receitas_receitadelicia.json}")
    private String receitaDeliciaFileName;

    private List<Receita> todasReceitas = new ArrayList<>();

    public List<Receita> listarTodas() {
        return this.todasReceitas;
    }

    public Receita encontrarPorId(String id) {
        return todasReceitas.stream()
                .filter(receita -> id.equals(receita.getId()))
                .findFirst()
                .orElse(null);
    }

    @PostConstruct
    public void carregarReceitas() {
        Gson gson = new Gson();
        List<Receita> receitasCarregadas = new ArrayList<>();

        try {
            try (FileReader reader = new FileReader(receiteriaFileName)) {
                List<Receita> tempReceitas = gson.fromJson(reader, new TypeToken<ArrayList<Receita>>() {}.getType());
                if (tempReceitas != null) {
                    receitasCarregadas.addAll(tempReceitas);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar receitas de " + receiteriaFileName + ": " + e.getMessage());
            }

            try (FileReader reader = new FileReader(cozinhaLegalFileName)) {
                List<Receita> tempReceitas = gson.fromJson(reader, new TypeToken<ArrayList<Receita>>() {}.getType());
                if (tempReceitas != null) {
                    receitasCarregadas.addAll(tempReceitas);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar receitas de " + cozinhaLegalFileName + ": " + e.getMessage());
            }

            try (FileReader reader = new FileReader(receitaDeliciaFileName)) {
                List<Receita> tempReceitas = gson.fromJson(reader, new TypeToken<ArrayList<Receita>>() {}.getType());
                if (tempReceitas != null) {
                    receitasCarregadas.addAll(tempReceitas);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar receitas de " + receitaDeliciaFileName + ": " + e.getMessage());
            }

            for (Receita receita : receitasCarregadas) {
                if (receita.getId() == null || receita.getId().isEmpty()) {
                    receita.setId(UUID.randomUUID().toString());
                }
            }
            this.todasReceitas = receitasCarregadas;
            System.out.println("DEBUG: " + this.todasReceitas.size() + " receitas carregadas com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro geral ao carregar receitas: " + e.getMessage());
        }
    }
}