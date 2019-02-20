package capstone.bwa.demo.controllers;


import capstone.bwa.demo.repositories.ReferencesLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("ref_links")
public class ReferencesLinkController {
    @Autowired
    ReferencesLinkRepository referencesLinkRepository;
}
