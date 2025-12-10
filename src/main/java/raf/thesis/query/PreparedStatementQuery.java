package raf.thesis.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.tree.Literal;

import java.util.List;

@Getter@Setter@AllArgsConstructor
public class PreparedStatementQuery {
    String query;
    List<Literal> arguments;
}
