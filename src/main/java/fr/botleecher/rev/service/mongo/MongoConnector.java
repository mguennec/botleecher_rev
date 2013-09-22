package fr.botleecher.rev.service.mongo;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.converters.EnumConverter;
import com.github.jmkgreen.morphia.query.Query;
import com.google.inject.Inject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 16/09/13
 * Time: 12:32
 * To change this template use File | Settings | File Templates.
 */
public class MongoConnector {

    private static final String DB_NAME = "botleecher";
    private final Datastore datastore;
    private final Morphia morphia;

    @Inject
    public MongoConnector(final Connection connection) throws Exception {
        morphia = new Morphia();
        morphia.mapPackage("fr.botleecher.rev.entities");
        morphia.getMapper().getConverters().addConverter(EnumConverter.class);
        datastore = morphia.createDatastore(connection.getConnection(), DB_NAME);
    }

    public <T> void update(final T object) throws Exception {
        if (morphia.getMapper().getMappedClass(object) != null) {
            final Key<T> key = datastore.save(object);
            morphia.getMapper().getMappedClass(object).getIdField().set(object, key.getId());
        }
    }

    public <T> T getOne(final T condition) throws Exception {
        /*final Query<T> query = datastore.find(tClass);
        if (morphia.getMapper().getMappedClass(condition) != null) {
            for (MappedField mappedField : morphia.getMapper().getMappedClass(condition).getMappedFields()) {
                final Object value = mappedField.getFieldValue(condition);
                if (value != null) {
                    query.filter(mappedField.getNameToStore(), value);
                }
            }
        }*/
        final Query<T> query = datastore.queryByExample(condition);
        return query.get();
    }


    public <T> List<T> get(final T condition) throws Exception {
        return get(0, condition);
    }

    public <T> List<T> get(final int limit, final T condition) throws Exception {
        return get(limit, 0, condition);
    }

    public <T> List<T> get(final int limit, final int offset, final T condition) throws Exception {
        return datastore.queryByExample(condition).limit(limit).offset(offset).asList();
    }

}
