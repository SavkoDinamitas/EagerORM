package raf.thesis.query.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderByNode {
    private Expression exp;
    private Ordering order;
}
