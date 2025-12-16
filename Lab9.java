import java.util.List;
import java.util.ArrayList;

// Лабораторна робота №9
// Тема: Патерн проектування "Відвідувач" (Visitor)

// 1. Ієрархія Елементів (Організаційна структура)

// Element Interface
interface OrganizationElement {
    // Метод для прийому Відвідувача (подвійна диспетчеризація)
    String accept(OrganizationVisitor visitor);
}

// Concrete Element 1: Співробітник
class Employee implements OrganizationElement {
    private final String position;
    private final double salary;

    public Employee(String position, double salary) {
        this.position = position;
        this.salary = salary;
    }

    // Геттери, які потрібні Відвідувачу
    public double getSalary() { return salary; }
    public String getPosition() { return position; }

    // Викликає відповідний метод visit у Відвідувача, передаючи себе
    @Override
    public String accept(OrganizationVisitor visitor) {
        return visitor.visitEmployee(this);
    }
}

// Concrete Element 2: Департамент
class Department implements OrganizationElement {
    private final String name;
    private final List<Employee> employees;

    public Department(String name, List<Employee> employees) {
        this.name = name;
        this.employees = employees;
    }

    // Геттери, які потрібні Відвідувачу
    public List<Employee> getEmployees() { return employees; }
    public String getName() { return name; }

    // Викликає відповідний метод visit у Відвідувача, передаючи себе
    @Override
    public String accept(OrganizationVisitor visitor) {
        return visitor.visitDepartment(this);
    }
}

// Concrete Element 3: Компанія
class Company implements OrganizationElement {
    private final String name;
    private final List<Department> departments;

    public Company(String name, List<Department> departments) {
        this.name = name;
        this.departments = departments;
    }

    // Геттери, які потрібні Відвідувачу
    public List<Department> getDepartments() { return departments; }
    public String getName() { return name; }

    // Викликає відповідний метод visit у Відвідувача, передаючи себе
    @Override
    public String accept(OrganizationVisitor visitor) {
        return visitor.visitCompany(this);
    }
}


// 2. Ієрархія Відвідувачів (Visitor)

// Visitor Interface
interface OrganizationVisitor {
    // Методи для відвідування кожного конкретного елемента
    String visitCompany(Company company);
    String visitDepartment(Department department);
    String visitEmployee(Employee employee);
}

// Concrete Visitor: Зарплатна відомість
class SalaryReportVisitor implements OrganizationVisitor {

    // Використовуємо StringBuilder для ефективного формування звіту
    private final StringBuilder reportBuilder = new StringBuilder();

    @Override
    public String visitCompany(Company company) {
        reportBuilder.setLength(0); // Очищаємо звіт перед новим формуванням
        reportBuilder.append("\n--- SALARY REPORT for Company: ").append(company.getName()).append(" ---\n");

        double totalSalary = 0;

        // Рекурсивний обхід структури
        for (Department dept : company.getDepartments()) {
            // Викликаємо accept на департаменті, який поверне фрагмент звіту
            String deptReportFragment = dept.accept(this);
            reportBuilder.append(deptReportFragment);
            totalSalary += calculateDepartmentTotal(dept);
        }

        reportBuilder.append("\n--- TOTAL COMPANY PAYOUT: ").append(totalSalary).append(" UAH ---\n");
        return reportBuilder.toString();
    }

    @Override
    public String visitDepartment(Department department) {
        StringBuilder deptReport = new StringBuilder();
        deptReport.append("\n  [Department: ").append(department.getName()).append("]");

        for (Employee emp : department.getEmployees()) {
            deptReport.append(emp.accept(this)); // Відвідувач "переходить" до Співробітника
        }

        double departmentTotal = calculateDepartmentTotal(department);
        deptReport.append("\n  [Department Total: ").append(departmentTotal).append(" UAH]\n");

        return deptReport.toString();
    }

    @Override
    public String visitEmployee(Employee employee) {
        // Логіка збору даних про зарплату
        return "\n    - " + employee.getPosition() + ": " + employee.getSalary() + " UAH";
    }

    // Допоміжний приватний метод для розрахунку загальної суми департаменту
    private double calculateDepartmentTotal(Department department) {
        double total = 0;
        for (Employee emp : department.getEmployees()) {
            total += emp.getSalary();
        }
        return total;
    }
}


// 3. Клієнтський Код

public class VisitorDemo {
    public static void main(String[] args) {

        // 1. Створення об'єктної структури (Елементів)
        Employee dev1 = new Employee("Senior Developer", 7000.0);
        Employee dev2 = new Employee("Junior Developer", 3000.0);
        Employee qa1 = new Employee("QA Engineer", 4500.0);
        Employee qa2 = new Employee("QA Lead", 6000.0);

        Department devDept = new Department("Development", List.of(dev1, dev2));
        Department qaDept = new Department("Quality Assurance", List.of(qa1, qa2));

        List<Department> allDepartments = new ArrayList<>();
        allDepartments.add(devDept);
        allDepartments.add(qaDept);

        Company techCorp = new Company("TechCorp Solutions", allDepartments);

        // 2. Створення конкретного Відвідувача (Репорту)
        SalaryReportVisitor salaryVisitor = new SalaryReportVisitor();

        // ----------------------------------------------------
        // A. Отримання репорту для всієї Компанії
        // ----------------------------------------------------
        System.out.println("\n--- Отримання репорту для всієї Компанії ---");
        // Клієнт викликає accept() на Компанії
        String companyReport = techCorp.accept(salaryVisitor);
        System.out.println(companyReport);

        // ----------------------------------------------------
        // B. Отримання репорту для конкретного Департаменту
        // ----------------------------------------------------
        System.out.println("\n--- Отримання репорту для конкретного Департаменту (QA) ---");
        // Клієнт викликає accept() на конкретному Департаменті

        // Створюємо новий об'єкт Відвідувача для чистоти репорту
        SalaryReportVisitor deptVisitor = new SalaryReportVisitor();
        String deptReport = qaDept.accept(deptVisitor);
        System.out.println(deptReport);
    }
}