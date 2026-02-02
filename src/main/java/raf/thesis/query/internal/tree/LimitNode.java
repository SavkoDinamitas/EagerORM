package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Select query AST node for storing required data for {@code LIMIT} clause generation.
 */
@NoArgsConstructor
@Getter
@Setter
public class LimitNode {
    private Integer limit;
    private Integer offset;
}
