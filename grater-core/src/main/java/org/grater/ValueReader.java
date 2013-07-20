package org.grater;

import java.sql.ResultSet;

import org.grater.model.Column;
import org.grater.model.Table;

public interface ValueReader {
	Object read(Table table, Column column, ResultSet resultSet);
}
