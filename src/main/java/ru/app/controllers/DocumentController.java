package ru.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.app.models.Document;
import ru.app.models.Paragraph;
import ru.app.services.DocumentService;
import ru.app.services.UserService;

import java.util.Comparator;
import java.util.Objects;

@Controller
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @GetMapping("/docs")
    public String getDocuments(Model model) {
        var documents = documentService.getUserDocuments(userService.getUser());
        model.addAttribute("documents", documents);
        return "documents";
    }

    @GetMapping("/doc/{id}")
    public String getDocument(@PathVariable int id, Model model) {
        var document = documentService.getDocument(Integer.toUnsignedLong(id));
        if (document == null) {
            model.addAttribute("reason", "Не найден документ с id " + id);
            return "error";
        }
        var user = userService.getUser();
        var editor = false;
        var owner = false;
        if (document.getOwner() != user) {
            for (var docRights : document.getDocumentUserRightsList()) {
                if (docRights.getUser() == user) {
                    if (Objects.equals(docRights.getRole().getName(), "ROLE_EDITOR"))
                        editor = true;
                }
            }
        } else {
            editor = true;
            owner = true;
        }
        model.addAttribute("editor", editor);
        model.addAttribute("document", document);
        model.addAttribute("owner", owner);
        model.addAttribute("lastEditedBy", document.getLastEditBy());
        if (!document.getParagraphList().isEmpty())
            document.setParagraphList(document.getParagraphList().stream()
                    .sorted(Comparator.comparingInt(Paragraph::getNumber)).toList());
        model.addAttribute("documentDivs", document.getParagraphList());
        return "editor";
    }

    @GetMapping("/doc/new")
    public String createDocument(Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("error", null);
        return "new_document";
    }

    @PostMapping("/doc/new")
    public String addDocument(@ModelAttribute("document") Document document, Model model) {
        if (document.getName() == null) {
            model.addAttribute("document", new Document());
            model.addAttribute("error", "Имя не указано!");
            return "new_document";
        }
        var doc = documentService.createDocument(userService.getUser(), document.getName());
        return "redirect:/doc/" + doc.getId();
    }
}
