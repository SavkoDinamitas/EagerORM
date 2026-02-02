package raf.thesis.query.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.internal.tree.Literal;

import java.util.List;

/**
 * Special class for storing required data for prepared statement query generation.
 */
@Getter@Setter@AllArgsConstructor
public class PreparedStatementQuery {
    String query;
    List<Literal> arguments;
}
