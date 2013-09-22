package fr.botleecher.rev.entities;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import fr.botleecher.rev.enums.SettingProperty;
import org.bson.types.ObjectId;

import java.util.ArrayList;
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

    private SettingProperty key;

    private List<String> value;

    public Setting() {
        // Nothing
    }

    public Setting(SettingProperty key) {
        this.key = key;
    }

    public Setting(SettingProperty key, List<String> value) {
        this(key);
        setValue(value);
    }

    public ObjectId getId() {
        return id;
    }

    public SettingProperty getKey() {
        return key;
    }

    public List<String> getValue() {
        return value;
    }

    public String getFirstValue() {
        return (value == null || value.isEmpty()) ? null : value.get(0);
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setKey(SettingProperty key) {
        this.key = key;
    }

    public void setValue(List<String> value) {
        this.value = new ArrayList<>(value);
    }
}
