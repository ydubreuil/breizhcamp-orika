package bzh.orika.sample2;

import ma.glasnost.orika.BoundMapperFacade;
import org.dozer.DozerBeanMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Sample2_Benchmark {

    @State(Scope.Benchmark)
    public static class OrikaState {

        final BoundMapperFacade<Person,RestPerson> mapper;
        final Person person;

        public OrikaState() {
            mapper = Sample2.getMapperFactory().getMapperFacade(Person.class, RestPerson.class, false);

            List<Name> aliases = new ArrayList<Name>();
            aliases.add(new Name("Joe", "Williams"));
            aliases.add(new Name("Terry", "Connor"));
            person = new Person(new Name("John","Doe"), new Date(), aliases);
        }
    }

    @State(Scope.Thread)
    public static class DozerState {

        final DozerBeanMapper mapper;

        final Person person;

        public DozerState() {
            List mappingFiles = new Vector<String>();
            mappingFiles.add("dozer-mapping-sample2.xml");
            mapper = new DozerBeanMapper(mappingFiles);

            List<Name> aliases = new ArrayList<Name>();
            aliases.add(new Name("Joe", "Williams"));
            aliases.add(new Name("Terry", "Connor"));
            person = new Person(new Name("John","Doe"), new Date(), aliases);
        }
    }

    @GenerateMicroBenchmark
    public RestPerson mapWithOrika(OrikaState state) {
        RestPerson dest = state.mapper.map(state.person);
        return dest;
    }

    @GenerateMicroBenchmark
    public RestPerson mapByHand(OrikaState state) {
        RestPerson dest = new RestPerson();

        if (state.person.getName() != null) {
            dest.setFirstName(state.person.getName().getFirst());
            dest.setLastName(state.person.getName().getLast());
        }

        if (state.person.getKnownAliases() != null) {
            String[][] aliases = new String[2][];

            if (state.person.getKnownAliases().get(0) != null) {
                aliases[0] = new String[2];
                aliases[0][0] = state.person.getKnownAliases().get(0).getFirst();
                aliases[0][1] = state.person.getKnownAliases().get(0).getLast();
            }
            if (state.person.getKnownAliases().get(1) != null) {
                aliases[1] = new String[2];
                aliases[1][0] = state.person.getKnownAliases().get(1).getFirst();
                aliases[1][1] = state.person.getKnownAliases().get(1).getLast();
            }
            dest.setAliases(aliases);
        }

        return dest;
    }

    @GenerateMicroBenchmark
    public RestPerson mapWithDozer(DozerState state) {
        RestPerson dest = state.mapper.map(state.person, RestPerson.class);
        return dest;
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + Sample2_Benchmark.class.getSimpleName() + ".*")
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

/*

Benchmark                                Mode   Samples         Mean   Mean error    Units
b.o.s.Sample2_Benchmark.mapByHand       thrpt         5       29,401        3,637   ops/us
b.o.s.Sample2_Benchmark.mapWithDozer    thrpt         5        0,086        0,011   ops/us
b.o.s.Sample2_Benchmark.mapWithOrika    thrpt         5        3,119        0,445   ops/us

*/
}
