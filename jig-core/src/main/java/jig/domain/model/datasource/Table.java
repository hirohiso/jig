package jig.domain.model.datasource;

public class Table {

    String value;

    public Table(String value) {
        this.value = value;
    }

    String name() {
        return value;
    }
}
