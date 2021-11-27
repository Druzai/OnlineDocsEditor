package ru.app.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "paragrahes")
@Getter
@Setter
public class Paragraph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    private String content;

    private String align;

    public Paragraph(Integer number, String content, String align){
        this.number = number;
        this.content = content;
        this.align = align;
    }

    public Paragraph() {
    }
}
