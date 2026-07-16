package com.bank.core.api.mapper;

import com.bank.core.api.dto.request.CreateAccountRequest;
import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "overdraftLimit", ignore = true)
    @Mapping(target = "interestRate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Account toEntity(CreateAccountRequest request);

    @Mapping(target = "ownerUsername", source = "user.username")
    AccountResponse toResponse(Account account);
}