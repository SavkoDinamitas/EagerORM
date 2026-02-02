package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Select query AST node for storing required data for {@code GROUP BY} clause generation.
 */
@AllArgsConstructor
@Getter
@Setter
public class GroupByNode {
    private List<Expression> expressions;
}
