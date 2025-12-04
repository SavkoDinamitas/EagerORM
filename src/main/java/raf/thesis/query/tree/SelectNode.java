package raf.thesis.query.tree;

import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.Join;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SelectNode {
    private final Class<?> root;
    private final String baseAlias = "%root";
    private final String baseTableName;
    private List<JoinNode> joinNodes = new ArrayList<>();
    @Setter
    private List<Expression> selectFieldNodes = new ArrayList<>();
    @Setter
    private WhereNode whereNode;
    @Setter
    private HavingNode havingNode;
    @Setter
    private GroupByNode groupByNode;
    @Setter
    private List<OrderByNode> orderByNodes;
    private LimitNode limitNode;
    @Setter
    private boolean distinct;

    public SelectNode(Class<?> root, String baseTableName) {
        this.root = root;
        this.baseTableName = baseTableName;
    }

    public void addJoinNode(List<JoinNode> jn){
        joinNodes.addAll(jn);
    }

    public void setLimit(int limit){
        limitNode = limitNode == null ? new LimitNode() : limitNode;
        limitNode.setLimit(limit);
    }

    public void setOffset(int offset){
        limitNode = limitNode == null ? new LimitNode() : limitNode;
        limitNode.setOffset(offset);
    }

    public void addSelectClauseColumn(FieldNode expr){
        selectFieldNodes.add(expr);
    }
}
