package raf.thesis.query;

import raf.thesis.query.dialect.Dialect;
import raf.thesis.query.tree.Literal;

import java.util.List;

public interface ToSql {
    String toSql(Dialect dialect, List<Literal> args);
}
