package br.com.rocket.partitura.repository;

import br.com.rocket.partitura.entity.Partitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartituraRepository extends JpaRepository<Partitura, Long> {
}
