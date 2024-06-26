## pomodoro_app
Приложение - фокусировочный таймер по технике Pomodoro.

## Паттерны проектирования

В данном проекте реализовано несколько паттернов проектирования. 

Мною были выбраны следующие паттерны проектирования:

- Шаблонный метод (Template Method)
- Одиночка (Singleton)

Шаблонный метод относится к категории паттернов поведения и служит одной простой цели - определить основу класса и создать возможность подклассам переопределять некоторые части родительского класса, при этом не изменяя его структуру в целом.
В данной работе есть абстрактный класс Timer и дочерние классы RestTimer, StartTimer, FocusTimer. В родительском классе определены основные поля и методы, а также абстрактные методы, которые будут переопределяться в соответствующих подклассах. Шаблонный метод позволил нам определить общий алгоритм поведения подклассов, избегая дублирования кода.

![Шаблонный метод](references/template.png)

Для класса Bridge (мостик) был использован паттерн "Одиночка". Этот паттерн гарантирует, что у класса есть только один экземпляр, и предоставляет к нему глобальную точку доступа.

![Одиночка](references/singleton.png)

## UML диаграммы

Диаграмма деятельности:

![Диаграмма деятельности](references/diagram_1.png)

Диаграмма коммуникации:

![Диаграмма коммуникации](references/diagram_2.png)

Диаграмма последовательности

![Диаграмма последовательности](references/diagram_3.png)

Диаграмма вариантов использования

![Диаграмма вариантов использования](references/diagram_4.png)

## Скриншоты приложения

![Скриншот 1](references/screenshot_1.png)

![Скриншот_2](references/screenshot_2.png)

![Скриншот_3](references/screenshot_3.png)

![Скриншот_4](references/screenshot_4.png)

![Скриншот_5](references/screenshot_5.png)

![Скриншот_6](references/screenshot_6.png)
