package org.grater.jdbc;

public class UpperCamelNameTranslator implements NameTranslator {

	@Override
	public String getTableForEntity(String entityName) {
		return camelToUpper(entityName);
	}

	@Override
	public String getPropertyForColumn(String tableName, String columnName) {
		return upperToCamel(columnName);
	}
	
	@Override
	public String getEntityForTable(String tableName) {
		return upperToCamel(tableName);
	}
	
	private String upperToCamel(String upper) {
		boolean nextIsUpper = false;
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < upper.length(); ++ i) {
			char ch = upper.charAt(i);
			if (ch == '_') {
				nextIsUpper = true;
			} else {
				if (nextIsUpper) {
					builder.append(Character.toUpperCase(ch));
					nextIsUpper = false;
				} else {
					builder.append(Character.toLowerCase(ch));
				}
			}
		}
		
		return builder.toString();
	}

	private String camelToUpper(String camel) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < camel.length(); ++ i) {
			char ch = camel.charAt(i);
			if (Character.isUpperCase(ch)) {
				builder.append("_");
				builder.append(ch);
			} else {
				builder.append(Character.toUpperCase(ch));
			}
		}
		return builder.toString();
	}
}
