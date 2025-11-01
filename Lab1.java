// Клас Singleton – керує підключенням до обраного сховища
class StorageManager {
    private static StorageManager instance; // єдиний екземпляр
    private String storageType; // тип сховища користувача

    // приватний конструктор — запобігає створенню інших об'єктів
    private StorageManager(String storageType) {
        this.storageType = storageType;
    }

    // Статичний метод доступу до єдиного екземпляра
    public static StorageManager getInstance(String storageType) {
        if (instance == null) {
            instance = new StorageManager(storageType);
            System.out.println("Створено підключення до: " + storageType);
        } else {
            System.out.println("Використовується існуюче підключення до: " + instance.storageType);
        }
        return instance;
    }

    // Метод для роботи з файлами (умовно)
    public void uploadFile(String fileName) {
        System.out.println("Файл '" + fileName + "' завантажено у " + storageType);
    }

    public void downloadFile(String fileName) {
        System.out.println("Файл '" + fileName + "' завантажено з " + storageType);
    }
}

// Клас користувача, який підключається до сховища
public class Lab1 {
    public static void main(String[] args) {
        // Перший користувач обирає локальне сховище
        StorageManager user1 = StorageManager.getInstance("Local Disk");
        user1.uploadFile("photo.png");

        // Інший користувач намагається вибрати інше сховище
        StorageManager user2 = StorageManager.getInstance("Amazon S3");
        user2.downloadFile("report.pdf");
    }
}
