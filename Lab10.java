import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

// Лабораторна робота №10
// Тема: Патерн проектування "Посередник" (Mediator)

// 1. Інтерфейс Посередника (Mediator Interface)

interface OrderFormMediator {
    // Центральний метод для обробки сповіщень від Колег
    void notify(Component sender, String event);
}

// 2. Абстрактний Компонент (Colleague Base Class)

abstract class Component {
    protected OrderFormMediator mediator;
    protected String name; // Для ідентифікації в консолі

    public Component(OrderFormMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    // Метод, який викликається при зміні стану компонента
    public void changed(String event) {
        System.out.println(">> Component " + name + " changed state: " + event);
        mediator.notify(this, event);
    }

    // Допоміжні методи для імітації UI
    public void setEnabled(boolean state) {
        System.out.println("  [UI] " + name + " is now: " + (state ? "ACTIVE" : "DISABLED"));
    }
    public void setRequired(boolean state) {
        System.out.println("  [UI] " + name + " is now: " + (state ? "REQUIRED" : "OPTIONAL"));
    }
    public void setVisible(boolean state) {
        System.out.println("  [UI] " + name + " is now: " + (state ? "VISIBLE" : "HIDDEN"));
    }
}

// 3. Конкретні Компоненти (Colleagues)

// Компонент 1: Дата доставки
class DateSelector extends Component {
    private String selectedDate = "2026-01-01";

    public DateSelector(OrderFormMediator mediator) { super(mediator, "DateSelector"); }

    // Імітація зміни стану
    public void simulateDateChange(String newDate) {
        System.out.println("\n*** User selects new date: " + newDate + " ***");
        this.selectedDate = newDate;
        changed("DateSelected"); // Сповіщаємо Посередника
    }
    public String getSelectedDate() { return selectedDate; }
}

// Компонент 2: Час доставки (залежить від Дати)
class TimeSelector extends Component {
    public TimeSelector(OrderFormMediator mediator) { super(mediator, "TimeSelector"); }

    public void setAvailableTimeSlots(List<String> slots) {
        System.out.println("  [UI] TimeSelector updated slots: " + slots);
    }
}

// Компонент 3: Чекбокс "Отримувач інша особа"
class RecipientCheckbox extends Component {
    private boolean isOtherRecipient = false;

    public RecipientCheckbox(OrderFormMediator mediator) { super(mediator, "RecipientCheckbox"); }

    public void simulateToggle(boolean state) {
        System.out.println("\n*** User toggles 'Other Recipient' to: " + state + " ***");
        this.isOtherRecipient = state;
        changed("RecipientChanged"); // Сповіщаємо Посередника
    }
    public boolean getState() { return isOtherRecipient; }
}

// Компонент 4: Поле Ім'я отримувача
class RecipientNameField extends Component {
    public RecipientNameField(OrderFormMediator mediator) { super(mediator, "RecipientNameField"); }
}

// Компонент 5: Поле Телефон отримувача
class RecipientPhoneField extends Component {
    public RecipientPhoneField(OrderFormMediator mediator) { super(mediator, "RecipientPhoneField"); }
}

// Компонент 6: Чекбокс "Самовивіз"
class SelfPickupCheckbox extends Component {
    private boolean isSelfPickup = false;

    public SelfPickupCheckbox(OrderFormMediator mediator) { super(mediator, "SelfPickupCheckbox"); }

    public void simulateToggle(boolean state) {
        System.out.println("\n*** User toggles 'Self-Pickup' to: " + state + " ***");
        this.isSelfPickup = state;
        changed("PickupChanged"); // Сповіщаємо Посередника
    }
    public boolean getState() { return isSelfPickup; }
}

// Компоненти, які треба деактивувати при самовивозі
class AddressField extends Component {
    public AddressField(OrderFormMediator mediator) { super(mediator, "AddressField"); }
}
class CourierNotesField extends Component {
    public CourierNotesField(OrderFormMediator mediator) { super(mediator, "CourierNotesField"); }
}


// 4. Конкретний Посередник (Concrete Mediator)

class FlowerOrderMediator implements OrderFormMediator {

    // Всі компоненти (Колеги), якими керує Посередник
    private final DateSelector dateSelector;
    private final TimeSelector timeSelector;
    private final RecipientCheckbox recipientCheckbox;
    private final RecipientNameField nameField;
    private final RecipientPhoneField phoneField;
    private final SelfPickupCheckbox pickupCheckbox;
    private final AddressField addressField;
    private final CourierNotesField courierNotesField;

    // Імітація бази даних доступних слотів
    private final Map<String, List<String>> timeSlotsDB = new HashMap<>();

    public FlowerOrderMediator(
            DateSelector dateSelector, TimeSelector timeSelector,
            RecipientCheckbox recipientCheckbox, RecipientNameField nameField,
            RecipientPhoneField phoneField, SelfPickupCheckbox pickupCheckbox,
            AddressField addressField, CourierNotesField courierNotesField) {

        // Реєстрація всіх компонентів (Колег)
        this.dateSelector = dateSelector;
        this.timeSelector = timeSelector;
        this.recipientCheckbox = recipientCheckbox;
        this.nameField = nameField;
        this.phoneField = phoneField;
        this.pickupCheckbox = pickupCheckbox;
        this.addressField = addressField;
        this.courierNotesField = courierNotesField;

        // Ініціалізація імітованих даних
        timeSlotsDB.put("2026-01-01", Arrays.asList("9:00-12:00", "15:00-18:00"));
        timeSlotsDB.put("2026-01-02", Arrays.asList("10:00-13:00", "14:00-17:00", "19:00-21:00"));
        timeSlotsDB.put("2026-01-03", Arrays.asList("9:00-11:00"));

        // Початкова настройка форми
        handlePickupChange(false);
        handleRecipientChange(false);
    }

