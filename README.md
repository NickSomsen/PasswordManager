# Password manager
Password and notes manager by [Nick Somsen](mailto:nicksomsen@gmail.com). Written completely in Java (Java Runtime class file version 62.0), only tested on Windows.

## Functionalities
- Add new notes by clicking the `Add Note` button
- Edit/Delete existing notes by clicking on a notes title
- Search trough note title and content, with highlighting

## Running
Run PasswordManager by opening/double clicking `PasswordManager.jar`.

Or via command line: `java -jar PasswordManager.jar`

## Dependencies (included in `.jar`):
In `lib` directory:
1. [JDBC SQLite driver](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)
2. [simple.json](https://code.google.com/archive/p/json-simple/downloads)

These have to be added to the classpath if not ran via `.jar`. 
IntelliJ: `File > Project Structure > Libraries > + > Java > select jar`.
