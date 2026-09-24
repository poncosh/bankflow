package com.multimatics.bankflow.transfer.repository;
import com.multimatics.bankflow.transfer.domain.Transfer; import java.util.*;
public interface TransferRepository { Transfer save(Transfer transfer); Optional<Transfer> findById(UUID id); List<Transfer> findAll(); }
