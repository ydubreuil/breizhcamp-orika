package bzh.orika.sample1;

import ma.glasnost.orika.BoundMapperFacade;
import org.apache.commons.beanutils.BeanUtils;
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
public class Sample1_Benchmark {

    @State(Scope.Benchmark)
    public static class OrikaState {

        final BoundMapperFacade<Person, RestPerson> mapper;
        final Person person;

        public OrikaState() {
            mapper = Sample1.getMapperFactory3().getMapperFacade(Person.class, RestPerson.class, false);

            person = new Person();
            person.setFirstName("Yoann");
            person.setLastName("Dubreuil");
            person.setHeightInCm(195);
        }
    }

    @State(Scope.Thread)
    public static class DozerState {

        final DozerBeanMapper mapper;

        final Person person;

        public DozerState() {
            List mappingFiles = new Vector<String>();
            mappingFiles.add("dozer-mapping-sample1.xml");
            mapper = new DozerBeanMapper(mappingFiles);

            person = new Person();
            person.setFirstName("Yoann");
            person.setLastName("Dubreuil");
            person.setHeightInCm(195);
        }
    }

    @State(Scope.Thread)
    public static class BeanUtilsState {

        final Person person;

        public BeanUtilsState() {
            person = new Person();
            person.setFirstName("Yoann");
            person.setLastName("Dubreuil");
            person.setHeightInCm(195);
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
        dest.setFirstName(state.person.getFirstName());
        dest.setLastName(state.person.getLastName());
        dest.setHeight(Integer.toString(state.person.getHeightInCm()));
        return dest;
    }

    @GenerateMicroBenchmark
    public RestPerson mapWithDozer(DozerState state) {
        RestPerson dest = state.mapper.map(state.person, RestPerson.class);
        return dest;
    }

    @GenerateMicroBenchmark
    public RestPerson mapWithBeanUtils(DozerState state) throws Exception {
        RestPerson dest = new RestPerson();
        BeanUtils.copyProperties(state.person, dest);
        return dest;
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + Sample1_Benchmark.class.getSimpleName() + ".*")
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

    /*
Benchmark                                    Mode   Samples         Mean   Mean error    Units
b.o.s.Sample1_Benchmark.mapByHand           thrpt         5       31,297        0,679   ops/us
b.o.s.Sample1_Benchmark.mapWithBeanUtils    thrpt         5        0,028        0,001   ops/us
b.o.s.Sample1_Benchmark.mapWithDozer        thrpt         5        0,115        0,002   ops/us
b.o.s.Sample1_Benchmark.mapWithOrika        thrpt         5        4,233        0,190   ops/us
     */
}
