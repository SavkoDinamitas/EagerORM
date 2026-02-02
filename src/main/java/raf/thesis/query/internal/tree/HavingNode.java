package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Select query AST node for storing required data for {@code HAVING} clause generation.
 */
@Getter
@Setter
@AllArgsConstructor
public class HavingNode {
    private Expression expression;
}
