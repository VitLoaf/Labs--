import java.util.Map;
import java.util.HashMap;

// Лабораторна робота №8
// Тема: Патерн проектування "Шаблонний метод"

// 1. Допоміжні класи (Сутності та Дані)

// Клас для представлення вхідних даних (наприклад, JSON з REST API)
class InputData {
    private final Map<String, Object> data;

    public InputData(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void remove(String key) {
        data.remove(key);
    }

    // Імітація отримання значення
    public Object get(String key) {
        return data.get(key);
    }
}

// Клас для представлення відповіді (статус, код, можливо тіло)
class UpdateResponse {
    private final int statusCode;
    private final String statusMessage;
    private String jsonBody = null; // Для Замовлення

    public UpdateResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public String getStatus() {
        return statusCode + " " + statusMessage;
    }

    // Додатковий метод для демонстрації вмісту
    public String getJsonBody() {
        return jsonBody;
    }
}

// Умовні сутності (для типів)
class ProductEntity {}
class UserEntity {}
class OrderEntity {}


// 2. Абстрактний клас (Template Method)

abstract class AbstractEntityUpdater {

    // Шаблонний метод: визначає незмінний алгоритм (послідовність кроків)
    public final UpdateResponse update(InputData input) {
        System.out.println("Starting update process...");

        Object entity = this.getEntity(input);
        boolean isValid = this.validateData(input, entity);

        // Хук: Дозволяє підкласам реагувати на невдалу валідацію
        if (!isValid) {
            this.onValidationFailure(entity);
            return new UpdateResponse(400, "Validation Failed");
        }

        // Хук: Дозволяє точково змінити вхідні дані перед збереженням
        InputData finalData = this.preSaveHook(input, entity);

        this.saveEntity(finalData);

        // Хук: Дозволяє змінити відповідь після успішного збереження
        UpdateResponse response = this.postSaveHook(new UpdateResponse(200, "OK"), entity);
        System.out.println("Update process finished.");
        return response;
    }

    // Абстрактні кроки: повинні бути реалізовані підкласами
    protected abstract Object getEntity(InputData input);
    protected abstract boolean validateData(InputData input, Object entity);
    protected abstract void saveEntity(InputData finalData);

    // Хук 1: Реакція на невдалу валідацію (порожня реалізація за замовчуванням)
    protected void onValidationFailure(Object entity) {
        // Базова поведінка: нічого не робити
    }

    // Хук 2: Перехоплення даних перед збереженням (повертає вхідні дані за замовчуванням)
    protected InputData preSaveHook(InputData input, Object entity) {
        return input;
    }

    // Хук 3: Модифікація відповіді після збереження (повертає базову відповідь за замовчуванням)
    protected UpdateResponse postSaveHook(UpdateResponse baseResponse, Object entity) {
        return baseResponse;
    }
}


// 3. Конкретні класи (Specific Implementations)

// A. ProductUpdater (Специфіка: Сповіщення при невдалій валідації)
class ProductUpdater extends AbstractEntityUpdater {

    protected ProductEntity getEntity(InputData input) {
        System.out.println("-> ProductUpdater: Fetching Product entity...");
        return new ProductEntity();
    }
    protected boolean validateData(InputData input, Object entity) {
        System.out.println("-> ProductUpdater: Validating data...");
        // Умовна валідація, тут має бути бізнес-логіка
        return true;
    }
    protected void saveEntity(InputData finalData) {
        System.out.println("-> ProductUpdater: Saving Product entity...");
    }

    // Перевизначення Хука 1: Сповіщення адміністратора
    @Override
    protected void onValidationFailure(Object entity) {
        System.out.println("-> ProductUpdater Hook: Sending admin notification via messenger about validation failure!");
    }
}

// B. UserUpdater (Специфіка: Заборона зміни поля 'email')
class UserUpdater extends AbstractEntityUpdater {

    protected UserEntity getEntity(InputData input) {
        System.out.println("-> UserUpdater: Fetching User entity...");
        return new UserEntity();
    }
    protected boolean validateData(InputData input, Object entity) {
        System.out.println("-> UserUpdater: Validating data (allowing email)...");
        return true;
    }
    protected void saveEntity(InputData finalData) {
        System.out.println("-> UserUpdater: Saving User entity (without email field)...");
    }

    // Перевизначення Хука 2: Фільтрація даних перед збереженням
    @Override
    protected InputData preSaveHook(InputData input, Object entity) {
        if (input.getData().containsKey("email")) {
            input.remove("email");
            System.out.println("-> UserUpdater Hook: Removed 'email' field from update request (policy restriction).");
        }
        return input;
    }
}

// C. OrderUpdater (Специфіка: Додавання JSON сутності до відповіді)
class OrderUpdater extends AbstractEntityUpdater {

    protected OrderEntity getEntity(InputData input) {
        System.out.println("-> OrderUpdater: Fetching Order entity...");
        return new OrderEntity();
    }
    protected boolean validateData(InputData input, Object entity) {
        System.out.println("-> OrderUpdater: Validating data...");
        return true;
    }
    protected void saveEntity(InputData finalData) {
        System.out.println("-> OrderUpdater: Saving Order entity...");
    }

    // Перевизначення Хука 3: Модифікація відповіді після збереження
    @Override
    protected UpdateResponse postSaveHook(UpdateResponse baseResponse, Object entity) {
        String orderJson = "{ \"orderId\": 456, \"status\": \"updated\", \"processedBy\": \"REST\" }";
        baseResponse.setJsonBody(orderJson);
        System.out.println("-> OrderUpdater Hook: Added JSON body to response.");
        return baseResponse;
    }
}


// 4. Клієнтський Код

public class TemplateMethodDemo {
    public static void main(String[] args) {

        // Приклад вхідних даних, що містять email (для UserUpdater)
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("name", "New Name");
        inputMap.put("email", "new@example.com");

        InputData data = new InputData(inputMap);

        System.out.println("\n--- 1. Оновлення Товар (Product) [Неуспішна валідація] ---");

        // Використовуємо анонімний клас для симуляції невдалої валідації
        ProductUpdater productUpdater = new ProductUpdater() {
            @Override
            protected boolean validateData(InputData input, Object entity) {
                System.out.println("-> ProductUpdater: Forcing validation failure...");
                return false; // Завжди повертаємо false для демонстрації Хука
            }
        };
        UpdateResponse resp1 = productUpdater.update(data);
        System.out.println("Final Response Status: " + resp1.getStatus());

        System.out.println("\n--- 2. Оновлення Користувач (User) [Фільтрація email] ---");

        // Валідація пройшла, але 'email' видаляється через Хук
        UserUpdater userUpdater = new UserUpdater();
        UpdateResponse resp2 = userUpdater.update(data);
        // Перевірка, що email видалено з даних, які пішли на збереження (через Hook)
        System.out.println("Input data still contains 'email': " + data.getData().containsKey("email"));
        System.out.println("Final Response Status: " + resp2.getStatus());

        System.out.println("\n--- 3. Оновлення Замовлення (Order) [Додавання JSON] ---");

        // До відповіді додається JSON через Хук
        OrderUpdater orderUpdater = new OrderUpdater();
        UpdateResponse resp3 = orderUpdater.update(data);
        System.out.println("Final Response Status: " + resp3.getStatus());
        System.out.println("Response Body Added: " + resp3.getJsonBody());
    }
}