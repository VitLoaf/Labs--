<?php

/// Лабораторна робота №4
// Тема: Патерн проектування "Адаптер"
// Мета: Інтеграція несумісних API (Slack, SMS) через єдиний інтерфейс Notification.

// 1. Target (Цільовий інтерфейс) - НЕ ЗМІНЮЄТЬСЯ
interface Notification
{
    /**
     * Відправляє сповіщення з заголовком і тілом повідомлення.
     * @param string $title Заголовок сповіщення.
     * @param string $message Тіло повідомлення.
     */
    public function send(string $title, string $message);
}

// 2. Існуючий компонент (Email)
class EmailNotification implements Notification
{
    private $adminEmail;

    public function __construct(string $adminEmail)
    {
        $this->adminEmail = $adminEmail;
    }

    public function send(string $title, string $message): void
    {
        // Імітація відправки email
        // mail($this->adminEmail, $title, $message); 
        echo "[EMAIL] Sent email to '{$this->adminEmail}' with title '$title' that says '$message'.\n";
    }
}

// ==========================================================
// АДАПТОВАНІ КЛАСИ (Adaptee) - НЕ СУМІСНІ З INTERFACE Notification
// ==========================================================

/**
 * Адаптований клас: API для відправки повідомлень у Slack.
 * Вимагає авторизації та chatId.
 */
class SlackApi
{
    private $login;
    private $apiKey;
    private $chatId;

    public function __construct(string $login, string $apiKey, string $chatId)
    {
        $this->login = $login;
        $this->apiKey = $apiKey;
        $this->chatId = $chatId;
    }

    /**
     * Несумісний метод: приймає лише один текстовий параметр.
     */
    public function post(string $text)
    {
        // Імітація авторизації та відправки
        echo "-> [Slack API] Authorized with login '{$this->login}' and key '{$this->apiKey}'.\n";
        echo "-> [Slack API] Posting to chat '{$this->chatId}': $text\n";
    }
}

/**
 * Адаптований клас: Сервіс для відправки SMS.
 * Вимагає phone та sender.
 */
class SmsSender
{
    private $phone;
    private $sender;

    public function __construct(string $phone, string $sender)
    {
        $this->phone = $phone;
        $this->sender = $sender;
    }

    /**
     * Несумісний метод: приймає лише тіло повідомлення.
     */
    public function sendSms(string $body)
    {
        // Імітація відправки SMS
        echo "-> [SMS API] Sending SMS from '{$this->sender}' to '{$this->phone}'.\n";
        echo "-> [SMS API] Body: $body\n";
    }
}

// ==========================================================
// КЛАСИ АДАПТЕРІВ (Adapter) - РЕАЛІЗУЮТЬ INTERFACE Notification
// ==========================================================

/**
 * Адаптер для Slack. Перетворює виклик Notification::send() на SlackApi::post().
 */
class SlackNotificationAdapter implements Notification
{
    private $slackApi;

    // Адаптер містить посилання на об'єкт Adaptee
    public function __construct(SlackApi $slackApi)
    {
        $this->slackApi = $slackApi;
    }

    // Реалізація цільового інтерфейсу
    public function send(string $title, string $message): void
    {
        // Логіка адаптації: об'єднуємо title та message, щоб відповідати SlackApi::post(text)
        $textToPost = "!!! ALERT: $title !!!\nMessage: $message";
        
        $this->slackApi->post($textToPost); // Виклик несумісного методу Adaptee
    }
}

/**
 * Адаптер для SMS. Перетворює виклик Notification::send() на SmsSender::sendSms().
 */
class SmsNotificationAdapter implements Notification
{
    private $smsSender;

    // Адаптер містить посилання на об'єкт Adaptee
    public function __construct(SmsSender $smsSender)
    {
        $this->smsSender = $smsSender;
    }

    // Реалізація цільового інтерфейсу
    public function send(string $title, string $message): void
    {
        // Логіка адаптації: SMS не підтримує довгий формат. Обмежуємо повідомлення.
        $smsBody = "ALERT: " . substr($message, 0, 80) . "..."; 
        
        $this->smsSender->sendSms($smsBody); // Виклик несумісного методу Adaptee
    }
}

// ==========================================================
// КЛІЄНТСЬКИЙ КОД (Client)
// ==========================================================

class ClientDemo
{
    /**
     * Функція, яка приймає будь-який об'єкт, що реалізує інтерфейс Notification.
     */
    public static function notifyAdmin(Notification $notification, string $title, string $message)
    {
        echo "\n--- Sending Notification via " . get_class($notification) . " ---\n";
        $notification->send($title, $message);
        echo "--------------------------------------------------------\n";
    }
}

// --- Демонстрація використання ---

$title = "CRITICAL SERVER FAILURE";
$message = "Database connection timed out (Code 500). System is operating in read-only mode.";

// 1. Оригінальний Email
$emailNotification = new EmailNotification("admin@corporation.com");
ClientDemo::notifyAdmin($emailNotification, $title, $message);

// 2. Slack (Через Адаптер)
$slackApi = new SlackApi("monitoring_bot", "SECURE_KEY_XYZ", "#alerts-dev");
$slackAdapter = new SlackNotificationAdapter($slackApi);
ClientDemo::notifyAdmin($slackAdapter, $title, $message);

// 3. SMS (Через Адаптер)
$smsSender = new SmsSender("+380991112233", "Monitoring");
$smsAdapter = new SmsNotificationAdapter($smsSender);
ClientDemo::notifyAdmin($smsAdapter, $title, $message);

?>
