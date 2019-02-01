package com.example.microservicesinactionbook;

import com.example.microservicesinactionbook.domain.Multiplication;
import com.example.microservicesinactionbook.domain.MultiplicationResultAttempt;
import com.example.microservicesinactionbook.domain.User;
import com.example.microservicesinactionbook.service.MultiplicationService;
import com.example.microservicesinactionbook.service.MultiplicationServiceImpl;
import com.example.microservicesinactionbook.service.RandomGeneratorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;

public class MultiplicationServiceImplTest {
    private MultiplicationService multiplicationService;

    @Mock
    RandomGeneratorService randomGeneratorService;

    @Before
    public void setUp() {
        //tell mockito process annotations
        MockitoAnnotations.initMocks(this);
        multiplicationService = new MultiplicationServiceImpl(randomGeneratorService);
    }

    @Test
    public void createRandomMultiplicationTest() {
        // random mocked rata from first 50 then 30
        given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

        //when
        Multiplication multiplication = multiplicationService.createRandomMultiplication();

        //assert
        assertThat(multiplication.getFactorA()).isEqualTo(50);
        assertThat(multiplication.getFactorB()).isEqualTo(30);
       // assertThat(multiplication.getResult()).isEqualTo(1500);
    }

    @Test
    public void checkCorrectAttemptsTest(){
        //given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("test_usr");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000);

        //when
        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        //assert
        assertThat(attemptResult).isTrue();
    }

    @Test
    public void checkWrongAttemptTest(){
        //given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("test_usr");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user,multiplication, 3010);

        //when
        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        //assert
        assertThat(attemptResult).isFalse();
    }

}
