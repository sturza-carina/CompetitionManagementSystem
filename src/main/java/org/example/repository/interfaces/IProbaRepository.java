package org.example.repository.interfaces;

import org.example.domain.Proba;

public interface IProbaRepository extends Repository<Proba,Long> {
    Iterable<Proba> findByCategorieVarsta(String categorieVarsta);
}
