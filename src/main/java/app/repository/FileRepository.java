package app.repository;

import app.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("fileRepository")
public interface FileRepository extends JpaRepository<Files, Integer>{
    Files findByName(String name);
    Files findByLink(String link);
    Files findFirstByName(String name);
}
