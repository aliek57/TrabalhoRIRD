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

public abstract class WebScrapper {

    public List<Receita> extrairReceitas(String url) throws IOException {
        List<Receita> receitas = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        // This method will be implemented by subclasses to get specific recipe links
        List<String> receitaUrls = extrairLinksReceitas(doc);

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
            Document doc = Jsoup.connect(url).get();
            Receita receita = new Receita();
            receita.setUrl(url);

            // These methods will be implemented by subclasses for site-specific extraction
            receita.setTitulo(extrairTitulo(doc));
            receita.setIngredientes(extrairIngredientes(doc));
            receita.setModoPreparo(extrairModoPreparo(doc));

            return receita;
        } catch (IOException e) {
            System.err.println("Erro ao extrair detalhes da receita em: " + url + " - " + e.getMessage());
            return null;
        }
    }

    protected abstract String extrairTitulo(Document doc);
    protected abstract String extrairIngredientes(Document doc);
    protected abstract String extrairModoPreparo(Document doc);

    // Helper methods that might be common across many sites, but can be overridden
    protected String extrairJsonLD(Document doc) {
        try {
            Elements scriptElements = doc.select("script[type=application/ld+json]");
            for (Element scriptElement : scriptElements) {
                String json = scriptElement.data();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                if (jsonObject.containsKey("@type") && jsonObject.get("@type").equals("Recipe")) {
                    return json;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair JSON-LD: " + e.getMessage());
        }
        return null;
    }

    protected String extrairIngredientesDoJsonLD(String jsonLD) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonLD);
            JSONArray ingredientsArray = (JSONArray) jsonObject.get("recipeIngredient");
            if (ingredientsArray != null) {
                return String.join(", ", ingredientsArray);
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair ingredientes do JSON-LD: " + e.getMessage());
        }
        return "Ingredientes não encontrados";
    }

    protected String extrairModoPreparoDoJsonLD(String jsonLD) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonLD);
            JSONArray instructionsArray = (JSONArray) jsonObject.get("recipeInstructions");
            if (instructionsArray != null) {
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
        return "Modo de preparo não encontrado";
    }
}

/*package com.example.receitas;

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

public class WebScrapper {

    public List<Receita> extrairReceitas(String url) throws IOException {
        List<Receita> receitas = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        Elements links = doc.select(".item.hover-zoom a[href*=/receita/]");

        for (Element link : links) {
            String receitaUrl = link.attr("abs:href");
            if (receitaUrl.contains("/receita/")) {
                Receita receita = extrairDetalhesReceita(receitaUrl);
                if (receita != null) {
                    receitas.add(receita);
                }
            }
        }

        return receitas;
    }

    private Receita extrairDetalhesReceita(String url) throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();
            Receita receita = new Receita();
            receita.setUrl(url);

            Element tituloElement = doc.selectFirst(".title h1");
            receita.setTitulo(tituloElement != null ? tituloElement.text() : "Título não encontrado");

            String jsonLD = extrairJsonLD(doc);
            if (jsonLD != null) {
                receita.setIngredientes(extrairIngredientesDoJsonLD(jsonLD));
                receita.setModoPreparo(extrairModoPreparoDoJsonLD(jsonLD));
            } else {
                receita.setIngredientes("Ingredientes não encontrados");
                receita.setModoPreparo("Modo de preparo não encontrado");
            }

            return receita;
        } catch (IOException e) {
            System.err.println("Erro ao extrair detalhes da receita em: " + url + " - " + e.getMessage());
            return null;
        }
    }

    private String extrairJsonLD(Document doc) {
        try {
            Elements scriptElements = doc.select("script[type=application/ld+json]");
            for (Element scriptElement : scriptElements) {
                String json = scriptElement.data();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                if (jsonObject.containsKey("@type") && jsonObject.get("@type").equals("Recipe")) {
                    return json;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair JSON-LD: " + e.getMessage());
        }
        return null;
    }

    private String extrairIngredientesDoJsonLD(String jsonLD) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonLD);
            JSONArray ingredientsArray = (JSONArray) jsonObject.get("recipeIngredient");
            if (ingredientsArray != null) {
                return String.join(", ", ingredientsArray);
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair ingredientes do JSON-LD: " + e.getMessage());
        }
        return "Ingredientes não encontrados";
    }

    private String extrairModoPreparoDoJsonLD(String jsonLD) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonLD);
            JSONArray instructionsArray = (JSONArray) jsonObject.get("recipeInstructions");
            if (instructionsArray != null) {
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
        return "Modo de preparo não encontrado";
    }
}
*/