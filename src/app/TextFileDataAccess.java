package app;

import java.util.List;

public interface TextFileDataAccess<T> {
    void update(T o1, int pk);
    List<?> read();
    void write(T o1);
    void delete(int inNum);
}
