/// Лабораторна робота №3
// Тема: Патерн проектування "Будівельник"

// Інтерфейс будівельника
interface QueryBuilder {

    QueryBuilder select(String table, String... columns);

    QueryBuilder where(String condition);

    QueryBuilder limit(int limit);

    String getSQL();
}

// Конкретний будівельник для PostgreSQL
class PostgreSQLQueryBuilder implements QueryBuilder {

    @Override
    public QueryBuilder select(String table, String... columns) {
        return this;
    }

    @Override
    public QueryBuilder where(String condition) {
        return this;
    }

    @Override
    public QueryBuilder limit(int limit) {
        return this;
    }

    @Override
    public String getSQL() {
        return "PostgreSQL SQL query";
    }
}

// Конкретний будівельник для MySQL
class MySQLQueryBuilder implements QueryBuilder {

    @Override
    public QueryBuilder select(String table, String... columns) {
        return this;
    }

    @Override
    public QueryBuilder where(String condition) {
        return this;
    }

    @Override
    public QueryBuilder limit(int limit) {
        return this;
    }

    @Override
    public String getSQL() {
        return "MySQL SQL query";
    }
}

// Клієнтський код
public class BuilderDemo {

    public static void main(String[] args) {

        // Робота з PostgreSQL
        QueryBuilder postgresBuilder = new PostgreSQLQueryBuilder();
        String postgresQuery = postgresBuilder
                .select("users", "id", "name", "email")
                .where("id > 10")
                .limit(5)
                .getSQL();

        System.out.println(postgresQuery);

        // Робота з MySQL
        QueryBuilder mysqlBuilder = new MySQLQueryBuilder();
        String mysqlQuery = mysqlBuilder
                .select("products", "id", "title", "price")
                .where("price < 100")
                .limit(10)
                .getSQL();

        System.out.println(mysqlQuery);
    }
}

