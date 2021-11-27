package ru.app.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = null;

    private String lastEditBy = null;

    @OneToOne
    private User owner;

    @OneToMany
    private List<DocumentUserRights> documentUserRightsList = new ArrayList<>();

    @OneToMany
    private List<Paragraph> paragraphList = new ArrayList<>();

    public void addParagraphs(List<Paragraph> paragraphs) {
        paragraphList.addAll(paragraphs);
    }

    public List<Paragraph> editParagraphs(List<Paragraph> paragraphs) {
        var paragraphNumbs = paragraphs.stream().map(Paragraph::getNumber).collect(Collectors.toList());
        for (Paragraph paragraph : paragraphList) {
            if (paragraphNumbs.contains(paragraph.getNumber())) {
                var parId = paragraphNumbs.indexOf(paragraph.getNumber());
                paragraph.setContent(paragraphs.get(parId).getContent());
                paragraph.setAlign(paragraphs.get(parId).getAlign());
            }
        }
        return this.paragraphList;
//        paragraphList.
//        paragraphList = paragraphList.stream().peek(paragraph1 -> {
//            if (Objects.equals(paragraph1.getNumber(), paragraph.getNumber()))
//                paragraph1.setContent(paragraph.getContent());
//        }).collect(Collectors.toList());
    }

    public List<Paragraph> deleteParagraphs(List<Paragraph> paragraphs) {
        var paragraphNumbs = paragraphs.stream().map(Paragraph::getNumber).collect(Collectors.toList());
        var paragraphToDelete = paragraphList.stream()
                .filter(par -> paragraphNumbs.contains(par.getNumber())).collect(Collectors.toList());
        paragraphList.removeIf(par -> paragraphNumbs.contains(par.getNumber()));
        return paragraphToDelete;
    }

    public DocumentUserRights addDocumentToUser(User user, Role role) {
        var docToUsr = new DocumentUserRights(user, role);
        documentUserRightsList.add(docToUsr);
        return docToUsr;
    }

    public void deleteDocumentToUser(User user, Role role) {
        documentUserRightsList.removeIf(doc -> doc.getUser() == user && doc.getRole() == role);
    }
}
