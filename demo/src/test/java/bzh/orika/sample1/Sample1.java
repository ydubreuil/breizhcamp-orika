package bzh.orika.sample1;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import ma.glasnost.orika.metadata.Type;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Sample1 {

    public static MapperFactory getMapperFactory() {
        // Initialisation d'une factory de mapper
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        return mapperFactory;
    }


    @Test
    public void simpleMapping() {
        // création d'un mapper par la factory configurée précédemment
        MapperFacade mapper = getMapperFactory().getMapperFacade();

        Person person = new Person();
        person.setFirstName("Yoann");
        person.setLastName("Dubreuil");
//        person.setHeightInCm(195);
//        person.setYearOfBirth(1979);
//        person.setSalutation(Salutation.MR);

        // map les champs de 'source' dans une nouvelle instance de type RestPerson
        RestPerson restPerson = mapper.map(person, RestPerson.class);

        Assert.assertTrue(person.getFirstName().equals(restPerson.getFirstName()));
        Assert.assertTrue(person.getLastName().equals(restPerson.getLastName()));

		//  -Dma.glasnost.orika.compilerStrategy=ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy

        // map dans l'autre sens
//        Person person2 = mapper.map(restPerson, Person.class);
//
//        Assert.assertTrue(person2.getFirstName().equals(person.getFirstName()));
//        Assert.assertTrue(person2.getLastName().equals(person.getLastName()));
//        Assert.assertTrue(person2.getHeight() == person.getHeight());

       // conversion de type
//        Assert.assertTrue(restPerson.getHeight().equals(Integer.toString(person.getHeightInCm())));

		// enumeration
//		Assert.assertTrue("MR".equals(restPerson.getSalutation()));

       // custom mapping
//        Assert.assertTrue(restPerson.getAge() == 35);

		// Bean properties -> map
//		Map<String,String> restPersonMap;
//		restPersonMap = mapper.map(restPerson, Map.class);
//
//		Assert.assertTrue(restPersonMap.containsKey("firstName"));
//		Assert.assertTrue("Yoann".equals(restPersonMap.get("firstName")));

       // custom type mapping
//       Assert.assertTrue("Mister".equals(restPerson.getSalutation()));

//        List<Person> personList = Collections.singletonList(person);
//        List<RestPerson> restPersons = mapper.mapAsList(personList, RestPerson.class);
//        Assert.assertTrue(restPersons.get(0).getFirstName().equals(restPerson.getFirstName()));

	}

    public static MapperFactory getMapperFactory2() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().useAutoMapping(false).build();

        // enregistrement du mapping
        mapperFactory.classMap(Person.class, RestPerson.class)
                .byDefault()
                .register();

        return mapperFactory;
    }

    public static MapperFactory getMapperFactory3() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // enregistrement du mapping
        mapperFactory.classMap(Person.class, RestPerson.class)
                .field("firstName", "firstName")
                .field("lastName", "lastName")
                .field("heightInCm", "height")
                .register();

        return mapperFactory;
    }

    public static MapperFactory getMapperFactory4() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // enregistrement du mapping
        mapperFactory.classMap(Person.class, RestPerson.class)
            .field("heightInCm", "height")
            .byDefault()
            .customize(
                new CustomMapper<Person, RestPerson>() {
                    @Override
                    public void mapAtoB(Person person, RestPerson restPerson,
                                        MappingContext context) {
                        restPerson.setAge(2014 - person.getYearOfBirth());
                    }

                    @Override
                    public void mapBtoA(RestPerson restPerson, Person person,
                                        MappingContext context) {
                        person.setYearOfBirth(restPerson.getAge() + 2014);
                    }
                }
            )
            .register();

        return mapperFactory;
    }

    public static class SalutationConverter extends BidirectionalConverter<Salutation,String> {

        @Override
        public String convertTo(Salutation salutation, Type<String> stringType) {
            switch (salutation) {
                case MISS:
                    return "Miss";
                case MR:
                    return "Mister";
                case MRS:
                    return "Mrs";
                case MS:
                    return "Ms";
            }
            return null;
        }

        @Override
        public Salutation convertFrom(String s, Type<Salutation> salutationType) {
            return null;
        }
    }

    public static MapperFactory getMapperFactory5() {
        MapperFactory mapperFactory = getMapperFactory4();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new SalutationConverter());

        return mapperFactory;
    }
}
