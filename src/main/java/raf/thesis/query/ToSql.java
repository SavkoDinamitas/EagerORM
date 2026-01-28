package raf.thesis.query;

import raf.thesis.query.dialect.Dialect;
import raf.thesis.query.tree.Literal;

import java.util.List;

/**
 * Helper interface for Expressions to call their specific Dialect methods in query generation.
 */
public interface ToSql {
    String toSql(Dialect dialect, List<Literal> args);
}
