package com.example.microservicesinactionbook;

import com.example.microservicesinactionbook.domain.Multiplication;
import com.example.microservicesinactionbook.domain.MultiplicationResultAttempt;
import com.example.microservicesinactionbook.domain.User;
import com.example.microservicesinactionbook.repository.MultiplicationResultAttemptRepository;
import com.example.microservicesinactionbook.repository.UserRepository;
import com.example.microservicesinactionbook.service.MultiplicationService;
import com.example.microservicesinactionbook.service.MultiplicationServiceImpl;
import com.example.microservicesinactionbook.service.RandomGeneratorService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class MultiplicationServiceImplTest {
    private MultiplicationService multiplicationService;

    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private MultiplicationResultAttemptRepository attemptRepository;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() {
        //tell mockito process annotations
        MockitoAnnotations.initMocks(this);
        multiplicationService = new MultiplicationServiceImpl(randomGeneratorService, attemptRepository, userRepository);
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
    public void checkCorrectAttemptsTest() {
        //given
        Multiplication multiplication = new Multiplication(50, 60);

        User user = new User("test_usr");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);
        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, true);

        given(userRepository.findByAlias("test_usr")).willReturn(Optional.empty());

        //when
        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        //assert
        assertThat(attemptResult).isTrue();
        verify(attemptRepository).save(verifiedAttempt);
    }

    @Test
    public void checkWrongAttemptTest() {
        //given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("test_usr");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);

        given(userRepository.findByAlias("test_usr")).willReturn(Optional.empty());

        //when
        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        //assert
        assertThat(attemptResult).isFalse();
        verify(attemptRepository).save(attempt);
    }

    @Test
    public void retrieveStatsTest() {
        //given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("test_usr");
        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(
                user,
                multiplication,
                3010,
                false);

        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(
                user,
                multiplication,
                3051,
                false);

        List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);

        given(userRepository.findByAlias("test_usr")).willReturn(Optional.empty());
        given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("test_usr")).willReturn(latestAttempts);

        //when
        List<MultiplicationResultAttempt> latestAttemptsResult = multiplicationService.getStatsForUser("test_usr");

        //then
        assertThat(latestAttemptsResult).isEqualTo(latestAttempts);
    }
}
