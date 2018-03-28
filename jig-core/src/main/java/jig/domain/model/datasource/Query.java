package jig.domain.model.datasource;

public class Query {

    String text;

    public Query(String text) {
        this.text = text;
    }

    public Tables extractTable(SqlType sqlType) {
        Table table = text == null ? sqlType.unexpectedTable() : sqlType.extractTable(text);
        return new Tables(table);
    }

    public static Query unsupported() {
        return new Query(null);
    }
}
