# y_lab_homework
Homework2

Запуск:
1. Убедитесь, что у вас установлены необходимые инструменты:
Java Development Kit (JDK): Убедитесь, что установлен JDK версии 17. Вы можете проверить это, выполнив команду:
java -version
Apache Maven: Убедитесь, что Maven установлен. Вы можете проверить это, выполнив команду:
mvn -v
2. Клонируйте проект
Если проект хранится в репозитории Git, клонируйте его с помощью:

git clone https://github.com/ne-kit-28/y_lab_homework.git
cd y_lab_homework
git checkout -b homework2 origin/homework2
cd homework2

3. Запустите докер
поднимите контейнер с БД:
docker compose up -d

4. Сборка проекта
Выполните команду, чтобы собрать проект. Это скомпилирует код, выполнит тесты и создаст исполняемый JAR-файл.

mvn clean install

5. Запуск проекта
После успешной сборки и запуска БД можно запустить проект.
java -jar target/habits-latest.jar