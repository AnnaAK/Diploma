package app.model;

import javax.persistence.*;

@Entity
@Table(name = "texture_files")
public class TextureFile {

    public TextureFile(){}
    public TextureFile(String t_name, String t_url) {
        this.tname=t_name;
        this.turl=t_url;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="texture_id")
    private int id;
    @Column(name="texture_name")
    private String tname;
    @Column(name="texture_url")
    private String turl;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return tname;
    }
    public void setName(String name) {
        this.tname = name;
    }
    public String getUrl() {
        return turl;
    }
    public void setUrl(String url) {
        this.turl = url;
    }

}
