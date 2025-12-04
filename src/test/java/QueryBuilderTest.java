import discovery.test2.Airplane;
import discovery.test2.Crew;
import discovery.test2.Flight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raf.thesis.metadata.scan.MetadataScanner;
import raf.thesis.query.QueryBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static raf.thesis.query.ConditionBuilder.*;

public class QueryBuilderTest {
    @BeforeAll
    public static void fillMetadata(){
        MetadataScanner ms = new MetadataScanner();
        ms.discoverMetadata("discovery.test2");
    }

    @Test
    public void testSingleJoinGeneration(){
        String check = QueryBuilder.select(Crew.class).join("pilot").generateJoinClauses();
        assertEquals("INNER JOIN pilots AS \"pilot\" ON ((\"pilot\".crewid) = (\"%root\".crewid))\n", check);
    }

    @Test
    public void testManyToManyJoinGeneration(){
        String check = QueryBuilder.select(Airplane.class).join("flights").generateJoinClauses();
        assertEquals("INNER JOIN airplanes_flights AS \"airplanes_flights\" ON ((\"airplanes_flights\".id) = (\"%root\".id))\n" +
                "INNER JOIN flights AS \"flights\" ON ((\"flights\".flightnumber) = (\"airplanes_flights\".flightnumber))\n", check);
    }

    @Test
    public void testInDepthJoinGeneration(){
        String check = QueryBuilder.select(Airplane.class).join("flights").join("flights.crew").join("flights.crew.pilot").generateJoinClauses();
        assertEquals("INNER JOIN airplanes_flights AS \"airplanes_flights\" ON ((\"airplanes_flights\".id) = (\"%root\".id))\n" +
                "INNER JOIN flights AS \"flights\" ON ((\"flights\".flightnumber) = (\"airplanes_flights\".flightnumber))\n" +
                "INNER JOIN crews AS \"flights.crew\" ON ((\"flights.crew\".crewid) = (\"flights\".crewid))\n" +
                "INNER JOIN pilots AS \"flights.crew.pilot\" ON ((\"flights.crew.pilot\".crewid) = (\"flights.crew\".crewid))\n", check);
    }

    @Test
    public void testSimpleSelectClauseGeneration(){
        String check = QueryBuilder.select(Airplane.class).generateSelectClause();
        assertEquals("SELECT\n\"%root\".id AS \"%root.id\",\n\"%root\".name AS \"%root.name\"\n FROM airplanes AS \"%root\"", check);
    }

    @Test
    public void testMultipleJoinSelectClauseGeneration(){
        String check = QueryBuilder.select(Airplane.class).join("flights").join("flights.crew").generateSelectClause();
        assertEquals("SELECT\n" +
                "\"%root\".id AS \"%root.id\",\n" +
                "\"%root\".name AS \"%root.name\",\n" +
                "\"flights\".flightnumber AS \"%root.flights.flightnumber\",\n" +
                "\"flights\".flighttype AS \"%root.flights.flighttype\",\n" +
                "\"flights.crew\".crewid AS \"%root.flights.crew.crewid\",\n" +
                "\"flights.crew\".crewnumber AS \"%root.flights.crew.crewnumber\"\n" +
                " FROM airplanes AS \"%root\"", check);
    }

    @Test
    void testWhereGenerationClause(){
        String check = QueryBuilder.select(Crew.class).where(
                and(
                        field("crewSize").gt(lit(5)),
                        field("crewSize").lt(lit(10)),
                        field("crewId").in(tuple(lit(1), lit(2), lit(3), lit(4)))
                )
        ).generateWhereClause();
        assertEquals("WHERE ((\"%root\".crewSize) > (5)) AND (((\"%root\".crewSize) < (10)) AND ((\"%root\".crewId) IN (1,2,3,4)))\n", check);
    }

    @Test
    void testLikeGenerationClause(){
        String check = QueryBuilder.select(Flight.class).where(
                field("flightType").like("L%")
        ).generateWhereClause();
        assertEquals("WHERE (\"%root\".flightType) LIKE ('L%')\n", check);
    }

    @Test
    void testSubQueryGeneration(){
        String check = QueryBuilder.select(Flight.class).join("crew").where(
                field("crew.crewSize").eq(
                        QueryBuilder.subQuery(Crew.class, max(field("crewSize"))
                        ).distinct()
                )
        ).build();
        assertEquals("SELECT\n" +
                "\"%root\".flightnumber AS \"%root.flightnumber\",\n" +
                "\"%root\".flighttype AS \"%root.flighttype\",\n" +
                "\"crew\".crewid AS \"%root.crew.crewid\",\n" +
                "\"crew\".crewnumber AS \"%root.crew.crewnumber\"\n" +
                " FROM flights AS \"%root\"\n" +
                "INNER JOIN crews AS \"crew\" ON ((\"crew\".crewid) = (\"%root\".crewid))\n" +
                "WHERE (\"crew\".crewSize) = (\n" +
                "(SELECT DISTINCT\n" +
                "MAX(\"%root\".crewSize)\n" +
                " FROM crews AS \"%root\"\n" +
                "))\n" +
                ";", check);
    }
}
