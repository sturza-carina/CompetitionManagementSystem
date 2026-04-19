package persistence.interfaces;

import model.Operator;

public interface IOperatorRepository extends Repository<Operator, Long> {
    Operator findByUsername(String username);
}
