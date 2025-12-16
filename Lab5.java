import java.util.Arrays;

// Лабораторна робота №5
// Тема: Патерн проектування "Міст" (Bridge)

// 1. Ієрархія Даних

/**
 * Клас Product для зберігання даних про товар.
 */
class Product {
    private final String id;
    private final String name;
    private final String description;
    private final String image;

    public Product(String id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    // Методи-гетери
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
}


// 2. Ієрархія Реалізації (Implementor) - Renderer

/**
 * Implementor (Реалізатор): Інтерфейс Renderer.
 * Визначає примітивні операції для подання даних.
 */
interface Renderer {
    String renderTitle(String title);
    String renderBlock(String content);
    String renderProductInfo(String name, String description, String image, String id);
    String renderFinal(String elements);
}

// Concrete Implementor 1
class HTMLRenderer implements Renderer {
    @Override
    public String renderTitle(String title) {
        return "\n  <h1>" + title + "</h1>";
    }

    @Override
    public String renderBlock(String content) {
        return "\n  <p>" + content + "</p>";
    }

    @Override
    public String renderProductInfo(String name, String description, String image, String id) {
        return "\n  <div class=\"product\">" +
               "\n    <h2>Product: " + name + " (ID: " + id + ")</h2>" +
               "\n    <img src=\"" + image + "\">" +
               "\n    <p>Description: " + description + "</p>" +
               "\n  </div>";
    }

    @Override
    public String renderFinal(String elements) {
        return "<html>\n<body>" + elements + "\n</body>\n</html>";
    }
}

// Concrete Implementor 2
class JsonRenderer implements Renderer {
    @Override
    public String renderTitle(String title) {
        return "\"title\": \"" + title + "\"";
    }

    @Override
    public String renderBlock(String content) {
        return ",\n\"content\": \"" + content + "\"";
    }

    @Override
    public String renderProductInfo(String name, String description, String image, String id) {
        return "\"product\": {" +
               "\n  \"id\": \"" + id + "\"," +
               "\n  \"name\": \"" + name + "\"," +
               "\n  \"description\": \"" + description + "\"," +
               "\n  \"image_url\": \"" + image + "\"" +
               "\n}";
    }

    @Override
    public String renderFinal(String elements) {
        return "{\n" + elements + "\n}";
    }
}

// Concrete Implementor 3
class XmlRenderer implements Renderer {
    @Override
    public String renderTitle(String title) {
        return "<title>" + title + "</title>";
    }

    @Override
    public String renderBlock(String content) {
        return "<content>" + content + "</content>";
    }

    @Override
    public String renderProductInfo(String name, String description, String image, String id) {
        return "<product id=\"" + id + "\">" +
               "<name>" + name + "</name>" +
               "<description>" + description + "</description>" +
               "<image>" + image + "</image>" +
               "</product>";
    }

    @Override
    public String renderFinal(String elements) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<page>" + elements + "\n</page>";
    }
}


// 3. Ієрархія Абстракції (Abstraction) - Page

/**
 * Abstraction: Абстрактний клас Page.
 * Містить посилання на Реалізатора (Renderer) через композицію (Міст).
 */
abstract class Page {
    // Посилання на Реалізацію (Міст)
    protected Renderer renderer;

    public Page(Renderer renderer) {
        this.renderer = renderer;
    }

    // Основний метод Абстракції
    abstract String view();
}

/**
 * Refined Abstraction 1: Проста сторінка.
 */
class SimplePage extends Page {
    private final String title;
    private final String content;

    public SimplePage(Renderer renderer, String title, String content) {
        super(renderer);
        this.title = title;
        this.content = content;
    }

    // Використовує Renderer для збирання кінцевого продукту
    @Override
    String view() {
        String elements = renderer.renderTitle(title) + renderer.renderBlock(content);
        return renderer.renderFinal(elements);
    }
}

/**
 * Refined Abstraction 2: Сторінка товару.
 */
class ProductPage extends Page {
    private final Product product;

    public ProductPage(Renderer renderer, Product product) {
        super(renderer);
        this.product = product;
    }

    // Використовує Renderer для збирання кінцевого продукту
    @Override
    String view() {
        // Рендеринг специфічних для товару даних
        String elements = renderer.renderProductInfo(
            product.getName(),
            product.getDescription(),
            product.getImage(),
            product.getId()
        );
        return renderer.renderFinal(elements);
    }
}


// 4. Клієнтський Код

public class BridgeDemo {
    public static void main(String[] args) {
        // Дані для сторінок
        Product gamingLaptop = new Product("L001", "Gaming Laptop", "High-performance PC for professional gamers.", "laptop_img.jpg");
        
        // 1. Створюємо Реалізаторів (Renderer)
        Renderer htmlRenderer = new HTMLRenderer();
        Renderer jsonRenderer = new JsonRenderer();
        Renderer xmlRenderer = new XmlRenderer();

        System.out.println("--- 1. SimplePage (Проста Сторінка) ---");

        // A. SimplePage, рендеринг у HTML
        Page aboutHtml = new SimplePage(htmlRenderer, "About Us", "We are a leading technology company established in 2020.");
        System.out.println("\n--- Simple Page HTML ---");
        System.out.println(aboutHtml.view());

        // B. SimplePage, рендеринг у JSON
        Page contactJson = new SimplePage(jsonRenderer, "Contact Us", "Please email or call our support team.");
        System.out.println("\n--- Simple Page JSON ---");
        System.out.println(contactJson.view());
        
        // C. SimplePage, рендеринг у XML
        Page privacyXml = new SimplePage(xmlRenderer, "Privacy Policy", "Data protection regulations are detailed here.");
        System.out.println("\n--- Simple Page XML ---");
        System.out.println(privacyXml.view());


        System.out.println("\n--- 2. ProductPage (Сторінка Товару) ---");
        
        // D. ProductPage, рендеринг у HTML
        Page laptopHtml = new ProductPage(htmlRenderer, gamingLaptop);
        System.out.println("\n--- Product Page HTML ---");
        System.out.println(laptopHtml.view());

        // E. ProductPage, рендеринг у JSON
        Page laptopJson = new ProductPage(jsonRenderer, gamingLaptop);
        System.out.println("\n--- Product Page JSON ---");
        System.out.println(laptopJson.view());

        // F. ProductPage, рендеринг у XML
        Page laptopXml = new ProductPage(xmlRenderer, gamingLaptop);
        System.out.println("\n--- Product Page XML ---");
        System.out.println(laptopXml.view());
    }
}
