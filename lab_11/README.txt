Для запуску програми
Необхідно встановити JDK 17 або новішої версії.
Також в CMD виконати
chcp 65001
mkdir out
javac -encoding UTF-8 --release 17 -cp "lib/*" -d out src/*.java
java -Dfile.encoding=UTF-8 -cp "out;lib/*" Main

Структура проєкту

src/Main.java — основна логіка програми.
src/Database.java — підключення та ініціалізація бази даних.
db.properties — параметри підключення.
schema.sql — створення таблиць.
data.sql — початкові дані.
lib/ — бібліотеки.