package ru.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.app.models.DocumentUserRights;
import ru.app.models.User;

import java.util.List;

@Repository
public interface DocumentUserRightsRepository extends JpaRepository<DocumentUserRights, Long> {
    List<DocumentUserRights> findDocumentUserRightsByUser(User user);
}
