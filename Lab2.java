// Інтерфейс продукту
interface SocialNetwork {
    void login(String username, String password);
    void postMessage(String message);
}

//Конкретний продукт: Facebook
class Facebook implements SocialNetwork {
    private String login;
    private String password;

    public Facebook(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public void login(String username, String password) {
        System.out.println("Підключення до Facebook через логін: " + username);
    }

    @Override
    public void postMessage(String message) {
        System.out.println("Публікація у Facebook: " + message);
    }
}

//Конкретний продукт: LinkedIn
class LinkedIn implements SocialNetwork {
    private String email;
    private String password;

    public LinkedIn(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public void login(String username, String password) {
        System.out.println("Підключення до LinkedIn через email: " + username);
    }

    @Override
    public void postMessage(String message) {
        System.out.println("Публікація у LinkedIn: " + message);
    }
}

//Абстрактний творець
abstract class SocialNetworkCreator {
    public abstract SocialNetwork createNetwork();

    public void publish(String username, String password, String message) {
        SocialNetwork network = createNetwork();
        network.login(username, password);
        network.postMessage(message);
    }
}

//Конкретний творець: Facebook
class FacebookCreator extends SocialNetworkCreator {
    private String login;
    private String password;

    public FacebookCreator(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public SocialNetwork createNetwork() {
        return new Facebook(login, password);
    }
}

// Конкретний творець: LinkedIn
class LinkedInCreator extends SocialNetworkCreator {
    private String email;
    private String password;

    public LinkedInCreator(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public SocialNetwork createNetwork() {
        return new LinkedIn(email, password);
    }
}

// Демонстрація роботи
public class Lab2 {
    public static void main(String[] args) {
        SocialNetworkCreator facebook = new FacebookCreator("userFB", "12345");
        facebook.publish("userFB", "12345", "Привіт, Facebook!");

        SocialNetworkCreator linkedIn = new LinkedInCreator("user@linkedin.com", "qwerty");
        linkedIn.publish("user@linkedin.com", "qwerty", "Вітаю, LinkedIn!");
    }
}
