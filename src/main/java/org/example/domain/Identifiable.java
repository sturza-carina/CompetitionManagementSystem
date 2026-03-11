package org.example.domain;

public interface Identifiable<Tid> {
    Tid getID();
    void setID(Tid id);
}
