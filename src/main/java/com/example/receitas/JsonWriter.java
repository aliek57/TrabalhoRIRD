package com.example.receitas;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonWriter {

    public void escreverJson(List<Receita> receitas, String nomeArquivo) throws IOException {
        JSONArray listaJson = new JSONArray();

        for (Receita receita : receitas) {
            JSONObject receitaJson = new JSONObject();
            receitaJson.put("titulo", receita.getTitulo());
            receitaJson.put("ingredientes", receita.getIngredientes());
            receitaJson.put("modoPreparo", receita.getModoPreparo());
            receitaJson.put("url", receita.getUrl());

            listaJson.add(receitaJson);
        }

        try (FileWriter file = new FileWriter(nomeArquivo)) {
            file.write(listaJson.toJSONString());
            System.out.println("Arquivo JSON criado: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo JSON: " + e.getMessage());
            throw e;
        }
    }
}