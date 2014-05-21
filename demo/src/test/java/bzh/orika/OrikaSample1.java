package bzh.orika;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;

public class OrikaSample1 {

    @Test
    public void simpleMapping() {
        // Initialisation d'une factory de mapper
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // enregistrement d'un mapping
        mapperFactory.classMap(PersonSource.class, PersonDest.class)
                .field("firstName", "givenName")
                .field("lastName", "sirName")
                .byDefault()
                .register();

        // création d'un mapper par la factory configuré précédemment
        MapperFacade mapper = mapperFactory.getMapperFacade();

        PersonSource personSource = new PersonSource();
        personSource.setFirstName("Yoann");
        personSource.setLastName("Dubreuil");

        // map les champs de 'source' dans une nouvelle instance de type PersonDest
        PersonDest destination = mapper.map(personSource, PersonDest.class);
        Assert.assertTrue(personSource.getFirstName().equals(destination.givenName));
    }
}
