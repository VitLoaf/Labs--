import java.lang.Math;

// Лабораторна робота №7
// Тема: Патерн проектування "Стратегія"

// 1. Інтерфейс Стратегії (Strategy Interface)

/**
 * Strategy: Інтерфейс для розрахунку вартості доставки.
 */
interface DeliveryCostCalculator {
    /**
     * Розраховує вартість доставки на основі параметрів замовлення.
     * @param orderTotal Загальна сума замовлення.
     * @param distanceKm Відстань до клієнта у кілометрах (double).
     * @return Фінальна вартість доставки (double).
     */
    double calculateCost(double orderTotal, double distanceKm);
}

// 2. Конкретні Стратегії (Concrete Strategies)

/**
 * Concrete Strategy 1: Стратегія Самовивозу.
 * Вартість доставки завжди 0.
 */
class SelfPickupStrategy implements DeliveryCostCalculator {
    @Override
    public double calculateCost(double orderTotal, double distanceKm) {
        System.out.println(" (SelfPickup: Cost is always zero)");
        return 0.0;
    }
}

/**
 * Concrete Strategy 2: Доставка зовнішньою службою.
 * Фіксована плата + комісія від суми замовлення.
 */
class ExternalDeliveryStrategy implements DeliveryCostCalculator {
    @Override
    public double calculateCost(double orderTotal, double distanceKm) {
        double baseFee = 45.0;
        double commission = orderTotal * 0.05; // 5%
        double totalCost = baseFee + commission;
        System.out.println(" (External Delivery: Base Fee 45.0 + 5% Commission (" + commission + "))");
        return totalCost;
    }
}

/**
 * Concrete Strategy 3: Доставка власною службою.
 * Плата залежить від відстані, але має мінімальну вартість.
 */
class OwnDeliveryStrategy implements DeliveryCostCalculator {
    @Override
    public double calculateCost(double orderTotal, double distanceKm) {
        double ratePerKm = 8.0;
        double minFee = 50.0;
        double calculatedFee = ratePerKm * distanceKm;

        double finalCost = Math.max(minFee, calculatedFee);
        System.out.println(" (Own Delivery: Distance " + distanceKm + "km @ " + ratePerKm + " UAH/km. Min fee: " + minFee + ")");
        return finalCost;
    }
}

// 3. Контекст (Context)

/**
 * Context: Клас DeliveryApp.
 * Містить посилання на об'єкт Стратегії та делегує йому розрахунок.
 */
class DeliveryApp {
    private DeliveryCostCalculator deliveryStrategy;

    // Контекст може приймати Стратегію через конструктор
    public DeliveryApp(DeliveryCostCalculator strategy) {
        this.deliveryStrategy = strategy;
    }

    // Або дозволяти змінювати її під час виконання
    public void setDeliveryStrategy(DeliveryCostCalculator strategy) {
        this.deliveryStrategy = strategy;
    }

    // Метод, який використовує стратегію
    public double getDeliveryCost(double orderTotal, double distanceKm) {
        System.out.print("App: Calculating cost using "
                + deliveryStrategy.getClass().getSimpleName());

        // Делегування виконання Стратегії
        return deliveryStrategy.calculateCost(orderTotal, distanceKm);
    }
}

// 4. Клієнтський Код

public class StrategyDemo {
    public static void main(String[] args) {
        double orderAmount = 550.0;
        double clientDistance = 6.5; // km

        System.out.println("Order Total: " + orderAmount + ", Distance: " + clientDistance + " km\n");

        // 1. Вибираємо Стратегію: Власна доставка (ініціалізація Контексту)
        DeliveryCostCalculator ownDelivery = new OwnDeliveryStrategy();
        DeliveryApp app = new DeliveryApp(ownDelivery);

        double cost1 = app.getDeliveryCost(orderAmount, clientDistance);
        System.out.println("Final Cost: " + cost1 + " UAH\n");

        // 2. Змінюємо Стратегію: Зовнішня служба доставки
        DeliveryCostCalculator externalDelivery = new ExternalDeliveryStrategy();
        app.setDeliveryStrategy(externalDelivery);

        double cost2 = app.getDeliveryCost(orderAmount, clientDistance);
        System.out.println("Final Cost: " + cost2 + " UAH\n");

        // 3. Змінюємо Стратегію: Самовивіз
        DeliveryCostCalculator pickup = new SelfPickupStrategy();
        app.setDeliveryStrategy(pickup);

        double cost3 = app.getDeliveryCost(orderAmount, clientDistance);
        System.out.println("Final Cost: " + cost3 + " UAH\n");
    }
}