package bowling.domain.state;

import bowling.domain.Pin;
import bowling.domain.Score;

import java.util.List;

public class Strike extends Finished {

    @Override
    public State bowl(Pin pin) {
        throw new UnsupportedOperationException("더 이상 공을 던질 수 없습니다.");
    }

    @Override
    public Score getScore() {
        return new Score(Pin.MAX_COUNT, 2);
    }

    @Override
    public Score calculateAdditionalScore(Score score) {
        return score.add(Pin.MAX_COUNT);
    }

    @Override
    public List<Pin> pins() {
        return List.of(Pin.of(10));
    }
}
