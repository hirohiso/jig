package org.dddjava.jig.domain.model.implementation.datasource;

/**
 * テーブル
 */
public class Table {

    String value;

    public Table(String value) {
        this.value = value;
    }

    String name() {
        return value;
    }
}
