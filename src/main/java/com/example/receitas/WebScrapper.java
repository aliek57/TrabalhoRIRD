package com.example.receitas;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class WebScrapper { // Make sure this is abstract

    public List<Receita> extrairReceitas(String url) throws IOException {
        List<Receita> receitas = new ArrayList<>();
        // Add a User-Agent header and a timeout to the connection
        Document doc = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .timeout(10 * 1000) // 10 seconds timeout
                            .get();
        System.out.println("DEBUG: Document fetched from: " + url); // Debug print

        List<String> receitaUrls = extrairLinksReceitas(doc);
        System.out.println("DEBUG: Found " + receitaUrls.size() + " recipe links."); // Debug print

        for (String receitaUrl : receitaUrls) {
            Receita receita = extrairDetalhesReceita(receitaUrl);
            if (receita != null) {
                receitas.add(receita);
            }
        }
        return receitas;
    }

    protected abstract List<String> extrairLinksReceitas(Document doc);

    protected Receita extrairDetalhesReceita(String url) throws IOException {
        try {
            // Add a User-Agent header and a timeout to the connection for individual recipe pages
            Document doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .timeout(10 * 1000) // 10 seconds timeout
                                .get();
            System.out.println("DEBUG: Details document fetched from: " + url); // Debug print

            Receita receita = new Receita();
            receita.setUrl(url);

            String titulo = extrairTitulo(doc);
            System.out.println("DEBUG: Título extraído: " + titulo); // Debug print
            receita.setTitulo(titulo);

            String ingredientes = extrairIngredientes(doc);
            System.out.println("DEBUG: Ingredientes extraídos: " + ingredientes.length() + " caracteres."); // Debug print
            receita.setIngredientes(ingredientes);

            String modoPreparo = extrairModoPreparo(doc);
            System.out.println("DEBUG: Modo de Preparo extraído: " + modoPreparo.length() + " caracteres."); // Debug print
            receita.setModoPreparo(modoPreparo);

            return receita;
        } catch (IOException e) {
            System.err.println("Erro ao extrair detalhes da receita em: " + url + " - " + e.getMessage());
            return null;
        }
    }

    protected abstract String extrairTitulo(Document doc);
    protected abstract String extrairIngredientes(Document doc);
    protected abstract String extrairModoPreparo(Document doc);

    // Changed visibility from private to protected to allow subclass access/override
    protected String extrairJsonLD(Document doc) {
        try {
            Elements scriptElements = doc.select("script[type=application/ld+json]");
            for (Element scriptElement : scriptElements) {
                String json = scriptElement.data();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                if (jsonObject.containsKey("@type") && jsonObject.get("@type").equals("Recipe")) {
                    System.out.println("DEBUG: JSON-LD encontrado."); // Debug print
                    return json;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair JSON-LD: " + e.getMessage());
        }
        System.out.println("DEBUG: JSON-LD não encontrado."); // Debug print
        return null;
    }

    // Changed visibility from private to protected
    protected String extrairIngredientesDoJsonLD(String jsonLD) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonLD);
            JSONArray ingredientsArray = (JSONArray) jsonObject.get("recipeIngredient");
            if (ingredientsArray != null) {
                System.out.println("DEBUG: Ingredientes do JSON-LD extraídos."); // Debug print
                return String.join(", ", ingredientsArray);
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair ingredientes do JSON-LD: " + e.getMessage());
        }
        System.out.println("DEBUG: Ingredientes do JSON-LD não encontrados."); // Debug print
        return "Ingredientes não encontrados";
    }

    // Changed visibility from private to protected
    protected String extrairModoPreparoDoJsonLD(String jsonLD) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonLD);
            JSONArray instructionsArray = (JSONArray) jsonObject.get("recipeInstructions");
            if (instructionsArray != null) {
                System.out.println("DEBUG: Modo de Preparo do JSON-LD extraído."); // Debug print
                StringBuilder modoPreparo = new StringBuilder();
                for (Object instruction : instructionsArray) {
                    if (instruction instanceof String) {
                        modoPreparo.append(instruction).append("\n");
                    } else if (instruction instanceof JSONObject) {
                        JSONObject step = (JSONObject) instruction;
                        modoPreparo.append(step.get("text")).append("\n");
                    }
                }
                return modoPreparo.toString();
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair modo de preparo do JSON-LD: " + e.getMessage());
        }
        System.out.println("DEBUG: Modo de Preparo do JSON-LD não encontrado."); // Debug print
        return "Modo de preparo não encontrado";
    }
}