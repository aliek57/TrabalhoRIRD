package com.example.receitas;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonWriter {

    private static final String ontologiaURI = "http://minha-ontologia-receitas.com.br/";

    @SuppressWarnings("unchecked")
    public void escreverJson(List<Receita> receitas, String nomeArquivo) throws IOException {
        JSONArray listaJson = new JSONArray();

        Map<String, String> context = new HashMap<>();
        context.put("prefix", ontologiaURI);
        context.put("titulo", "prefix:titulo");
        context.put("ingredientes", "prefix:ingredientes");
        context.put("modoPreparo", "prefix:modoPreparo");
        context.put("url", "prefix:url");
        context.put("id", "prefix:id");

        for (Receita receita : receitas) {
            if (receita.getId() == null || receita.getId().isEmpty()) {
                receita.setId(UUID.randomUUID().toString());
            }

            JSONObject recursoSemantico = new JSONObject();

            recursoSemantico.put("@context", context);
            recursoSemantico.put("@type", "prefix:Receita");

            recursoSemantico.put("id", receita.getId());
            recursoSemantico.put("titulo", receita.getTitulo());
            recursoSemantico.put("ingredientes", receita.getIngredientes());
            recursoSemantico.put("modoPreparo", receita.getModoPreparo());
            recursoSemantico.put("url", receita.getUrl());

            listaJson.add(recursoSemantico);
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