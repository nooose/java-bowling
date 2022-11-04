package bowling.domain;

public class NormalFrame extends Frame {
    private static final int MIN_FRAME_NUMBER = 1;
    private static final int MAX_FRAME_NUMBER = 9;

    public NormalFrame() {
        this(1);
    }

    public NormalFrame(int number) {
        checkSize(number);

        this.number = number;
    }

    private void checkSize(int number) {
        if (number < MIN_FRAME_NUMBER || number > MAX_FRAME_NUMBER) {
            throw new IllegalArgumentException(MIN_FRAME_NUMBER + "~" + MAX_FRAME_NUMBER + " 사이의 숫자만 가능합니다.");
        }
    }

    public void bowlV2(int number) {
        state = state.bowl(Pin.of(number));

        if (state.isFinished()) {
            scoreV2 = state.getScore();
            nextFrame = nextFrame();
        }
    }

    @Override
    public boolean canBowl() {
        return !state.isFinished();
    }

    @Override
    public Frame nextFrame() {
        if (number == MAX_FRAME_NUMBER) {
            nextFrame = new FinalFrame(number + 1);
            return nextFrame;
        }

        nextFrame = new NormalFrame(number + 1);
        return nextFrame;
    }

    @Override
    public boolean isLastFrame() {
        return false;
    }
}
