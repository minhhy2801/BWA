package capstone.bwa.demo.controllers;


import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ReferencesLinkEntity;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.ReferencesLinkRepository;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("ref_links")
public class ReferencesLinkController {

    @Autowired
    ReferencesLinkRepository referencesLinkRepository;

    @Autowired
    CategoryRepository categoryRepository;
}
