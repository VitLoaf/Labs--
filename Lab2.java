// Лабораторна робота №2
// Тема: Патерн проектування "Фабричний метод"

public class FactoryMethodSocialMediaDemo {

    public static void main(String[] args) {

        // Публікація повідомлення у Facebook
        SocialNetworkFactory facebookFactory =
                new FacebookFactory("user_login", "user_password");
        SocialNetwork facebook = facebookFactory.createSocialNetwork();
        facebook.postMessage("Hello Facebook!");

        // Публікація повідомлення у LinkedIn
        SocialNetworkFactory linkedInFactory =
                new LinkedInFactory("user@email.com", "user_password");
        SocialNetwork linkedIn = linkedInFactory.createSocialNetwork();
        linkedIn.postMessage("Hello LinkedIn!");
    }
}

// Продукт
interface SocialNetwork {

    boolean authenticate();

    void postMessage(String message);
}

// Конкретний продукт Facebook
class Facebook implements SocialNetwork {

    private String login;
    private String password;

    public Facebook(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public boolean authenticate() {
        return true;
    }

    @Override
    public void postMessage(String message) {
        System.out.println("Facebook post: " + message);
    }
}

// Конкретний продукт LinkedIn
class LinkedIn implements SocialNetwork {

    private String email;
    private String password;

    public LinkedIn(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean authenticate() {
        return true;
    }

    @Override
    public void postMessage(String message) {
        System.out.println("LinkedIn post: " + message);
    }
}

// Абстрактний творець
abstract class SocialNetworkFactory {

    public abstract SocialNetwork createSocialNetwork();
}

// Конкретна фабрика Facebook
class FacebookFactory extends SocialNetworkFactory {

    private String login;
    private String password;

    public FacebookFactory(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public SocialNetwork createSocialNetwork() {
        return new Facebook(login, password);
    }
}

// Конкретна фабрика LinkedIn
class LinkedInFactory extends SocialNetworkFactory {

    private String email;
    private String password;

    public LinkedInFactory(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public SocialNetwork createSocialNetwork() {
        return new LinkedIn(email, password);
    }
}
