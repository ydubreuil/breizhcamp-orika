package bzh.orika;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Assert;
import org.junit.Test;

public class Sample1 {
	public static class PersonSource {
		String firstName;
		String lastName;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}

	public static class PersonDest {
		public String givenName;
		public String sirName;
	}

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

		// Utilisation du mapper
		PersonSource source = new PersonSource();
		source.setFirstName("Yoann");
		source.setLastName("Dubreuil");

		// map les champs de 'source' dans une nouvelle instance de type PersonDest
		PersonDest destination = mapper.map(source, PersonDest.class);
		Assert.assertTrue(source.getFirstName().equals(destination.givenName));
	}
}
