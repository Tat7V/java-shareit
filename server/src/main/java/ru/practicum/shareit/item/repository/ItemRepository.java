package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i JOIN FETCH i.owner WHERE i.request.id = :requestId")
    List<Item> findByRequestIdWithOwner(@Param("requestId") Long requestId);

    List<Item> findByOwnerIdOrderById(Long ownerId);

    @Query("select i from Item i " +
            "where i.available = true " +
            "and (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%')))")
    List<Item> searchAvailableItems(String text);

    List<Item> findByRequestId(Long requestId);
}
