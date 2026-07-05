package com.bank.core.api.mapper;

import com.bank.core.api.dto.response.TransactionResponse;
import com.bank.core.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "sourceAccountNumber", source = "sourceAccount.accountNumber")
    @Mapping(target = "sourceAccountType", source = "sourceAccount.accountType")
    @Mapping(target = "destinationAccountNumber", source = "destinationAccount.accountNumber")
    @Mapping(target = "destinationAccountType", source = "destinationAccount.accountType")
    TransactionResponse toResponse(Transaction transaction);
}