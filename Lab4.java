// Інтерфейс сповіщень
interface Notification {
    void send(String title, String message);
}

// Конкретний продукт: Email
class EmailNotification implements Notification {
    private String adminEmail;

    public EmailNotification(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    @Override
    public void send(String title, String message) {
        // mail(adminEmail, title, message);
        System.out.println("Відправлено Email '" + title + "' до " + adminEmail + ": " + message);
    }
}

// Сторонній клас: Slack API (не сумісний із Notification)
class SlackService {
    private String login;
    private String apiKey;

    public SlackService(String login, String apiKey) {
        this.login = login;
        this.apiKey = apiKey;
    }

    public void sendMessage(String chatId, String message) {
        System.out.println("Повідомлення у Slack (" + chatId + "): " + message);
    }
}

// Адаптер: SlackNotificationAdapter
class SlackNotificationAdapter implements Notification {
    private SlackService slackService;
    private String chatId;

    public SlackNotificationAdapter(SlackService slackService, String chatId) {
        this.slackService = slackService;
        this.chatId = chatId;
    }

    @Override
    public void send(String title, String message) {
        String formattedMessage = "[" + title + "] " + message;
        slackService.sendMessage(chatId, formattedMessage);
    }
}

// Сторонній клас: SMS-сервіс
class SMSService {
    private String phone;
    private String sender;

    public SMSService(String phone, String sender) {
        this.phone = phone;
        this.sender = sender;
    }

    public void sendText(String text) {
        System.out.println("SMS до " + phone + " від " + sender + ": " + text);
    }
}

// Адаптер: SMSNotificationAdapter
class SMSNotificationAdapter implements Notification {
    private SMSService smsService;

    public SMSNotificationAdapter(SMSService smsService) {
        this.smsService = smsService;
    }

    @Override
    public void send(String title, String message) {
        String text = title + ": " + message;
        smsService.sendText(text);
    }
}

// Клієнтський код
public class Lab4 {
    public static void main(String[] args) {
        // Конкретний продукт: Email
        Notification email = new EmailNotification("admin@example.com");
        email.send("Звіт", "Ваш звіт відправлено успішно!");

        // Адаптер: Slack
        SlackService slackService = new SlackService("user123", "api-key-123");
        Notification slack = new SlackNotificationAdapter(slackService, "#team_chat");
        slack.send("Нове повідомлення", "Команда, перевірте оновлення.");

        // Адаптер: SMS
        SMSService smsService = new SMSService("+380501112233", "SystemBot");
        Notification sms = new SMSNotificationAdapter(smsService);
        sms.send("Увага", "Ваш код підтвердження: 4821");
    }
}
