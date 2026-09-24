package com.multimatics.bankflow.transfer.repository;
import com.multimatics.bankflow.transfer.domain.Transfer; import org.springframework.stereotype.Repository; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository public class InMemoryTransferRepository implements TransferRepository {
 private final Map<UUID,Transfer> store=new ConcurrentHashMap<>(); public Transfer save(Transfer t){store.put(t.id(),t);return t;}
 public Optional<Transfer> findById(UUID id){return Optional.ofNullable(store.get(id));} public List<Transfer> findAll(){return List.copyOf(store.values());}
}
