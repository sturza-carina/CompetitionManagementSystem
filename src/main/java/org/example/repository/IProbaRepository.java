package org.example.repository;

import org.example.domain.Operator;
import org.example.domain.Proba;

public interface IProbaRepository extends Repository<Proba,Long>{
    Iterable<Proba> findByCategorieVarsta(String categorieVarsta);
}
