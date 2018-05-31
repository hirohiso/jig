package org.dddjava.jig.domain.model.implementation.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * テーブル一覧
 */
public class Tables {
    private final List<Table> tables;

    public Tables(Table table) {
        this(Collections.singletonList(table));
    }

    private Tables(List<Table> tables) {
        this.tables = tables;
    }

    public static Tables nothing() {
        return new Tables(Collections.emptyList());
    }

    public Tables merge(Tables other) {
        ArrayList<Table> list = new ArrayList<>(this.tables);
        list.addAll(other.tables);
        return new Tables(list);
    }

    public String asText() {
        return tables.stream()
                .map(Table::name)
                .distinct()
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
