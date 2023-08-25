package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.CreditCard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

/**
 * Crud repository to store credit cards
 */
@Repository("CreditCardRepo")
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    CreditCard getById(String creditCardNumber);

    CreditCard findAllById(int userId);

    CreditCard findAllById(String creditCardNumber);

    List<CreditCard> findAllByOwnerId(int userId);
}
