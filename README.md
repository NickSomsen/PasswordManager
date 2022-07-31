# Password manager
Password and notes manager by [Nick Somsen](mailto:nicksomsen@gmail.com) written in Java.

## Functionalities
- Add new notes by clicking the `Add Note` button.
- Edit/expand existing notes by clicking on a notes title
- Delete notes by removing all note content in the edit GUI and clicking `Save`


Dependencies (in `lib` directory):
1. [JDBC SQLite driver](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)
2. [simple.json](https://code.google.com/archive/p/json-simple/downloads)

These have to be added to the classpath. 
IntelliJ: `File > Project Structure > Libraries > + > Java > select jar`.
