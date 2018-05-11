package app.repository;

import app.model.TextureFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("textureRepository")
public interface TextureRepository extends JpaRepository<TextureFile, Integer>{
    TextureFile findByTname(String name);
    TextureFile findFirstByTname(String name);
}