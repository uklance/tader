package org.tader.autogen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.tader.AutoGenerateStrategy;
import org.tader.PropertyDef;
import org.tader.TypeCoercer;
import org.tader.jdbc.ConnectionCallback;
import org.tader.jdbc.JdbcTemplate;

public class QueryAutoGenerateStrategy implements AutoGenerateStrategy {
	private final JdbcTemplate jdbcTemplate;
	private final TypeCoercer typeCoercer;
	private final ConnectionCallback<Object> callback;
	private final Class<?> resultType;
	
	public QueryAutoGenerateStrategy(JdbcTemplate jdbcTemplate, TypeCoercer typeCoercer, final String sql, Class<?> resultType) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.typeCoercer = typeCoercer;
		this.resultType = resultType;
		this.callback = new ConnectionCallback<Object>() {
			@Override
			public Object handle(Connection con) throws SQLException {
				ResultSet rs = con.createStatement().executeQuery(sql);
				if (rs.next()) {
					return rs.getObject(1);
				}
				return null;
			}
		};
	}
	
	@Override
	public Object generate(PropertyDef propDef, int increment) {
		Object result = jdbcTemplate.execute(callback);
		return typeCoercer.coerce(result, resultType);
	}
}
