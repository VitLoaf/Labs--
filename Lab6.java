import java.util.HashMap;
import java.util.Map;

// Лабораторна робота №6
// Тема: Патерн проектування "Замісник" (Proxy)

// 1. Subject: Інтерфейс Downloader (Інтерфейс Сервісу)
interface Downloader {
    // Повертає контент файлу (наприклад, рядок)
    String download(String url);
}

// 2. Real Subject: Клас SimpleDownloader (Справжній Сервіс, НЕ ЗМІНЮЄТЬСЯ)
class SimpleDownloader implements Downloader {

    public SimpleDownloader() {
        // Конструктор
    }

    // Імітація тривалого завантаження
    @Override
    public String download(String url) {
        System.out.println("-> SimpleDownloader: Downloading file from " + url + "...");
        // Тут була б реальна логіка взаємодії з мережею
        try {
            // Імітація затримки
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Content of " + url;
    }
}

// 3. Proxy: Клас CachingDownloaderProxy (Замісник)
class CachingDownloaderProxy implements Downloader {

    private final Downloader realDownloader; // Посилання на Справжній Сервіс
    private final Map<String, String> cache; // Кеш для зберігання результатів

    public CachingDownloaderProxy(Downloader realDownloader) {
        this.realDownloader = realDownloader;
        this.cache = new HashMap<>(); // Ініціалізація кешу
    }

    // Метод, який контролює доступ до оригінального завантажувача
    @Override
    public String download(String url) {

        // 1. Логіка Замісника: Перевірка кешу
        if (cache.containsKey(url)) {
            System.out.println("-> CachingProxy: Returning cached content for " + url);
            return cache.get(url);
        } else {
            // 2. Якщо кешу немає, викликаємо метод справжнього об'єкта
            String content = realDownloader.download(url);

            // 3. Кешуємо результат перед поверненням
            cache.put(url, content);
            System.out.println("-> CachingProxy: Cached new content for " + url);
            return content;
        }
    }
}

// 4. Клієнтський Код
public class ProxyDemo {
    public static void main(String[] args) {

        // Створюємо справжній об'єкт (SimpleDownloader)
        Downloader simpleDownloader = new SimpleDownloader();

        // Створюємо Замісника (Proxy), передаючи йому справжній об'єкт
        Downloader proxy = new CachingDownloaderProxy(simpleDownloader);

        String file1 = "https://example.com/data/file1.txt";
        String file2 = "https://example.com/data/image.jpg";

        System.out.println("--- 1. Перше завантаження (Викликається SimpleDownloader і кешується) ---");

        // Виклик 1: Файл 1 завантажується вперше
        String content1 = proxy.download(file1);
        System.out.println("Result: " + content1.substring(0, 18) + "...");

        // Виклик 2: Файл 2 завантажується вперше
        String content2 = proxy.download(file2);
        System.out.println("Result: " + content2.substring(0, 18) + "...");


        System.out.println("\n--- 2. Повторне завантаження (Дані беруться з кешу) ---");

        // Виклик 3: Файл 1 завантажується повторно (Викликається Proxy, SimpleDownloader ігнорується)
        String content3 = proxy.download(file1);
        System.out.println("Result: " + content3.substring(0, 18) + "...");

        // Виклик 4: Файл 2 завантажується повторно
        String content4 = proxy.download(file2);
        System.out.println("Result: " + content4.substring(0, 18) + "...");

        System.out.println("\nКешування успішно продемонстроване: SimpleDownloader викликався лише двічі.");
    }
}