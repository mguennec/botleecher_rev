package fr.botleecher.rev.service.mongo.entities;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Property;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 16/09/13
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
@Entity("settings")
public class Setting {

    @Id
    private ObjectId id;

    private String key;

    @Property
    private List<String> value;

    public Setting() {
        // Nothing
    }

    public Setting(String key) {
        this.key = key;
    }

    public Setting(String key, List<String> value) {
        this.key = key;
        this.value = value;
    }

    public ObjectId getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public List<String> getValue() {
        return value;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
