package ru.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.app.models.Paragraph;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
}
