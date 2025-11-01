// Інтерфейс будівельника запитів
interface QueryBuilder {
    QueryBuilder select(String table, String... fields);
    QueryBuilder where(String condition);
    QueryBuilder limit(int limit);
    String getSQL();
}

// Конкретний будівельник: MySQL
class MySQLQueryBuilder implements QueryBuilder {
    private StringBuilder query = new StringBuilder();

    @Override
    public QueryBuilder select(String table, String... fields) {
        query.append("SELECT ");
        if (fields.length == 0) {
            query.append("*");
        } else {
            query.append(String.join(", ", fields));
        }
        query.append(" FROM ").append(table);
        return this;
    }

    @Override
    public QueryBuilder where(String condition) {
        query.append(" WHERE ").append(condition);
        return this;
    }

    @Override
    public QueryBuilder limit(int limit) {
        query.append(" LIMIT ").append(limit);
        return this;
    }

    @Override
    public String getSQL() {
        return query.toString() + ";";
    }
}

// Конкретний будівельник: PostgreSQL
class PostgreSQLQueryBuilder implements QueryBuilder {
    private StringBuilder query = new StringBuilder();

    @Override
    public QueryBuilder select(String table, String... fields) {
        query.append("SELECT ");
        if (fields.length == 0) {
            query.append("*");
        } else {
            query.append(String.join(", ", fields));
        }
        query.append(" FROM ").append(table);
        return this;
    }

    @Override
    public QueryBuilder where(String condition) {
        query.append(" WHERE ").append(condition);
        return this;
    }

    @Override
    public QueryBuilder limit(int limit) {
        query.append(" LIMIT ").append(limit);
        return this;
    }

    @Override
    public String getSQL() {
        return query.toString() + ";";
    }
}

// Директор
class QueryDirector {
    public String createSimpleUserQuery(QueryBuilder builder) {
        return builder
                .select("users", "id", "name", "email")
                .where("active = true")
                .limit(10)
                .getSQL();
    }
}

// Клієнтський код
public class Lab3 {
    public static void main(String[] args) {
        QueryDirector director = new QueryDirector();

        // Конкретний продукт: MySQL
        QueryBuilder mySQLBuilder = new MySQLQueryBuilder();
        String mysqlQuery = director.createSimpleUserQuery(mySQLBuilder);
        System.out.println("MySQL Query:");
        System.out.println(mysqlQuery);

        // Конкретний продукт: PostgreSQL
        QueryBuilder postgresBuilder = new PostgreSQLQueryBuilder();
        String postgresQuery = director.createSimpleUserQuery(postgresBuilder);
        System.out.println("\nPostgreSQL Query:");
        System.out.println(postgresQuery);
    }
}
