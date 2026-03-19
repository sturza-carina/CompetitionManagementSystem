package org.example.repository.interfaces;

import org.example.domain.Operator;

public interface IOperatorRepository extends Repository<Operator, Long> {
    Operator findByUsername(String username);
}
