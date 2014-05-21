package bzh.orika;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.dozer.DozerBeanMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class OrikaSample1Benchmark {

    @State(Scope.Benchmark)
    public static class OrikaState {

        final BoundMapperFacade<PersonSource,PersonDest> mapper;
        final PersonSource personSource;

        public OrikaState() {
            // Initialisation d'une factory de mapper
            MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

            // enregistrement d'un mapping
            mapperFactory.classMap(PersonSource.class, PersonDest.class)
                    .field("firstName", "givenName")
                    .field("lastName", "sirName")
                    .byDefault()
                    .register();

            // création d'un mapper par la factory configurée précédemment
            mapper = mapperFactory.getMapperFacade(PersonSource.class, PersonDest.class, false);

            personSource = new PersonSource();
            personSource.setFirstName("Yoann");
            personSource.setLastName("Dubreuil");
        }
    }

    @State(Scope.Thread)
    public static class DozerState {

        final DozerBeanMapper mapper;

        final PersonSource personSource;

        public DozerState() {
            List mappingFiles = new Vector<String>();
            mappingFiles.add("dozer-mapping.xml");
            mapper = new DozerBeanMapper(mappingFiles);

            personSource = new PersonSource();
            personSource.setFirstName("Yoann");
            personSource.setLastName("Dubreuil");
        }
    }

    @GenerateMicroBenchmark
    public PersonDest mapWithOrika(OrikaState state) {
        PersonDest dest = state.mapper.map(state.personSource);
        return dest;
    }

    @GenerateMicroBenchmark
    public PersonDest mapByHand(OrikaState state) {
        PersonDest dest = new PersonDest();
        dest.setGivenName(new String(state.personSource.getFirstName()));
        dest.setSirName(new String(state.personSource.getLastName()));
        return dest;
    }

    @GenerateMicroBenchmark
    public PersonDest mapWithDozer(DozerState state) {
        PersonDest dest = state.mapper.map(state.personSource, PersonDest.class);
        return dest;
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + OrikaSample1Benchmark.class.getSimpleName() + ".*")
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
}
