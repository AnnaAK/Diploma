package app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "upload_files")
public class Files {
    public Files(){}
    public Files(String key, String link, String name) {
        this.key = key;
        this.link =link;
        this.name =name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="file_id")
    private int id;
    @Column(name="file_name")
    private String name;
    @Column(name="url_link")
    private String link;
    @Column(name="file_key")
    private String key;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
