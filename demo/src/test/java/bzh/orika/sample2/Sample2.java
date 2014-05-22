/*
 * Copyright (C) 2011-2013 Orika authors
 * Copyright (C) 2014 Yoann Dubreuil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bzh.orika.sample2;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sample2 {

    public static MapperFactory getMapperFactory() {
        // Initialisation d'une factory de mapper
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(Person.class, RestPerson.class)
                .field("name.first", "firstName")
                .field("name.last", "lastName")
                .field("knownAliases{first}", "aliases{[0]}")
                .field("knownAliases{last}", "aliases{[1]}")
                .byDefault()
                .register();

        return mapperFactory;
    }

    @Test
    public void nestedElements() {
        MapperFacade mapper = getMapperFactory().getMapperFacade();

        List<Name> aliases = new ArrayList<Name>();
        aliases.add(new Name("Joe", "Williams"));
        aliases.add(new Name("Terry", "Connor"));
        Person source = new Person(new Name("John","Doe"), new Date(), aliases);

        RestPerson dest = mapper.map(source, RestPerson.class);

        Assert.assertNotNull(dest);
        Assert.assertEquals(source.getName().getFirst(), dest.getFirstName());
        Assert.assertEquals(source.getName().getLast(), dest.getLastName());
        Assert.assertEquals(source.getKnownAliases().get(0).getFirst(), dest.getAliases()[0][0]);
        Assert.assertEquals(source.getKnownAliases().get(0).getLast(), dest.getAliases()[0][1]);
        Assert.assertEquals(source.getKnownAliases().get(1).getFirst(), dest.getAliases()[1][0]);
        Assert.assertEquals(source.getKnownAliases().get(1).getLast(), dest.getAliases()[1][1]);
    }
}