    // Центральний метод обробки логіки взаємодії
    @Override
    public void notify(Component sender, String event) {

        // 1. Логіка: Дата доставки впливає на Час доставки
        if (sender == dateSelector && event.equals("DateSelected")) {
            handleDateChange(dateSelector.getSelectedDate());
        }

        // 2. Логіка: Чекбокс "Інша особа" впливає на обов'язковість/видимість полів
        else if (sender == recipientCheckbox && event.equals("RecipientChanged")) {
            handleRecipientChange(recipientCheckbox.getState());
        }

        // 3. Логіка: Чекбокс "Самовивіз" впливає на всі поля доставки
        else if (sender == pickupCheckbox && event.equals("PickupChanged")) {
            handlePickupChange(pickupCheckbox.getState());
        }
    }

    // Приватні методи, що містять конкретну логіку

    private void handleDateChange(String selectedDate) {
        List<String> slots = timeSlotsDB.getOrDefault(selectedDate, Arrays.asList("No slots available"));
        timeSelector.setAvailableTimeSlots(slots);
    }

    private void handleRecipientChange(boolean isOtherRecipient) {
        if (isOtherRecipient) {
            nameField.setVisible(true);
            nameField.setRequired(true);
            phoneField.setVisible(true);
            phoneField.setRequired(true);
        } else {
            nameField.setVisible(false);
            nameField.setRequired(false);
            phoneField.setVisible(false);
            phoneField.setRequired(false);
        }
    }

    private void handlePickupChange(boolean isSelfPickup) {
        boolean deliveryActive = !isSelfPickup;

        // Деактивуємо всі компоненти, що стосуються доставки
        addressField.setEnabled(deliveryActive);
        courierNotesField.setEnabled(deliveryActive);
        dateSelector.setEnabled(deliveryActive);
        timeSelector.setEnabled(deliveryActive);

        // Чекбокс "Інша особа" може бути активний, але його залежності можуть бути невидимими
        recipientCheckbox.setEnabled(deliveryActive);

        // Якщо самовивіз, приховуємо поля Ім'я/Телефон, незалежно від RecipientCheckbox
        if (isSelfPickup) {
            nameField.setVisible(false);
            nameField.setRequired(false);
            phoneField.setVisible(false);
            phoneField.setRequired(false);
        } else {
            // При поверненні до доставки, відновлюємо стан залежно від RecipientCheckbox
            handleRecipientChange(recipientCheckbox.getState());
        }
    }
}


// 5. Клієнтський Код (Демонстрація)
class Main {
    public static void main(String[] args) {

        // 1. Ініціалізація компонентів

        // Компоненти (передаємо null, а потім призначаємо Посередника)
        DateSelector dateSelector = new DateSelector(null);
        TimeSelector timeSelector = new TimeSelector(null);
        RecipientCheckbox recipientCheckbox = new RecipientCheckbox(null);
        RecipientNameField nameField = new RecipientNameField(null);
        RecipientPhoneField phoneField = new RecipientPhoneField(null);
        SelfPickupCheckbox pickupCheckbox = new SelfPickupCheckbox(null);
        AddressField addressField = new AddressField(null);
        CourierNotesField courierNotesField = new CourierNotesField(null);

        // Створення Посередника, який зв'язує їх усіх
        FlowerOrderMediator mediator = new FlowerOrderMediator(
                dateSelector, timeSelector, recipientCheckbox, nameField,
                phoneField, pickupCheckbox, addressField, courierNotesField
        );

        // Налаштування Посередника в Компонентах
        dateSelector.mediator = mediator;
        timeSelector.mediator = mediator;
        recipientCheckbox.mediator = mediator;
        nameField.mediator = mediator;
        phoneField.mediator = mediator;
        pickupCheckbox.mediator = mediator;
        addressField.mediator = mediator;
        courierNotesField.mediator = mediator;

        System.out.println("-----------------------------------------------------");
        System.out.println("          Початковий стан форми (Доставка активна)          ");
        System.out.println("-----------------------------------------------------");

        // СЦЕНАРІЙ 1: Зміна дати доставки (Дата впливає на Час)
        dateSelector.simulateDateChange("2026-01-02");
        dateSelector.simulateDateChange("2026-01-03");

        // СЦЕНАРІЙ 2: Зміна отримувача (Чекбокс впливає на поля Ім'я/Телефон)
        recipientCheckbox.simulateToggle(true); // Встановлюємо "Інша особа" = true

        // СЦЕНАРІЙ 3: Вмикаємо Самовивіз (Самовивіз впливає на всі поля доставки)
        // Коли вмикається самовивіз, усі поля доставки деактивуються/ховаються
        pickupCheckbox.simulateToggle(true);

        // СЦЕНАРІЙ 4: Вимикаємо Самовивіз (Відновлюється стан доставки та полів отримувача)
        // Поля Ім'я/Телефон повинні знову стати активними та обов'язковими, оскільки RecipientCheckbox == true
        pickupCheckbox.simulateToggle(false);

        System.out.println("-----------------------------------------------------");
        System.out.println("        Фінальний стан: Доставка, Інша особа активна        ");
        System.out.println("-----------------------------------------------------");
    }
}