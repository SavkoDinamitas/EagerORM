package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Select query AST node for storing required data for single {@code ORDER BY} clause generation.
 */
@AllArgsConstructor
@Getter
public class OrderByNode {
    private Expression exp;
    private Ordering order;
}
