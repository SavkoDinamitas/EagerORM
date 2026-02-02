package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Select query AST node for storing required data for {@code WHERE} clause generation.
 */
@AllArgsConstructor
@Getter
@Setter
public class WhereNode {
    Expression expression;
}
