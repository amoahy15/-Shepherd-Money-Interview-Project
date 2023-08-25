package com.shepherdmoney.interviewproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;;

@RestController
@RequestMapping("/api") // Assuming you have a base path for your API
public class CreditCardController {

    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    @Autowired
    public CreditCardController(CreditCardRepository creditCardRepository, UserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        try {
            // Find the user by userId
            User user = userRepository.findById(payload.getUserId()).orElse(null);

            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Create a new credit card entity
            CreditCard creditCard = new CreditCard();
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setNumber(payload.getCardNumber());
            creditCard.setOwner(user);

            // Save the credit card to the repository
            creditCardRepository.save(creditCard);

            return new ResponseEntity<>(creditCard.getId(), HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exceptions here and return appropriate response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/credit-card/all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        try {
            List<CreditCard> creditCards = creditCardRepository.findAllByOwnerId(userId);

            List<CreditCardView> creditCardViews = creditCards.stream()
                    .map(creditCard -> new CreditCardView(creditCard.getIssuanceBank(), creditCard.getNumber()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(creditCardViews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/credit-card/user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        CreditCard creditCard = creditCardRepository.getById(creditCardNumber);

        if (creditCard != null) {
            return new ResponseEntity<>(creditCard.getOwner().getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/credit-card/update-balance")
    public ResponseEntity<String> updateBalanceHistory(@RequestBody UpdateBalancePayload[] payload) {
        try {
            for (UpdateBalancePayload transaction : payload) {
                CreditCard creditCard = creditCardRepository.findAllById(transaction.getCreditCardNumber());

                if (creditCard == null) {
                    return new ResponseEntity<>("Credit card not found", HttpStatus.BAD_REQUEST);
                }

                LocalDate transactionDate = LocalDate.parse(transaction.getDate());
                LocalDate today = LocalDate.now();

                // Skip transactions with dates in the future
                if (transactionDate.isAfter(today)) {
                    continue;
                }

                // Update balance history
                creditCard.getBalanceHistory().add(new BalanceHistory());
                creditCardRepository.save(creditCard);
            }

            return new ResponseEntity<>("Balance history updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
