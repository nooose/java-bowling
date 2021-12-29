package bowling.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NormalFrameTest {
    private static Frame frame;

    @BeforeEach
    void beforeEach() {
        frame = NormalFrame.init();
    }

    @DisplayName("bowl 호출 후 종료 상태이면 새로운 Frame을 생성하여 추가한다.")
    @Test
    void frameStateStrikeOrSpareOrMissOrGutter() {
        assertThat(frame.bowl(Ball.of(10, State.READY))).isEqualTo(NormalFrame.next(FrameIndex.of(2)));
    }

    @DisplayName("bowl 호출 후 종료 상태가 아니면 새로운 Frame을 생성하지 않고 현재 Frame을 반환한다.")
    @Test
    void frameStateReadyOrFirst() {
        assertThat(frame.getFrameIndex()).isEqualTo(frame.bowl(Ball.of(5, State.READY)).getFrameIndex());
    }

    @DisplayName("bowl 호출 후 다음 index가 마지막인 경우 FinalFrame을 추가하고 반환한다.")
    @Test
    void finalFrame() {
        for (int i = 0; i < FrameIndex.MAX; i++) {
            frame = frame.bowl(Ball.of(10, State.READY));
        }
        assertThat(frame).isInstanceOf(FinalFrame.class);
    }

    @DisplayName("NormalFrame에서 첫 번째에 스트라이크 친 경우 한 번만 투구한다.")
    @Test
    void strike() {
        frame.bowl(Ball.of(10, State.READY));
        assertThat(frame.symbol()).isEqualTo(State.STRIKE.getSymbol());
        assertThat(frame.isEnd()).isTrue();
    }

    @DisplayName("NormalFrame은 최대 2번 투구한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "5, 4, 5|4, 9",
            "5, 0, 5|-, 5",
            "0, 0, -|-, 0",
            "0, 3, -|3, 3"
    })
    void twoBowlsWithScore(int bowl1, int bowl2, String symbol, int score) {
        List<Integer> pinNumbers = Arrays.asList(bowl1, bowl2);
        for (int pinNumber : pinNumbers) {
            frame.bowl(Ball.of(pinNumber, State.READY));
        }
        assertThat(frame.symbol()).isEqualTo(symbol);
        assertThat(frame.isEnd()).isTrue();
        assertThat(frame.score().getScore()).isEqualTo(score);

    }

    @DisplayName("NormalFrame은 최대 2번 투구한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "8, 2, 8|/",
            "0, 10, -|/"
    })
    void twoBowls(int bowl1, int bowl2, String symbol) {
        List<Integer> pinNumbers = Arrays.asList(bowl1, bowl2);
        for (int pinNumber : pinNumbers) {
            frame.bowl(Ball.of(pinNumber, State.READY));
        }
        assertThat(frame.symbol()).isEqualTo(symbol);
        assertThat(frame.isEnd()).isTrue();
    }

    @DisplayName("NormalFrame에서 쓰러트릴 수 있는 볼링핀의 총 갯수는 10을 초과할 수 없다.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 10",
            "5, 6"
    })
    void exception(int bowl1, int bowl2) {
        List<Integer> pinNumbers = Arrays.asList(bowl1, bowl2);
        assertThatThrownBy( ()-> {
            for (int pinNumber : pinNumbers) {
                frame.bowl(Ball.of(pinNumber, State.READY));
            }
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가 점수 계산시에 스코어 계산 가능 상태이면 현재 점수 반환")
    @Test
    void calculateAdditionalScore() {
        // given
        NormalFrame normalFrame = NormalFrame.of(FrameIndex.of(5), Balls.init());
        Score prevScore = Score.next(ScoreValue.of(10), ScoreBonus.oneMore());

        // when
        Score result = normalFrame.additionalScore(prevScore);

        // then
        assertThat(result.getScore()).isEqualTo(10);
    }

    @DisplayName("추가 점수 계산시에 다음 프레임이 null 이면 점수를 계산할 수 없다.")
    @Test
    void calculateAdditionalScoreNextFrameNull() {
        // given
        NormalFrame normalFrame = NormalFrame.of(FrameIndex.of(5), Balls.init());
        Score prevScore = Score.strike();

        // when
        Score result = normalFrame.additionalScore(prevScore);

        // then
        assertThat(result.canCalculate()).isFalse();
    }
}
