package com.openkoda.core.form;

public enum FieldDbType {

    VARCHAR_255("varchar(255)", "varchar"),
    VARCHAR_1000("varchar(1000)", "varchar"),
    VARCHAR_262144("varchar(262144)", "varchar"),
    BIGINT("bigint", "int8"),
    NUMERIC("numeric", null),
    BOOLEAN("boolean", "bool"),
    DATE("date", null),
    TIMESTAMP_W_TZ("timestamp with time zone", "timestamptz"),
    TIME_W_TZ("time with time zone", "timetz"),
    ;

    private String value;
    private String columnType;

    FieldDbType(String value, String columnType) {
        this.value = value;
        this.columnType = columnType;
    }

    public String getColumnType() {
        return columnType != null ? columnType : value;
    }

    public String getValue() {
        return value;
    }
}
