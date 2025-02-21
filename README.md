# Expense Tracker

This is an app that can be used to track your expenses using a database.
It will show the analytics through useful charts.

To start the database:
```shell
docker compose -f docker-compose.yaml up
```

If it is the first time you start up the database you have to at least create 1 user, in `LoginController.initDataSource()` you will find these lines, uncomment them:

```
//userRepository.deleteAll();
//userRepository.save(new User("user", String.valueOf("user".hashCode())));
```

More than one client can be connected to the same database, even if it is not hosted locally.
It has to be at least hosted in a device connected to the same Wi-Fi network, and then you can connect to it by replacing `App.JDBC_URL` with this string:

```java
private static final String JDBC_URL = "jdbc:postgresql://{YOUR_IP_ADDRESS}:543/jdbc_schema?user=user&password=secret&ssl=false";
```

Remember to replace `{YOUR_IP_ADDRESS}` with your ip address

## Features

* Protection to user input
* Auto refresh the charts every time you change the expenses
* Pie chart that shows in which type of expenses you sped the most
* Area chart that shows the expenses through the year
* Import/export data using .json files
* Multi user support

### Images and icons found in:

[icons8](https://icons8.it/icons)

## Class Diagram
```mermaid
classDiagram

    Application <|-- App
    App --> ViewController

    DataSource <|-- ViewController
    Closeable <|-- ViewController

    ViewController <|.. LoginViewController
    LoginViewController --> Repository

    ViewController <|.. MainPageViewController
    MainPageViewController --> Repository
    MainPageViewController --> ChartController
    MainPageViewController --> AddExpenseDialog

    Closeable <|-- ChartController
    DataToCharts <|-- ChartController

    ChartController <|.. ExpenseAreaChartController
    ExpenseAreaChartController --> MainPageViewController

    ChartController <|.. ExpensePieChartController
    ExpensePieChartController --> MainPageViewController

    Repository <|.. ExpenseRepository
    ExpenseRepository --> Expense

    Repository <|.. UserRepository
    UserRepository --> User

    Expense --> ExpenseType
    Expense --> PayingMethod

    Dialog <|-- AddExpenseDialog
    AddExpenseDialog --> Expense


class Application {
    +start(primaryStage : Stage) : void
}
class App {
    - JDBC_Driver : String
    - JDBC_URL : String
    - createDataSource() : HikariDataSource
    + main(args : String[]) : void
    + start(primaryStage : Stage) : void
}


class DataSource {
    + initDataSource(hikariDataSource : HikariDataSource) : void
}
class Closeable {
    + close() : void
}
class ViewController{
   + close() : void
   + initDataSource(hikariDataSource : HikariDataSource) : void
}

class Repository{
     + findById(id : ID) : Optional<T>
     + findAll() : Iterable<T>
     + save(entity : T) : T
     + deleteById(id : ID) : void
     + deleteAll() : void
}

class LoginViewController {
    - tfPassword : PasswordField
    - tfUsername : TextField
    - hikariDataSource : HikariDataSource
    - onCancelClicked() : void
    - onOKClicked() : void
    - launchApplication() : void
    + close() : void
    + initDataSource(hikariDataSource : HikariDataSource) : void
}

class MainPageViewController {
    - hikariDataSource : HikariDataSource
    + initialize() : void
    + initDataSource(hikariDataSource : HikariDataSource) : void
    + close() : void
}

class DataToCharts {
    + initCharts(expensesData : ObservableList<Expense>, mainPageViewController : MainPageViewController) : void
    + updateCharts(expensesData : ObservableList<Expense>) : void
}

class ChartController {
    + initCharts(expensesData : ObservableList<Expense>, mainPageViewController : MainPageViewController) : void
    + updateCharts(expensesData : ObservableList<Expense>) : void
    + showAboutInformation(contentText : String )
    + close() : void
}

class ExpenseAreaChartController {
    + close() : void
    + initCharts(expensesData : ObservableList<Expense>, mainPageViewController : MainPageViewController) : void
    + updateCharts(expensesData : ObservableList<Expense>) : void
}

class ExpensePieChartController {
    + close() : void
    + initCharts(expensesData : ObservableList<Expense>, mainPageViewController : MainPageViewController) : void
    + updateCharts(expensesData : ObservableList<Expense>) : void
}

class ExpenseRepository {
    - LOG : Logger
    - hikariDataSource : HikariDataSource
    + ExpenseRepository(hikariDataSource : HikariDataSource)
    + findById(id : Long) : Optional<Expense>
    + findAll() : Iterable<Expense>
    + save(entity : Expense) : Expense
    + deleteById(id : Long) : void
    + deleteAll() : void
}

class Expense {
    - id : Long
    - amount : Double
    - date : LocalDate
    - description : String

    + getAllExpenseTypes() : String
    + getAllPayingMethods() : String
}
class ExpenseType {
    Miscellaneous
    Subscription
    EatingOut
    Groceries
    Debt
    Car
}
class PayingMethod {
    Card
    Cash
}

class UserRepository {
    - hikariDataSource : HikariDataSource
    + UserRepository(hikariDataSource : HikariDataSource)
    + findById(id : Long) : Optional<User>
    + findAll() : Iterable<User>
    + save(entity : User) : User
    + deleteById(id : Long) : void
    + deleteAll() : void
}
class User {
    - id : Long
    - username : String
    - password : String
}

class AddExpenseDialog {
    + initialize() : void
    + AddExpenseDialog()
}
```
