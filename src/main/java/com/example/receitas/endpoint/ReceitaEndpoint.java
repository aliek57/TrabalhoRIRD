package com.example.receitas.endpoint;

import com.example.receitas.Receita;
import com.example.receitas.persistencia.ReceitaDAO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@CrossOrigin
public class ReceitaEndpoint {

    @Autowired
    private ReceitaDAO receitaDao;

    private static final String ontologiaURI = "http://minha-ontologia-receitas.com.br/";

    @GetMapping(value = "/receitas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Receita>> listarTodasReceitas() {
        List<Receita> receitas = receitaDao.listarTodas();
        return ResponseEntity.status(HttpStatus.OK).body(receitas);
    }

    @GetMapping(value = "/receitas-semanticas", produces = "application/ld+json")
    public ResponseEntity<?> listarTodasReceitasSemanticas(HttpServletRequest request) {
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Receita> receitas = receitaDao.listarTodas();

        Map<String, String> context = new HashMap<>();
        context.put("prefix", ontologiaURI);
        context.put("titulo", "prefix:titulo");
        context.put("ingredientes", "prefix:ingredientes");
        context.put("modoPreparo", "prefix:modoPreparo");
        context.put("url", "prefix:url");

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        for (Receita receita : receitas) {
            Map<String, Object> recursoSemantico = new TreeMap<>();
            recursoSemantico.put("@id", baseUrl + "/receita?id=" + receita.getId());

            recursoSemantico.put("@context", context);
            recursoSemantico.put("@type", "prefix:Receita");
            recursoSemantico.put("titulo", receita.getTitulo());
            recursoSemantico.put("ingredientes", receita.getIngredientes());
            recursoSemantico.put("modoPreparo", receita.getModoPreparo());
            recursoSemantico.put("url", receita.getUrl());
            maps.add(recursoSemantico);
        }
        return ResponseEntity.status(HttpStatus.OK).body(maps);
    }

    @GetMapping(value = "/receita", produces = "application/ld+json")
    public ResponseEntity<?> listarReceitaPorId(HttpServletRequest request, @RequestParam("id") String id) {
        Receita receita = receitaDao.encontrarPorId(id);

        if (receita == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Map<String, String> context = new HashMap<>();
        context.put("prefix", ontologiaURI);
        context.put("titulo", "prefix:titulo");
        context.put("ingredientes", "prefix:ingredientes");
        context.put("modoPreparo", "prefix:modoPreparo");
        context.put("url", "prefix:url");

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        Map<String, Object> recursoSemantico = new TreeMap<>();
        recursoSemantico.put("@id", baseUrl + "/receita?id=" + receita.getId());
        recursoSemantico.put("@context", context);
        recursoSemantico.put("@type", "prefix:Receita");
        recursoSemantico.put("titulo", receita.getTitulo());
        recursoSemantico.put("ingredientes", receita.getIngredientes());
        recursoSemantico.put("modoPreparo", receita.getModoPreparo());
        recursoSemantico.put("url", receita.getUrl());

        return ResponseEntity.status(HttpStatus.OK).body(recursoSemantico);
    }
}