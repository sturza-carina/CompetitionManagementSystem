package persistence.interfaces;

import model.Proba;

public interface IProbaRepository extends Repository<Proba,Long> {
    Iterable<Proba> findByCategorieVarsta(String categorieVarsta);
}
