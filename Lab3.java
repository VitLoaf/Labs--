import java.util.StringJoiner;
import java.util.Arrays;

/// Лабораторна робота №3
// Тема: Патерн проектування "Будівельник"

/**
 * Інтерфейс QueryBuilder (Builder). 
 * Визначає спільний інтерфейс для всіх конкретних будівельників.
 */
interface QueryBuilder {

    /** Додає частину SELECT <columns> FROM <table> до запиту. */
    QueryBuilder select(String table, String... columns);

    /** Додає частину WHERE <condition> до запиту. */
    QueryBuilder where(String condition);

    /** Додає частину LIMIT <limit> до запиту. */
    QueryBuilder limit(int limit);

    /** Повертає кінцевий сформований SQL-запит. */
    String getSQL();
}

/**
 * Конкретний будівельник для PostgreSQL.
 * Зберігає частини запиту і реалізує логіку збирання.
 */
class PostgreSQLQueryBuilder implements QueryBuilder {

    // Внутрішній стан для зберігання частин запиту
    private String selectPart = "";
    private String wherePart = "";
    private String limitPart = "";

    @Override
    public QueryBuilder select(String table, String... columns) {
        // Формуємо список колонок через кому
        String columnList = String.join(", ", columns);
        this.selectPart = "SELECT " + columnList + " FROM " + table;
        return this; // Повертаємо this для ланцюгового виклику
    }

    @Override
    public QueryBuilder where(String condition) {
        if (condition != null && !condition.isEmpty()) {
            this.wherePart = " WHERE " + condition;
        }
        return this; // Повертаємо this для ланцюгового виклику
    }

    @Override
    public QueryBuilder limit(int limit) {
        // PostgreSQL використовує синтаксис LIMIT
        if (limit > 0) {
            this.limitPart = " LIMIT " + limit;
        }
        return this; // Повертаємо this для ланцюгового виклику
    }

    @Override
    public String getSQL() {
        // Збираємо усі частини запиту
        return selectPart + wherePart + limitPart + ";";
    }
}

/**
 * Конкретний будівельник для MySQL.
 * Реалізує логіку збирання, дозволяючи специфічні відмінності.
 */
class MySQLQueryBuilder implements QueryBuilder {

    // Внутрішній стан для зберігання частин запиту
    private String selectPart = "";
    private String wherePart = "";
    private String limitPart = "";

    @Override
    public QueryBuilder select(String table, String... columns) {
        // Формуємо список колонок через кому
        String columnList = String.join(", ", columns);
        this.selectPart = "SELECT " + columnList + " FROM " + table;
        return this; // Повертаємо this
    }

    @Override
    public QueryBuilder where(String condition) {
        if (condition != null && !condition.isEmpty()) {
            this.wherePart = " WHERE " + condition;
        }
        return this; // Повертаємо this
    }

    @Override
    public QueryBuilder limit(int limit) {
        // MySQL також використовує синтаксис LIMIT
        if (limit > 0) {
            this.limitPart = " LIMIT " + limit;
        }
        return this; // Повертаємо this
    }

    @Override
    public String getSQL() {
        // Збираємо усі частини запиту
        return selectPart + wherePart + limitPart + ";";
    }
}

/**
 * Клієнтський код (Director/Client). 
 * Демонструє використання будівельників.
 */
public class BuilderDemo {

    public static void main(String[] args) {

        System.out.println("=== Робота з PostgreSQL ===");
        // Робота з PostgreSQL
        QueryBuilder postgresBuilder = new PostgreSQLQueryBuilder();
        
        String postgresQuery = postgresBuilder
                .select("users", "id", "name", "email")
                .where("id > 10 AND status = 'active'")
                .limit(5)
                .getSQL();

        System.out.println("PostgreSQL Query:\n" + postgresQuery);
        // Очікуваний результат: SELECT id, name, email FROM users WHERE id > 10 AND status = 'active' LIMIT 5;

        System.out.println("\n=== Робота з MySQL ===");
        // Робота з MySQL
        QueryBuilder mysqlBuilder = new MySQLQueryBuilder();
        
        String mysqlQuery = mysqlBuilder
                .select("products", "id", "title", "price")
                .where("price < 100")
                .limit(10)
                .getSQL();

        System.out.println("MySQL Query:\n" + mysqlQuery);
        // Очікуваний результат: SELECT id, title, price FROM products WHERE price < 100 LIMIT 10;
        
        System.out.println("\n=== Демонстрація гнучкості (запит без LIMIT) ===");
        
        String partialQuery = postgresBuilder
                .select("orders", "order_id", "date")
                .where("date > '2025-01-01'")
                .getSQL(); 
                
        System.out.println("PostgreSQL Query:\n" + partialQuery);
        // Очікуваний результат: SELECT order_id, date FROM orders WHERE date > '2025-01-01';
    }
}
