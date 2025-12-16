// Лабораторна робота №1
// Тема: Патерн проектування "Одинак (Singleton)"

public class SingletonFileStorageDemo {

    public static void main(String[] args) {

        // Отримання єдиного екземпляра менеджера сховищ
        StorageManager manager = StorageManager.getInstance();

        // Створення користувача та призначення йому локального сховища
        User user1 = new User("user_01");
        manager.assignStorageToUser(user1.getUserId(), new LocalDiskStorage());
        user1.uploadFile("document.txt");

        // Створення користувача та призначення йому Amazon S3
        User user2 = new User("user_02");
        manager.assignStorageToUser(user2.getUserId(), new AmazonS3Storage());
        user2.downloadFile("photo.png");
    }
}

// Інтерфейс сховища, який визначає спільний контракт для всіх реалізацій
interface IStorage {

    void uploadFile(String fileName);

    void downloadFile(String fileName);

    void deleteFile(String fileName);
}

// Реалізація сховища для локального диску
class LocalDiskStorage implements IStorage {

    @Override
    public void uploadFile(String fileName) {
        System.out.println("Файл " + fileName + " завантажено на локальний диск");
    }

    @Override
    public void downloadFile(String fileName) {
        System.out.println("Файл " + fileName + " завантажено з локального диску");
    }

    @Override
    public void deleteFile(String fileName) {
        System.out.println("Файл " + fileName + " видалено з локального диску");
    }
}

// Реалізація сховища для Amazon S3
class AmazonS3Storage implements IStorage {

    @Override
    public void uploadFile(String fileName) {
        System.out.println("Файл " + fileName + " завантажено в Amazon S3");
    }

    @Override
    public void downloadFile(String fileName) {
        System.out.println("Файл " + fileName + " завантажено з Amazon S3");
    }

    @Override
    public void deleteFile(String fileName) {
        System.out.println("Файл " + fileName + " видалено з Amazon S3");
    }
}

// Клас користувача, який працює зі сховищем через StorageManager
class User {

    private String userId;

    // Конструктор користувача
    public User(String userId) {
        this.userId = userId;
    }

    // Повертає ідентифікатор користувача
    public String getUserId() {
        return userId;
    }

    // Завантаження файлу у призначене користувачу сховище
    public void uploadFile(String fileName) {
        StorageManager.getInstance()
                .getStorageForUser(userId)
                .uploadFile(fileName);
    }

    // Отримання файлу зі сховища
    public void downloadFile(String fileName) {
        StorageManager.getInstance()
                .getStorageForUser(userId)
                .downloadFile(fileName);
    }

    // Видалення файлу зі сховища
    public void deleteFile(String fileName) {
        StorageManager.getInstance()
                .getStorageForUser(userId)
                .deleteFile(fileName);
    }
}

// Клас-Одинак, який централізовано керує сховищами
class StorageManager {

    // Єдиний екземпляр класу (volatile для багатопотокової безпеки)
    private static volatile StorageManager instance;

    // Зв'язок між користувачем та його сховищем
    private java.util.Map<String, IStorage> userStorageMap = new java.util.HashMap<>();

    // Приватний конструктор забороняє створення екземплярів через new
    private StorageManager() {
    }

    // Глобальна точка доступу до єдиного екземпляра
    public static StorageManager getInstance() {
        if (instance == null) {
            synchronized (StorageManager.class) {
                if (instance == null) {
                    instance = new StorageManager();
                }
            }
        }
        return instance;
    }

    // Призначає сховище конкретному користувачу
    public void assignStorageToUser(String userId, IStorage storage) {
        userStorageMap.put(userId, storage);
    }

    // Повертає сховище, призначене користувачу
    public IStorage getStorageForUser(String userId) {
        return userStorageMap.get(userId);
    }
}
