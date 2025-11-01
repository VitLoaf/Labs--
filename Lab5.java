// Інтерфейс реалізації: Renderer
interface Renderer {
    String renderTitle(String title);
    String renderText(String text);
    String renderImage(String url);
    String renderLink(String url, String title);
    String render();
}

// Конкретна реалізація: HTMLRenderer
class HTMLRenderer implements Renderer {
    @Override
    public String renderTitle(String title) {
        return "<h1>" + title + "</h1>";
    }

    @Override
    public String renderText(String text) {
        return "<p>" + text + "</p>";
    }

    @Override
    public String renderImage(String url) {
        return "<img src='" + url + "' />";
    }

    @Override
    public String renderLink(String url, String title) {
        return "<a href='" + url + "'>" + title + "</a>";
    }

    @Override
    public String render() {
        return "<!-- HTML Render Complete -->";
    }
}

// Конкретна реалізація: JsonRenderer
class JsonRenderer implements Renderer {
    @Override
    public String renderTitle(String title) {
        return "\"title\": \"" + title + "\"";
    }

    @Override
    public String renderText(String text) {
        return "\"text\": \"" + text + "\"";
    }

    @Override
    public String renderImage(String url) {
        return "\"image\": \"" + url + "\"";
    }

    @Override
    public String renderLink(String url, String title) {
        return "\"link\": {\"url\": \"" + url + "\", \"title\": \"" + title + "\"}";
    }

    @Override
    public String render() {
        return "{ \"render\": \"json complete\" }";
    }
}

// Конкретна реалізація: XmlRenderer
class XmlRenderer implements Renderer {
    @Override
    public String renderTitle(String title) {
        return "<title>" + title + "</title>";
    }

    @Override
    public String renderText(String text) {
        return "<text>" + text + "</text>";
    }

    @Override
    public String renderImage(String url) {
        return "<image src='" + url + "' />";
    }

    @Override
    public String renderLink(String url, String title) {
        return "<link url='" + url + "'>" + title + "</link>";
    }

    @Override
    public String render() {
        return "<!-- XML Render Complete -->";
    }
}

// Абстракція: Page
abstract class Page {
    protected Renderer renderer;

    public Page(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract String render();
}

// Конкретна абстракція: SimplePage
class SimplePage extends Page {
    private String title;
    private String content;

    public SimplePage(Renderer renderer, String title, String content) {
        super(renderer);
        this.title = title;
        this.content = content;
    }

    @Override
    public String render() {
        return renderer.renderTitle(title) + "\n" + renderer.renderText(content);
    }
}

// Допоміжний клас: Product
class Product {
    private String id;
    private String name;
    private String description;
    private String image;

    public Product(String id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
}

// Конкретна абстракція: ProductPage
class ProductPage extends Page {
    private Product product;

    public ProductPage(Renderer renderer, Product product) {
        super(renderer);
        this.product = product;
    }

    @Override
    public String render() {
        return renderer.renderTitle(product.getName()) + "\n"
                + renderer.renderText(product.getDescription()) + "\n"
                + renderer.renderImage(product.getImage()) + "\n"
                + renderer.renderLink("/product/" + product.getId(), "Детальніше");
    }
}

// Клієнтський код
public class Lab5 {
    public static void main(String[] args) {
        // HTML рендер
        Renderer html = new HTMLRenderer();
        Page page1 = new SimplePage(html, "Головна сторінка", "Вітаємо на сайті!");
        System.out.println(page1.render());
        System.out.println(html.render());

        // JSON рендер
        Renderer json = new JsonRenderer();
        Product product = new Product("101", "Ноутбук", "Потужний ноутбук з 16 ГБ RAM", "notebook.jpg");
        Page page2 = new ProductPage(json, product);
        System.out.println(page2.render());
        System.out.println(json.render());

        // XML рендер
        Renderer xml = new XmlRenderer();
        Page page3 = new SimplePage(xml, "Контакти", "Зв’яжіться з нами через форму");
        System.out.println(page3.render());
        System.out.println(xml.render());
    }
}
