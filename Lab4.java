import java.util.Arrays;

/// Лабораторна робота №4
// Тема: Патерн проектування "Адаптер"

// ==========================================================
// 1. TARGET (Цільовий інтерфейс) - НЕ ЗМІНЮЄТЬСЯ
// ==========================================================

/**
 * Цільовий інтерфейс, який очікує клієнт.
 */
interface Notification {
    void send(String title, String message);
}

// 2. Існуючий компонент (Email)
class EmailNotification implements Notification {
    private final String adminEmail;

    public EmailNotification(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    @Override
    public void send(String title, String message) {
        // Імітація відправки email
        System.out.println("[EMAIL] Sent email to '" + this.adminEmail + "' with title '" + title + "' that says '" + message + "'.");
    }
}

// ==========================================================
// АДАПТОВАНІ КЛАСИ (Adaptee) - НЕ СУМІСНІ З INTERFACE Notification
// ==========================================================

/**
 * Адаптований клас: API для відправки повідомлень у Slack.
 * Вимагає login, apiKey та chatId.
 */
class SlackApi {
    private final String login;
    private final String apiKey;
    private final String chatId;

    public SlackApi(String login, String apiKey, String chatId) {
        this.login = login;
        this.apiKey = apiKey;
        this.chatId = chatId;
    }

    /**
     * Несумісний метод: приймає лише один текстовий параметр.
     */
    public void post(String text) {
        // Імітація авторизації та відправки
        System.out.println("-> [Slack API] Authorized with login '" + this.login + "' and key '" + this.apiKey + "'.");
        System.out.println("-> [Slack API] Posting to chat '" + this.chatId + "': " + text);
    }
}

/**
 * Адаптований клас: Сервіс для відправки SMS.
 * Вимагає phone та sender.
 */
class SmsSender {
    private final String phone;
    private final String sender;

    public SmsSender(String phone, String sender) {
        this.phone = phone;
        this.sender = sender;
    }

    /**
     * Несумісний метод: приймає лише тіло повідомлення.
     */
    public void sendSms(String body) {
        // Імітація відправки SMS
        System.out.println("-> [SMS API] Sending SMS from '" + this.sender + "' to '" + this.phone + "'.");
        System.out.println("-> [SMS API] Body: " + body);
    }
}

// ==========================================================
// КЛАСИ АДАПТЕРІВ (Adapter) - РЕАЛІЗУЮТЬ INTERFACE Notification
// ==========================================================

/**
 * Адаптер для Slack. Перетворює виклик Notification::send() на SlackApi::post().
 */
class SlackNotificationAdapter implements Notification {
    private final SlackApi slackApi;

    // Адаптер містить посилання на об'єкт Adaptee
    public SlackNotificationAdapter(SlackApi slackApi) {
        this.slackApi = slackApi;
    }

    // Реалізація цільового інтерфейсу Notification
    @Override
    public void send(String title, String message) {
        // Логіка адаптації: об'єднуємо title та message, щоб відповідати SlackApi::post(text)
        String textToPost = "!!! ALERT: " + title + " !!!\nMessage: " + message;
        
        this.slackApi.post(textToPost); // Виклик несумісного методу Adaptee
    }
}

/**
 * Адаптер для SMS. Перетворює виклик Notification::send() на SmsSender::sendSms().
 */
class SmsNotificationAdapter implements Notification {
    private final SmsSender smsSender;

    // Адаптер містить посилання на об'єкт Adaptee
    public SmsNotificationAdapter(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    // Реалізація цільового інтерфейсу Notification
    @Override
    public void send(String title, String message) {
        // Логіка адаптації: SMS не підтримує довгий формат. Обмежуємо повідомлення.
        String smsBody = "ALERT: " + message.substring(0, Math.min(message.length(), 80)) + "..."; 
        
        this.smsSender.sendSms(smsBody); // Виклик несумісного методу Adaptee
    }
}

// ==========================================================
// КЛІЄНТСЬКИЙ КОД (Client)
// ==========================================================

public class AdapterDemo {

    /**
     * Функція, яка приймає будь-який об'єкт, що реалізує інтерфейс Notification.
     */
    public static void notifyAdmin(Notification notification, String title, String message) {
        System.out.println("\n--- Sending Notification via " + notification.getClass().getSimpleName() + " ---");
        notification.send(title, message);
        System.out.println("--------------------------------------------------------");
    }

    public static void main(String[] args) {

        String title = "CRITICAL SERVER FAILURE";
        String message = "Database connection timed out (Code 500). System is operating in read-only mode.";

        // 1. Оригінальний Email
        Notification emailNotification = new EmailNotification("admin@corporation.com");
        notifyAdmin(emailNotification, title, message);

        // 2. Slack (Через Адаптер)
        SlackApi slackApi = new SlackApi("monitoring_bot", "SECURE_KEY_XYZ", "#alerts-dev");
        Notification slackAdapter = new SlackNotificationAdapter(slackApi);
        notifyAdmin(slackAdapter, title, message);

        // 3. SMS (Через Адаптер)
        SmsSender smsSender = new SmsSender("+380991112233", "Monitoring");
        Notification smsAdapter = new SmsNotificationAdapter(smsSender);
        notifyAdmin(smsAdapter, title, message);
    }
}

